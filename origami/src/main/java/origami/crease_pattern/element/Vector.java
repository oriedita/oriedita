package origami.crease_pattern.element;

import origami.Epsilon;

public class Vector extends Point{
    /* Vector subclass
        contains wrapper constructors and pretty much all Point's methods
     */
    public Vector(double x, double y){ super(x, y); }

    public Vector(Point p){ super(p); }

    public Vector(){
        super();
    }

    public Vector(double a, Point p, double b, Point q) {
        super(a, p, b, q);
    }

    public static double dotProduct(Vector vec1, Vector vec2) {
        return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();
    }

    public static Vector subtract(Vector vec1, Vector vec2){
        return new Vector(vec1.getX() - vec2.getX(), vec1.getY() - vec2.getY());
    }

    public static Vector add(Vector vec1, Vector vec2){
        return new Vector(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY());
    }

    public static Vector scale(Vector vec, double scale){
        return new Vector(vec.getX() * scale, vec.getY() * scale);
    }

    public static Vector midPoint(Vector vec1, Vector vec2){
        return scale(Vector.add(vec1, vec2), 0.5);
    }

    public static Vector normalize(Vector vec){
        double magnitude = magnitude(vec);

        return Math.abs(magnitude) < Epsilon.VECTOR_NORMALIZE_THRESHOLD ? vec : new Vector(vec.getX() / magnitude, vec.getY() / magnitude);
    }

    public static double magnitude(Vector vec){
        return Math.sqrt(vec.getX() * vec.getX() + vec.getY() * vec.getY());
    }

    public static Vector rotate90(Vector vec){
        return new Vector(-vec.getY(), vec.getX());
    }
}
