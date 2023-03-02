package oriedita.editor.canvas.animation;

import java.util.function.Consumer;

public class Animation {
    private final Consumer<Double> setter;
    private final double from;
    private double to;
    private long startTime;
    private long duration;
    private long durationLeft;
    private double progress;
    private final Interpolation interpolation;

    public Animation(Consumer<Double> setter, double from, double to, long duration, Interpolation interpolation) {
        this.setter = setter;
        this.from = from;
        this.to = to;
        this.duration = duration*1000*1000;
        durationLeft = this.duration;
        this.interpolation = interpolation;
        startTime = -1;
        progress = -1;
    }

    public void start() {
        startTime = System.nanoTime();
        progress = 0;
    }

    public boolean isStarted() {
        return progress >= 0;
    }

    public boolean isFinished() {
        return progress > 1;
    }

    public boolean update() {
        if (!isStarted() || isFinished()) {
            return false;
        }
        long currentTime = System.nanoTime() - startTime;
        progress =  currentTime*1.0 / duration;
        durationLeft = duration - currentTime;
        setter.accept(interpolation.interpolate(from, to, Math.max(Math.min(1, progress), 0)));
        return true;
    }

    public void setTo(double to) {
        this.to = to;
    }

    public void addTime(long duration) {
        this.duration = durationLeft + duration;
    }

    @Override
    public String toString() {
        return "Animation{" +
                "from=" + from +
                ", to=" + to +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", durationLeft=" + durationLeft +
                ", progress=" + progress +
                '}';
    }
}
