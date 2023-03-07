package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.canvas.animation.Interpolation;
import oriedita.editor.canvas.animation.LinearInterpolation;

public class InterpolationFactory {
    @Named("default_animation_interpolation")
    @Produces
    @ApplicationScoped
    public static Interpolation defaultInterpolation() {
        return new LinearInterpolation();
    }
}
