package fold;

import java.io.File;

public interface Exporter<T> {
    void exportFile(File file, T save) throws FoldFileFormatException;
}
