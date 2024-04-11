package origami.crease_pattern.element;

public class Vector extends Point{
    /* Vector subclass
        contains wrapper constructors and pretty much all Point's methods
     */
    public Vector(double x, double y){
        super(x, y);
    }

    public Vector(Point p){
        super(p);
    }

    public Vector(){
        super();
    }

    public Vector(double a, Point p, double b, Point q) {
        super(a, p, b, q);
    }
}
