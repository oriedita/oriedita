package origami.crease_pattern;

/**
 * Thrown when folding fails at an early stage.
 */
public class FoldingException extends Exception {
    public FoldingException(String message) {
        super(message);
    }
}
