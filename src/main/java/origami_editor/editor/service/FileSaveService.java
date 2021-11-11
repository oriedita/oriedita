package origami_editor.editor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import origami_editor.editor.Canvas;
import origami_editor.editor.MouseMode;
import origami_editor.editor.Save;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.export.Cp;
import origami_editor.editor.export.Obj;
import origami_editor.editor.export.Orh;
import origami_editor.editor.json.DefaultObjectMapper;
import origami_editor.tools.ResourceUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@Component
public class FileSaveService {
    private final JFrame frame;
    private final Canvas canvas;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FileModel fileModel;
    private final ApplicationModel applicationModel;
    private final HistoryStateModel historyStateModel;
    private final CanvasModel canvasModel;
    private final InternalDivisionRatioModel internalDivisionRatioModel;
    private final FoldedFigureModel foldedFigureModel;
    private final GridModel gridModel;
    private final AngleSystemModel angleSystemModel;
    private final CameraModel creasePatternCameraModel;
    private final FoldedFiguresList foldedFiguresList;
    private final BackgroundModel backgroundModel;

    public FileSaveService(
            @Qualifier("mainFrame") JFrame frame,
            Canvas canvas,
            CreasePattern_Worker mainCreasePatternWorker,
            FileModel fileModel,
            ApplicationModel applicationModel,
            HistoryStateModel historyStateModel,
            CanvasModel canvasModel,
            InternalDivisionRatioModel internalDivisionRatioModel,
            FoldedFigureModel foldedFigureModel,
            GridModel gridModel,
            AngleSystemModel angleSystemModel,
            CameraModel creasePatternCameraModel,
            FoldedFiguresList foldedFiguresList,
            BackgroundModel backgroundModel) {
        this.frame = frame;
        this.canvas = canvas;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.fileModel = fileModel;
        this.applicationModel = applicationModel;
        this.historyStateModel = historyStateModel;
        this.canvasModel = canvasModel;
        this.internalDivisionRatioModel = internalDivisionRatioModel;
        this.foldedFigureModel = foldedFigureModel;
        this.gridModel = gridModel;
        this.angleSystemModel = angleSystemModel;
        this.creasePatternCameraModel = creasePatternCameraModel;
        this.foldedFiguresList = foldedFiguresList;
        this.backgroundModel = backgroundModel;
    }

    public void developmentView_initialization() {
        mainCreasePatternWorker.reset();
        mainCreasePatternWorker.initialize();

        //camera_of_orisen_nyuuryokuzu	の設定;
        canvas.creasePatternCamera.setCameraPositionX(0.0);
        canvas.creasePatternCamera.setCameraPositionY(0.0);
        canvas.creasePatternCamera.setCameraAngle(0.0);
        canvas.creasePatternCamera.setCameraMirror(1.0);
        canvas.creasePatternCamera.setCameraZoomX(1.0);
        canvas.creasePatternCamera.setCameraZoomY(1.0);
        canvas.creasePatternCamera.setDisplayPositionX(350.0);
        canvas.creasePatternCamera.setDisplayPositionY(350.0);

        mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);

        canvasModel.reset();
        internalDivisionRatioModel.reset();
        foldedFigureModel.reset();

