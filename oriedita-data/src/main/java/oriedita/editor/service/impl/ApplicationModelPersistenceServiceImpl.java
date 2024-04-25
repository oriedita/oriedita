package oriedita.editor.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.util.zip.ZipInputStream;

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

    public void importApplicationModel(ZipInputStream zis){
        try {
            StringBuilder s = new StringBuilder();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                s.append(new String(buffer, 0, read));
            }

            ObjectMapper objectMapper = new DefaultObjectMapper();
            objectMapper.configure(SerializationFeature.CLOSE_CLOSEABLE, false);
            ApplicationModel loadedApplicationModel = objectMapper.readValue(s.toString(), ApplicationModel.class);
            applicationModel.set(loadedApplicationModel);
        } catch (JsonMappingException e) {
            // Can't map imported application state
            JOptionPane.showMessageDialog(frame.get(), "<html>Failed to map application state.", "State load failed", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // Imported application state isn't accessible
            JOptionPane.showMessageDialog(frame.get(), "<html>Failed to import application state.", "State load failed", JOptionPane.ERROR_MESSAGE);
            Logger.error(e);
        } catch (Exception e){
            Logger.error(e);
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
