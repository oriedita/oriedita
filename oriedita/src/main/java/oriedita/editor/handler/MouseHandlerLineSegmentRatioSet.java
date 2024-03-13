package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.InternalDivisionRatioModel;
import origami.Epsilon;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.LINE_SEGMENT_RATIO_SET_28)
public class MouseHandlerLineSegmentRatioSet extends BaseMouseHandlerInputRestricted {
    private final InternalDivisionRatioModel internalDivisionRatioModel;

    @Inject
    public MouseHandlerLineSegmentRatioSet(InternalDivisionRatioModel internalDivisionRatioModel) {
        this.internalDivisionRatioModel = internalDivisionRatioModel;
    }

    //マウス操作(mouseMode==28線分内分入力 でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getLineStep().clear();

        Point p = d.getCamera().TV2object(p0);

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
        Point p = d.getCamera().TV2object(p0);
        d.getLineStep().set(0, d.getLineStep().get(0).withA(p));

        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
            } else {
                d.getLineCandidate().add(new LineSegment(p, p, d.getLineColor()));
            }
            d.getLineStep().set(0, d.getLineStep().get(0).withA(d.getLineCandidate().get(0).getA()));
        }
    }

    //マウス操作(mouseMode==28線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        LineSegment l0 = d.getLineStep().get(0);
        l0 = l0.withA(p);
        Point closestPoint = d.getClosestPoint(p);

        if (p.distance(closestPoint) <= d.getSelectionDistance()) {
            l0 = l0.withA(closestPoint);
        }
        d.getLineStep().set(0, l0);
        if (Epsilon.high.gt0(l0.determineLength())) {
            double internalDivisionRatio_s = internalDivisionRatioModel.getInternalDivisionRatioS();
            double internalDivisionRatio_t = internalDivisionRatioModel.getInternalDivisionRatioT();
            if ((internalDivisionRatio_s == 0.0) && (internalDivisionRatio_t == 0.0)) {
            }
            if ((internalDivisionRatio_s == 0.0) && (internalDivisionRatio_t != 0.0)) {
                d.addLineSegment(l0);
            }
            if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t == 0.0)) {
                d.addLineSegment(l0);
            }
            if ((internalDivisionRatio_s != 0.0) && (internalDivisionRatio_t != 0.0)) {
                LineSegment s_ad = new LineSegment();
                s_ad.setColor(d.getLineColor());
                double nx = (internalDivisionRatio_t * l0.determineBX() + internalDivisionRatio_s * l0.determineAX())
                        / (internalDivisionRatio_s + internalDivisionRatio_t);
                double ny = (internalDivisionRatio_t * l0.determineBY() + internalDivisionRatio_s * l0.determineAY())
                        / (internalDivisionRatio_s + internalDivisionRatio_t);
                d.addLineSegment(s_ad.withCoordinates(l0.determineAX(), l0.determineAY(), nx, ny));
                d.addLineSegment(s_ad.withCoordinates(l0.determineBX(), l0.determineBY(), nx, ny));
            }
            d.record();
        }

        d.getLineStep().clear();
    }
}
