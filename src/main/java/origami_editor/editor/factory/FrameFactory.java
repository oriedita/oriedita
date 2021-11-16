package origami_editor.editor.factory;


import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import origami_editor.editor.service.ResetService;
import origami_editor.editor.service.ResetServiceImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import javax.swing.*;

@Module
public abstract class FrameFactory {
    @Provides
    @Singleton
    @Named("mainFrame")
    public static JFrame mainFrame() {
        return new JFrame();
    }

    @Binds
    abstract ResetService resetService(ResetServiceImpl resetService);
}
