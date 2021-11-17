package origami_editor.editor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lwjgl.PointerBuffer;
import origami_editor.editor.Canvas;
import origami_editor.editor.LineStyle;
import origami_editor.editor.MouseMode;
import origami_editor.editor.canvas.CreasePattern_Worker;
import origami_editor.editor.databinding.*;
import origami_editor.editor.drawing.tools.Camera;
import origami_editor.editor.exception.FileReadingException;
import origami_editor.editor.export.Cp;
import origami_editor.editor.export.Obj;
import origami_editor.editor.export.Orh;
import origami_editor.editor.export.Svg;
import origami_editor.editor.json.DefaultObjectMapper;
import origami_editor.editor.save.Save;
import origami_editor.editor.save.SaveV1;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

@Singleton
public class FileSaveService {
    private final JFrame frame;
    private final Camera creasePatternCamera;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final Canvas canvas;
    private final FileModel fileModel;
    private final ApplicationModel applicationModel;
    private final CanvasModel canvasModel;
    private final FoldedFiguresList foldedFiguresList;
    private final ResetService resetService;
    private final BackgroundModel backgroundModel;

    @Inject
    public FileSaveService(
            @Named("mainFrame") JFrame frame,
            @Named("creasePatternCamera") Camera creasePatternCamera,
            CreasePattern_Worker mainCreasePatternWorker,
            Canvas canvas,
            FileModel fileModel,
            ApplicationModel applicationModel,
            CanvasModel canvasModel,
            FoldedFiguresList foldedFiguresList,
            ResetService resetService,
            BackgroundModel backgroundModel) {
        this.frame = frame;
        this.creasePatternCamera = creasePatternCamera;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.canvas = canvas;
        this.fileModel = fileModel;
        this.applicationModel = applicationModel;
        this.canvasModel = canvasModel;
        this.foldedFiguresList = foldedFiguresList;
        this.resetService = resetService;
        this.backgroundModel = backgroundModel;
    }

