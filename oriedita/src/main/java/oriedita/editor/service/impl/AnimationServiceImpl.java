package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import oriedita.editor.canvas.animation.Animation;
import oriedita.editor.canvas.animation.Interpolation;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.service.AnimationService;
import origami.crease_pattern.element.Point;

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

        Animation a = new Animation(setter, from, to, (long) (time*1000*applicationModel.getAnimationSpeed()), interpolation);

        if (animations.containsKey(key)) {
            animations.get(key).combine(a);
        } else {
            animations.put(key, a);
        }
        isAnimating = true;
    }

    @Override
    public Interpolation getDefaultInterpolation() {
        return defaultInterpolation;
    }

    @Override
    public void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, UnaryOperator<Point> calculateEndValue, double time, Interpolation interpolation) {
        Point current = getter.get();

        if (animations.containsKey(key+"_x")) {
            current.setX(animations.get(key+"_x").getTo());
        }
        if (animations.containsKey(key+"_y")) {
            current.setY(animations.get(key+"_y").getTo());
        }
        Point to = calculateEndValue.apply(current);

        if (!applicationModel.getAnimations()) {
            setter.accept(to);
            return;
        }
        animate(key+"_x", x -> {
            Point newP = new Point();
            newP.set(getter.get());
            newP.setX(x);
            setter.accept(newP);
        }, () -> getter.get().getX(), to.getX(), time, interpolation);
        animate(key+"_y", y -> {
            Point newP = new Point();
            newP.set(getter.get());
            newP.setY(y);
            setter.accept(newP);
        }, () -> getter.get().getY(), to.getY(), time, interpolation);
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
}
