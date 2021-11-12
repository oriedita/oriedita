package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import origami_editor.editor.service.ApplicationModelPersistenceService;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class OrigamiEditor {
    private static Object getBean(ContainerLifecycle lifecycle, Class<?> clazz) {
        BeanManager beanManager = lifecycle.getBeanManager();
        Bean<?> bean = beanManager.getBeans(clazz).iterator().next();

        return beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));
    }

    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        ContainerLifecycle lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        lifecycle.startApplication(null);

        // First restore the applicationModel from disk.
        ApplicationModelPersistenceService applicationModelPersistenceService = (ApplicationModelPersistenceService) getBean(lifecycle, ApplicationModelPersistenceService.class);
        applicationModelPersistenceService.restoreApplicationModel();

        // Start the app.
        App app = (App) getBean(lifecycle, App.class);

        SwingUtilities.invokeLater(() -> {
            FlatLaf.registerCustomDefaultsSource("origami_editor.editor.themes");

            app.start();

            if (argv.length == 1) {
                // We got a file
                app.fileSaveService.openFile(new File(argv[0]));
            }
        });
    }
}