        gridModel.reset();
        angleSystemModel.reset();
        creasePatternCameraModel.reset();
    }

    public void openFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        fileModel.setSaved(true);
        fileModel.setSavedFileName(file.getAbsolutePath());
        applicationModel.setDefaultDirectory(file.getParent());

        Save memo_temp = readImportFile(file);
        System.out.println("readFile2Memo() 終了");

        if (memo_temp != null) {
            //Initialization of development drawing started
            developmentView_initialization();
            //Deployment parameter initialization

            foldedFiguresList.removeAllElements();

            mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainCreasePatternWorker.setSave_for_reading(memo_temp);
            mainCreasePatternWorker.record();
        }
    }

    public void openFile() {
        System.out.println("readFile2Memo() 開始");

        if (saveUnsavedFile()) return;

        File file = selectOpenFile();

        openFile(file);
    }

    private boolean saveUnsavedFile() {
        if (!fileModel.isSaved()) {
            int choice = JOptionPane.showConfirmDialog(frame, "<html>Current file not saved.<br/>Do you want to save it?", "File not saved", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                saveFile();
            } else {
                return choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.CANCEL_OPTION;
            }
        }
        return false;
    }

    public void importFile() {
        if (saveUnsavedFile()) return;

        System.out.println("readFile2Memo() 開始");
        File importFile = selectImportFile();
        Save memo_temp = readImportFile(importFile);
        System.out.println("readFile2Memo() 終了");

        if (memo_temp != null) {
            fileModel.setSavedFileName(null);

            //Initialization of development drawing started
            developmentView_initialization();
            //Deployment parameter initialization

            foldedFiguresList.removeAllElements();

            //Initialization of folding prediction map started

            mainCreasePatternWorker.setCamera(canvas.creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainCreasePatternWorker.setSave_for_reading(memo_temp);
            mainCreasePatternWorker.record();
        }
    }

    public void exportFile() {
        File exportFile = selectExportFile();

        if (exportFile == null) {
            return;
        }

        if (exportFile.getName().endsWith(".png") || exportFile.getName().endsWith(".jpg") || exportFile.getName().endsWith(".jpeg") || exportFile.getName().endsWith(".svg")) {
            canvas.flg61 = false;
            if ((canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61) && (mainCreasePatternWorker.getDrawingStage() == 4)) {
                canvas.flg61 = true;
                mainCreasePatternWorker.setDrawingStage(0);
            }

            fileModel.setExportImageFileName(exportFile.getAbsolutePath());
            canvas.flg_wi = true;
            canvas.repaint();//Necessary to not export the green border
        } else if (exportFile.getName().endsWith(".cp")) {
            Cp.exportFile(mainCreasePatternWorker.getSave_for_export(), exportFile);
        } else if (exportFile.getName().endsWith(".orh")) {
            Orh.exportFile(mainCreasePatternWorker.getSave_for_export_with_applicationModel(), exportFile);
        }
    }

    /**
     * Change the extension of the selected file in a fileChooser when changing the filefilter.
     */
    void applyFileChooserSwitchUpdate(JFileChooser fileChooser) {
        fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, e -> {
            // Can also be AcceptAllFileFilter, then nothing should happen.
            if (e.getNewValue() instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter filter = (FileNameExtensionFilter) e.getNewValue();

                String newExtension = filter.getExtensions()[0];
                String fileName = ((BasicFileChooserUI) fileChooser.getUI()).getFileName();

                String fileBaseName = fileName;
                if (fileName.lastIndexOf(".") > -1) {
                    fileBaseName = fileName.substring(0, fileName.lastIndexOf("."));
                }

                fileChooser.setSelectedFile(new File(fileBaseName + "." + newExtension));
            }
        });
    }

    public File selectOpenFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Open");

        fileChooser.setFileFilter(new FileNameExtensionFilter("All supported files (*.ori, *.cp)", "cp", "ori"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Origami Editor (*.ori)", "ori"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp"));

        fileChooser.showOpenDialog(frame);

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null && selectedFile.exists()) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
            fileModel.setSavedFileName(selectedFile.getAbsolutePath());
            fileModel.setSaved(true);
            historyStateModel.reset();

            applicationModel.addRecentFile(selectedFile);

            return selectedFile;
        }

        return null;
    }

    File selectSaveFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Save As");

        fileChooser.setAcceptAllFileFilterUsed(false);

        FileNameExtensionFilter oriFilter = new FileNameExtensionFilter("Origami Editor (*.ori)", "ori");
        fileChooser.setFileFilter(oriFilter);
        FileNameExtensionFilter cpFilter = new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp");
        fileChooser.addChoosableFileFilter(cpFilter);
        fileChooser.setSelectedFile(new File("untitled.ori"));

        applyFileChooserSwitchUpdate(fileChooser);

        File selectedFile;
        int choice = JOptionPane.NO_OPTION;
        do {
            int saveChoice = fileChooser.showSaveDialog(frame);

            if (saveChoice != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null && !selectedFile.getName().endsWith(".ori") && !selectedFile.getName().endsWith(".cp")) {
                if (fileChooser.getFileFilter() == oriFilter) {
                    selectedFile = new File(selectedFile.getPath() + ".ori");
                } else if (fileChooser.getFileFilter() == cpFilter) {
                    selectedFile = new File(selectedFile.getPath() + ".cp");
                }
            }

            if (selectedFile != null && selectedFile.exists()) {
                choice = JOptionPane.showConfirmDialog(frame, "<html>File already exists.<br/>Do you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        } while (selectedFile != null && selectedFile.exists() && choice != JOptionPane.YES_OPTION);

        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
            fileModel.setSavedFileName(selectedFile.getAbsolutePath());
            fileModel.setSaved(true);
            applicationModel.addRecentFile(selectedFile);
        }

        return selectedFile;
    }

    public File selectImportFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Import");

        fileChooser.setFileFilter(new FileNameExtensionFilter("All supported files", "cp", "orh", "ori"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Orihime (*.orh)", "orh"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Origami Editor (*.ori)", "ori"));

        fileChooser.showOpenDialog(frame);

        File selectedFile = fileChooser.getSelectedFile();
        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
        }

        return selectedFile;
    }

    public File selectExportFile() {
        JFileChooser fileChooser = new JFileChooser(applicationModel.getDefaultDirectory());
        fileChooser.setDialogTitle("Export");

        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.png)", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.jpg)", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image (*.svg)", "svg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CP / ORIPA (*.cp)", "cp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Orihime (*.orh)", "orh"));
        fileChooser.setSelectedFile(new File("creasepattern.png"));

        applyFileChooserSwitchUpdate(fileChooser);

        File selectedFile;
        int choice = JOptionPane.NO_OPTION;
        do {
            int saveChoice = fileChooser.showSaveDialog(frame);

            if (saveChoice != JFileChooser.APPROVE_OPTION) {
                return null;
            }

            selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                choice = JOptionPane.showConfirmDialog(frame, "<html>File already exists.<br/>Do you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        } while (selectedFile != null && selectedFile.exists() && choice == JOptionPane.NO_OPTION);

        if (selectedFile != null) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
        }

        return selectedFile;
    }

    public Save readImportFile(File file) {
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
                    return mapper.readValue(file, Save.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (file.getName().endsWith(".obj")) {
                save = Obj.importFile(file);
            }

            if (file.getName().endsWith(".cp")) {
                save = Cp.importFile(file);
            }

            if (file.getName().endsWith(".orh")) {
                save = Orh.importFile(file);
            }

        } catch (IOException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(frame, "Opening of the saved file failed", "Opening failed", JOptionPane.ERROR_MESSAGE);

            fileModel.setSavedFileName(null);

            return new Save();
        }

        return save;
    }

    public void saveFile() {
        if (fileModel.getSavedFileName() == null) {
            saveAsFile();

            return;
        }

        File file = new File(fileModel.getSavedFileName());

        Save save = mainCreasePatternWorker.getSave_for_export();
        save.setVersion(ResourceUtil.getVersionFromManifest());

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    public void saveAsFile() {
        File file = selectSaveFile();

        if (file == null) {
            return;
        }

        Save save = mainCreasePatternWorker.getSave_for_export();
        save.setVersion(ResourceUtil.getVersionFromManifest());

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    void saveAndName2File(Save save, File fname) {
        if (fname.getName().endsWith(".ori")) {
            try {
                ObjectMapper mapper = new DefaultObjectMapper();

                mapper.writeValue(fname, save);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (fname.getName().endsWith(".cp")) {
            if (!save.canSaveAsCp()) {
                JOptionPane.showMessageDialog(frame, "The saved .cp file does not contain circles and yellow aux lines. Save as a .ori file to also save these lines.", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            Cp.exportFile(save, fname);
        } else {
            JOptionPane.showMessageDialog(frame, "Unknown file type, cannot save", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void readBackgroundImageFromFile() {
        FileDialog fd = new FileDialog(frame, "Select Image File.", FileDialog.LOAD);
        fd.setVisible(true);
        String img_background_fname = fd.getDirectory() + fd.getFile();
        try {
            if (fd.getFile() != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image img_background = tk.getImage(img_background_fname);

                if (img_background != null) {
                    backgroundModel.setBackgroundImage(img_background);
                    backgroundModel.setDisplayBackground(true);
                    backgroundModel.setLockBackground(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
