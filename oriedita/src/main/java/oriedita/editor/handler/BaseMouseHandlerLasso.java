package oriedita.editor.handler;

import origami.crease_pattern.element.Point;

public abstract class BaseMouseHandlerLasso extends BaseMouseHandler{
    @Override
    public void mouseMoved(Point p0) {}

    @Override
    public void mousePressed(Point p0) {
        if(d.getLinePath().getPathIterator(null).isDone()) {
            Point p = d.getCamera().TV2object(p0);
            d.getLinePath().moveTo(p.getX(), p.getY());
        }
    }

    @Override
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        d.getLinePath().lineTo(p.getX(), p.getY());
    }

    @Override
    public void mouseReleased(Point p0) {
        d.getLinePath().closePath();
        performAction();
        d.getLinePath().reset();
    }

    protected abstract void performAction();
}
