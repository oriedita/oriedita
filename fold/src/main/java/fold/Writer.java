package fold;

import java.io.IOException;

public interface Writer<T> {
    void write(T save) throws IOException;
}