    public void openFile(File file) throws FileReadingException {
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
            resetService.developmentView_initialization();
            //Deployment parameter initialization

            mainCreasePatternWorker.setCamera(creasePatternCamera);//20170702この１行を入れると、解凍したjarファイルで実行し、最初にデータ読み込んだ直後はホイールでの展開図拡大縮小ができなくなる。jarのままで実行させた場合はもんだいないようだ。原因不明。
            mainCreasePatternWorker.setSave_for_reading(memo_temp);
            mainCreasePatternWorker.record();
        }
    }

    public void openFile() {
        System.out.println("readFile2Memo() 開始");

        if (saveUnsavedFile()) return;

        File file = selectOpenFile();

        try {
            openFile(file);
        } catch (FileReadingException e) {
            e.printStackTrace();
        }
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
        Save memo_temp = null;
        try {
            memo_temp = readImportFile(importFile);
        } catch (FileReadingException e) {
            e.printStackTrace();
        }
        System.out.println("readFile2Memo() 終了");

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

            Svg.exportFile(mainCreasePatternWorker.foldLineSet, creasePatternCamera, displayCpLines, lineWidth, intLineWidth, lineStyle, pointSize, foldedFiguresList, exportFile);
        } else if (exportFile.getName().endsWith(".png") || exportFile.getName().endsWith(".jpg") || exportFile.getName().endsWith(".jpeg")) {
            writeImageFile(exportFile);
        } else if (exportFile.getName().endsWith(".cp")) {
            Cp.exportFile(mainCreasePatternWorker.getSave_for_export(), exportFile);
        } else if (exportFile.getName().endsWith(".orh")) {
            Orh.exportFile(mainCreasePatternWorker.getSave_for_export_with_applicationModel(), exportFile);
        }
    }

    public void writeImageFile(File file) {//i=1　png, 2=jpg
        if (file != null) {
            String fname = file.getName();

            String formatName;

            if (fname.endsWith("png")) {
                formatName = "png";
            } else if (fname.endsWith("jpg")) {
                formatName = "jpg";
            } else {
                file = new File(fname + ".png");
                formatName = "png";
            }

            //	ファイル保存

            try {
                BufferedImage myImage = canvas.getGraphicsConfiguration().createCompatibleImage(canvas.getSize().width, canvas.getSize().height);
                Graphics g = myImage.getGraphics();

                canvas.hideOperationFrame = true;
                canvas.paintComponent(g);
                canvas.hideOperationFrame = false;

                if (canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61 && mainCreasePatternWorker.getDrawingStage() == 4) { //枠設定時の枠内のみ書き出し 20180524
                    int xMin = (int) mainCreasePatternWorker.operationFrameBox.getXMin();
                    int xMax = (int) mainCreasePatternWorker.operationFrameBox.getXMax();
                    int yMin = (int) mainCreasePatternWorker.operationFrameBox.getYMin();
                    int yMax = (int) mainCreasePatternWorker.operationFrameBox.getYMax();

                    ImageIO.write(myImage.getSubimage(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1), formatName, file);

                } else {//Full export without frame
                    System.out.println("2018-529_");

                    ImageIO.write(myImage, formatName, file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("終わりました");
        }
    }

    public File selectOpenFile() {
        PointerBuffer outPath = memAllocPointer(1);

        File selectedFile = null;
        try {
            int result = NFD_OpenDialog("ori,cp;ori;cp", applicationModel.getDefaultDirectory(), outPath);

            if (result == NFD_OKAY) {
                selectedFile = new File(outPath.getStringUTF8(0));
                nNFD_Free(outPath.get(0));
            }
        } finally {
            memFree(outPath);
        }

        if (selectedFile != null && selectedFile.exists()) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());
            fileModel.setSavedFileName(selectedFile.getAbsolutePath());
            fileModel.setSaved(true);

            applicationModel.addRecentFile(selectedFile);

            return selectedFile;
        }

        return null;
    }

    File selectSaveFile() {
        PointerBuffer outPath = memAllocPointer(1);

        File selectedFile;
        try {
            int result = NFD_SaveDialog("ori;cp", applicationModel.getDefaultDirectory(), outPath);

            if (result != NFD_OKAY) {
                return null;
            }

            selectedFile = new File(outPath.getStringUTF8(0));

            nNFD_Free(outPath.get(0));
        } finally {
            memFree(outPath);
        }

        applicationModel.setDefaultDirectory(selectedFile.getParent());
        fileModel.setSavedFileName(selectedFile.getAbsolutePath());
        fileModel.setSaved(true);
        applicationModel.addRecentFile(selectedFile);

        return selectedFile;
    }

    public File selectImportFile() {
        PointerBuffer outPath = memAllocPointer(1);

        File selectedFile = null;
        try {
            frame.setEnabled(false);
            int result = NFD_OpenDialog("cp,orh,ori;cp;orh;ori", applicationModel.getDefaultDirectory(), outPath);
            frame.setEnabled(true);

            if (result == NFD_OKAY) {
                selectedFile = new File(outPath.getStringUTF8(0));
                nNFD_Free(outPath.get(0));
            }
        } finally {
            memFree(outPath);
        }

        if (selectedFile != null && selectedFile.exists()) {
            applicationModel.setDefaultDirectory(selectedFile.getParent());

            return selectedFile;
        }

        return null;
    }

    public File selectExportFile() {
        PointerBuffer outPath = memAllocPointer(1);

        File selectedFile;
        try {
            int result = NFD_SaveDialog("png;jpg;svg;cp;orh", applicationModel.getDefaultDirectory(), outPath);

            if (result != NFD_OKAY) {
                return null;
            }

            selectedFile = new File(outPath.getStringUTF8(0));
            nNFD_Free(outPath.get(0));
        } finally {
            memFree(outPath);
        }

        applicationModel.setDefaultDirectory(selectedFile.getParent());

        return selectedFile;
    }

    public Save readImportFile(File file) throws FileReadingException {
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
                    throw new FileReadingException(e);
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

            return new SaveV1();
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

        saveAndName2File(save, file);

        fileModel.setSaved(true);
    }

    public void saveAsFile() {
        File file = selectSaveFile();

        if (file == null) {
            return;
        }

        Save save = mainCreasePatternWorker.getSave_for_export();

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
