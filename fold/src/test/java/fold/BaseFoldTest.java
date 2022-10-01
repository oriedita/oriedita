package fold;

import fold.io.FoldWriter;
import fold.io.FoldReader;
import fold.model.FoldFile;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class BaseFoldTest {
    Reader<FoldFile> reader;
    Writer<FoldFile> writer;

    @BeforeEach
    void beforeEach() {
        reader = new FoldReader();
        writer = new FoldWriter<>();
    }

    FoldFile loadFile(String name) throws IOException {
        return reader.read(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).getFile()));
    }
}
