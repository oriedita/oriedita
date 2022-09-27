package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerLineSegmentRatioSet extends BaseMouseHandlerInputRestricted {
    private final InternalDivisionRatioModel internalDivisionRatioModel;

    @Inject
    public MouseHandlerLineSegmentRatioSet(InternalDivisionRatioModel internalDivisionRatioModel) {
        this.internalDivisionRatioModel = internalDivisionRatioModel;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.LINE_SEGMENT_RATIO_SET_28;
    }

    //マウス操作(mouseMode==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getLineStep().clear();

        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) < d.getSelectionDistance()) {
            LineSegment s = new LineSegment(p, closestPoint, d.getLineColor());
            s.setActive(LineSegment.ActiveState.ACTIVE_B_2);
            d.lineStepAdd(s);
            return;
        }

        LineSegment s = new LineSegment(p, p, d.getLineColor());
        s.setActive(LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s);
    }

    //マウス操作(mouseMode==28線分入力 でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));
        d.getLineStep().get(0).setA(p);

        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
            } else {
                d.getLineCandidate().add(new LineSegment(p, p, d.getLineColor()));
            }
            d.getLineStep().get(0).setA(d.getLineCandidate().get(0).getA());
        }
    }

    //マウス操作(mouseMode==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = new Point();
        p.set(d.getCamera().TV2object(p0));

        d.getLineStep().get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.getSelectionDistance()) {
            d.getLineStep().get(0).setA(closestPoint);
        }
        if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
            double internalDivisionRatio_s = internalDivisionRatioModel.getInternalDivisionRatioS();
            double internalDivisionRatio_t = internalDivisionRatioModel.getInternalDivisionRatioT();
            if ((internalDivisionRatio_s == 0.0) && (internalDivisionRatio_t == 0.0)) {
            }
            if ((internalDivisionRatio_s == 0.0) && (internalDivisionRatio_t != 0.0)) {
                d.addLineSegment(d.getLineStep().get(0));
            }
            if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t == 0.0)) {
                d.addLineSegment(d.getLineStep().get(0));
            }
            if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t != 0.0)) {
                LineSegment s_ad = new LineSegment();
                s_ad.setColor(d.getLineColor());
                double nx = (internalDivisionRatio_t * d.getLineStep().get(0).determineBX() + internalDivisionRatio_s * d.getLineStep().get(0).determineAX()) / (internalDivisionRatio_s + internalDivisionRatio_t);
                double ny = (internalDivisionRatio_t * d.getLineStep().get(0).determineBY() + internalDivisionRatio_s * d.getLineStep().get(0).determineAY()) / (internalDivisionRatio_s + internalDivisionRatio_t);
                s_ad.set(d.getLineStep().get(0).determineAX(), d.getLineStep().get(0).determineAY(), nx, ny);
                d.addLineSegment(s_ad);
                s_ad.set(d.getLineStep().get(0).determineBX(), d.getLineStep().get(0).determineBY(), nx, ny);
                d.addLineSegment(s_ad);
            }
            d.record();
        }

        d.getLineStep().clear();
    }
}
