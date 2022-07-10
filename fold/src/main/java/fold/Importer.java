package fold;

import fold.model.FoldFile;

import java.io.File;

public interface Importer {
    FoldFile importFile(File file) throws FoldFileFormatException;
}
