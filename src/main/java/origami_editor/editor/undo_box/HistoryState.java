package origami_editor.editor.undo_box;

import origami_editor.editor.Save;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryState {
    int undoTotal = 1000;//Number of times you can undo up to how many times ago
    Deque<byte[]> history = new ArrayDeque<>();
    Deque<byte[]> future = new ArrayDeque<>();
    byte[] current;

    public HistoryState() {
    }

    public boolean isEmpty() {
        return history.isEmpty() && future.isEmpty() && current == null;
    }

    public void record(Save s0) {
        if (current != null) history.addFirst(current);
        try {
            current = convertToBytes(s0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Future becomes irrelevant
        future.clear();

        while (history.size() > undoTotal) {
            history.removeLast();
        }

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
        try {
            return (Save) convertFromBytes(current);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new Save();
    }

    public Save undo() {
        if (history.isEmpty()) {
            return getCurrent();
        }

        future.addFirst(current);
        current = history.removeFirst();

        return getCurrent();
    }

    public Save redo() {
        if (future.isEmpty()) {
            return getCurrent();
        }

        history.addFirst(current);
        current = future.removeFirst();

        return getCurrent();
    }

    public void setUndoTotal(int i_new) {
        undoTotal = i_new;

        while (history.size() > undoTotal) {
            history.removeLast();
        }

        while (future.size() > undoTotal) {
            future.removeLast();
        }
    }
}
