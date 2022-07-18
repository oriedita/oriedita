package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.FoldFileFormatException;
import fold.Importer;
import fold.adapter.FoldFileAdapter;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;

public class DefaultImporter implements Importer {
    @Override
    public FoldFile importFile(File file) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            return new FoldFileAdapter().convert(mapper.readValue(file, fold.model.internal.FoldFile.class));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
