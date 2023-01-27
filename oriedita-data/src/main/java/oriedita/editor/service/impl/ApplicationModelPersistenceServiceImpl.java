package oriedita.editor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.json.DefaultObjectMapper;
import oriedita.editor.service.ApplicationModelPersistenceService;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static oriedita.editor.tools.ResourceUtil.getAppDir;

@ApplicationScoped
public class ApplicationModelPersistenceServiceImpl implements ApplicationModelPersistenceService {

    public static final String CONFIG_JSON = "config.json";
    private final FrameProvider frame;
    private final ApplicationModel applicationModel;

    @Inject
    public ApplicationModelPersistenceServiceImpl(FrameProvider frame, ApplicationModel applicationModel) {
        this.frame = frame;
        this.applicationModel = applicationModel;
    }

    @Override
    public void init() {
        restoreApplicationModel();
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
            JOptionPane.showMessageDialog(frame.get(), "<html>Failed to load application state.<br/>Loading default application configuration.", "State load failed", JOptionPane.WARNING_MESSAGE);

            if (!configFile.renameTo(storage.resolve(CONFIG_JSON + ".old").toFile())) {
                Logger.error("Not allowed to move config.json");
            }

            applicationModel.reset();
        }
    }

    public void persistApplicationModel() {
        Path storage = getAppDir();

        if (!storage.toFile().exists()) {
            if (!storage.toFile().mkdirs()) {
                Logger.error("Failed to create directory for application model");

                return;
            }
        }

        ObjectMapper mapper = new DefaultObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            ApplicationModel tempApplicationModel = new ApplicationModel();
            tempApplicationModel.set(applicationModel);
            mapper.writeValue(storage.resolve(CONFIG_JSON).toFile(), tempApplicationModel);
        } catch (IOException e) {
            Logger.error(e, "Unable to write applicationModel to disk.");
        }
    }
}
