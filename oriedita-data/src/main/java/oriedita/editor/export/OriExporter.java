package oriedita.editor.export;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.export.api.FileExporter;
import oriedita.editor.json.DefaultObjectMapper;
import oriedita.editor.save.Save;

import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class OriExporter implements FileExporter {
    @Override
    public void doExport(Save save, File file) throws IOException {
        new DefaultObjectMapper().writeValue(file, save);
    }

    @Override
    public String getName() {
        return "Ori";
    }

    @Override
    public String getExtension() {
        return ".ori";
    }

    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".ori");
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
