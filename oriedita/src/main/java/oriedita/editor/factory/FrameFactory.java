package oriedita.editor.factory;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import javax.swing.*;

@ApplicationScoped
public class FrameFactory {
    @Produces
    @Singleton
    @Named("mainFrame")
    public static JFrame mainFrame() {
        return new JFrame();
    }
}
