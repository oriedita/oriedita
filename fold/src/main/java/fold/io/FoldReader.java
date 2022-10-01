package fold.io;

import fold.model.FoldFile;

import java.io.InputStream;

public class FoldReader extends CustomFoldReader<FoldFile> {
    public FoldReader(InputStream inputStream) {
        super(FoldFile.class, inputStream);
    }
}
