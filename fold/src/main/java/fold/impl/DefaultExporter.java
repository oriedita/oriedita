package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.Exporter;
import fold.FoldFileFormatException;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;
import fold.model.internal.InternalFoldFile;

import java.io.File;
import java.io.IOException;

public class DefaultExporter<T extends FoldFile> implements Exporter<T> {
    @Override
    public void exportFile(File file, T foldFile) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            mapper.writeValue(file, AdapterFactory.getFoldFileAdapter().convertBack(foldFile, new InternalFoldFile()));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
