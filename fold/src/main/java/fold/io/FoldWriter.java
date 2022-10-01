package fold.io;

import fold.model.FoldFile;

import java.io.OutputStream;

public class FoldWriter extends CustomFoldWriter<FoldFile>  {
    public FoldWriter(OutputStream outputStream) {
        super(outputStream);
    }
}
