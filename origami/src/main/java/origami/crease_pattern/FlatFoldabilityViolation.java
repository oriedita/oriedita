package origami.crease_pattern;

import origami.crease_pattern.element.Point;

public class FlatFoldabilityViolation {
    private final Rule violatedRule;
    private final Color color;
    private final Point point;

    public FlatFoldabilityViolation(Point p, Rule violatedRule, Color color) {
        this.point = p;
        this.violatedRule = violatedRule;
        this.color = color;
    }

    public Rule getViolatedRule() {
        return violatedRule;
    }

    public Color getColor() {
        return color;
    }

    public Point getPoint() {
        return point;
    }

    public enum Rule {
        NUMBER_OF_FOLDS, ANGLES, MAEKAWA, LITTLE_BIG_LITTLE, NONE
    }
    public enum Color {
        NOT_ENOUGH_MOUNTAIN, NOT_ENOUGH_VALLEY, EQUAL, CORRECT, UNKNOWN
    }
}
