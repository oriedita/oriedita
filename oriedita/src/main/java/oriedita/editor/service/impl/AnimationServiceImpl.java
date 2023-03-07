package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.animation.Animation;
import oriedita.editor.canvas.animation.Interpolation;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.service.AnimationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@ApplicationScoped
public class AnimationServiceImpl implements AnimationService {
    ApplicationModel applicationModel;
    private final Map<String, Animation> animations;
    private boolean isAnimating;
    private final Interpolation defaultInterpolation;

    @Inject
    public AnimationServiceImpl(ApplicationModel applicationModel,
                                @Named("default_animation_interpolation") Interpolation defaultInterpolation) {
        this.applicationModel = applicationModel;
        this.defaultInterpolation = defaultInterpolation;
        animations = new HashMap<>();
    }

    @Override
    public synchronized void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time, Interpolation interpolation) {
        double from = getter.get();
        double current = from;
        if (animations.containsKey(key)) {
            current = animations.get(key).getTo();
        }
        double to = calculateEndValue.apply(current);
        if (!applicationModel.getAnimations()) {
            setter.accept(to);
            return;
        }

        Animation a = new Animation(setter, from, to, (long) (time*1000), interpolation);

        if (animations.containsKey(key)) {
            animations.get(key).combine(a);
        } else {
            animations.put(key, a);
        }
        isAnimating = true;
    }

    @Override
    public void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time) {
        animate(key, setter, getter, calculateEndValue, time, defaultInterpolation);
    }

    synchronized public void update() {
        List<String> finishedN = new ArrayList<>();
        for (Map.Entry<String, Animation> animationEntry : animations.entrySet()) {
            Animation animation = animationEntry.getValue();
            if (!animation.update()){
                if (!animation.isStarted()) animation.start();
                else finishedN.add(animationEntry.getKey());
            }
        }
        for (String s : finishedN) {
            animations.remove(s);
        }
        isAnimating = !animations.isEmpty();
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public boolean isAnimating(String key) {
        return animations.containsKey(key);
    }
}
