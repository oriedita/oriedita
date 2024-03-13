package oriedita.editor.service;

import oriedita.editor.canvas.animation.Interpolation;
import origami.crease_pattern.element.Point;

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

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time) {
        animate(key, setter, getter, calculateEndValue, time, getDefaultInterpolation());
    }

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, double endValue, double time, Interpolation interpolation) {
        animate(key, setter, getter, n -> endValue, time, interpolation);
    }

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, double endValue, double time) {
        animate(key, setter, getter, n -> endValue, time);
    }

    Interpolation getDefaultInterpolation();

    /**
     *
     * @param key unique string identifying the animation. animations with the same key will overwrite each other
     * @param setter method to set the animated value
     * @param getter method to get the animated value
     * @param calculateEndPoint method to calculate the value that should be animated to, based on the current value (before the animation)
     * @param time time to animate
     * @param interpolation interpolation curve to use for the animation
     */
    void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, UnaryOperator<Point> calculateEndPoint, double time, Interpolation interpolation);

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, UnaryOperator<Point> calculateEndPoint, double time) {
        animatePoint(key, setter, getter, calculateEndPoint, time, getDefaultInterpolation());
    }

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, Point endPoint, double time, Interpolation interpolation) {
        animatePoint(key, setter, getter, p -> endPoint, time, interpolation);
    }

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, Point endPoint, double time) {
        animatePoint(key, setter, getter, endPoint, time, getDefaultInterpolation());
    }


    /**
     * updates all currently running animations and sets their respective properties to the correct value
     */
    void update();

    /**
     * @return true if any animation is currently running
     */
    boolean isAnimating();
}
