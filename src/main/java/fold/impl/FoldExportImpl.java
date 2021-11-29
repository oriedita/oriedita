package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.FoldExport;
import fold.FoldFileFormatException;
import fold.FoldFileProcessor;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;

public class FoldExportImpl implements FoldExport {
    private final FoldFileProcessor processor;

    public FoldExportImpl(FoldFileProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void exportFoldFile(File file, FoldFile foldFile) throws FoldFileFormatException {
        processor.process(foldFile);
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            mapper.writeValue(file, foldFile);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
