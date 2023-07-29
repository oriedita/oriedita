package oriedita.editor.swing;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.datatransfer.SaveTransferable;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.AnimationService;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.FileSaveService;
import oriedita.editor.service.LookAndFeelService;
import oriedita.editor.service.ResetService;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.swing.dialog.PreferenceDialog;
import oriedita.editor.tools.LookAndFeelUtil;
import oriedita.editor.tools.ResourceUtil;
import origami.crease_pattern.element.Point;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@ApplicationScoped
public class AppMenuBar {
    private final FrameProvider frameProvider;
    private final TaskExecutorService foldingExecutor;
    private final ApplicationModel applicationModel;
    private final LookAndFeelService lookAndFeelService;
    private final FileSaveService fileSaveService;
    private final ButtonService buttonService;
    private final CanvasModel canvasModel;
    private final FileModel fileModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final FoldedFigureModel foldedFigureModel;
    private final ResetService resetService;
    private final FoldedFiguresList foldedFiguresList;
    private JCheckBoxMenuItem showPointRangeCheckBox;//点を探す範囲
    private JCheckBoxMenuItem pointOffsetCheckBox;//点を離すかどうか
    private JCheckBoxMenuItem gridInputAssistCheckBox;//高密度用入力をするかどうか
    private JCheckBoxMenuItem showCommentsCheckbox;//文章
    private JCheckBoxMenuItem showCpLinesCheckBox;//折線
    private JCheckBoxMenuItem showAuxLinesCheckBox;//補助活線cyan
    private JCheckBoxMenuItem showLiveAuxLinesCheckBox;//補助画線
    private JCheckBoxMenuItem showStandardFaceMarksCheckBox;//Marking lines such as crosses and reference planes
    private JCheckBoxMenuItem cpOnTopCheckBox;//展開図を折り上がり予想図の上に描く
    private JCheckBoxMenuItem darkModeCheckBox;
    private JCheckBoxMenuItem preciseZoomCheckBox;
    private JCheckBoxMenuItem showSelfIntersectionCheckBox;
    private JCheckBoxMenuItem useAdvancedCheck4Display;
    private JCheckBoxMenuItem displayTopPanel;
    private JCheckBoxMenuItem displayBottomPanel;
    private JCheckBoxMenuItem displayLeftPanel;
    private JCheckBoxMenuItem displayRightPanel;
    private JCheckBoxMenuItem moveFoldedModelWithCp;
    private JCheckBoxMenuItem doAnimations;
    private JMenuItem scaleCP;
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem saveAsButton;
    private JMenuItem exportButton;
    private JMenuItem prefButton;
    private JMenuItem exitButton;
    private JMenuItem toggleHelpMenuItem;
    private JMenuItem showConfigFolderMenuItem;
    private JMenuItem showAutosaveFolderMenuItem;
    private JMenu openRecentMenu;
    private JMenuItem clearRecentFileMenuItem;
    private JMenuItem copyButton;
    private JMenuItem cutButton;
    private JMenuItem pasteButton;
    private JMenuItem pasteOffsetButton;
    private JMenuItem selectAllButton;
    private JMenuItem unselectAllButton;
    private JMenuItem invertMVButton;
    private AppMenuBarUI appMenuBarUI;
    private PreferenceDialog preferenceDialog;
    private final AnimationService animationService;
    private final CameraModel creasePatternCameraModel;
    private final Camera camera;
    @Inject
    public AppMenuBar(
            FrameProvider frameProvider,
            @Named("foldingExecutor") TaskExecutorService foldingExecutor,
            ApplicationModel applicationModel,
            LookAndFeelService lookAndFeelService,
            FileSaveService fileSaveService,
            ButtonService buttonService,
            @Any CanvasModel canvasModel,
            FileModel fileModel,
            @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
            FoldedFigureModel foldedFigureModel,
            ResetService resetService,
            FoldedFiguresList foldedFiguresList,
            AnimationService animationService,
            Camera camera,
            CameraModel creasePatternCameraModel
    ) {
        this.frameProvider = frameProvider;
        this.foldingExecutor = foldingExecutor;
        this.applicationModel = applicationModel;
        this.lookAndFeelService = lookAndFeelService;
        this.fileSaveService = fileSaveService;
        this.buttonService = buttonService;
        this.canvasModel = canvasModel;
        this.fileModel = fileModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.foldedFigureModel = foldedFigureModel;
        this.resetService = resetService;
        this.foldedFiguresList = foldedFiguresList;
        this.animationService = animationService;
        this.camera = camera;
        this.creasePatternCameraModel = creasePatternCameraModel;
    }

