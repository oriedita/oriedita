package fold;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public interface Writer<T> {
    void write(OutputStream outputStream, T save) throws IOException;

    default void write(File file, T save) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            write(fileOutputStream, save);
        }
    }
}
