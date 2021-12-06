package oriedita.editor.service;

import org.tinylog.Logger;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveV1;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

@Singleton
public class HistoryState {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    int undoTotal = 1000;//Number of times you can undo up to how many times ago
    Deque<byte[]> history = new ArrayDeque<>();
    Deque<byte[]> future = new ArrayDeque<>();
    byte[] current;

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.pcs.addPropertyChangeListener(propertyChangeListener);
    }

    @Inject
    public HistoryState() {
    }

    public void reset() {
        history.clear();
        future.clear();
        current = null;

        this.pcs.firePropertyChange(null, null, null);
    }

    public boolean isEmpty() {
        return history.isEmpty() && future.isEmpty() && current == null;
    }

    public void record(Save s0) {
        if (current != null) history.addFirst(current);
        try {
            current = convertToBytes(s0);
        } catch (IOException e) {
            Logger.error(e, "Saving current state failed");
        }

        // Future becomes irrelevant
        future.clear();

        while (history.size() > undoTotal) {
            history.removeLast();
        }

        this.pcs.firePropertyChange(null, null, null);
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public boolean canRedo() {
        return !future.isEmpty();
    }

    private Save getCurrent() {
        if (current == null) {
            return new SaveV1();
        }

        try {
            return (Save) convertFromBytes(current);
        } catch (IOException | ClassNotFoundException e) {
            Logger.error(e, "Restoring current save failed");
        }

        return new SaveV1();
    }

    public Save undo() {
        if (history.isEmpty()) {
            return getCurrent();
        }

        future.addFirst(current);
        current = history.removeFirst();

        this.pcs.firePropertyChange(null, null, null);

        return getCurrent();
    }

    public Save redo() {
        if (future.isEmpty()) {
            return getCurrent();
        }

        history.addFirst(current);
        current = future.removeFirst();

        this.pcs.firePropertyChange(null, null, null);

        return getCurrent();
    }
}
