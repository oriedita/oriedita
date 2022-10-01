package fold.io;

import fold.Reader;
import fold.model.FoldFile;

import java.io.IOException;
import java.io.InputStream;

import static fold.json.Fold.json;

public class CustomReader<T extends FoldFile> implements Reader<T> {
    private final Class<T> tClass;

    public CustomReader(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T read(InputStream inputStream) throws IOException {
        return json.beanFrom(tClass, inputStream);
    }
}
