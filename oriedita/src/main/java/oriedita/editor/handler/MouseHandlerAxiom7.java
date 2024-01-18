package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

@ApplicationScoped
@Handles(MouseMode.AXIOM_7)
public class MouseHandlerAxiom7 extends BaseMouseHandlerInputRestricted{
    private Point midPoint = new Point();
    @Inject
    public MouseHandlerAxiom7(){}

    //マウス操作(マウスを動かしたとき)を行う関数
    public void mouseMoved(Point p0) {
        if (d.getLineStep().isEmpty()) {
            super.mouseMoved(p0);
        }
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        // 1. target point
        if(d.getLineStep().isEmpty()){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        // 2. target segment
        // Don't allow segment that spans through the target point
        if(d.getLineStep().size() == 1){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

            LineSegment temp = new LineSegment(d.getLineStep().get(0).getA(), closestLineSegment.determineClosestEndpoint(d.getLineStep().get(0).getA()));
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance() &&
                    OritaCalc.isLineSegmentParallel(temp, closestLineSegment) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // 3. reference segment
        if(d.getLineStep().size() == 2){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

            // Don't allow segment that is parallel to target segment
            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance() &&
                    OritaCalc.isLineSegmentParallel(closestLineSegment, d.getLineStep().get(1)) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                closestLineSegment.setColor(LineColor.ORANGE_4);
                d.lineStepAdd(closestLineSegment);
            }
            return;
        }

        // index 3 and 4 are the purple indicators
        // 4. destination line (case 2)
        // Don't accept segment that is parallel to the purple indicators
        if(d.getLineStep().size() == 5){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));

            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance()) ||
                    OritaCalc.isLineSegmentParallel(closestLineSegment, d.getLineStep().get(3)) != OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                return;
            }

            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    @Override
    public void mouseDragged(Point p0) {}

    @Override
    public void mouseReleased(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        // First 3 are clicked
        if(d.getLineStep().size() == 3){
            midPoint = drawAxiom7FoldIndicators(d.getLineStep().get(0), d.getLineStep().get(1), d.getLineStep().get(2));
        }

        // Case 1: Click on the purple indicators auto expand the purple indicators to the nearest lines
        // (Kinda works)
        if(d.getLineStep().size() == 5 && d.getClosestPoint(p).distance(p) > d.getSelectionDistance()){
            if (OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(3)) < d.getSelectionDistance() ||
                    OritaCalc.determineLineSegmentDistance(p, d.getLineStep().get(4)) < d.getSelectionDistance()) {
                LineSegment s = d.getClosestLineStepSegment(p, 4, 5);
                s = new LineSegment(s.getB(), s.getA(), d.getLineColor());
                s = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), s);

                d.addLineSegment(s);
                d.record();
                d.getLineStep().clear();
            }
        }

        // Case 2: Click on destination line to extend result line from midpoint
        if(d.getLineStep().size() == 6){
            LineSegment ls3 = d.getLineStep().get(3);
            LineSegment midTemp = new LineSegment(
                    midPoint,
                    new Point(
                            midPoint.getX() + ls3.determineBX() - ls3.determineAX(),
                            midPoint.getY() + ls3.determineBY() - ls3.determineAY()));
            LineSegment result = getExtendedSegment(midTemp, d.getLineStep().get(5), d.getLineColor());
            d.addLineSegment(result);
            d.record();
            d.getLineStep().clear();
        }
    }

    public Point drawAxiom7FoldIndicators(LineSegment target, LineSegment targetSegment, LineSegment refSegment){
        LineSegment temp = target.withB(
                new Point(
                        target.determineAX() + refSegment.determineBX() - refSegment.determineAX(),
                        target.determineAY() + refSegment.determineBY() - refSegment.determineAY()));
        LineSegment extendLine = getExtendedSegment(temp, targetSegment, LineColor.PURPLE_8);

        if (extendLine == null) { return null; }

        Point mid = OritaCalc.midPoint(target.getA(), OritaCalc.findIntersection(extendLine, targetSegment));

        LineSegment s1 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 25), mid), LineColor.PURPLE_8));
        LineSegment s2 = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), new LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, -25), mid), LineColor.PURPLE_8));

        d.lineStepAdd(s1);
        d.lineStepAdd(s2);
        return mid;
    }

    public LineSegment getExtendedSegment(LineSegment s_o, LineSegment s_k, LineColor icolo) {
        Point cross_point = new Point();

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            return null;
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.PARALLEL_EQUAL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = s_k.getA();
            if (OritaCalc.distance(s_o.getA(), s_k.getA()) > OritaCalc.distance(s_o.getA(), s_k.getB())) {
                cross_point = s_k.getB();
            }
        }

        if (OritaCalc.isLineSegmentParallel(s_o, s_k, Epsilon.UNKNOWN_1EN7) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
            cross_point = OritaCalc.findIntersection(s_o, s_k);
        }

        LineSegment add_sen = new LineSegment(cross_point, s_o.getA(), icolo);

        if (Epsilon.high.gt0(add_sen.determineLength())) {
            return add_sen;
        }

        return null;
    }
}
