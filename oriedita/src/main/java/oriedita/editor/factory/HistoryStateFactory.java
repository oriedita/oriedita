package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.impl.DequeHistoryState;

public class HistoryStateFactory {
    @Produces
    @ApplicationScoped
    @Named("normal")
    public HistoryState normalHistoryState() {
        return new DequeHistoryState();
    }

    @Produces
    @ApplicationScoped
    @Named("aux")
    public HistoryState auxHistoryState() {
        return new DequeHistoryState();
    }
}
