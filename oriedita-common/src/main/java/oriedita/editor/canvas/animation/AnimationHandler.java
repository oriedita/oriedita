package oriedita.editor.canvas.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AnimationHandler {
    private final Map<String, Animation> names;
    private boolean isAnimating;
    private Interpolation defaultInterpolation;

    public AnimationHandler(Interpolation defaultInterpolation) {
        this.defaultInterpolation = defaultInterpolation;
        names = new HashMap<>();
    }

    public void animate(String name, Consumer<Double> setter, double from, double to, double time) {
        if (names.containsKey(name)) {
            names.get(name).setTo(to);
            names.get(name).addTime((long) (time*1000));
        } else {
            Animation a = new Animation(setter, from, to, (long) (time*1000), defaultInterpolation);
            names.put(name, a);
        }
        isAnimating = true;
    }

    public void update() {
        List<String> finishedN = new ArrayList<>();
        for (Map.Entry<String, Animation> animationEntry : names.entrySet()) {
            Animation animation = animationEntry.getValue();
            if (!animation.update()){
                if (!animation.isStarted()) animation.start();
                else finishedN.add(animationEntry.getKey());
            }
        }
        for (String s : finishedN) {
            names.remove(s);
        }
        isAnimating = !names.isEmpty();
    }

    public boolean animating() {
        return isAnimating;
    }
}
