package oriedita.editor.service;

import oriedita.editor.canvas.animation.Interpolation;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface AnimationService {
    /**
     * animates a value from the current value (determined by applying getter) to a new value, which is
     * determined by applying calculateEndValue either on the current value, if no animation of the same key
     * is currently running; or on the endValue of the currently running animation of the same key if one exists.
     * the animation runs for time seconds, and the animated value is set using setter each time update is called.
     * @param key identifier of the animation, should be unique for each property
     * @param setter method to set the value of the animated property during the animation
     * @param getter method to get the starting value of the animated property, to determine the end value
     * @param calculateEndValue method to determine the end value of the animated property, is called with the current value
     *                          if no animation with the same key is running, or with the end value of the current animation
     *                          with the same key if one is running
     * @param time animation duration in seconds
     * @param interpolation interpolation to be used for the animation
     */
    void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time, Interpolation interpolation);

    void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time);

    /**
     * updates all currently running animations and sets their respective properties to the correct value
     */
    void update();

    /**
     * @return true if any animation is currently running
     */
    boolean isAnimating();
}
