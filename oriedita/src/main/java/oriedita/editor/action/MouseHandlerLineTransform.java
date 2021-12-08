package oriedita.editor.action;

import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;
import origami.Epsilon;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class MouseHandlerLineTransform extends BaseMouseHandlerLineSelect{

    protected CanvasModel canvasModel;
    protected FoldLineSet lines;
    protected double addx, addy;

    private BufferedImage image;
    private Point baseOffset;
    private boolean cacheTooBig;
    private boolean needsRerender = false;
    protected boolean active = false;
    private double lastZoomX;
    private double lastZoomY;
    private double lastAngle;

    protected MouseHandlerLineTransform(CanvasModel canvasModel) {
        this.canvasModel = canvasModel;
    }

    @Override
    public void mousePressed(Point p0) {
        super.mousePressed(p0);

        addx = addy = 0;
        FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        Save save = new SaveV1();
        d.foldLineSet.getMemoSelectOption(save, 2);
        ori_s_temp.setSave(save);
        lines = ori_s_temp;
        active = true;
        needsRerender = true;
    }

    @Override
    public void mouseDragged(Point p0) {
        super.mouseDragged(p0);
        if (Epsilon.high.gt0(d.lineStep.get(0).determineLength())) {
            addx = -d.lineStep.get(0).determineBX() + d.lineStep.get(0).determineAX();
            addy = -d.lineStep.get(0).determineBY() + d.lineStep.get(0).determineAY();
        }
    }

    @Override
    public void mouseReleased(Point p0) {
        canvasModel.setSelectionOperationMode(CanvasModel.SelectionOperationMode.NORMAL_0);//  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        Point p = new Point();
        p.set(d.camera.TV2object(p0));
        d.lineStep.get(0).setA(p);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) <= d.selectionDistance) {
            d.lineStep.get(0).setA(closestPoint);
        }
        addx = -d.lineStep.get(0).determineBX() + d.lineStep.get(0).determineAX();
        addy = -d.lineStep.get(0).determineBY() + d.lineStep.get(0).determineAY();
        d.lineStep.clear();
        active = false;
        image = null;
        cacheTooBig = false;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        if (!active) {
            return;
        }
        needsRerender = needsRerender || updateNeedsRerender(camera, settings);
        if (updateNeedsRerender(camera, settings)) {
            cacheTooBig = false;
        }
        lastZoomX = camera.getCameraZoomX();
        lastZoomY = camera.getCameraZoomY();
        lastAngle = camera.getCameraAngle();


        if (needsRerender && lines != null && !cacheTooBig) {
            cacheTooBig = !initCacheImage(g2, settings);
            if (!cacheTooBig) {
                rerender(camera, settings);
                needsRerender = false;
            } else {
                needsRerender = true;
            }
        }

        if (image != null) {
            Point origin = camera.object2TV(new Point(0,0));
            Point delta = new Point(addx, addy);
            Point deltaTransformed = camera.object2TV(delta);
            g2.drawImage(image,
                    (int) (baseOffset.getX() + deltaTransformed.getX() - origin.getX()),
                    (int) (baseOffset.getY() + deltaTransformed.getY() - origin.getY()),
                    image.getWidth(), image.getHeight(), null);
        }
        if (cacheTooBig && lines != null) {
            drawDirect(g2, camera, settings);
        }
    }

    private boolean initCacheImage(Graphics2D g2, DrawingSettings settings) {
        int minX = (int) lines.get_x_min();
        int maxX = (int) lines.get_x_max() + 1;
        int minY = (int) lines.get_y_min();
        int maxY = (int) lines.get_y_max() + 1;

        int width = maxX - minX;
        int height = maxY - minY;
        boolean cacheTooBig = width * height < settings.getWidth() * settings.getHeight() * 1.5;
        if (!cacheTooBig) {
            image = g2.getDeviceConfiguration().createCompatibleImage(width, height, BufferedImage.BITMASK);
            baseOffset = new Point(minX, minY);
        }
        return !cacheTooBig;
    }

    protected void drawDirect(Graphics2D g2, Camera camera, DrawingSettings settings) {
        Point origin = camera.object2TV(new Point(0,0));
        Point delta = new Point(addx, addy);
        Point deltaTransformed = camera.object2TV(delta);

        int minx = (int) -(deltaTransformed.getX() - origin.getX());
        int miny = (int) -(deltaTransformed.getY() - origin.getY());
        int maxx = minx + settings.getWidth();
        int maxy = miny + settings.getHeight();

        // do cohen-sutherland clipping
        for (int i = 1; i <= lines.getTotal(); i++) {
            LineSegment s = lines.get(i);
            Point a = camera.object2TV(s.getA());
            Point b = camera.object2TV(s.getB());
            int regionA = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, a);
            int regionB = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, b);
            if ((regionA & regionB) == 0) {
                Point pa = new Point(s.getA());
                pa.move(delta);
                Point pb = new Point(s.getB());
                pb.move(delta);
                LineSegment s2 = new LineSegment();
                s2.set(s);
                s2.set(pa, pb);
                DrawingUtil.drawCpLine(g2, s2, camera, settings.getLineStyle(), settings.getLineWidth(),
                        d.pointSize, settings.getWidth(), settings.getHeight());
            }
        }
    }

    protected void rerender(Camera camera, DrawingSettings settings) {
        int width = image.getWidth();
        int height = image.getHeight();
        Point zero = camera.TV2object(new Point(0,0));
        Point boObject = camera.TV2object(baseOffset);
        FoldLineSet ori_s_temp = new FoldLineSet();
        ori_s_temp.set(lines);
        ori_s_temp.move(zero.getX() - boObject.getX(), zero.getY() - boObject.getY());
        Graphics2D g = image.createGraphics();
        g.setBackground(new Color(0f,0,0,0));
        for (int i = 1; i <= ori_s_temp.getTotal(); i++) {
            DrawingUtil.drawCpLine(g, ori_s_temp.get(i), camera, settings.getLineStyle(), settings.getLineWidth(), d.pointSize, width, height);
        }
    }

    protected boolean updateNeedsRerender(Camera camera, DrawingSettings settings) {
        return determineCameraChanged(camera);

    }

    private boolean determineCameraChanged(Camera camera) {
        return camera.getCameraZoomX() != lastZoomX || camera.getCameraZoomY() != lastZoomY || camera.getCameraAngle() != lastAngle;
    }
}
