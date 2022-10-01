package fold;

import fold.io.FoldReader;
import fold.model.FoldFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public abstract class BaseFoldTest {
    FoldFile loadFile(String name) throws IOException {
        return new FoldReader(new FileInputStream(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).getFile())).read();
    }
}
