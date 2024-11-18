package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Polygon;

@ApplicationScoped
@Handles(MouseMode.SELECT_POLYGON_66)
public class MouseHandlerSelectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerSelectPolygon() {}

    @Override
    protected void performAction() {
        Polygon polygon = new Polygon(d.getLineStep().stream().map(LineSegment::getA).toList());
        d.getFoldLineSet().select_Takakukei(polygon, "select");
    }
}
