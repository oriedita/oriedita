package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Graphics2D;

enum DrawPointStep { SELECT_POINT }

@ApplicationScoped
@Handles(MouseMode.DRAW_POINT_14)
public class MouseHandlerDrawPoint extends StepMouseHandler<DrawPointStep> {
    private Point targetPoint;

    @Inject
    private CanvasModel canvasModel;

    @Inject
    public MouseHandlerDrawPoint() {
        super(DrawPointStep.SELECT_POINT);
        steps.addNode(StepNode.createNode_MD_R(DrawPointStep.SELECT_POINT, this::move_drag_select_point, this::release_click_drag_point));
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        DrawingUtil.drawStepVertex(g2, targetPoint, d.getLineColor(), camera, d.getGridInputAssist());
    }

    @Override
    public void reset() {
        targetPoint = null;
        move_drag_select_point(canvasModel.getMouseObjPosition());
        steps.setCurrentStep(DrawPointStep.SELECT_POINT);
    }

    // Click drag point
    private void move_drag_select_point(Point p) {
        targetPoint = p;
        if(p.distance(d.getClosestPoint(p)) < d.getSelectionDistance()) {
            targetPoint = d.getClosestPoint(p);
        }
    }
    private DrawPointStep release_click_drag_point(Point p) {
        LineSegment mtsLineSegment = d.getFoldLineSet().closestLineSegmentSearch(p);
        LineSegment mts = new LineSegment(mtsLineSegment.getA(), mtsLineSegment.getB());//mtsは点pに最も近い線分

        if (OritaCalc.determineLineSegmentDistance(targetPoint, mts) > d.getSelectionDistance()) {
            reset();
            return DrawPointStep.SELECT_POINT;
        }

        //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
        //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
        Point pk = OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), p);//pkは点pの（線分を含む直線上の）影

        //点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数	public int oc.hakononaka(Ten p1,Ten pa,Ten p2){}

        if (OritaCalc.isInside(mts.getA(), pk, mts.getB()) == 2) {
            //線分の分割-----------------------------------------
            d.getFoldLineSet().applyLineSegmentDivide(mtsLineSegment, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
            d.record();
        }

        reset();
        return DrawPointStep.SELECT_POINT;
    }
}
