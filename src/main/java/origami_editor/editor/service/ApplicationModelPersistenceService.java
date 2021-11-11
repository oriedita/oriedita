package origami_editor.editor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import origami_editor.editor.databinding.ApplicationModel;
import origami_editor.editor.json.DefaultObjectMapper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static origami_editor.tools.ResourceUtil.getAppDir;

@Service
public class ApplicationModelPersistenceService {
    public static final String CONFIG_JSON = "config.json";

    private final JFrame frame;
    private final ApplicationModel applicationModel;

    public ApplicationModelPersistenceService(@Qualifier("mainFrame") JFrame frame, ApplicationModel applicationModel) {
        this.frame = frame;
        this.applicationModel = applicationModel;

        applicationModel.addPropertyChangeListener(e -> persistApplicationModel());
    }

    public void restoreApplicationModel() {
        Path storage = getAppDir();
        File configFile = storage.resolve(CONFIG_JSON).toFile();

        if (!configFile.exists()) {
            applicationModel.reset();

            return;
        }

        ObjectMapper mapper = new DefaultObjectMapper();

        try {
            ApplicationModel loadedApplicationModel = mapper.readValue(configFile, ApplicationModel.class);

            applicationModel.set(loadedApplicationModel);
        } catch (IOException e) {
            // An application state is found, but it is not valid.
            JOptionPane.showMessageDialog(frame, "<html>Failed to load application state.<br/>Loading default application configuration.", "State load failed", JOptionPane.WARNING_MESSAGE);

            if (!configFile.renameTo(storage.resolve(CONFIG_JSON + ".old").toFile())) {
                System.err.println("Not allowed to move config.json");
            }

            applicationModel.reset();
        }
    }

    public void persistApplicationModel() {
        Path storage = getAppDir();

        if (!storage.toFile().exists()) {
            if (!storage.toFile().mkdirs()) {
                System.err.println("Failed to create directory for application model");

                return;
            }
        }

        ObjectMapper mapper = new DefaultObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(storage.resolve(CONFIG_JSON).toFile(), applicationModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
