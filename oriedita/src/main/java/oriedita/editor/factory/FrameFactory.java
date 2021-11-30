package oriedita.editor.factory;


import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import oriedita.editor.service.ResetService;
import oriedita.editor.service.ResetServiceImpl;

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
