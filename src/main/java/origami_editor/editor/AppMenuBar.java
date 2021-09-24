package origami_editor.editor;

import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FileModel;

import javax.swing.*;
import java.awt.*;

public class AppMenuBar extends JMenuBar {
    public JCheckBoxMenuItem showPointRangeCheckBox;//点を探す範囲
    public JCheckBoxMenuItem pointOffsetCheckBox;//点を離すかどうか
    public JCheckBoxMenuItem gridInputAssistCheckBox;//高密度用入力をするかどうか
    public JCheckBoxMenuItem displayCommentsCheckBox;//文章
    public JCheckBoxMenuItem displayCpLinesCheckBox;//折線
    public JCheckBoxMenuItem displayAuxLinesCheckBox;//補助活線cyan
    public JCheckBoxMenuItem displayLiveAuxLinesCheckBox;//補助画線
    public JCheckBoxMenuItem displayStandardFaceMarksCheckBox;//Marking lines such as crosses and reference planes
    public JCheckBoxMenuItem cpOnTopCheckBox;//展開図を折り上がり予想図の上に描く
    JMenuItem newButton;
    JMenuItem openButton;
    JMenuItem saveButton;
    JMenuItem saveAsButton;
    JMenuItem exportButton;
    JMenuItem importButton;
    private JMenuItem toggleHelpMenuItem;

    public AppMenuBar(App app) {
        createElements();
        CanvasModel canvasModel = app.canvasModel;

        newButton.addActionListener(e -> {
            app.setHelp("zen_syokika");

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
            app.setHelp("yomi");

            app.Button_shared_operation();

            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.openFile();
        });
        saveButton.addActionListener(e -> {
            app.setHelp("kaki");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveFile();
            app.mainDrawingWorker.record();
        });
        saveAsButton.addActionListener(e -> {
            app.setHelp("kaki");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;
            app.saveAsFile();
            app.mainDrawingWorker.record();
        });
        exportButton.addActionListener(e -> {
            app.setHelp("writeImage");
            if (app.mouseMode != MouseMode.OPERATION_FRAME_CREATE_61) {
                app.Button_shared_operation();
                app.mainDrawingWorker.setDrawingStage(0);
            }//枠設定時(==61)には、その枠を消さないためにes1.set_i_egaki_dankaiを０にしないでおく　20180524
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.writeImage();
            app.repaintCanvas();
        });
        importButton.addActionListener(e -> {
            app.setHelp("yomi");
            app.Button_shared_operation();
            app.mouseDraggedValid = false;
            app.mouseReleasedValid = false;

            app.importFile();
        });
        showPointRangeCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_ten_sagasi");

            getData(canvasModel);
        });
        pointOffsetCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_ten_hanasi");

            getData(canvasModel);
        });
        gridInputAssistCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_kou_mitudo_nyuuryoku");

            if (gridInputAssistCheckBox.isSelected()) {
                System.out.println(" kou_mitudo_nyuuryoku on");
            } else {
                System.out.println(" kou_mitudo_nyuuryoku off");
            }
            getData(canvasModel);
        });
        displayCommentsCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_bun");
            getData(canvasModel);
        });
        displayCpLinesCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_cp");
            getData(canvasModel);
        });
        displayAuxLinesCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_a0");
            getData(canvasModel);
        });
        displayLiveAuxLinesCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_a1");
            getData(canvasModel);
        });
        displayStandardFaceMarksCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_mejirusi");
            getData(canvasModel);
        });
        cpOnTopCheckBox.addActionListener(e -> {
            app.setHelp("ckbox_cp_ue");

            getData(canvasModel);
        });
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
        newButton.setMnemonic('N');
        fileMenu.add(newButton);

        openButton = new JMenuItem("Open");
        openButton.setMnemonic('O');
        fileMenu.add(openButton);

        saveButton = new JMenuItem("Save");
        saveButton.setMnemonic('S');
        fileMenu.add(saveButton);

        saveAsButton = new JMenuItem("Save as");
        saveAsButton.setMnemonic('a');
        fileMenu.add(saveAsButton);

        exportButton = new JMenuItem("Export");
        exportButton.setMnemonic('E');
        fileMenu.add(exportButton);
        importButton = new JMenuItem("Import");
        importButton.setMnemonic('I');
        fileMenu.add(importButton);

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
