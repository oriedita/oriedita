package oriedita.editor.canvas.animation;

public class LinearInterpolation implements Interpolation{
    @Override
    public double getAnimationProgress(double timeProgress) {
        return timeProgress;
    }
}
