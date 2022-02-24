package origami.crease_pattern;

import origami.crease_pattern.element.LineSegment;

import java.util.Map;

public class FlatFoldabilityViolation {
    private final Rule violatedRule;
    private final Color color;
    private final Map<LineSegment, Boolean> littleBigLittleViolations;

    public FlatFoldabilityViolation(Rule violatedRule, Color color) {
        this(violatedRule, color, null);
    }

    public FlatFoldabilityViolation(Rule violatedRule, Color color, Map<LineSegment, Boolean> littleBigLittleViolations) {
        this.violatedRule = violatedRule;
        this.color = color;
        this.littleBigLittleViolations = littleBigLittleViolations;
    }

    public Rule getViolatedRule() {
        return violatedRule;
    }

    public Color getColor() {
        return color;
    }

    public Map<LineSegment, Boolean> getLittleBigLittleViolations() {
        return littleBigLittleViolations;
    }

    public enum Rule {
        NUMBER_OF_FOLDS, ANGLES, MAEKAWA, LITTLE_BIG_LITTLE, NONE
    }
    public enum Color {
        NOT_ENOUGH_MOUNTAIN, NOT_ENOUGH_VALLEY, EQUAL, CORRECT
    }
}
