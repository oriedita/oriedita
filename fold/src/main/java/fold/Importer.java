package fold;

import fold.model.internal.FoldFile;

import java.io.File;

public interface Importer {
    FoldFile importFile(File file) throws FoldFileFormatException;
}
