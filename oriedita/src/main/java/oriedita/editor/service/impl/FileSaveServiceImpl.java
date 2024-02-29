package oriedita.editor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.Canvas;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.LineStyle;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.export.Cp;
import oriedita.editor.export.Dxf;
import oriedita.editor.export.Fold;
import oriedita.editor.export.Obj;
import oriedita.editor.export.Orh;
import oriedita.editor.export.Svg;
import oriedita.editor.json.DefaultObjectMapper;
import oriedita.editor.save.BaseSave;
import oriedita.editor.save.FileVersionTester;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveConverter;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.ApplicationModelPersistenceService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.ResetService;
import oriedita.editor.swing.dialog.ExportDialog;
import oriedita.editor.swing.dialog.FileDialogUtil;
import oriedita.editor.swing.dialog.SaveTypeDialog;
import oriedita.editor.tools.ResourceUtil;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.List;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static oriedita.editor.swing.dialog.FileDialogUtil.openFileDialog;
import static oriedita.editor.swing.dialog.FileDialogUtil.saveFileDialog;

@ApplicationScoped
public class FileSaveServiceImpl implements FileSaveService {
    private final FrameProvider frame;
    private final Camera creasePatternCamera;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final Fold fold;
    private final Canvas canvas;
    private final FileModel fileModel;
    private final ApplicationModel applicationModel;
    private final FoldedFiguresList foldedFiguresList;
    private final ResetService resetService;
    private final ButtonService buttonService;
    private final BackgroundModel backgroundModel;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private Path autoSavePath;

    private ScheduledThreadPoolExecutor schedulePool;
    private ScheduledFuture<?> autoSaveFuture;

    @Inject
    private ApplicationModelPersistenceService applicationModelPersistenceService;


    @Inject
    public FileSaveServiceImpl(
            FrameProvider frame,
            @Named("creasePatternCamera") Camera creasePatternCamera,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            Fold fold,
            Canvas canvas,
            FileModel fileModel,
            ApplicationModel applicationModel,
            FoldedFiguresList foldedFiguresList,
            ResetService resetService,
            BackgroundModel backgroundModel,
            ButtonService buttonService) {
        this.frame = frame;
        this.creasePatternCamera = creasePatternCamera;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.fold = fold;
        this.canvas = canvas;
        this.fileModel = fileModel;
        this.applicationModel = applicationModel;
        this.foldedFiguresList = foldedFiguresList;
        this.resetService = resetService;
        this.buttonService = buttonService;
        this.backgroundModel = backgroundModel;
    }

