package fold;

import java.io.IOException;

public class FoldFileFormatException extends IOException {
    public FoldFileFormatException(String message) {
        super(message);
    }

    public FoldFileFormatException(Throwable cause) {
        super(cause);
    }
}
