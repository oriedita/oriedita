package fold;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Reader<T> {
    T read(InputStream inputStream) throws IOException;

    default T read(File file) throws IOException {
        return read(new FileInputStream(file));
    }
}
