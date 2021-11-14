package origami_editor.editor.service;


import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import javax.swing.*;

@Singleton
public class FrameFactory {
    @Produces
    @Singleton
    @Named("mainFrame")
    public JFrame mainFrame() {
        return new JFrame();
    }
}
