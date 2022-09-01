package fold.impl;

import fold.Exporter;
import fold.FoldFileFormatException;
import fold.json.Fold;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fold.json.Fold.json;

public class DefaultExporter<T extends FoldFile> implements Exporter<T> {
    @Override
    public void exportFile(File file, T foldFile) throws FoldFileFormatException {
        try {
            Files.writeString(file.toPath(), json.asString(foldFile));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
