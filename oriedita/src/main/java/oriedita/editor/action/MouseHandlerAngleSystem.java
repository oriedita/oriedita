package oriedita.editor.action;

import oriedita.editor.action.selector.*;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.element.LineColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.EnumSet;

@Singleton
public class MouseHandlerAngleSystem extends BaseMouseHandler_WithSelector {
    CreasePatternPointSelector pStartSelector;
    AngleSystemLineSetCalculator lineSetCalculator;
    LineSelectorFromCollection candidateSelector;
    LineExtender finalLineSelector;

    @Inject
    public MouseHandlerAngleSystem() {
    }

    @Override
    public void setupSelectors() {
        pStartSelector = registerStartingSelector(
                new CreasePatternPointSelector(
                        CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_ONLY,
                        LineColor.RED_1
                ),
                () -> lineSetCalculator
        );
        lineSetCalculator = registerSelector(
                new AngleSystemLineSetCalculator(
                        pStartSelector::getSelection,
                        new CreasePatternPointSelector(
                                CreasePatternPointSelector.SelectionMode.CLOSEST_POINT_OR_FREE,
                                LineColor.BLUE_2
                        )
                ),
                () -> candidateSelector
        );
        candidateSelector = registerSelector(
                new LineSelectorFromCollection(
                        lineSetCalculator::getSelection,
                        LineColor.PURPLE_8,
                        LineSelectorFromCollection.NoCloseLineValue.NONE
                ),
                () -> finalLineSelector);
        candidateSelector.onFinish(lineSegment -> {
            lineSetCalculator.setHidden(true);
            candidateSelector.setHidden(true);
        });
        candidateSelector.onFail(this::reset);
        finalLineSelector = registerSelector(
                new LineExtender(
                        candidateSelector::getSelection,
                        d::getLineColor,
                        new LineSelectorFromCollection(
                                d.foldLineSet::getLines,
                                LineColor.GREEN_6,
                                LineSelectorFromCollection.NoCloseLineValue.MOUSE_POS
                        )
                ), null
        );
        finalLineSelector.onFinish(lineSegment -> {
            if (lineSegment != null && Epsilon.high.gt0(lineSegment.determineLength())) {
                d.addLineSegment(lineSegment);
                d.record();
            }
        });
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.ANGLE_SYSTEM_16;
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }
}
