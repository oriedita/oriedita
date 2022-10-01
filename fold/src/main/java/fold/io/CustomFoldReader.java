package fold.io;

import fold.FoldFileFormatException;
import fold.Reader;
import fold.model.FoldFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static fold.json.Fold.json;

public class CustomFoldReader<T extends FoldFile> implements Reader<T> {
    private final Class<T> tClass;
    private final InputStream inputStream;

    public CustomFoldReader(Class<T> tClass, File file) throws IOException{
        this(tClass, new FileInputStream(file));
    }

    public CustomFoldReader(Class<T> tClass, InputStream inputStream) {
        this.tClass = tClass;
        this.inputStream = inputStream;
    }

    @Override
    public T read() throws IOException {
        try {
            return json.beanFrom(tClass, inputStream);
        } catch (IOException e) {
            throw new FoldFileFormatException(e);
        }
    }
}
