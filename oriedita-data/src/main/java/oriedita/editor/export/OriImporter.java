package oriedita.editor.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.export.api.FileImporter;
import oriedita.editor.json.DefaultObjectMapper;
import oriedita.editor.save.BaseSave;
import oriedita.editor.save.FileVersionTester;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveConverter;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class OriImporter implements FileImporter {
    private final FrameProvider frame;
    private boolean askOnUnknownFormat = true;

    @Inject
    public OriImporter(
            FrameProvider frame
    ) {
        this.frame = frame;
    }

    public OriImporter(FrameProvider frame, boolean askOnUnknownFormat) {
        this(frame);
        this.askOnUnknownFormat = askOnUnknownFormat;
    }
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".ori");
    }

    @Override
    public Save doImport(File file) throws IOException {
        ObjectMapper mapper = new DefaultObjectMapper();
        Save readSave = mapper.readValue(file, Save.class);
        FileVersionTester versionTester = mapper.readValue(file, FileVersionTester.class);
        if (readSave.getClass() == BaseSave.class && versionTester.getVersion() == null) { // happens when the version id is not recognized
            int result = JOptionPane.NO_OPTION;
            if (askOnUnknownFormat) {
                result = JOptionPane.showConfirmDialog(frame.get(), "This file was created using a newer version of oriedita.\n" +
                                "Using it with this version of oriedita might remove parts of the file.\n" +
                                "Do you want to open the file anyways?", "File created in newer version",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }

            switch (result) {
                case JOptionPane.YES_OPTION:
                    return SaveConverter.convertToNewestSave(readSave);
                case JOptionPane.NO_OPTION:
                    return null;
            }
        }
        return SaveConverter.convertToNewestSave(readSave);
    }
}
