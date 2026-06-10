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
import java.util.ArrayList;

public abstract class BaseMouseHandlerLineTransform <T extends Enum <T>> extends StepMouseHandler<T> {

    protected T enum_step_first;
    protected T enum_step_second;
    protected Point delta;
    protected Save save;
    protected FoldLineSet fls_selected;
    protected boolean multiple;

    private Point anchor;
    private Point candidate;
    private ArrayList<Point> destinations;
    private LineSegment l1;
    private boolean snapping;
    private final AngleSystemModel angleSystemModel;

    // Rendering
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
        delta = new Point(0,0);
        save = null;
        fls_selected = null;
        multiple = false;

        anchor = null;
        candidate = null;
        destinations = new ArrayList<>();
        l1 = null;
        snapping = false;

        lines = null;
        image = null;
        cacheTooBig = false;
        needsRerender = false;
        active = false;
        lastZoomX = 0;
        lastZoomY = 0;
        lastAngle = 100000;
        bottomLeft = null;
        topRight = null;
    }

    @Override
    public void mouseDragged(Point p0, MouseEvent e) {
        super.mouseDragged(p0, e);
        snapping = e.isControlDown();
    }

    @Override
    public void mousePressed(Point p, MouseEvent e, int b) {
        super.mousePressed(p,e,b);
        multiple = e.isShiftDown();
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);

        if(l1 != null)
            DrawingUtil.drawLineStep(g2, l1, camera, settings.getLineWidth());
        if (anchor != null)
            DrawingUtil.drawStepVertex(g2, anchor, LineColor.GREEN_6, camera);
        if (candidate != null)
            DrawingUtil.drawStepVertex(g2, candidate, LineColor.GREEN_6, camera);
        for (Point p : destinations)
            DrawingUtil.drawStepVertex(g2, p, LineColor.GREEN_6, camera);

        if (!active)
            return;

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

    private void initCacheImage(Graphics2D g2, Camera camera, DrawingSettings settings) {
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

    private void drawDirect(Graphics2D g2, Camera camera, DrawingSettings settings) {
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

    private void rerender(Camera camera, DrawingSettings settings) {
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

    private boolean determineCameraChanged(Camera camera) {
        return camera.getCameraZoomX() != lastZoomX || camera.getCameraZoomY() != lastZoomY || camera.getCameraAngle() != lastAngle;
    }

    protected void first_click_hover(Point p) {
        reset();
        active = true;
        needsRerender = true;
        fls_selected = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        save = SaveProvider.createInstance();
        d.getFoldLineSet().getMemoSelectOption(save, 2); // get selected lines
        fls_selected.setSave(save);
        lines = fls_selected;

        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance()) {
            anchor = tmpPoint;
            candidate =  tmpPoint;
        }
        else {
            candidate = p;
            anchor = p;
        }
    }

    protected void first_click_drag(Point p) {
        if (anchor == null)
            return;

        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance())
            candidate = tmpPoint;
        else
            candidate = p;

        l1 = new LineSegment(anchor, candidate, LineColor.GREEN_6);

        if(snapping)
            snapLine();

        delta = new Point(
                l1.determineBX() - l1.determineAX(),
                l1.determineBY() - l1.determineAY()
        );
    }

    protected T first_click_release(Point p) {
        if (anchor == null || candidate == null)
            return enum_step_first;

        if (anchor.distance(candidate) < d.getSelectionDistance())
            return enum_step_second;

        doAction();
        d.record();
        d.check4();
        return enum_step_first;
    }

    protected void further_click_hover(Point p){
        Point tmpPoint = d.getClosestPoint(p);
        if (p.distance(tmpPoint) < d.getSelectionDistance())
            candidate = tmpPoint;
        else
            candidate = p;
    }

    protected T further_click_release(Point p){
        if (candidate == null)
            return enum_step_second;
        // If Shift is held you can select multiple points
        if (multiple) {
            candidate = null;
            Point tmpPoint = d.getClosestPoint(p);
            if (p.distance(tmpPoint) < d.getSelectionDistance())
                destinations.add(tmpPoint);
            else
                destinations.add(p);
            return enum_step_second;
        }
        else {
            // Process all points placed with Shift first
            for (Point dest : destinations) {
                l1 = new LineSegment(anchor, dest,  LineColor.GREEN_6);
                delta = new Point(
                        l1.determineBX() - l1.determineAX(),
                        l1.determineBY() - l1.determineAY());
                // "multiple" is reused in doAction implementations of moveAction
                // to deselect the moved points.
                multiple = true;
                doAction();
            }
            // Then do the single point (either the only selected end point or the last one when stopped holding Shift)
            // And set it back to "false" to keep the last copy selected
            multiple = false;

            Point tmpPoint = d.getClosestPoint(p);
            if (p.distance(tmpPoint) < d.getSelectionDistance())
                candidate = tmpPoint;
            else
                candidate = p;

            l1 = new LineSegment(anchor, candidate, LineColor.GREEN_6);
            delta = new Point(
                    l1.determineBX() - l1.determineAX(),
                    l1.determineBY() - l1.determineAY());
            doAction();

            d.record();
            d.check4();
            return enum_step_first;
        }
    }

    private void snapLine() {
        l1 = l1.withB(SnappingUtil.snapToClosePointInActiveAngleSystem(
                d, l1.getA(), l1.getB(),
                angleSystemModel.getCurrentAngleSystemDivider(), angleSystemModel.getAngles()));
    }

    // Actual move/copy action
    protected abstract void doAction();
}
