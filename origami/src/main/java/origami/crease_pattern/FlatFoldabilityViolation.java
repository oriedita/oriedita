package origami.crease_pattern;

public class FlatFoldabilityViolation {
    private final Rule violatedRule;
    private final Color color;

    public FlatFoldabilityViolation(Rule violatedRule, Color color) {
        this.violatedRule = violatedRule;
        this.color = color;
    }

    public Rule getViolatedRule() {
        return violatedRule;
    }

    public Color getColor() {
        return color;
    }

    public enum Rule {
        NUMBER_OF_FOLDS, ANGLES, MAEKAWA, LITTLE_BIG_LITTLE, NONE
    }
    public enum Color {
        NOT_ENOUGH_MOUNTAIN, NOT_ENOUGH_VALLEY, EQUAL, CORRECT, UNKNOWN
    }
}
