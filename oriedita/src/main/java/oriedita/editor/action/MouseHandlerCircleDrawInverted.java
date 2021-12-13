package oriedita.editor.action;

import oriedita.editor.action.selector.*;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.StraightLine;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerCircleDrawInverted extends BaseMouseHandler_WithSelector {
    LineOrCircleSelector first;
    LineOrCircleSelector fromCircle;
    ElementSelector<Circle> fromLine;

    @Inject
    public MouseHandlerCircleDrawInverted() {
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CIRCLE_DRAW_INVERTED_46;
    }

    @Override
    protected void setupSelectors() {
        first = registerStartingSelector(
                new LineOrCircleSelector(
                        new LineSelectorFromCollection(
                                d.foldLineSet::getLines,
                                LineColor.GREEN_6,
                                LineSelectorFromCollection.NoCloseLineValue.NONE
                        ),
                        new CircleSelectorFromIterable(
                                d.foldLineSet::getCircles,
                                (circle) -> LineColor.GREEN_6
                        )
                ), () -> {
                    if (first.getSelection() instanceof Circle) {
                        return fromCircle;
                    } else if (first.getSelection() instanceof LineSegment) {
                        return fromLine;
                    }
                    return null;
                }
        );
        fromCircle = registerSelector(
                new LineOrCircleSelector(
                    new CircleSelectorFromIterable(
                            d.foldLineSet::getCircles,
                            (c) -> LineColor.CYAN_3
                    ).thenGet(eh -> {
                        Circle e0 = (Circle) first.getSelection();
                        if (Math.abs(OritaCalc.distance(e0.determineCenter(), eh.determineCenter()) - e0.getR()) < Epsilon.UNKNOWN_1EN7) {
                            LineSegment s_add = eh.turnAround_CircleToLineSegment(e0);
                            s_add.setColor(LineColor.CYAN_3);
                            return s_add;
                        }
                        return null;
                    }, (e,g,c,s) -> DrawingUtil.drawLineStep(g,e,c,s)
                    ),
                    new CircleSelectorFromIterable(
                            d.foldLineSet::getCircles,
                            (c) -> LineColor.GREEN_6
                    ).then(c -> {
                        Circle c0 = (Circle) first.getSelection();
                        if (Math.abs(OritaCalc.distance(c0.determineCenter(), c.determineCenter()) - c0.getR()) >= Epsilon.UNKNOWN_1EN7) {
                            Circle e_add = c.turnAround(c0);
                            e_add.setColor(LineColor.CYAN_3);
                            return e_add;
                        }
                        return null;
                    }, true, true)
                ),
                null
        );
        fromCircle.onFinish(o -> {
            if (o instanceof Circle) {
                d.addCircle((Circle) o);
                d.record();
            } else if (o instanceof LineSegment) {
                d.addLineSegment((LineSegment) o);
                d.record();
            }
        });
        fromLine = registerSelector(
                new CircleSelectorFromIterable(
                        d.foldLineSet::getCircles,
                        c -> LineColor.GREEN_6
                ).then(c -> {
                    LineSegment s0 = (LineSegment) first.getSelection();
                    StraightLine ty = new StraightLine(s0);
                    //s0上に(x,y)がくるとき
                    if (ty.calculateDistance(c.determineCenter()) < Epsilon.UNKNOWN_1EN7) {
                        return null;
                    }

                    //s0が(x,y)を通らないとき。
                    Circle e_add = c.turnAround_LineSegmentToCircle(s0);
                    e_add.setColor(LineColor.CYAN_3);
                    return e_add;
                }, true, true),
                null
        );
        fromLine.onFinish(circle -> {
            d.addCircle(circle);
            d.record();
        });
        onAnyFail(this::reset, fromCircle, fromLine);
    }

    /*

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        Circle closest_circumference = new Circle(100000.0, 100000.0, 10.0, LineColor.PURPLE_8); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));

        if (d.lineStep.size() + d.circleStep.size() == 0) {
            LineSegment closestLineSegment = new LineSegment();
            closestLineSegment.set(d.getClosestLineSegment(p));

            if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) < OritaCalc.distance_circumference(p, closest_circumference)) {//線分の方が円周より近い
                if (OritaCalc.determineLineSegmentDistance(p, closestLineSegment) > d.selectionDistance) {
                    return;
                }

                closestLineSegment.setColor(LineColor.GREEN_6);
                d.lineStepAdd(closestLineSegment);
                return;
            }

            d.lineStep.clear();
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }

            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
            return;
        }

        if (d.lineStep.size() + d.circleStep.size() == 1) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.selectionDistance) {
                return;
            }
            d.circleStep.add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.RED_1));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.lineStep.size() == 1) && (d.circleStep.size() == 1)) {
            add_hanten(d.lineStep.get(0), d.circleStep.get(0));
            d.lineStep.clear();
            d.circleStep.clear();
        }

        if ((d.lineStep.size() == 0) && (d.circleStep.size() == 2)) {
            add_hanten(d.circleStep.get(0), d.circleStep.get(1));
            d.lineStep.clear();
            d.circleStep.clear();
        }
    }

    public void add_hanten(Circle e0, Circle eh) {
        //e0の円周が(x,y)を通るとき
        if (Math.abs(OritaCalc.distance(e0.determineCenter(), eh.determineCenter()) - e0.getR()) < Epsilon.UNKNOWN_1EN7) {
            LineSegment s_add = new LineSegment();
            s_add.set(eh.turnAround_CircleToLineSegment(e0));
            //s_add.setcolor(3);
            d.addLineSegment(s_add);
            d.record();
            return;
        }

        //e0の円周が(x,y)を通らないとき。e0の円周の外部に(x,y)がくるとき//e0の円周の内部に(x,y)がくるとき
        Circle e_add = new Circle();
        e_add.set(eh.turnAround(e0));
        d.addCircle(e_add);
        d.record();
    }

    public void add_hanten(LineSegment s0, Circle eh) {
        StraightLine ty = new StraightLine(s0);
        //s0上に(x,y)がくるとき
        if (ty.calculateDistance(eh.determineCenter()) < Epsilon.UNKNOWN_1EN7) {
            return;
        }

        //s0が(x,y)を通らないとき。
        Circle e_add = new Circle();
        e_add.set(eh.turnAround_LineSegmentToCircle(s0));
        d.addCircle(e_add);
        d.record();
    }
*/
}
