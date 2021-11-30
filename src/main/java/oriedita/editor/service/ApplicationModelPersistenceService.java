package oriedita.editor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.json.DefaultObjectMapper;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static oriedita.editor.tools.ResourceUtil.getAppDir;

@Singleton
public class ApplicationModelPersistenceService {
    private static final Logger logger = LogManager.getLogger(ApplicationModelPersistenceService.class);
    public static final String CONFIG_JSON = "config.json";

    private final JFrame frame;
    private final ApplicationModel applicationModel;

    @Inject
    public ApplicationModelPersistenceService(@Named("mainFrame") JFrame frame, ApplicationModel applicationModel) {
        this.frame = frame;
        this.applicationModel = applicationModel;
    }

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
            JOptionPane.showMessageDialog(frame, "<html>Failed to load application state.<br/>Loading default application configuration.", "State load failed", JOptionPane.WARNING_MESSAGE);

            if (!configFile.renameTo(storage.resolve(CONFIG_JSON + ".old").toFile())) {
                logger.error("Not allowed to move config.json");
            }

            applicationModel.reset();
        }
    }

    public void persistApplicationModel() {
        Path storage = getAppDir();

        if (!storage.toFile().exists()) {
            if (!storage.toFile().mkdirs()) {
                logger.error("Failed to create directory for application model");

                return;
            }
        }

        ObjectMapper mapper = new DefaultObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(storage.resolve(CONFIG_JSON).toFile(), applicationModel);
        } catch (IOException e) {
            logger.error("Unable to write applicationModel to disk.", e);
        }
    }
}
