package fold;

import fold.model.FoldFile;

import java.io.File;

public interface FoldImport {
    FoldFile importFoldFile(File file) throws FoldFileFormatException;
}
