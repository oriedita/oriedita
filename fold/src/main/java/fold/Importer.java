package fold;

import fold.model.FoldFile;

import java.io.File;

public interface Importer<T> {
    T importFile(File file) throws FoldFileFormatException;
}
