package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.impl.DequeHistoryState;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class HistoryStateFactory {
    @Provides
    @Singleton
    @Named("normal")
    public static HistoryState normalHistoryState() {
        return new DequeHistoryState();
    }

    @Provides
    @Singleton
    @Named("aux")
    public static HistoryState auxHistoryState() {
        return new DequeHistoryState();
    }
}
