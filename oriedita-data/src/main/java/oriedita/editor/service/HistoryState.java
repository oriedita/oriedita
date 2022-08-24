package oriedita.editor.service;

import oriedita.editor.save.Save;

import java.beans.PropertyChangeListener;

public interface HistoryState {
    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void reset();

    boolean isEmpty();

    void record(Save s0);

    boolean canUndo();

    boolean canRedo();

    Save undo();

    Save redo();
}
