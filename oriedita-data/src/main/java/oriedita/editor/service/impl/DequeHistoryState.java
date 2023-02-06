package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.editor.service.HistoryState;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@ApplicationScoped
public class DequeHistoryState implements HistoryState {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    final int UNDO_TOTAL_MAX = 1000;
    int undoTotal = 50; // Number of times you can undo up to how many times ago
                        // is increased automatically up to UNDO_TOTAL_MAX if enough ram is available
    Deque<byte[]> history = new ArrayDeque<>();
    Deque<byte[]> future = new ArrayDeque<>();
    byte[] current;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.pcs.addPropertyChangeListener(propertyChangeListener);
    }

    @Inject
    public DequeHistoryState() {
    }

    @Override
    public void reset() {
        history.clear();
        future.clear();
        current = null;

        this.pcs.firePropertyChange(null, null, null);
    }

    @Override
    public boolean isEmpty() {
        return history.isEmpty() && future.isEmpty() && current == null;
    }

    @Override
    public void record(Save s0) {
        if (current != null) history.addFirst(current);
        updateUndoTotal();
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

    private void updateUndoTotal() {
        long entrySize = 0;
        if (current != null) {
            entrySize = current.length;
        }
        entrySize = Math.max(entrySize, 100000);
        long freeMemory = Runtime.getRuntime().freeMemory();
        Logger.info("Free Memory: {}, Last undo entry size: {}, max undo entries: {}, current undo entries: {}",
                freeMemory, entrySize, undoTotal, history.size());
        if (freeMemory < entrySize*30) {
            undoTotal = (int) (undoTotal * 0.9) + 2; // never goes below 20
        } else if ( undoTotal < UNDO_TOTAL_MAX && freeMemory > entrySize*80) {
            undoTotal += 2;
        }
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DeflaterOutputStream comp = new DeflaterOutputStream(bos);
             ObjectOutputStream out = new ObjectOutputStream(comp)) {
            out.writeObject(object);
            out.close();
            return bos.toByteArray();
        }
    }
    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             InflaterInputStream decomp = new InflaterInputStream(bis);
             ObjectInputStream in = new ObjectInputStream(decomp)) {
            return in.readObject();
        }
    }

    @Override
    public boolean canUndo() {
        return !history.isEmpty();
    }

    @Override
    public boolean canRedo() {
        return !future.isEmpty();
    }

    private Save getCurrent() {
        if (current == null) {
            return SaveProvider.createInstance();
        }

        try {
            return (Save) convertFromBytes(current);
        } catch (IOException | ClassNotFoundException e) {
            Logger.error(e, "Restoring current save failed");
        }

        return SaveProvider.createInstance();
    }

    @Override
    public Save undo() {
        if (history.isEmpty()) {
            return getCurrent();
        }

        future.addFirst(current);
        current = history.removeFirst();

        this.pcs.firePropertyChange(null, null, null);

        return getCurrent();
    }

    @Override
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
