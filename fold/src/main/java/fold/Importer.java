package fold;

import java.io.File;

public interface Importer<T> {
    T importFile(File file) throws FoldFileFormatException;
}
