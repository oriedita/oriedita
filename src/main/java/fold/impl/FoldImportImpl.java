package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.FoldFileFormatException;
import fold.FoldFileProcessor;
import fold.FoldImport;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;

public class FoldImportImpl implements FoldImport {
    private final FoldFileProcessor processor;

    public FoldImportImpl(FoldFileProcessor processor) {
        this.processor = processor;
    }

    @Override
    public FoldFile importFoldFile(File file) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();
            FoldFile foldFile = mapper.readValue(file, FoldFile.class);

            processor.process(foldFile);

            return foldFile;
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
