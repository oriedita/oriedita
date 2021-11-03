package origami_editor.editor;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.databinding.FileModel;
import origami_editor.editor.transfer.SaveTransferable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AppMenuBar extends JMenuBar {
    private final App app;
    private JCheckBoxMenuItem showPointRangeCheckBox;//点を探す範囲
    private JCheckBoxMenuItem pointOffsetCheckBox;//点を離すかどうか
    private JCheckBoxMenuItem gridInputAssistCheckBox;//高密度用入力をするかどうか
    private JCheckBoxMenuItem displayCommentsCheckBox;//文章
    private JCheckBoxMenuItem displayCpLinesCheckBox;//折線
    private JCheckBoxMenuItem displayAuxLinesCheckBox;//補助活線cyan
    private JCheckBoxMenuItem displayLiveAuxLinesCheckBox;//補助画線
    private JCheckBoxMenuItem displayStandardFaceMarksCheckBox;//Marking lines such as crosses and reference planes
    private JCheckBoxMenuItem cpOnTopCheckBox;//展開図を折り上がり予想図の上に描く
    private JCheckBoxMenuItem darkModeCheckBox;
    private JCheckBoxMenuItem preciseZoomCheckBox;
    private JCheckBoxMenuItem displaySelfIntersectionCheckBox;
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem saveAsButton;
    private JMenuItem exportButton;
    private JMenuItem importButton;
    private JMenuItem exitButton;
    private JMenuItem toggleHelpMenuItem;
    private JMenuItem toggleConsoleMenuItem;
    private JMenu openRecentMenu;
    private JMenuItem clearRecentFileMenuItem;

    private JMenuItem copyButton;
    private JMenuItem cutButton;
    private JMenuItem pasteButton;
    private JMenuItem pasteOffsetButton;

    public AppMenuBar(App app) {
        this.app = app;
        createElements();
        ApplicationModel applicationModel = app.applicationModel;

        app.registerButton(newButton, "newAction");
        app.registerButton(openButton, "openAction");
        app.registerButton(openRecentMenu, "openRecentAction");
        app.registerButton(saveButton, "saveAction");
        app.registerButton(saveAsButton, "saveAsAction");
        app.registerButton(importButton, "importAction");
        app.registerButton(exportButton, "exportAction");
        app.registerButton(exitButton, "exitAction");
        app.registerButton(showPointRangeCheckBox, "showPointRangeAction");
        app.registerButton(pointOffsetCheckBox, "pointOffsetAction");
        app.registerButton(gridInputAssistCheckBox, "gridInputAssistAction");
        app.registerButton(displayCommentsCheckBox, "displayCommentsAction");
        app.registerButton(displayCpLinesCheckBox, "displayCpLinesAction");
        app.registerButton(displayAuxLinesCheckBox, "displayAuxLinesAction");
        app.registerButton(displayLiveAuxLinesCheckBox, "displayLiveAuxLinesAction");
        app.registerButton(displayStandardFaceMarksCheckBox, "displayStandardFaceMarksAction");
        app.registerButton(cpOnTopCheckBox, "cpOnTopAction");
        app.registerButton(toggleHelpMenuItem, "toggleHelpAction");
        app.registerButton(toggleConsoleMenuItem, "toggleConsoleAction");
        app.registerButton(darkModeCheckBox, "toggleDarkModeAction");
        app.registerButton(preciseZoomCheckBox, "preciseZoomAction");
        app.registerButton(displaySelfIntersectionCheckBox, "displaySelfIntersectionAction");

        app.registerButton(copyButton, "copyClipboardAction");
        app.registerButton(cutButton, "cutClipboardAction");
        app.registerButton(pasteButton, "pasteClipboardAction");
        app.registerButton(pasteOffsetButton, "pasteOffsetClipboardAction");

        newButton.addActionListener(e -> {
            if (!app.fileModel.isSaved()) {
                int choice = JOptionPane.showConfirmDialog(null, "<html>Current file not saved.<br/>Do you want to save it?", "File not saved", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    app.saveFile();
                } else if (choice == JOptionPane.CLOSED_OPTION || choice == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            app.fileModel.reset();
            //展開図の初期化　開始
            //settei_syokika_cp();//展開図パラメータの初期化
            app.developmentView_initialization();
            //展開図の初期化　終了
            //
            //折畳予測図のの初期化　開始
            app.OZ = app.temp_OZ;//20171223この行は不要かもしれないが、一瞬でもOZが示すOriagari_Zuがなくなることがないように念のために入れておく
            app.foldedFigures.clear();
            app.addNewFoldedFigure();
            app.setFoldedFigureIndex(0);
            app.configure_initialize_prediction();
            //折畳予測図のの初期化　終了

            app.Button_shared_operation();
            app.repaintCanvas();

            app.canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

            app.mainCreasePatternWorker.record();
            app.mainCreasePatternWorker.auxRecord();
        });
        openButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.openFile();
        });
        clearRecentFileMenuItem.addActionListener(e -> {
            app.applicationModel.setRecentFileList(new ArrayList<>());
        });

        saveButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveFile();
        });
        saveAsButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveAsFile();
        });
        exportButton.addActionListener(e -> {
            if (app.canvasModel.getMouseMode() != MouseMode.OPERATION_FRAME_CREATE_61) {
                app.mainCreasePatternWorker.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.exportFile();
            app.repaintCanvas();
        });
        importButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.importFile();
        });
        exitButton.addActionListener(e -> app.closing());
        showPointRangeCheckBox.addActionListener(e -> getData(applicationModel));
        pointOffsetCheckBox.addActionListener(e -> getData(applicationModel));
        gridInputAssistCheckBox.addActionListener(e -> {
            if (gridInputAssistCheckBox.isSelected()) {
                System.out.println(" kou_mitudo_nyuuryoku on");
            } else {
                System.out.println(" kou_mitudo_nyuuryoku off");
            }
            getData(applicationModel);
        });
        displayCommentsCheckBox.addActionListener(e -> getData(applicationModel));
        displayCpLinesCheckBox.addActionListener(e -> getData(applicationModel));
        displayAuxLinesCheckBox.addActionListener(e -> getData(applicationModel));
        displayLiveAuxLinesCheckBox.addActionListener(e -> getData(applicationModel));
        displayStandardFaceMarksCheckBox.addActionListener(e -> getData(applicationModel));
        cpOnTopCheckBox.addActionListener(e -> getData(applicationModel));
        toggleHelpMenuItem.addActionListener(e -> {
            app.applicationModel.toggleHelpVisible();

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.repaintCanvas();
        });
        toggleConsoleMenuItem.addActionListener(e -> {
            app.applicationModel.toggleConsoleVisible();

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
        });
        darkModeCheckBox.addActionListener(e -> {
            applicationModel.toggleDarkMode();

            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Restore custom colors in grid and folded figure for this color scheme?", "Restore colors", JOptionPane.YES_NO_OPTION)) {
                if (FlatLaf.isLafDark()) {
                    app.applicationModel.setGridColor(Colors.GRID_LINE_DARK);
                    app.applicationModel.setGridScaleColor(Colors.GRID_SCALE_DARK);

                    app.foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT_DARK);
                    app.foldedFigureModel.setBackColor(Colors.FIGURE_BACK_DARK);
                } else {
                    app.applicationModel.setGridColor(Colors.GRID_LINE);
                    app.applicationModel.setGridScaleColor(Colors.GRID_SCALE);

                    app.foldedFigureModel.setFrontColor(Colors.FIGURE_FRONT);
                    app.foldedFigureModel.setBackColor(Colors.FIGURE_BACK);
                }
            }
        });
        preciseZoomCheckBox.addActionListener(e -> applicationModel.togglePreciseZoom());
        displaySelfIntersectionCheckBox.addActionListener(e -> applicationModel.toggleDisplaySelfIntersection());

        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            Save save = new Save();
            app.mainCreasePatternWorker.foldLineSet.getSaveForSelectFolding(save);


            clipboard.setContents(new SaveTransferable(save), (clipboard1, contents) -> {});
        });
        cutButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            Save save = new Save();
            app.mainCreasePatternWorker.foldLineSet.getSaveForSelectFolding(save);


            clipboard.setContents(new SaveTransferable(save), (clipboard1, contents) -> {});

            app.mainCreasePatternWorker.foldLineSet.delSelectedLineSegmentFast();

            app.mainCreasePatternWorker.record();
        });
        pasteButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipboardContents = clipboard.getContents(null);

            try {
                if (clipboardContents.isDataFlavorSupported(SaveTransferable.saveFlavor)) {
                    Save save = (Save) clipboardContents.getTransferData(SaveTransferable.saveFlavor);

                    app.mainCreasePatternWorker.setSaveForPaste(save);
                    app.repaintCanvas();
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

                    app.mainCreasePatternWorker.setSave_for_reading_tuika(save);
                    app.repaintCanvas();
                }
            } catch (IOException | UnsupportedFlavorException ignored) {
                // We don't know how to paste this
            }
        });
    }

    private void createElements() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        add(fileMenu);

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

        importButton = new JMenuItem("Import");
        fileMenu.add(importButton);

        fileMenu.addSeparator();

        exitButton = new JMenuItem("Exit");
        fileMenu.add(exitButton);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        add(editMenu);

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

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        add(viewMenu);

        darkModeCheckBox = new JCheckBoxMenuItem("Dark Mode");
        viewMenu.add(darkModeCheckBox);
        preciseZoomCheckBox = new JCheckBoxMenuItem("Smooth zoom");
        viewMenu.add(preciseZoomCheckBox);
        showPointRangeCheckBox = new JCheckBoxMenuItem("Show point range");
        viewMenu.add(showPointRangeCheckBox);
        pointOffsetCheckBox = new JCheckBoxMenuItem("Offset cursor");
        viewMenu.add(pointOffsetCheckBox);
        gridInputAssistCheckBox = new JCheckBoxMenuItem("Grid input assist");
        viewMenu.add(gridInputAssistCheckBox);
        displayCommentsCheckBox = new JCheckBoxMenuItem("Display comments");
        viewMenu.add(displayCommentsCheckBox);
        displayCpLinesCheckBox = new JCheckBoxMenuItem("Display cp lines");
        viewMenu.add(displayCpLinesCheckBox);
        displayAuxLinesCheckBox = new JCheckBoxMenuItem("Display aux lines");
        viewMenu.add(displayAuxLinesCheckBox);
        displayLiveAuxLinesCheckBox = new JCheckBoxMenuItem("Display live aux lines");
        viewMenu.add(displayLiveAuxLinesCheckBox);
        displayStandardFaceMarksCheckBox = new JCheckBoxMenuItem("Display standard face marks");
        viewMenu.add(displayStandardFaceMarksCheckBox);
        cpOnTopCheckBox = new JCheckBoxMenuItem("Crease pattern on top");
        viewMenu.add(cpOnTopCheckBox);
        displaySelfIntersectionCheckBox = new JCheckBoxMenuItem("Display self intersection");
        viewMenu.add(displaySelfIntersectionCheckBox);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        add(helpMenu);

        toggleHelpMenuItem = new JMenuItem("Toggle help");
        helpMenu.add(toggleHelpMenuItem);

        toggleConsoleMenuItem = new JMenuItem("Toggle console");
        if (System.console() == null) {
            helpMenu.add(toggleConsoleMenuItem);
        }
    }

    public void getData(ApplicationModel applicationModel) {
        applicationModel.setDisplayPointSpotlight(showPointRangeCheckBox.isSelected());
        applicationModel.setDisplayPointOffset(pointOffsetCheckBox.isSelected());
        applicationModel.setDisplayGridInputAssist(gridInputAssistCheckBox.isSelected());
        applicationModel.setDisplayComments(displayCommentsCheckBox.isSelected());
        applicationModel.setDisplayCpLines(displayCpLinesCheckBox.isSelected());
        applicationModel.setDisplayAuxLines(displayAuxLinesCheckBox.isSelected());
        applicationModel.setDisplayLiveAuxLines(displayLiveAuxLinesCheckBox.isSelected());
        applicationModel.setDisplayMarkings(displayStandardFaceMarksCheckBox.isSelected());
        applicationModel.setDisplayCreasePatternOnTop(cpOnTopCheckBox.isSelected());
        applicationModel.setDarkMode(darkModeCheckBox.isSelected());
        applicationModel.setPreciseZoom(preciseZoomCheckBox.isSelected());
        applicationModel.setDisplaySelfIntersection(displaySelfIntersectionCheckBox.isSelected());
    }

    public void setData(ApplicationModel applicationModel) {
        showPointRangeCheckBox.setSelected(applicationModel.getDisplayPointSpotlight());
        pointOffsetCheckBox.setSelected(applicationModel.getDisplayPointOffset());
        gridInputAssistCheckBox.setSelected(applicationModel.getDisplayGridInputAssist());
        displayCommentsCheckBox.setSelected(applicationModel.getDisplayComments());
        displayCpLinesCheckBox.setSelected(applicationModel.getDisplayCpLines());
        displayAuxLinesCheckBox.setSelected(applicationModel.getDisplayAuxLines());
        displayLiveAuxLinesCheckBox.setSelected(applicationModel.getDisplayLiveAuxLines());
        displayStandardFaceMarksCheckBox.setSelected(applicationModel.getDisplayMarkings());
        cpOnTopCheckBox.setSelected(applicationModel.getDisplayCreasePatternOnTop());
        darkModeCheckBox.setSelected(applicationModel.getLaf().equals(FlatDarkLaf.class.getName()));
        preciseZoomCheckBox.setSelected(applicationModel.isPreciseZoom());
        displaySelfIntersectionCheckBox.setSelected(applicationModel.getDisplaySelfIntersection());

        openRecentMenu.removeAll();
        if (applicationModel.getRecentFileList().isEmpty()) {
            JMenuItem noItemsMenuItem = new JMenuItem("No items");
            noItemsMenuItem.setEnabled(false);
            openRecentMenu.add(noItemsMenuItem);
        }
        for (File recentFile : applicationModel.getRecentFileList()) {
            JMenuItem recentFileMenuItem = new JMenuItem(recentFile.getName());
            recentFileMenuItem.addActionListener(e -> app.openFile(recentFile));
            openRecentMenu.add(recentFileMenuItem);
        }
        openRecentMenu.addSeparator();
        openRecentMenu.add(clearRecentFileMenuItem);
    }

    public void setData(FileModel fileModel) {
    }
}
