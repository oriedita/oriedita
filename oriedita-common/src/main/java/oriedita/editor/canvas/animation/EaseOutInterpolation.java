package oriedita.editor.canvas.animation;

public class EaseOutInterpolation implements Interpolation {

    @Override
    public double getAnimationProgress(double x) {
        return -x*x + 2*x;
    }
}
