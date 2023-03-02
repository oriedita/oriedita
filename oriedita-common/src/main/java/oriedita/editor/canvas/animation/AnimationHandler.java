package oriedita.editor.canvas.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnimationHandler {
    private final Map<String, Animation> animations;
    private boolean isAnimating;
    private Interpolation defaultInterpolation;

    public AnimationHandler(Interpolation defaultInterpolation) {
        this.defaultInterpolation = defaultInterpolation;
        animations = new HashMap<>();
    }

    public void animate(String name, Consumer<Double> setter, double from, double to, double time) {
        Animation a = new Animation(setter, from, to, (long) (time*1000), defaultInterpolation);

        if (animations.containsKey(name)) {
            animations.get(name).combine(a);
        } else {
            animations.put(name, a);
        }
        isAnimating = true;

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

    public boolean animating() {
        return isAnimating;
    }

    public boolean isAnimating(String key) {
        return animations.containsKey(key);
    }

    public double getFinalValueOr(String key, double defaultValue) {
        if (isAnimating(key)) {
            return animations.get(key).getTo();
        }
        return defaultValue;
    }
}
