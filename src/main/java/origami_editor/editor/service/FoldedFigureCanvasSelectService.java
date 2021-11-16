package origami_editor.editor.service;

import javax.inject.Inject;
import javax.inject.Singleton;

import origami.crease_pattern.element.Point;
import origami.folding.FoldedFigure;
import origami_editor.editor.Canvas;
import origami_editor.editor.databinding.CanvasModel;
import origami_editor.editor.databinding.FoldedFigureModel;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

@Singleton
public class FoldedFigureCanvasSelectService {
    private final FoldedFiguresList foldedFiguresList;
    private final FoldedFigureModel foldedFigureModel;
    private final CanvasModel canvasModel;

    @Inject
    public FoldedFigureCanvasSelectService(FoldedFiguresList foldedFiguresList, FoldedFigureModel foldedFigureModel, CanvasModel canvasModel) {
        this.foldedFiguresList = foldedFiguresList;
        this.foldedFigureModel = foldedFigureModel;
        this.canvasModel = canvasModel;
    }

    public Canvas.MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p) {//A function that determines which of the development and folding views the Ten obtained with the mouse points to.
        //20171216
        //hyouji_flg==2,ip4==0  omote
        //hyouji_flg==2,ip4==1	ura
        //hyouji_flg==2,ip4==2	omote & ura
        //hyouji_flg==2,ip4==3	omote & ura

        //hyouji_flg==3,ip4==0  omote
        //hyouji_flg==3,ip4==1	ura
        //hyouji_flg==3,ip4==2	omote & ura
        //hyouji_flg==3,ip4==3	omote & ura

        //hyouji_flg==5,ip4==0  omote
        //hyouji_flg==5,ip4==1	ura
        //hyouji_flg==5,ip4==2	omote & ura
        //hyouji_flg==5,ip4==3	omote & ura & omote2 & ura2

        //OZ_hyouji_mode=0;  nun
        //OZ_hyouji_mode=1;  omote
        //OZ_hyouji_mode=2;  ura
        //OZ_hyouji_mode=3;  omote & ura
        //OZ_hyouji_mode=4;  omote & ura & omote2 & ura2

        int tempFoldedFigureIndex = -1;
        Canvas.MouseWheelTarget temp_i_cp_or_oriagari = Canvas.MouseWheelTarget.CREASE_PATTERN_0;
        FoldedFigure_Drawer drawer;
        FoldedFigure OZi;
        for (int i = 0; i < foldedFiguresList.getSize(); i++) {
            drawer = foldedFiguresList.getElementAt(i);
            OZi = drawer.foldedFigure;

            int OZ_display_mode = 0;//No fold-up diagram display
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.WIRE_2) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.TRANSPARENT_3) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 3;
            }//	omote & ura

            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.FRONT_0)) {
                OZ_display_mode = 1;
            }//	omote
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BACK_1)) {
                OZ_display_mode = 2;
            }//	ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.BOTH_2)) {
                OZ_display_mode = 3;
            }//	omote & ura
            if ((OZi.displayStyle == FoldedFigure.DisplayStyle.PAPER_5) && (OZi.ip4 == FoldedFigure.State.TRANSPARENT_3)) {
                OZ_display_mode = 4;
            }//	omote & ura & omote2 & ura2

            if (drawer.wireFrame_worker_drawer2.isInsideFront(p) > 0) {
                if (((OZ_display_mode == 1) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = Canvas.MouseWheelTarget.FOLDED_FRONT_1;
                    tempFoldedFigureIndex = i;
                }
            }

            if (drawer.wireFrame_worker_drawer2.isInsideRear(p) > 0) {
                if (((OZ_display_mode == 2) || (OZ_display_mode == 3)) || (OZ_display_mode == 4)) {
                    temp_i_cp_or_oriagari = Canvas.MouseWheelTarget.FOLDED_BACK_2;
                    tempFoldedFigureIndex = i;
                }
            }

            if (drawer.wireFrame_worker_drawer2.isInsideTransparentFront(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = Canvas.MouseWheelTarget.TRANSPARENT_FRONT_3;
                    tempFoldedFigureIndex = i;
                }
            }

            if (drawer.wireFrame_worker_drawer2.isInsideTransparentRear(p) > 0) {
                if (OZ_display_mode == 4) {
                    temp_i_cp_or_oriagari = Canvas.MouseWheelTarget.TRANSPARENT_BACK_4;
                    tempFoldedFigureIndex = i;
                }
            }
        }

        canvasModel.setMouseInCpOrFoldedFigure(temp_i_cp_or_oriagari);

        if (tempFoldedFigureIndex > -1) {
            setFoldedFigureIndex(tempFoldedFigureIndex);
        }

        return temp_i_cp_or_oriagari;
    }

    void setFoldedFigureIndex(int i) {//Processing when OZ is switched
        System.out.println("foldedFigureIndex = " + i);

        FoldedFigure_Drawer newSelectedItem = foldedFiguresList.getElementAt(i);
        foldedFiguresList.setSelectedItem(newSelectedItem);

        // Load data from this foldedFigure to the ui.
        newSelectedItem.getData(foldedFigureModel);
    }
}
