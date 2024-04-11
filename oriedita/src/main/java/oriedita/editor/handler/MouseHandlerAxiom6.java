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

import java.util.Arrays;
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

        LineSegment closestLineSegment = new LineSegment(d.getClosestLineSegment(p));
        if (!(OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < d.getSelectionDistance())) { return; }
        closestLineSegment.setColor(LineColor.GREEN_6);
        d.lineStepAdd(closestLineSegment);
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
            Object[] commonTangents = normalAxiom6(p1, p2, s1, s2);

            if(commonTangents != null){
                for(Object tangent : commonTangents){
                    if(tangent instanceof StraightLine){
                        StraightLine o = ((StraightLine) tangent).clone();

                        LineSegment result = new LineSegment(o.getNormal(), o.getOppositeNormal(), LineColor.MAGENTA_5);
                        Point projectPoint = OritaCalc.findProjection(result, p1);
                        result = result.withA(projectPoint);
                        result = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), result);
                        result = result.withAB(result.getB(), result.getA());
                        result = OritaCalc.fullExtendUntilHit(d.getFoldLineSet(), result);

                        d.lineStepAdd(result);
                    }
                }
            }
//            d.getLineStep().clear();
        }
    }

    private double[] getPolynomial(int degree, double a, double b, double c, double d) {
        switch (degree) {
            case 1:
                return new double[]{-d / c};
            case 2:
                double discriminant = Math.pow(c, 2.0) - (4.0 * b * d); // quadratic
                // 0 solution
                if (discriminant < -Epsilon.UNKNOWN_1EN6) {
                    return new double[]{};
                }
                // 1 solution
                double q1 = -c / (2.0 * b);
                if (discriminant < Epsilon.UNKNOWN_1EN6) {
                    return new double[]{q1};
                }
                // 2 solutions
                double q2 = Math.sqrt(discriminant) / (2.0 * b);
                return new double[]{q1 + q2, q1 - q2};
            case 3:
                // Cubic
                // Cardano's formula. convert to depressed cubic
                double a2 = b / a;
                double a1 = c / a;
                double a0 = d / a;
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
                if (Math.abs(d0) < Epsilon.UNKNOWN_1EN6) {
                    double s = Math.pow(r, 1.0 / 3.0);
                    if (r < 0.0) {
                        return new double[]{};
                    }
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

    private Object[] normalAxiom6(Point p1, Point p2, StraightLine s1, StraightLine s2) {
        Point s1Normal = s1.getNormal();
        Point s2Normal = s2.getNormal();

        if (Math.abs(1.0 - (dotProduct(s1Normal, p1) / s1.getC())) < 0.02) {
            return null;
        }

        Point line_vec = rotate90(s1Normal);
        Point vec1 = subtract2(
                add2(p1, scale2(s1Normal, s1.getC())),
                scale2(p2, 2.0));
        Point vec2 = subtract2(
                scale2(s1Normal, s1.getC()),
                p1);
        double c1 = dotProduct(p2, s2Normal) - s2.getC();
        double c2 = 2.0 * dotProduct(vec2, line_vec);
        double c3 = dotProduct(vec2, vec2);
        double c4 = dotProduct(add2(vec1, vec2), line_vec);
        double c5 = dotProduct(vec1, vec2);
        double c6 = dotProduct(line_vec, s2Normal);
        double c7 = dotProduct(vec2, s2Normal);

        double a = c6;
        double b = c1 + c4 * c6 + c7;
        double c = c1 * c2 + c5 * c6 + c4 * c7;
        double d = c1 * c3 + c5 * c7;

        int polynomial_degree = 0;
        if (Math.abs(c) > Epsilon.UNKNOWN_1EN6) { polynomial_degree = 1; }
        if (Math.abs(b) > Epsilon.UNKNOWN_1EN6) { polynomial_degree = 2; }
        if (Math.abs(a) > Epsilon.UNKNOWN_1EN6) { polynomial_degree = 3; }

        Stream<StraightLine> map = Arrays.stream(getPolynomial(polynomial_degree, a, b, c, d))
                .mapToObj(n -> add2(scale2(s1Normal, s1.getC()), scale2(line_vec, n)))
                .map(p -> new StraightLine(normalize2(subtract2(p, p1)), dotProduct(normalize2(subtract2(p, p1)), midPoint(p, p1))));

        return map.toArray();
    }

    private double dotProduct(Point vec1, Point vec2) {
        return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();
    }

    private Point subtract2(Point vec1, Point vec2){
        return new Point(vec1.getX() - vec2.getX(), vec1.getY() - vec2.getY());
    }

    private Point add2(Point vec1, Point vec2){
        return new Point(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY());
    }

    private Point scale2(Point vec, double scale){
        return new Point(vec.getX() * scale, vec.getY() * scale);
    }

    private Point midPoint(Point vec1, Point vec2){
        return scale2(add2(vec1, vec2), 0.5);
    }

    private Point normalize2(Point vec){
        double magnitude = vec.distance(new Point());

        return Math.abs(magnitude) < 0.00001 ? vec : new Point(vec.getX() / magnitude, vec.getY() / magnitude);
    }

    private Point rotate90(Point vec){
        return new Point(-vec.getY(), vec.getX());
    }
}
