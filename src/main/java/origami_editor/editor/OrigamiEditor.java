package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import origami_editor.editor.service.ApplicationModelPersistenceService;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class OrigamiEditor {
    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");


        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext("origami_editor.editor");
        App app = context.getBean(App.class);

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("origami_editor.editor.themes");

            try {
                UIManager.setLookAndFeel(app.applicationModel.getLaf());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            app.start();

            if (argv.length == 1) {
                // We got a file
                app.fileSaveService.openFile(new File(argv[0]));
            }
        });
    }
}
