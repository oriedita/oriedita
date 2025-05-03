package oriedita.editor.swing.component;

import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DraggableTextField extends JTextField {

    private Point lastPos;
    private Point startPos;
    private final List<Consumer<Integer>> tickListeners = new ArrayList<>();
    private final List<BiConsumer<Integer, Boolean>> rawListeners = new ArrayList<>();
    private boolean fine;
    private boolean dragged = false;
    private final Timer timer;
    private final int tickDistance;
    private boolean shiftDown = false;

    public DraggableTextField() {this(3);}

    public DraggableTextField(int tickDistance) {
        this.tickDistance = tickDistance;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            shiftDown = e.isShiftDown();
            return false;
        });
        // when the mouse is outside the component, onDrag events slow down which looks like lagging.
        // the timer makes sure the mouse position is updated frequently enough.
        timer = new Timer(20, (e) -> {
            var mouseLoc = MouseInfo.getPointerInfo().getLocation();
            var loc = getLocationOnScreen();
            mouseLoc.translate(-loc.x, -loc.y);
            mouseDragged(mouseLoc, shiftDown);
        });
        timer.setRepeats(true);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                selectAll();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragged = false;
                lastPos = e.getPoint();
                startPos = e.getPoint();
                e.consume(); // prevents the text field from getting focus at mousedown,
                             // which would override dragging behavior with selecting text
                timer.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragged) {
                    e.consume();
                }
                timer.stop();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DraggableTextField.this.mouseDragged(e.getPoint(), e.isShiftDown());
            }
        });
    }

    private void mouseDragged(Point p, boolean shiftDown) {
        if (isFocusOwner()) {
            return;
        }
        if (shiftDown) {
            fine = true;
        } else {
            fine = false;
        }
        dragged = true;
        var newPos = p;
        var deltaX = newPos.x - startPos.x;
        var lastDeltaX = lastPos.x - startPos.x;
        rawListeners.forEach(l -> l.accept(newPos.x - lastPos.x, fine));
        int fineMultiplier = 5;
        var valX = deltaX / (tickDistance* (fine? fineMultiplier :1));
        var lastValX = lastDeltaX / (tickDistance* (fine? fineMultiplier :1));
        var deltaVal = valX - lastValX;
        if (deltaVal != 0) {
            tickListeners.forEach(l -> l.accept(deltaVal));
        }

        lastPos = newPos;
    }

    /**
     * adds a listener to the drag event. fires when a distance of at least tickDistance is dragged across, triggering the listener
     * with with how many ticks were dragged across (sign indicates the drag direction).
     *
     * e.g. if a tick is 5 pixels and the mouse was dragged 16 pixels to the left, the listener would fire with -3.
     * @param listener listener to be called when dragging
     */
    public void addTickListener(Consumer<Integer> listener) {
        tickListeners.add(listener);
    }

    /**
     * adds a listener to the drag event. Fires when the mouse is dragged, triggering the listener with how many pixels the mouse was dragged
     * (sign indicates drag direction), and whether fine mode is activated.
     * @param listener listener to be called when dragging
     */
    public void addRawListener(BiConsumer<Integer, Boolean> listener) {
        rawListeners.add(listener);
    }
}
