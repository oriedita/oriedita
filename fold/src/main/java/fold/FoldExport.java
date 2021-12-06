package fold;

import fold.model.FoldFile;

import java.io.File;

public interface FoldExport {
    void exportFoldFile(File file, FoldFile save) throws FoldFileFormatException;
}
