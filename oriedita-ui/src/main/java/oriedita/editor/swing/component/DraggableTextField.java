package oriedita.editor.swing.component;

import javax.swing.JTextField;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DraggableTextField extends JTextField {

    private Point lastPos;
    private Point startPos;
    private final int fineMultiplier = 5;
    private final List<Consumer<Integer>> listeners = new ArrayList<>();
    private boolean fine;

    public DraggableTextField(int tickDistance) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPos = e.getPoint();
                startPos = e.getPoint();
                e.consume(); // prevents the text field from getting focus at mousedown,
                             // which would override dragging behavior with selecting text
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isFocusOwner()) {
                    return;
                }
                if (e.isShiftDown()) {
                    fine = true;
                } else {
                    fine = false;
                }
                var newPos = e.getPoint();
                var deltaX = newPos.x - startPos.x;
                var lastDeltaX = lastPos.x - startPos.x;
                var valX = deltaX / (tickDistance* (fine? fineMultiplier:1));
                var lastValX = lastDeltaX / (tickDistance* (fine? fineMultiplier:1));
                var deltaVal = valX - lastValX;
                if (deltaVal != 0) {
                    listeners.forEach(l -> l.accept(deltaVal));
                }

                lastPos = newPos;
            }
        });
    }

    /**
     * adds a listener to the drag event. fires when a distance of at least tickDistance is dragged across, triggering the listener
     * with with how many ticks were dragged across (sign indicates the drag direction).
     * @param listener listener to be called when dragging
     */
    public void onDrag(Consumer<Integer> listener) {
        listeners.add(listener);
    }
}
