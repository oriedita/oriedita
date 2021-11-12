package origami_editor.editor;

import com.formdev.flatlaf.FlatLaf;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import origami_editor.editor.databinding.ApplicationModel;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class OrigamiEditor {
    public static void main(String[] argv) throws InterruptedException, InvocationTargetException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        ContainerLifecycle lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        lifecycle.startApplication(null);

        BeanManager beanManager = lifecycle.getBeanManager();

        Bean<?> bean = beanManager.getBeans(App.class).iterator().next();

        App app = (App) lifecycle.getBeanManager().getReference(bean, App.class, beanManager.createCreationalContext(bean));

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
