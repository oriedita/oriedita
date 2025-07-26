package oriedita.editor.handler.step;

import oriedita.editor.handler.MouseModeHandler;
import origami.crease_pattern.element.Point;

/**
 * represents a Node in a StepGraph[T]. The actions of the step are always run in a fixed order:
 * <p>runHighlightSelection: always run as soon as the step is activated. Runs at least once before runPressAction.</p>
 * <p>
 * runPressAction: is run exactly once when the mouse is pressed. after this, runDragAction may be ran multiple times.
 * If runPressAction returns an Enum step corresponding to a different node, runDragAction and runReleaseAction will not be run.
 * </p>
 * <p>
 * runDragAction: can be run between runPressAction and runReleaseAction. It is also possible for this to never be executed
 * if the mouse is not moved between pressing and releasing
 * </p>
 * <p>
 *     runReleaseAction: is run exactly once when the mouse is released
 * </p>
 *
 * @param <T>
 */
public interface IStepNode<T extends Enum<T>> {
    T getStep();

    void runHighlightSelection(Point mousePos);
    T runPressAction(Point mousePos, MouseModeHandler.Feature mouseButton);
    void runDragAction(Point mousePos);
    T runReleaseAction(Point mousePos);
}
