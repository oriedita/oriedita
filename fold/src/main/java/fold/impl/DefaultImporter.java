package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.FoldFileFormatException;
import fold.Importer;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;
import fold.model.internal.InternalFoldFile;

import java.io.File;
import java.io.IOException;

public class DefaultImporter implements Importer<FoldFile> {
    @Override
    public FoldFile importFile(File file) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            return AdapterFactory.getFoldFileAdapter()
                    .convert(mapper.readValue(file, InternalFoldFile.class));
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