    public void resetCPView(){
        animationService.animatePoint(Animations.INITIAL_POS_DISPLAY,
                camera::setDisplayPosition,
                camera::getDisplayPosition,
                new Point(350, 350),
                AnimationDurations.SCALE_SPEED);
        animationService.animatePoint(Animations.INITIAL_POS_CAMERA,
                camera::setCameraPosition,
                camera::getCameraPosition,
                new Point(0, 0),
                AnimationDurations.SCALE_SPEED);

        animationService.animate(Animations.ZOOM_CP,
                creasePatternCameraModel::setScale,
                creasePatternCameraModel::getScale,
                1.0,
                AnimationDurations.SCALE_SPEED);
        canvasModel.markDirty();
    }

    public void init() {

        applicationModel.addPropertyChangeListener(e -> setData(applicationModel));

        //--------------------------------------------------------------------------------------------------
        frameProvider.get().addWindowListener(new WindowAdapter() {//ウィンドウの状態が変化したときの処理
            //終了ボタンを有効化
            public void windowClosing(WindowEvent evt) {
                closing();//Work to be done when pressing X at the right end of the upper side of the window
            }//終了ボタンを有効化 ここまで。
        });//Processing when the window state changes Up to here.

        createElements();

        buttonService.addDefaultListener(appMenuBarUI);

        buttonService.registerButton(newButton, "newAction");
        buttonService.registerButton(openButton, "openAction");
        buttonService.registerButton(openRecentMenu, "openRecentAction");
        buttonService.registerButton(saveButton, "saveAction");
        buttonService.registerButton(saveAsButton, "saveAsAction");
        buttonService.registerButton(exportButton, "exportAction");
        buttonService.registerButton(prefButton, "prefAction");
        buttonService.registerButton(exitButton, "exitAction");
        buttonService.registerButton(showPointRangeCheckBox, "showPointRangeAction");
        buttonService.registerButton(pointOffsetCheckBox, "pointOffsetAction");
        buttonService.registerButton(gridInputAssistCheckBox, "gridInputAssistAction");
        buttonService.registerButton(showCommentsCheckbox, "displayCommentsAction");
        buttonService.registerButton(showCpLinesCheckBox, "displayCpLinesAction");
        buttonService.registerButton(showAuxLinesCheckBox, "displayAuxLinesAction");
        buttonService.registerButton(showLiveAuxLinesCheckBox, "displayLiveAuxLinesAction");
        buttonService.registerButton(showStandardFaceMarksCheckBox, "displayStandardFaceMarksAction");
        buttonService.registerButton(displayTopPanel, "displayTopPanel");
        buttonService.registerButton(displayBottomPanel, "displayBottomPanel");
        buttonService.registerButton(displayLeftPanel, "displayLeftPanel");
        buttonService.registerButton(displayRightPanel, "displayRightPanel");
        buttonService.registerButton(cpOnTopCheckBox, "cpOnTopAction");
        buttonService.registerButton(toggleHelpMenuItem, "toggleHelpAction");
        buttonService.registerButton(showConfigFolderMenuItem, "showConfigFolderAction");
        buttonService.registerButton(showAutosaveFolderMenuItem, "showAutosaveFolderAction");
        buttonService.registerButton(darkModeCheckBox, "toggleDarkModeAction");
        buttonService.registerButton(preciseZoomCheckBox, "preciseZoomAction");
        buttonService.registerButton(showSelfIntersectionCheckBox, "displaySelfIntersectionAction");
        buttonService.registerButton(useAdvancedCheck4Display, "useAdvancedCheck4DisplayAction");
        buttonService.registerButton(moveFoldedModelWithCp, "moveFoldedModelWithCp");
        buttonService.registerButton(doAnimations, "doAnimations");

        buttonService.registerButton(copyButton, "copyClipboardAction");
        buttonService.registerButton(cutButton, "cutClipboardAction");
        buttonService.registerButton(pasteButton, "pasteClipboardAction");
        buttonService.registerButton(pasteOffsetButton, "pasteOffsetClipboardAction");
        buttonService.registerButton(selectAllButton, "selectAllAction");
        buttonService.registerButton(unselectAllButton, "unselectAllAction");
        buttonService.registerButton(invertMVButton, "zen_yama_tani_henkanAction");

        newButton.addActionListener(e -> {
            if (!fileModel.isSaved()) {
                int choice = JOptionPane.showConfirmDialog(frameProvider.get(), "<html>Current file not saved.<br/>Do you want to save it?", "File not saved", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    fileSaveService.saveFile();
                } else if (choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            fileModel.reset();
            //展開図の初期化　開始
            //settei_syokika_cp();//展開図パラメータの初期化
            resetService.developmentView_initialization();
            //展開図の初期化　終了
            //
            //折畳予測図のの初期化　開始
            foldedFiguresList.removeAllElements();
            //折畳予測図のの初期化　終了

            canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

            mainCreasePatternWorker.record();
            mainCreasePatternWorker.auxRecord();
        });
        openButton.addActionListener(e -> fileSaveService.openFile());
        clearRecentFileMenuItem.addActionListener(e -> applicationModel.setRecentFileList(new ArrayList<>()));

        saveButton.addActionListener(e -> fileSaveService.saveFile());
        saveAsButton.addActionListener(e -> fileSaveService.saveAsFile());
        exportButton.addActionListener(e -> {
            if (canvasModel.getMouseMode() != MouseMode.OPERATION_FRAME_CREATE_61) {
                mainCreasePatternWorker.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524

            fileSaveService.exportFile();
        });
        prefButton.addActionListener(e -> {
            if(preferenceDialog == null){
                preferenceDialog = new PreferenceDialog(applicationModel, lookAndFeelService, frameProvider, foldedFigureModel, "Preferences", frameProvider.get(), buttonService);
            }
            preferenceDialog.setSize(preferenceDialog.getRootPane().getPreferredSize());
            preferenceDialog.setMinimumSize(preferenceDialog.getRootPane().getMinimumSize());
            preferenceDialog.setResizable(false);
            preferenceDialog.setData(applicationModel);
            preferenceDialog.setLocationRelativeTo(prefButton);
            preferenceDialog.setAlwaysOnTop(false);
            preferenceDialog.updateTempModel(applicationModel);
            preferenceDialog.setVisible(true);
        });
        exitButton.addActionListener(e -> closing());
        showPointRangeCheckBox.addActionListener(e -> getData(applicationModel));
        pointOffsetCheckBox.addActionListener(e -> getData(applicationModel));
        gridInputAssistCheckBox.addActionListener(e -> {
            if (gridInputAssistCheckBox.isSelected()) {
                Logger.info(" kou_mitudo_nyuuryoku on");
            } else {
                Logger.info(" kou_mitudo_nyuuryoku off");
            }
            getData(applicationModel);
        });
        showCommentsCheckbox.addActionListener(e -> getData(applicationModel));
        showCpLinesCheckBox.addActionListener(e -> getData(applicationModel));
        showAuxLinesCheckBox.addActionListener(e -> getData(applicationModel));
        showLiveAuxLinesCheckBox.addActionListener(e -> getData(applicationModel));
        showStandardFaceMarksCheckBox.addActionListener(e -> getData(applicationModel));
        cpOnTopCheckBox.addActionListener(e -> getData(applicationModel));
        toggleHelpMenuItem.addActionListener(e -> applicationModel.toggleHelpVisible());
        moveFoldedModelWithCp.addActionListener(e -> getData(applicationModel));
        showAutosaveFolderMenuItem.addActionListener(e -> {
            try {
                File f = ResourceUtil.getTempDir().toFile();
                if (!f.exists()) {
                    f.mkdirs();
                }
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        showConfigFolderMenuItem.addActionListener(e -> {
            try {
                File f = ResourceUtil.getAppDir().toFile();
                if (!f.exists()) {
                    f.mkdirs();
                }
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        darkModeCheckBox.addActionListener(e -> {
            lookAndFeelService.toggleDarkMode();

            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frameProvider.get(), "Restore custom colors in grid and folded figure for this color scheme?", "Restore colors", JOptionPane.YES_NO_OPTION)) {
                if (FlatLaf.isLafDark()) {
                    applicationModel.setGridColor(Colors.GRID_LINE_DARK);
                    applicationModel.setGridScaleColor(Colors.GRID_SCALE_DARK);

                    foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT_DARK);
                    foldedFigureModel.setBackColor(Colors.FIGURE_BACK_DARK);
                } else {
                    applicationModel.setGridColor(Colors.GRID_LINE);
                    applicationModel.setGridScaleColor(Colors.GRID_SCALE);

                    foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT);
                    foldedFigureModel.setBackColor(Colors.FIGURE_BACK);
                }
            }
        });
        preciseZoomCheckBox.addActionListener(e -> applicationModel.togglePreciseZoom());
        showSelfIntersectionCheckBox.addActionListener(e -> applicationModel.toggleDisplaySelfIntersection());
        useAdvancedCheck4Display.addActionListener(e -> applicationModel.toggleUseAdvancedCheck4Display());
        displayTopPanel.addActionListener(e -> getData(applicationModel));
        displayBottomPanel.addActionListener(e -> getData(applicationModel));
        displayRightPanel.addActionListener(e -> getData(applicationModel));
        displayLeftPanel.addActionListener(e -> getData(applicationModel));
        doAnimations.addActionListener(e -> getData(applicationModel));
        scaleCP.addActionListener(e -> {
            resetCPView();
            getData(applicationModel);
        });

        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            Save save = SaveProvider.createInstance();
            mainCreasePatternWorker.getFoldLineSet().getSaveForSelectFolding(save);

            clipboard.setContents(new SaveTransferable(save), (clipboard1, contents) -> {
            });
        });
        cutButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            Save save = SaveProvider.createInstance();
            mainCreasePatternWorker.getFoldLineSet().getSaveForSelectFolding(save);


            clipboard.setContents(new SaveTransferable(save), (clipboard1, contents) -> {
            });

            mainCreasePatternWorker.getFoldLineSet().delSelectedLineSegmentFast();

            mainCreasePatternWorker.record();
        });
        pasteButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipboardContents = clipboard.getContents(null);

            try {
                if (clipboardContents.isDataFlavorSupported(SaveTransferable.saveFlavor)) {
                    Save save = (Save) clipboardContents.getTransferData(SaveTransferable.saveFlavor);

                    mainCreasePatternWorker.setSaveForPaste(save);
                }
            } catch (IOException | UnsupportedFlavorException ignored) {
                // We don't know how to paste this
            }
        });
        pasteOffsetButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipboardContents = clipboard.getContents(null);

