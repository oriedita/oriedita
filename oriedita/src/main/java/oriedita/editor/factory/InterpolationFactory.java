package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.canvas.animation.EaseOutInterpolation;
import oriedita.editor.canvas.animation.Interpolation;

public class InterpolationFactory {
    @Named("default_animation_interpolation")
    @Produces
    @ApplicationScoped
    public static Interpolation defaultInterpolation() {
        return new EaseOutInterpolation();
    }
}
