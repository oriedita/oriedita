package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;
import origami.crease_pattern.element.StraightLine;
import origami.crease_pattern.element.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
@Handles(MouseMode.AXIOM_6)
public class MouseHandlerAxiom6 extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerAxiom6() {
    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        if(d.getLineStep().size() < 2){
            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.lineStepAdd(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                return;
            }
        }

        if(d.getLineStep().size() < 4){
            LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));
            if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) { return; }
            closestLineSegment.setColor(LineColor.GREEN_6);
            d.lineStepAdd(closestLineSegment);
        }
    }

    @Override
    public void mouseDragged(Point p0) {}

    @Override
    public void mouseReleased(Point p0) {
        if(d.getLineStep().size() == 4){
            Point p1 = new Point(d.getLineStep().get(0).determineAX(), d.getLineStep().get(0).determineAY());
            Point p2 = new Point(d.getLineStep().get(1).determineAX(), d.getLineStep().get(1).determineAY());
            StraightLine s1 = new StraightLine(d.getLineStep().get(2));
            StraightLine s2 = new StraightLine(d.getLineStep().get(3));

            //TODO: results not showing what I want to see
            List<StraightLine> commonTangents = normalAxiom6(p1, p2, s1, s2);

            if(commonTangents != null){
                for(StraightLine tangent : commonTangents){

                    Point projectPoint1 = tangent.findProjection(p1);
                    Point projectPoint2 = tangent.findProjection(p2);

                    LineSegment result = new LineSegment(projectPoint1, projectPoint2, d.getLineColor());
                    result = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), result);
                    result = result.withAB(result.getB(), result.getA());
                    result = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), result);

                    d.addLineSegment(result);
                    d.record();
                }
                d.getLineStep().clear();
            }
        }
    }

    private double[] getPolynomial(int degree, double a, double b, double c, double d) {
        switch (degree) {
            case 2:
                return new double[]{-a / b};
            case 3:
                double discriminant = Math.pow(b, 2.0) - (4.0 * a * c); // quadratic
                // 0 solution
                if (discriminant < -Epsilon.AXIOM_THRESHOLD) {
                    return new double[]{};
                }
                // 1 solution
                double q1 = -b / (2.0 * c);
                if (discriminant < Epsilon.AXIOM_THRESHOLD) {
                    return new double[]{q1};
                }
                // 2 solutions
                double q2 = Math.sqrt(discriminant) / (2.0 * c);
                return new double[]{q1 + q2, q1 - q2};
            case 4:
                // Cubic
                // Cardano's formula. convert to depressed cubic
                double a2 = c / d;
                double a1 = b / d;
                double a0 = a / d;
                double q = (3.0 * a1 - Math.pow(a2, 2.0)) / 9.0;
                double r = (9.0 * a2 * a1 - 27.0 * a0 - 2.0 * Math.pow(a2, 3.0)) / 54.0;
                double d0 = Math.pow(q, 3.0) + Math.pow(r, 2.0);
                double u = -a2 / 3.0;

                // 1 solution
                if (d0 > 0.0) {
                    double sqrt_d0 = Math.sqrt(d0);
                    double s = cubrt(r + sqrt_d0);
                    double t = cubrt(r - sqrt_d0);
                    return new double[]{u + s + t};
                }
                // 2 solutions
                if (Math.abs(d0) < Epsilon.AXIOM_THRESHOLD) {
                    if (r < 0.0) {
                        return new double[]{};
                    }
                    double s = Math.pow(r, 1.0 / 3.0);
                    return new double[]{u + 2.0 * s, u - s};
                }
                // 3 solutions
                double sqrt_d0 = Math.sqrt(-d0);
                double phi = Math.atan2(sqrt_d0, r) / 3.0;
                double r_s = Math.pow((Math.pow(r, 2.0) - d0), 1.0 / 6.0);
                double s_r = r_s * Math.cos(phi);
                double s_i = r_s * Math.sin(phi);
                return new double[]{u + 2.0 * s_r,
                        u - s_r - Math.sqrt(3.0) * s_i,
                        u - s_r + Math.sqrt(3.0) * s_i};
            default:
                return new double[]{};
        }
    }

    private double cubrt(double n) {
        return n < 0 ? -Math.pow(-n, 1.0 / 3.0) : Math.pow(n, 1.0 / 3.0);
    }

    private List<StraightLine> normalAxiom6(Point p1, Point p2, StraightLine s1, StraightLine s2) {
        Vector p1Vec = new Vector(p1);
        Vector p2Vec = new Vector(p2);

        double s1Magnitude = s1.getMagnitude();
        double s2Magnitude = s2.getMagnitude();
        StraightLine s1Normalized = new StraightLine(s1.getA() / s1Magnitude,
                s1.getB() / s1Magnitude,
                s1.getC() / s1Magnitude);
        StraightLine s2Normalized = new StraightLine(s2.getA() / s2Magnitude,
                s2.getB() / s2Magnitude,
                s2.getC() / s2Magnitude);

        Vector s1Normal = new Vector(s1Normalized.getNormal());
        Vector s2Normal = new Vector(s2Normalized.getNormal());

        if (Math.abs(1.0 - (Vector.dotProduct(s1Normal, p1Vec) / s1Normalized.getC())) < 0.02) { return null; }

        Vector line_vec = Vector.rotate90(s1Normal);
        Vector vec1 = Vector.subtract(
                Vector.add(p1Vec, Vector.scale(s1Normal, s1Normalized.getC())),
                Vector.scale(p2Vec, 2.0));
        Vector vec2 = Vector.subtract(
                Vector.scale(s1Normal, s1Normalized.getC()),
                p1Vec);

        double c1 = Vector.dotProduct(p2Vec, s2Normal) - s2Normalized.getC();
        double c2 = Vector.dotProduct(vec2, line_vec) * 2.0;
        double c3 = Vector.dotProduct(vec2, vec2);
        double c4 = Vector.dotProduct(Vector.add(vec1, vec2), line_vec);
        double c5 = Vector.dotProduct(vec1, vec2);
        double c6 = Vector.dotProduct(line_vec, s2Normal);
        double c7 = Vector.dotProduct(vec2, s2Normal);

        double d = c6;
        double c = c1 + c4 * c6 + c7;
        double b = c1 * c2 + c5 * c6 + c4 * c7;
        double a = c1 * c3 + c5 * c7;

        int polynomial_degree = 0;
        if (Math.abs(d) > Epsilon.AXIOM_THRESHOLD) { polynomial_degree = 4; }
        if (Math.abs(c) > Epsilon.AXIOM_THRESHOLD) { polynomial_degree = 3; }
        if (Math.abs(b) > Epsilon.AXIOM_THRESHOLD) { polynomial_degree = 2; }

        Stream<StraightLine> map = Arrays.stream(getPolynomial(polynomial_degree, a, b, c, d))
                .mapToObj(n -> Vector.add(Vector.scale(s1Normal, s1Normalized.getC()),
                        Vector.scale(line_vec, n)))
                .map(p -> new StraightLine(Vector.normalize(Vector.subtract(p, p1Vec)),
                        Vector.dotProduct(Vector.normalize(Vector.subtract(p, p1Vec)), Vector.midPoint(p, p1Vec))));

        return map.toList();
    }
}