            try {
                if (clipboardContents.isDataFlavorSupported(SaveTransferable.saveFlavor)) {
                    Save save = (Save) clipboardContents.getTransferData(SaveTransferable.saveFlavor);

                    mainCreasePatternWorker.setSave_for_reading_tuika(save);
                }
            } catch (IOException | UnsupportedFlavorException ignored) {
                // We don't know how to paste this
            }
        });
        selectAllButton.addActionListener(e -> mainCreasePatternWorker.select_all());
        unselectAllButton.addActionListener(e -> mainCreasePatternWorker.unselect_all());
        invertMVButton.addActionListener(e -> {
            mainCreasePatternWorker.allMountainValleyChange();
            mainCreasePatternWorker.unselect_all(false);
        });
    }

    public AppMenuBarUI getAppMenuBarUI() {
        return appMenuBarUI;
    }

    private void createElements() {
        appMenuBarUI = new AppMenuBarUI();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        appMenuBarUI.add(fileMenu);

        newButton = new JMenuItem("New");
        fileMenu.add(newButton);

        openButton = new JMenuItem("Open...");
        fileMenu.add(openButton);

        openRecentMenu = new JMenu("Open Recent");
        fileMenu.add(openRecentMenu);

        clearRecentFileMenuItem = new JMenuItem("Clear");
        openRecentMenu.add(clearRecentFileMenuItem);

        saveButton = new JMenuItem("Save");
        fileMenu.add(saveButton);

        saveAsButton = new JMenuItem("Save as...");
        fileMenu.add(saveAsButton);

        fileMenu.addSeparator();

        exportButton = new JMenuItem("Export");
        fileMenu.add(exportButton);

        JMenuItem importButton = new JMenuItem("Import");
        importButton.setActionCommand("importAction");
        fileMenu.add(importButton);

        JMenuItem importAddButton = new JMenuItem("Import (Add)");
        importAddButton.setActionCommand("inputDataAction");
        fileMenu.add(importAddButton);

        fileMenu.addSeparator();

        prefButton = new JMenuItem("Preferences");
        fileMenu.add(prefButton);

        exitButton = new JMenuItem("Exit");
        fileMenu.add(exitButton);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        appMenuBarUI.add(editMenu);

        copyButton = new JMenuItem("Copy");
        editMenu.add(copyButton);

        cutButton = new JMenuItem("Cut");
        editMenu.add(cutButton);

        JMenu pasteMenu = new JMenu("Paste");
        editMenu.add(pasteMenu);

        pasteButton = new JMenuItem("Paste");
        pasteMenu.add(pasteButton);

        pasteOffsetButton = new JMenuItem("Paste (offset)");
        pasteMenu.add(pasteOffsetButton);

        selectAllButton = new JMenuItem("Select All");
        editMenu.add(selectAllButton);

        unselectAllButton = new JMenuItem("Unselect all");
        editMenu.add(unselectAllButton);

        invertMVButton = new JMenuItem("Invert MV");
        editMenu.add(invertMVButton);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        appMenuBarUI.add(viewMenu);

        darkModeCheckBox = new JCheckBoxMenuItem("Dark Mode");
        viewMenu.add(darkModeCheckBox);
        preciseZoomCheckBox = new JCheckBoxMenuItem("Smooth zoom");
        viewMenu.add(preciseZoomCheckBox);
        pointOffsetCheckBox = new JCheckBoxMenuItem("Offset cursor");
        viewMenu.add(pointOffsetCheckBox);
        gridInputAssistCheckBox = new JCheckBoxMenuItem("Grid input assist");
        viewMenu.add(gridInputAssistCheckBox);

        JMenu showMenuGroup = new JMenu("Show");
        viewMenu.add(showMenuGroup);
        showPointRangeCheckBox = new JCheckBoxMenuItem("Mouse range");
        showMenuGroup.add(showPointRangeCheckBox);
        showCommentsCheckbox = new JCheckBoxMenuItem("Comments");
        showMenuGroup.add(showCommentsCheckbox);
        showCpLinesCheckBox = new JCheckBoxMenuItem("Cp lines");
        showMenuGroup.add(showCpLinesCheckBox);
        showAuxLinesCheckBox = new JCheckBoxMenuItem("Aux lines");
        showMenuGroup.add(showAuxLinesCheckBox);
        showLiveAuxLinesCheckBox = new JCheckBoxMenuItem("Live aux lines");
        showMenuGroup.add(showLiveAuxLinesCheckBox);
        showStandardFaceMarksCheckBox = new JCheckBoxMenuItem("Standard face marks");
        showMenuGroup.add(showStandardFaceMarksCheckBox);
        showSelfIntersectionCheckBox = new JCheckBoxMenuItem("Self intersection");
        showMenuGroup.add(showSelfIntersectionCheckBox);

        cpOnTopCheckBox = new JCheckBoxMenuItem("Crease pattern on top");
        viewMenu.add(cpOnTopCheckBox);
        useAdvancedCheck4Display = new JCheckBoxMenuItem("Use Advanced cAMV display");
        viewMenu.add(useAdvancedCheck4Display);

        JMenu displayPanelMenu = new JMenu("Panels");
        viewMenu.add(displayPanelMenu);
        displayTopPanel = new JCheckBoxMenuItem("Top Panel");
        displayPanelMenu.add(displayTopPanel);
        displayBottomPanel = new JCheckBoxMenuItem("Bottom Panel");
        displayPanelMenu.add(displayBottomPanel);
        displayLeftPanel = new JCheckBoxMenuItem("Left Panel");
        displayPanelMenu.add(displayLeftPanel);
        displayRightPanel = new JCheckBoxMenuItem("Right Panel");
        displayPanelMenu.add(displayRightPanel);
        moveFoldedModelWithCp = new JCheckBoxMenuItem("Move Folded Model with CP");
        viewMenu.add(moveFoldedModelWithCp);
        doAnimations = new JCheckBoxMenuItem("Animations");
        viewMenu.add(doAnimations);
        scaleCP = new JMenuItem("Scale back CP");
        viewMenu.add(scaleCP);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        appMenuBarUI.add(helpMenu);

        toggleHelpMenuItem = new JMenuItem("Toggle help");
        helpMenu.add(toggleHelpMenuItem);
        showConfigFolderMenuItem = new JMenuItem("Open Config Folder");
        helpMenu.add(showConfigFolderMenuItem);
        showAutosaveFolderMenuItem = new JMenuItem("Open Autosave Folder");
        helpMenu.add(showAutosaveFolderMenuItem);
    }

    public void getData(ApplicationModel applicationModel) {
        applicationModel.setDisplayPointSpotlight(showPointRangeCheckBox.isSelected());
        applicationModel.setDisplayPointOffset(pointOffsetCheckBox.isSelected());
        applicationModel.setDisplayGridInputAssist(gridInputAssistCheckBox.isSelected());
        applicationModel.setDisplayComments(showCommentsCheckbox.isSelected());
        applicationModel.setDisplayCpLines(showCpLinesCheckBox.isSelected());
        applicationModel.setDisplayAuxLines(showAuxLinesCheckBox.isSelected());
        applicationModel.setDisplayLiveAuxLines(showLiveAuxLinesCheckBox.isSelected());
        applicationModel.setDisplayMarkings(showStandardFaceMarksCheckBox.isSelected());
        applicationModel.setDisplayCreasePatternOnTop(cpOnTopCheckBox.isSelected());
        applicationModel.setLaf(LookAndFeelUtil.determineLafForDarkMode(darkModeCheckBox.isSelected()));
        applicationModel.setPreciseZoom(preciseZoomCheckBox.isSelected());
        applicationModel.setDisplaySelfIntersection(showSelfIntersectionCheckBox.isSelected());
        applicationModel.setAdvancedCheck4Display(useAdvancedCheck4Display.isSelected());
        applicationModel.setDisplayTopPanel(displayTopPanel.isSelected());
        applicationModel.setDisplayBottomPanel(displayBottomPanel.isSelected());
        applicationModel.setDisplayLeftPanel(displayLeftPanel.isSelected());
        applicationModel.setDisplayRightPanel(displayRightPanel.isSelected());
        applicationModel.setMoveFoldedModelWithCp(moveFoldedModelWithCp.isSelected());
        applicationModel.setAnimations(doAnimations.isSelected());
    }

    public void setData(ApplicationModel applicationModel) {
        showPointRangeCheckBox.setSelected(applicationModel.getDisplayPointSpotlight());
        pointOffsetCheckBox.setSelected(applicationModel.getDisplayPointOffset());
        gridInputAssistCheckBox.setSelected(applicationModel.getDisplayGridInputAssist());
        showCommentsCheckbox.setSelected(applicationModel.getDisplayComments());
        showCpLinesCheckBox.setSelected(applicationModel.getDisplayCpLines());
        showAuxLinesCheckBox.setSelected(applicationModel.getDisplayAuxLines());
        showLiveAuxLinesCheckBox.setSelected(applicationModel.getDisplayLiveAuxLines());
        showStandardFaceMarksCheckBox.setSelected(applicationModel.getDisplayMarkings());
        cpOnTopCheckBox.setSelected(applicationModel.getDisplayCreasePatternOnTop());
        darkModeCheckBox.setSelected(applicationModel.getLaf().equals(FlatDarkLaf.class.getName()));
        preciseZoomCheckBox.setSelected(applicationModel.isPreciseZoom());
        showSelfIntersectionCheckBox.setSelected(applicationModel.getDisplaySelfIntersection());
        useAdvancedCheck4Display.setSelected(applicationModel.getAdvancedCheck4Display());
        displayTopPanel.setSelected(applicationModel.getDisplayTopPanel());
        displayBottomPanel.setSelected(applicationModel.getDisplayBottomPanel());
        displayLeftPanel.setSelected(applicationModel.getDisplayLeftPanel());
        displayRightPanel.setSelected(applicationModel.getDisplayRightPanel());
        moveFoldedModelWithCp.setSelected(applicationModel.getMoveFoldedModelWithCp());
        doAnimations.setSelected(applicationModel.getAnimations());

        openRecentMenu.removeAll();
        if (applicationModel.getRecentFileList().isEmpty()) {
            JMenuItem noItemsMenuItem = new JMenuItem("No items");
            noItemsMenuItem.setEnabled(false);
            openRecentMenu.add(noItemsMenuItem);
        }
        for (File recentFile : applicationModel.getRecentFileList()) {
            JMenuItem recentFileMenuItem = new JMenuItem(recentFile.getName());
            recentFileMenuItem.addActionListener(e -> {
                try {
                    fileSaveService.openFile(recentFile);
                    // Move this file to the top of the recent file list.
                    applicationModel.addRecentFile(recentFile);
                } catch (FileReadingException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frameProvider.get(), "An error occurred when reading this file", "Read Error", JOptionPane.ERROR_MESSAGE);
                    applicationModel.removeRecentFile(recentFile);
                }
            });
            openRecentMenu.add(recentFileMenuItem);
        }
        openRecentMenu.addSeparator();
        openRecentMenu.add(clearRecentFileMenuItem);
    }

    public void closing() {
        if (!fileModel.isSaved()) {
            int option = JOptionPane.showConfirmDialog(frameProvider.get(), "Save crease pattern before exiting?", "Save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            switch (option) {
                case JOptionPane.YES_OPTION:
                    fileSaveService.saveFile();

                    foldingExecutor.stopTask();
                    System.exit(0);
                case JOptionPane.NO_OPTION:
                    foldingExecutor.stopTask();
                    System.exit(0);
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        } else {
            foldingExecutor.stopTask();
            System.exit(0);
        }
    }

    private static class AppMenuBarUI extends JMenuBar {

    }
}