    @Override
    public void openFile(File file) throws FileReadingException {
        if (file == null || !file.exists()) {
            return;
        }

        fileModel.setSaved(true);
        fileModel.setSavedFileName(file.getAbsolutePath());
        applicationModel.setDefaultDirectory(file.getParent());

        Save memo_temp = readImportFile(file);
        Logger.info("readFile2Memo() 終了");

        if (memo_temp != null) {
            //Initialization of development drawing started
            resetService.developmentView_initialization();
            //Deployment parameter initialization

            mainCreasePatternWorker.setCamera(creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainCreasePatternWorker.setSave_for_reading(memo_temp);
            mainCreasePatternWorker.record();
        }

        applicationModel.addPropertyChangeListener((event) -> {
            if (event.getPropertyName() ==null || event.getPropertyName().equals("autoSaveInterval")) {
                updateAutoSave(applicationModel.getAutoSaveInterval());
            }
        });
    }

    @Override
    public void openFile() {
        Logger.info("readFile2Memo() 開始");

        if (saveUnsavedFile()) return;

        File file = selectOpenFile();

        try {
            openFile(file);
        } catch (FileReadingException e) {
            Logger.error(e, "Error during file read");
            JOptionPane.showMessageDialog(frame.get(), "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void importPref() {
        Path importPath = Path.of(FileDialogUtil.openFileDialog(
                frame.get(),
                "Import...",
                applicationModel.getDefaultDirectory(),
                new String[]{"*.oriconfig"},
                null));
        File zipFile = importPath.toFile();
        String extension = ".oriconfig";

        if(!zipFile.getName().endsWith(extension)){
            JOptionPane.showMessageDialog(frame.get(), String.format("The file must have %s as the extension", extension),"Wrong import file format", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ZipEntry ze;
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))){
            while ((ze = zis.getNextEntry()) != null){
                if (ze.getName().equals("config.json")){
                    applicationModelPersistenceService.importApplicationModel(zis);
                } else if (ze.getName().equals("hotkey.properties")){
                    readImportHotkey(zis, ze, buttonService);
                }
            }
        } catch (IOException e) {
            Logger.info("zis closed");
            throw new RuntimeException(e);
        }
    }

    private void readImportHotkey(ZipInputStream zis, ZipEntry ze, ButtonService buttonService){
        try {
            ResourceBundle userBundle = new PropertyResourceBundle(zis);

            for(String action : userBundle.keySet()){
                String importKeyStroke = userBundle.getString(action);
                String bundleName = ze.getName().split("\\.")[0];
                buttonService.setKeyStroke(KeyStroke.getKeyStroke(importKeyStroke), action);
                ResourceUtil.updateBundleKey(bundleName, action, importKeyStroke);
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void exportPref(){
        File configDir = new File(ResourceUtil.getAppDir().toUri());
        List<String> fileNames = new ArrayList<>();

        for (File file : Objects.requireNonNull(configDir.listFiles())){
            fileNames.add(file.getName());
        }

        Path exportPath = Path.of(FileDialogUtil.saveFileDialog(frame.get(), "Export...", applicationModel.getDefaultDirectory(), new String[]{"*.oriconfig"}, null));

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
            fileNames.forEach(fileName -> {
                Path filePath = ResourceUtil.getAppDir().resolve(fileName);
                try (InputStream fis = new FileInputStream(filePath.toFile())) {
                    zout.putNextEntry(new ZipEntry(fileName));
                    fis.transferTo(zout);
                    zout.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean saveUnsavedFile() {
        if (!fileModel.isSaved()) {
            int choice = JOptionPane.showConfirmDialog(frame.get(), "<html>Current file not saved.<br/>Do you want to save it?", "File not saved", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                saveFile();
            } else {
                return choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.CANCEL_OPTION;
            }
        }
        return false;
    }

    @Override
    public void importFile() {
        if (saveUnsavedFile()) return;

        Logger.info("readFile2Memo() 開始");
        File importFile = selectImportFile();
        Save memo_temp = null;
        try {
            memo_temp = readImportFile(importFile);
        } catch (FileReadingException e) {
            Logger.error(e, "Error during file import");
            JOptionPane.showMessageDialog(null, "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
        }
        Logger.info("readFile2Memo() 終了");

        if (memo_temp != null) {
            fileModel.setSavedFileName(null);

            //Initialization of development drawing started
            resetService.developmentView_initialization();
            //Deployment parameter initialization

            //Initialization of folding prediction map started

            mainCreasePatternWorker.setCamera(creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainCreasePatternWorker.setSave_for_reading(memo_temp);
            mainCreasePatternWorker.record();
        }
    }

    @Override
    public void exportFile() {
        File exportFile = selectExportFile();

        if (exportFile == null) {
            return;
        }

        if (exportFile.getName().endsWith(".svg")) {
            boolean displayCpLines = applicationModel.getDisplayCpLines();
            float lineWidth = applicationModel.determineCalculatedLineWidth();
            int intLineWidth = applicationModel.getLineWidth();
            LineStyle lineStyle = applicationModel.getLineStyle();
            int pointSize = applicationModel.getPointSize();
            boolean showText = applicationModel.getDisplayComments();

            Svg.exportFile(mainCreasePatternWorker.getFoldLineSet(), mainCreasePatternWorker.getTextWorker().getTexts(), showText, mainCreasePatternWorker.getCamera(), displayCpLines, lineWidth, intLineWidth, lineStyle, pointSize, foldedFiguresList, exportFile);
        } else if (exportFile.getName().endsWith(".png") || exportFile.getName().endsWith(".jpg") || exportFile.getName().endsWith(".jpeg")) {
            canvas.writeImageFile(exportFile);
        } else if (exportFile.getName().endsWith(".cp")) {
            Cp.exportFile(mainCreasePatternWorker.getSave_for_export(), exportFile);
        } else if (exportFile.getName().endsWith(".orh")) {
            Orh.exportFile(mainCreasePatternWorker.getSave_for_export_with_applicationModel(), exportFile);
        } else if (exportFile.getName().endsWith(".fold")) {
            try {
                fold.exportFile(mainCreasePatternWorker.getSave_for_export(), exportFile);
            } catch (InterruptedException | FileReadingException e) {
                e.printStackTrace();
            }
        } else if (exportFile.getName().endsWith(".dxf")) {
            Dxf.exportFile(mainCreasePatternWorker.getSave_for_export(), exportFile);
        }
    }

    public File selectOpenFile() {
        String fileName = openFileDialog(frame.get(), "Open File", applicationModel.getDefaultDirectory(), new String[]{"*.ori", "*.cp", "*.fold"}, "Supported files (.ori, .cp, .fold)");

        if (fileName == null) {
            return null;
        }

        File selectedFile = new File(fileName);

        if (!selectedFile.exists()) {
            return null;
        }

        applicationModel.setDefaultDirectory(selectedFile.getParent());
        fileModel.setSavedFileName(selectedFile.getAbsolutePath());
        fileModel.setSaved(true);

        applicationModel.addRecentFile(selectedFile);

        return selectedFile;
    }

    @Override
    public File selectSaveFile() {
        String saveType = SaveTypeDialog.showSaveTypeDialog(frame.get());

        if (saveType == null) {
            return null;
        }

        String fileName = saveFileDialog(frame.get(), "Save As...", applicationModel.getDefaultDirectory(), new String[]{"*" + saveType}, null);

        if (fileName == null) {
            return null;
        }

        if (!fileName.endsWith(saveType)) {
            fileName += saveType;
        }

        File selectedFile = new File(fileName);

        applicationModel.setDefaultDirectory(selectedFile.getParent());
        fileModel.setSavedFileName(selectedFile.getAbsolutePath());
        fileModel.setSaved(true);
        applicationModel.addRecentFile(selectedFile);

        return selectedFile;
    }

    @Override
    public File selectImportFile() {
        String fileName = openFileDialog(frame.get(), "Import...", applicationModel.getDefaultDirectory(), new String[]{"*.ori", "*.cp", "*.orh", "*.fold"}, "Supported files (.ori, .cp, .orh, .fold)");

        if (fileName == null) {
            return null;
        }

        File selectedFile = new File(fileName);

        if (!selectedFile.exists()) {
            return null;
        }

        applicationModel.setDefaultDirectory(selectedFile.getParent());

        return selectedFile;
    }

    @Override
    public File selectExportFile() {
        String exportType = ExportDialog.showExportDialog(frame.get());

        if (exportType == null) {
            return null;
        }

        String fileName = saveFileDialog(frame.get(), "Export...", applicationModel.getDefaultDirectory(), new String[]{"*" + exportType}, null);

        if (fileName == null) {
            return null;
        }

        if (!fileName.endsWith(exportType)) {
            fileName += exportType;
        }

        File selectedFile = new File(fileName);

        applicationModel.setDefaultDirectory(selectedFile.getParent());

        return selectedFile;
    }

    @Override
    public Save readImportFile(File file) throws FileReadingException {
        return readImportFile(file, true);
    }

    @Override
    public Save readImportFile(File file, boolean askOnUnknownFormat) {
        if (file == null) {
            return null;
        }

        if (!file.exists()) {
            return null;
        }

        Save save = null;

        try {
            if (file.getName().endsWith(".ori")) {
                try {
                    ObjectMapper mapper = new DefaultObjectMapper();
                    Save readSave = mapper.readValue(file, Save.class);
                    FileVersionTester versionTester = mapper.readValue(file, FileVersionTester.class);
                    if (readSave.getClass() == BaseSave.class && versionTester.getVersion() == null) { // happens when the version id is not recognized
                        int result = JOptionPane.NO_OPTION;
                        if (askOnUnknownFormat) {
                            result = JOptionPane.showConfirmDialog(frame.get(), "This file was created using a newer version of oriedita.\n" +
                                            "Using it with this version of oriedita might remove parts of the file.\n" +
                                            "Do you want to open the file anyways?", "File created in newer version",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        }

                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                return SaveConverter.convertToNewestSave(readSave);
                            case JOptionPane.NO_OPTION:
                                return null;
                        }
                    }
                    return SaveConverter.convertToNewestSave(readSave);
                } catch (IOException e) {
                    throw new FileReadingException(e);
                }
            }

            if (file.getName().endsWith(".obj")) {
                save = Obj.importFile(file);
            }

            if (file.getName().endsWith(".fold")) {
                save = fold.importFile(file);
            }

            if (file.getName().endsWith(".cp")) {
                save = Cp.importFile(file);
            }

            if (file.getName().endsWith(".orh")) {
                save = Orh.importFile(file);
            }

        } catch (IOException | FileReadingException e) {
            Logger.error(e, "Opening file failed");

            JOptionPane.showMessageDialog(frame.get(), "Opening of the saved file failed", "Opening failed", JOptionPane.ERROR_MESSAGE);

            fileModel.setSavedFileName(null);

            return SaveProvider.createInstance();
        }

        return save;
    }

    @Override
    public void saveFile() {
        if (fileModel.getSavedFileName() == null) {
            saveAsFile();

            return;
        }

        File file = new File(fileModel.getSavedFileName());

        Save save = mainCreasePatternWorker.getSave_for_export();

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    @Override
    public void saveAsFile() {
        File file = selectSaveFile();

        if (file == null) {
            return;
        }

        Save save = mainCreasePatternWorker.getSave_for_export();

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    public void saveAndName2File(Save save, File fname) {
        if (fname.getName().endsWith(".ori")) {
            try {
                ObjectMapper mapper = new DefaultObjectMapper();

                mapper.writeValue(fname, save);
            } catch (IOException e) {
                Logger.error(e, "Writing .ori failed");
            }
        } else if (fname.getName().endsWith(".cp")) {
            if (!save.canSaveAsCp()) {
                JOptionPane.showMessageDialog(frame.get(), "The saved .cp file does not contain circles, text and yellow aux lines. Save as a .ori file to also save these lines.", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            Cp.exportFile(save, fname);
        } else if (fname.getName().endsWith((".fold"))){
            try {
                fold.exportFile(mainCreasePatternWorker.getSave_for_export(), fname);
            } catch (InterruptedException | FileReadingException e) {
                e.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(frame.get(), "Unknown file type, cannot save", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    @Override
    public boolean readBackgroundImageFromFile() {
        String filename = openFileDialog(frame.get(), "Select Image File.", applicationModel.getDefaultDirectory(), new String[]{"*.png", "*.jpg"}, "Supported image formats (.png, .jpg)");

        if (filename != null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Image img_background = tk.getImage(filename);

            if (img_background != null) {
                backgroundModel.setBackgroundImage(img_background);
                backgroundModel.setDisplayBackground(true);
                backgroundModel.setLockBackground(false);
            }
        }

        return filename != null;
    }

    @Override
    public void initAutoSave() {
        schedulePool = new ScheduledThreadPoolExecutor(1);

        autoSavePath = ResourceUtil.getTempDir().resolve("oriedita-autosave-" + df.format(new Date()));
        autoSavePath.toFile().mkdirs();

        updateAutoSave(applicationModel.getAutoSaveInterval());
    }

    private void updateAutoSave(long delay) {
        if (schedulePool == null) {
            // Do not continue if initAutoSave has not been called.
            return;
        }

        if (autoSaveFuture != null) {
            autoSaveFuture.cancel(false);
        }

        if (delay < 0) {
            Logger.info("Disabling autoSave");
            return;
        }

        Logger.info("Setting autoSave interval to " + delay + " minutes");

        autoSaveFuture = schedulePool.scheduleAtFixedRate(this::autoSaveFile, delay, delay, TimeUnit.MINUTES);
    }

    private void autoSaveFile() {
        String savedFileName = fileModel.getSavedFileName();

        String fileName;
        String namePart;
        if (savedFileName == null) {
            namePart = "unsaved";
        } else {

            namePart = new File(savedFileName).getName();
            namePart = namePart.substring(0, namePart.lastIndexOf("."));

        }

        fileName = df.format(new Date()) + "_" + namePart + ".ori";

        Save save = mainCreasePatternWorker.getSave_for_export();

        File file = autoSavePath.resolve(fileName).toFile();

        saveAndName2File(save, file);
    }
}
