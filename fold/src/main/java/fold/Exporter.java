package fold;

import fold.model.FoldFile;

import java.io.File;

public interface Exporter {
    void exportFile(File file, FoldFile save) throws FoldFileFormatException;
}
