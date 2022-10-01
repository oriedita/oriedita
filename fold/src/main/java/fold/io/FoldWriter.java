package fold.io;

import fold.FoldFileFormatException;
import fold.Writer;
import fold.model.FoldFile;

import java.io.IOException;
import java.io.OutputStream;

import static fold.json.Fold.json;

public class FoldWriter<T extends FoldFile> implements Writer<T> {
    @Override
    public void write(OutputStream file, T foldFile) throws FoldFileFormatException {
        try {
            json.write(foldFile, file);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
