package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Polygon;

@ApplicationScoped
@Handles(MouseMode.UNSELECT_POLYGON_67)
public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    @Inject
    public MouseHandlerUnselectPolygon() {}

    @Override
    protected void performAction() {
        Polygon polygon = new Polygon(d.getLineStep().stream().map(LineSegment::getA).toList());
        d.getFoldLineSet().select_Takakukei(polygon, "unselectAction");
    }
}
