package origami_editor.editor.factory;


import dagger.Module;
import dagger.Provides;

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
}
