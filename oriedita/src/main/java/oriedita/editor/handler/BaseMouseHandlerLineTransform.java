package oriedita.editor.handler;

import org.tinylog.Logger;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.handler.step.StepMouseHandler;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.tools.SnappingUtil;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class BaseMouseHandlerLineTransform <T extends Enum <T>> extends StepMouseHandler<T> {

    protected final AngleSystemModel angleSystemModel;
    protected boolean snapping;
    protected FoldLineSet ori_s_temp;
    protected int total_old, total_new;
    protected Point delta;
    protected Point p1, p2;
    protected LineSegment l1;
    protected Save save;

    private FoldLineSet lines;
    private BufferedImage image;
    private boolean cacheTooBig;
    private boolean needsRerender;
    private boolean active;
    private double lastZoomX;
    private double lastZoomY;
    private double lastAngle;
    private Point bottomLeft, topRight;


    protected BaseMouseHandlerLineTransform(T step, AngleSystemModel angleSystemModel) {
        super(step);
        this.angleSystemModel = angleSystemModel;
    }

    @Override
    public void reset() {
        resetStep();
        image = null;
        needsRerender = false;
        bottomLeft = null;
        topRight = null;
        cacheTooBig = false;
        active = false;
        lastAngle = 100000;
        lastZoomX = 0;
        lastZoomY = 0;
        delta = new Point(0,0);
        p1 = null;
        p2 = null;
        lines = null;
        l1 = null;
        total_old = 0;
        total_new = 0;
        snapping = false;
        ori_s_temp = null;
        save = null;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent e) {
        super.mouseDragged(p0, e);
        snapping = e.isControlDown();
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        if(l1 != null)
            DrawingUtil.drawLineStep(g2, l1, camera, settings.getLineWidth());
        DrawingUtil.drawStepVertex(g2, p1, LineColor.GREEN_6, camera);
        DrawingUtil.drawStepVertex(g2, p2, LineColor.GREEN_6, camera);

        if (!active || lines == null) {
            return;
        }
        if (lines.getTotal() < 1000) { // no need to cache with so few lines
            drawDirect(g2, camera, settings);
            return;
        }
        if (determineCameraChanged(camera)) {
            cacheTooBig = false;
            needsRerender = true;
        }
        lastZoomX = camera.getCameraZoomX();
        lastZoomY = camera.getCameraZoomY();
        lastAngle = camera.getCameraAngle();

        if (needsRerender && lines != null && !cacheTooBig) {
            initCacheImage(g2, camera, settings);
            if (image != null) { // image won't be created if it would be too big
                rerender(camera, settings);
                needsRerender = false;
            }
        }

        if (image != null) {
            Point origin = camera.object2TV(new Point(0, 0));
            Point deltaTransformed = camera.object2TV(delta);
            Logger.info(delta);
            g2.drawImage(image,
                    (int) (bottomLeft.getX() + deltaTransformed.getX() - origin.getX()),
                    (int) (bottomLeft.getY() + deltaTransformed.getY() - origin.getY()),
                    image.getWidth(), image.getHeight(), null);
        }
        if (image == null && lines != null) {
            drawDirect(g2, camera, settings);
        }
    }

    protected void initCacheImage(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (bottomLeft == null) {
            double minX = lines.getMinX();
            double maxX = lines.getMaxX();
            double minY = lines.getMinY();
            double maxY = lines.getMaxY();

            bottomLeft = camera.object2TV(new Point(minX, minY));
            topRight = camera.object2TV(new Point(maxX, maxY));
        }

        int width = (int) (topRight.getX() - bottomLeft.getX()) + 3;
        int height = (int) (topRight.getY() - bottomLeft.getY()) + 3;
        image = null;
        if (width * height < settings.getWidth() * settings.getHeight() * 1.5) {
            image = g2.getDeviceConfiguration().createCompatibleImage(width, height, BufferedImage.BITMASK);
            bottomLeft = bottomLeft.move(new Point(-1, -1));
        }
    }

    protected void drawDirect(Graphics2D g2, Camera camera, DrawingSettings settings) {
        Point origin = camera.object2TV(new Point(0, 0));
        Point deltaTransformed = camera.object2TV(delta);
        int minx = (int) -(deltaTransformed.getX() - origin.getX());
        int miny = (int) -(deltaTransformed.getY() - origin.getY());
        int maxx = minx + settings.getWidth();
        int maxy = miny + settings.getHeight();

        // do cohen-sutherland clipping
        for (var s : lines.getLineSegmentsIterable()) {
            Point a = camera.object2TV(s.getA());
            Point b = camera.object2TV(s.getB());
            int regionA = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, a);
            int regionB = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, b);
            if ((regionA & regionB) == DrawingUtil.CENTER) {
                Point pa = s.getA().move(delta);
                Point pb = s.getB().move(delta);
                LineSegment s2 = new LineSegment(pa, pb, s.getColor());
                DrawingUtil.drawCpLine(g2, s2, camera, settings.getLineStyle(), settings.getLineWidth(),
                        d.getPointSize(), settings.getWidth(), settings.getHeight(), settings.useRoundedEnds());
            }
        }
    }

    protected void rerender(Camera camera, DrawingSettings settings) {
        Point zero = camera.TV2object(new Point(0, 0));
        Point boObject = camera.TV2object(bottomLeft);
        FoldLineSet ori_s_temp = new FoldLineSet();
        ori_s_temp.set(lines);
        ori_s_temp.move(zero.getX() - boObject.getX(), zero.getY() - boObject.getY());
        Graphics2D g = image.createGraphics();
        g.setBackground(new Color(0f, 0, 0, 0));
        for (var ls : ori_s_temp.getLineSegmentsIterable()) {
            DrawingUtil.drawCpLine(g, ls, camera, settings.getLineStyle(),
                    settings.getLineWidth(), d.getPointSize(), image.getWidth(), image.getHeight(), settings.useRoundedEnds());
        }
    }

    protected boolean determineCameraChanged(Camera camera) {
        return camera.getCameraZoomX() != lastZoomX || camera.getCameraZoomY() != lastZoomY || camera.getCameraAngle() != lastAngle;
    }

    protected void move_click_drag_point(Point p) {
        reset();
        active = true;
        needsRerender = true;

        ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        save = SaveProvider.createInstance();
        d.getFoldLineSet().getMemoSelectOption(save, 2);
        ori_s_temp.setSave(save);
        lines = ori_s_temp;

        p1 = p;
        p2 = p;
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            p1 = new Point(tmpPoint);
            p2 = new Point(tmpPoint);
        }
    }

    protected void drag_click_drag_point(Point p) {
        if (p1 == null)
            return;

        p2 = p;
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance())
            p2 = new Point(tmpPoint);

        l1 = new LineSegment(p1, p2, LineColor.GREEN_6);

        if(snapping)
            snapLine();

        delta = new Point(
                l1.determineBX() - l1.determineAX(),
                l1.determineBY() - l1.determineAY()
        );
    }

    protected abstract T release_click_drag_point(Point p);

    protected void click_select_2(Point p){
        p2 = p;
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance())
            p2 = new Point(tmpPoint);
    }

    protected abstract T release_select_2(Point p);

    protected void snapLine() {
        l1 = l1.withB(SnappingUtil.snapToClosePointInActiveAngleSystem(
                d, l1.getA(), l1.getB(),
                angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));
    }

    protected abstract void doAction();
}
