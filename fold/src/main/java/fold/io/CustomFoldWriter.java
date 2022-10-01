package fold.io;

import fold.FoldFileFormatException;
import fold.Writer;
import fold.model.FoldFile;

import java.io.*;

import static fold.json.Fold.json;

public class CustomFoldWriter<T extends FoldFile> implements Writer<T> {
    private final OutputStream outputStream;

    public CustomFoldWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(T foldFile) throws FoldFileFormatException {
        try {
            json.write(foldFile, outputStream);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
