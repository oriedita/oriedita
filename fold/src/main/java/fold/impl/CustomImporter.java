package fold.impl;

import fold.FoldFileFormatException;
import fold.Importer;
import fold.json.Fold;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fold.json.Fold.json;

public class CustomImporter<T extends FoldFile> implements Importer<T> {
    private final Class<T> tClass;

    public CustomImporter(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T importFile(File file) throws FoldFileFormatException {
        try {
            return json.beanFrom(tClass, Files.newBufferedReader(file.toPath()));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
