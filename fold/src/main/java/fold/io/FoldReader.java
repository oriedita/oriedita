package fold.io;

import fold.FoldFileFormatException;
import fold.Reader;
import fold.model.FoldFile;

import java.io.IOException;
import java.io.InputStream;

import static fold.json.Fold.json;

public class FoldReader implements Reader<FoldFile> {
    @Override
    public FoldFile read(InputStream file) throws FoldFileFormatException {
        try {
            return json.beanFrom(FoldFile.class, file);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
