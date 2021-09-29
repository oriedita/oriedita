package origami_editor.editor;

import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FileModel;

import javax.swing.*;
import java.awt.*;

public class AppMenuBar extends JMenuBar {
    private JCheckBoxMenuItem showPointRangeCheckBox;//点を探す範囲
    private JCheckBoxMenuItem pointOffsetCheckBox;//点を離すかどうか
    private JCheckBoxMenuItem gridInputAssistCheckBox;//高密度用入力をするかどうか
    private JCheckBoxMenuItem displayCommentsCheckBox;//文章
    private JCheckBoxMenuItem displayCpLinesCheckBox;//折線
    private JCheckBoxMenuItem displayAuxLinesCheckBox;//補助活線cyan
    private JCheckBoxMenuItem displayLiveAuxLinesCheckBox;//補助画線
    private JCheckBoxMenuItem displayStandardFaceMarksCheckBox;//Marking lines such as crosses and reference planes
    private JCheckBoxMenuItem cpOnTopCheckBox;//展開図を折り上がり予想図の上に描く
    private JMenuItem newButton;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem saveAsButton;
    private JMenuItem exportButton;
    private JMenuItem importButton;
    private JMenuItem exitButton;
    private JMenuItem toggleHelpMenuItem;

    public AppMenuBar(App app) {
        createElements();
        CanvasModel canvasModel = app.canvasModel;

        app.registerButton(newButton, "newAction");
        app.registerButton(openButton, "openAction");
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

        newButton.addActionListener(e -> {
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

            app.mainDrawingWorker.record();
            app.mainDrawingWorker.auxRecord();
        });
        openButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.openFile();
        });
        saveButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveFile();
            app.mainDrawingWorker.record();
        });
        saveAsButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveAsFile();
            app.mainDrawingWorker.record();
        });
        exportButton.addActionListener(e -> {
            if (app.mouseMode != MouseMode.OPERATION_FRAME_CREATE_61) {
                app.mainDrawingWorker.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.writeImage();
            app.repaintCanvas();
        });
        importButton.addActionListener(e -> {
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.importFile();
        });
        exitButton.addActionListener(e -> app.closing());
        showPointRangeCheckBox.addActionListener(e -> getData(canvasModel));
        pointOffsetCheckBox.addActionListener(e -> getData(canvasModel));
        gridInputAssistCheckBox.addActionListener(e -> {
            if (gridInputAssistCheckBox.isSelected()) {
                System.out.println(" kou_mitudo_nyuuryoku on");
            } else {
                System.out.println(" kou_mitudo_nyuuryoku off");
            }
            getData(canvasModel);
        });
        displayCommentsCheckBox.addActionListener(e -> getData(canvasModel));
        displayCpLinesCheckBox.addActionListener(e -> getData(canvasModel));
        displayAuxLinesCheckBox.addActionListener(e -> getData(canvasModel));
        displayLiveAuxLinesCheckBox.addActionListener(e -> getData(canvasModel));
        displayStandardFaceMarksCheckBox.addActionListener(e -> getData(canvasModel));
        cpOnTopCheckBox.addActionListener(e -> getData(canvasModel));
        toggleHelpMenuItem.addActionListener(e -> {
            app.explanation.setVisible(!app.explanation.isVisible());

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.repaintCanvas();
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

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        add(viewMenu);

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

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        add(helpMenu);

        toggleHelpMenuItem = new JMenuItem("Toggle help");

        toggleHelpMenuItem.setMargin(new Insets(0, 0, 0, 0));
        helpMenu.add(toggleHelpMenuItem);

        toggleHelpMenuItem.setMargin(new Insets(0, 0, 0, 0));
    }

    public void getData(CanvasModel canvasModel) {
        canvasModel.setDisplayPointSpotlight(showPointRangeCheckBox.isSelected());
        canvasModel.setDisplayPointOffset(pointOffsetCheckBox.isSelected());
        canvasModel.setDisplayGridInputAssist(gridInputAssistCheckBox.isSelected());
        canvasModel.setDisplayComments(displayCommentsCheckBox.isSelected());
        canvasModel.setDisplayCpLines(displayCpLinesCheckBox.isSelected());
        canvasModel.setDisplayAuxLines(displayAuxLinesCheckBox.isSelected());
        canvasModel.setDisplayLiveAuxLines(displayLiveAuxLinesCheckBox.isSelected());
        canvasModel.setDisplayMarkings(displayStandardFaceMarksCheckBox.isSelected());
        canvasModel.setDisplayCreasePatternOnTop(cpOnTopCheckBox.isSelected());
    }

    public void setData(CanvasModel canvasModel) {
        showPointRangeCheckBox.setSelected(canvasModel.getDisplayPointSpotlight());
        pointOffsetCheckBox.setSelected(canvasModel.getDisplayPointOffset());
        gridInputAssistCheckBox.setSelected(canvasModel.getDisplayGridInputAssist());
        displayCommentsCheckBox.setSelected(canvasModel.getDisplayComments());
        displayCpLinesCheckBox.setSelected(canvasModel.getDisplayCpLines());
        displayAuxLinesCheckBox.setSelected(canvasModel.getDisplayAuxLines());
        displayLiveAuxLinesCheckBox.setSelected(canvasModel.getDisplayLiveAuxLines());
        displayStandardFaceMarksCheckBox.setSelected(canvasModel.getDisplayMarkings());
        cpOnTopCheckBox.setSelected(canvasModel.getDisplayCreasePatternOnTop());
    }

    public void setData(FileModel fileModel) {
    }
}
