package fold;

import fold.model.internal.FoldFile;

import java.io.File;

public interface Exporter {
    void exportFile(File file, FoldFile save) throws FoldFileFormatException;
}
