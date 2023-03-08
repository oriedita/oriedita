package oriedita.editor.canvas.animation;

public interface Interpolation {
    double getAnimationProgress(double timeProgress);

    default double interpolate(double from, double to, double timeProgress) {
        double animationProgress = getAnimationProgress(timeProgress);
        return (1-animationProgress) * from + (animationProgress*to);
    }
}
