package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.BackgroundModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.ApplicationModelPersistenceService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.ResetService;
import oriedita.editor.swing.dialog.ExportDialog;
import oriedita.editor.swing.dialog.FileDialogUtil;
import oriedita.editor.swing.dialog.SaveTypeDialog;
import oriedita.editor.tools.ResourceUtil;
import oriedita.editor.export.api.FileExporter;
import oriedita.editor.export.api.FileImporter;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
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
    private final FileModel fileModel;
    private final ApplicationModel applicationModel;
    private final ResetService resetService;
    private final ButtonService buttonService;
    private final BackgroundModel backgroundModel;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private Path autoSavePath;
    private final Iterable<FileImporter> importers;
    private final Iterable<FileExporter> exporters;
    private final String extension = ".oriconfig";

    private ScheduledThreadPoolExecutor schedulePool;
    private ScheduledFuture<?> autoSaveFuture;

    @Inject
    private ApplicationModelPersistenceService applicationModelPersistenceService;


    @Inject
    public FileSaveServiceImpl(
            FrameProvider frame,
            @Any Instance<FileImporter> importers,
            @Any Instance<FileExporter> exporters,
            @Named("creasePatternCamera") Camera creasePatternCamera,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            FileModel fileModel,
            ApplicationModel applicationModel,
            ResetService resetService,
            BackgroundModel backgroundModel,
            ButtonService buttonService) {
        this.frame = frame;
        this.importers = importers;
        this.exporters = exporters;
        this.creasePatternCamera = creasePatternCamera;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.fileModel = fileModel;
        this.applicationModel = applicationModel;
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
        // Grab file
        String importPathStr = FileDialogUtil.openFileDialog(
                frame.get(),
                "Import...",
                applicationModel.getDefaultDirectory(),
                new String[]{"*" + extension},
                null);
        if(importPathStr == null){ return; }
        Path importPath = Path.of(importPathStr);
        File zipFile = importPath.toFile();

        // Double check
        if(!zipFile.getName().endsWith(extension)){
            JOptionPane.showMessageDialog(frame.get(), String.format("The file must have %s as the extension", extension),"Wrong import file format", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Go through each file
        ZipEntry ze;
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))){
            while ((ze = zis.getNextEntry()) != null){
                if (ze.getName().equals("config.json")){
                    applicationModelPersistenceService.importApplicationModel(zis);
                } else if (ze.getName().endsWith(".properties")){
                    readPropertiesFiles(zis, ze, buttonService);
                }
            }
        } catch (IOException e) {
            Logger.info("zis closed");
            throw new RuntimeException(e);
        }
    }

    private void readPropertiesFiles(ZipInputStream zis, ZipEntry ze, ButtonService buttonService){
        try {
            ResourceBundle userBundle = new PropertyResourceBundle(zis);
            String bundleName = ze.getName().split("\\.")[0];

            switch(bundleName){
                case "hotkey":
                    ResourceUtil.clearBundle(bundleName);
                    buttonService.removeAllKeyBinds();

                    for(String action : userBundle.keySet()){
                        String importKeyStroke = userBundle.getString(action);
                        buttonService.setKeyStroke(KeyStroke.getKeyStroke(importKeyStroke), action);
                        ResourceUtil.updateBundleKey(bundleName, action, importKeyStroke);
                    }

                    break;
                default:
                    break;
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void exportPref(){
        // Select exporting directory + choose file name
        String exportPathStr = FileDialogUtil.saveFileDialog(frame.get(),
                "Export...",
                applicationModel.getDefaultDirectory(),
                new String[]{"*" + extension},
                null);
        if(exportPathStr == null){ return; }

        // Add extension if needed
        if(!exportPathStr.endsWith(extension)){
            exportPathStr += extension;
        }
        Path exportPath = Path.of(exportPathStr);

        // Grab config & hotkey.properties file
        Path configDir = ResourceUtil.getAppDir();
        List<String> fileNames = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(configDir.toUri()).listFiles())){
            fileNames.add(file.getName());
        }

        // Write files
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
            fileNames.forEach(fileName -> {
                Path filePath = configDir.resolve(fileName);
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

        try {
            for (var exporter : exporters) {
                if (exporter.supports(exportFile)) {
                    exporter.doExport(mainCreasePatternWorker.getSave_for_export_with_applicationModel(), exportFile);
                }
            }
        } catch (IOException ex) {
            Logger.error(ex, "Failed to save");

            JOptionPane.showMessageDialog(frame.get(), "Failed to save, please check the logs.", "Failed to save", JOptionPane.ERROR_MESSAGE);
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
        String exportType = ExportDialog.showExportDialog(frame.get(), exporters);

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
        if (file == null || !file.exists()) return null;

        try {
            for (var importer : this.importers) {
                if (importer.supports(file)) {
                    return importer.doImport(file);
                }
            }

            Logger.error("No importers!");
        } catch (IOException e) {
            Logger.error(e, "Opening file failed");

            JOptionPane.showMessageDialog(frame.get(), "Opening of the saved file failed", "Opening failed", JOptionPane.ERROR_MESSAGE);
            fileModel.setSavedFileName(null);

            return SaveProvider.createInstance();
        }

        return null;
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
        try {
            for (var exporter : exporters) {
                if (exporter.supports(fname)) {
                    exporter.doExport(save, fname);
                    return;
                }
            }

            JOptionPane.showMessageDialog(frame.get(), "Unknown file type, cannot save", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            Logger.error(e, "Failed to save");

            JOptionPane.showMessageDialog(frame.get(), "Failed to save, please check the logs.", "Failed to save", JOptionPane.ERROR_MESSAGE);
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

    @Override
    public void openFileInFE() {
        File currentFile = new File(fileModel.getSavedFileName());
        openFileInFE(currentFile);
    }

    @Override
    public void openFileInFE(File file) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (!Desktop.isDesktopSupported()) {
                throw new UnsupportedOperationException("Desktop is not supported");
            }

            String filePath = file.getAbsolutePath();

            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"explorer.exe", "/select,", filePath});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", "-R", filePath});
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("linux")) {

                Process mimeProcess = Runtime.getRuntime().exec(new String[]{"xdg-mime", "query", "default", "inode/directory"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(mimeProcess.getInputStream()));
                String line = reader.readLine().toLowerCase();

                if (line.contains("nautilus")) {
                    Runtime.getRuntime().exec(new String[]{"nautilus", "--select", filePath});
                } else if (line.contains("dolphin")) {
                    Runtime.getRuntime().exec(new String[]{"dolphin", "--select", filePath});
                } else if (line.contains("thunar")) {
                    Runtime.getRuntime().exec(new String[]{"thunar", filePath});
                } else if (line.contains("nemo")) {
                    Runtime.getRuntime().exec(new String[]{"nemo", "--no-desktop", "--browser", filePath});
                } else {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", file.getParent()});
                }

            } else {
                throw new UnsupportedOperationException("Platform not supported");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
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
