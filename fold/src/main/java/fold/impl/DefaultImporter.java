package fold.impl;

import fold.FoldFileFormatException;
import fold.Importer;
import fold.json.Fold;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fold.json.Fold.json;

public class DefaultImporter implements Importer<FoldFile> {
    @Override
    public FoldFile importFile(File file) throws FoldFileFormatException {
        try {
            return json.beanFrom(FoldFile.class, Files.newBufferedReader(file.toPath()));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
