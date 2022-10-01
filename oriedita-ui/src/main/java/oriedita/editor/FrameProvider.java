package oriedita.editor;

import jakarta.enterprise.context.ApplicationScoped;

import javax.swing.*;

/**
 * Provides a pointer to the main JFrame.
 */
@ApplicationScoped
public class FrameProvider {
    private volatile JFrame frame;

    private static final Object lock = new Object();

    public JFrame get() {
        if (frame == null) {
            synchronized (lock) {
                if (frame == null) {
                    frame = new JFrame();
                }
            }
        }

        return frame;
    }
}
