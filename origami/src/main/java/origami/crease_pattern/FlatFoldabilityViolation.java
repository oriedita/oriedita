package origami.crease_pattern;

import origami.crease_pattern.element.LineSegment;

import java.util.LinkedHashMap;
import java.util.Map;

public class FlatFoldabilityViolation {
    private final Rule violatedRule;
    private final Color color;
    private final LineSegment[] lineSegments;
    private final boolean[] violatingLBL;

    public FlatFoldabilityViolation(Rule violatedRule, Color color) {
        this(violatedRule, color, null);
    }

    public FlatFoldabilityViolation(Rule violatedRule, Color color, LinkedHashMap<LineSegment, Boolean> littleBigLittleViolations) {
        this.violatedRule = violatedRule;
        this.color = color;
        if (littleBigLittleViolations != null) {
            lineSegments = new LineSegment[littleBigLittleViolations.size()];
            violatingLBL = new boolean[littleBigLittleViolations.size()];

            int i = 0;
            for (Map.Entry<LineSegment, Boolean> entry : littleBigLittleViolations.entrySet()) {
                lineSegments[i] = entry.getKey();
                violatingLBL[i] = entry.getValue();
                i++;
            }
        } else {
            lineSegments = null;
            violatingLBL = null;
        }
    }

    public Rule getViolatedRule() {
        return violatedRule;
    }

    public Color getColor() {
        return color;
    }

    public LineSegment[] getLineSegments() {
        return lineSegments;
    }
    public boolean[] getViolatingLBL() {
        return violatingLBL;
    }

    public enum Rule {
        NUMBER_OF_FOLDS, ANGLES, MAEKAWA, LITTLE_BIG_LITTLE, NONE
    }
    public enum Color {
        NOT_ENOUGH_MOUNTAIN, NOT_ENOUGH_VALLEY, EQUAL, CORRECT
    }
}
