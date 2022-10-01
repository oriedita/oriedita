package fold;

import java.io.IOException;

public interface Reader<T> {
    T read() throws IOException;
}
