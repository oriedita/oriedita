package fold.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fold.Exporter;
import fold.FoldFileFormatException;
import fold.json.FoldObjectMapper;
import fold.model.FoldFile;

import java.io.File;
import java.io.IOException;

public class DefaultExporter implements Exporter {
    @Override
    public void exportFile(File file, FoldFile foldFile) throws FoldFileFormatException {
        try {
            ObjectMapper mapper = new FoldObjectMapper();

            mapper.writeValue(file, foldFile);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
