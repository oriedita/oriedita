package oriedita.editor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import javax.swing.KeyStroke;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ButtonServiceTest {
    public static final String TEST_KEY = "test";
    public static final KeyStroke TEST_KEYSTROKE = KeyStroke.getKeyStroke("T");
    private ButtonService service;
    @BeforeEach
    void setUp() {
        service = createInstance();
    }

    public abstract ButtonService createInstance();

    @Test
    public void testSetKeyStroke() {
        JButton button = new JButton();
        service.registerButton(button, TEST_KEY);

        service.setKeyStroke(TEST_KEYSTROKE, TEST_KEY);

        // registered buttons with an associated Keystroke should appear in the helpInputMap
        assertSame(service.getHelpInputMap().get(TEST_KEYSTROKE), button);
        assertEquals(TEST_KEY, service.getActionFromKeystroke(TEST_KEYSTROKE));

        service.setKeyStroke(null, TEST_KEY);
        // buttons without Keystrokes need to be removed from the helpInputMap again
        assertFalse(service.getHelpInputMap().containsKey(TEST_KEYSTROKE));
        assertNull(service.getActionFromKeystroke(TEST_KEYSTROKE));
    }

    @Test
    public void testAddKeyStrokeChangeHandler() {
        JButton button = new JButton();
        service.registerButton(button, TEST_KEY);

        AtomicInteger listenerCallCount = new AtomicInteger(0);
        PropertyChangeListener listener = e -> {
            int callCount = listenerCallCount.incrementAndGet();
            assertEquals(e.getPropertyName(), TEST_KEY);
            switch (callCount) {
                case 1:
                    assertNull(e.getOldValue());
                    assertEquals(e.getNewValue(), TEST_KEYSTROKE);
                    break;
                case 2:
                    assertEquals(e.getOldValue(), TEST_KEYSTROKE);
                    assertNull(e.getNewValue());
                    break;
                default:
                    fail();
                    break;
            }
        };
        service.addKeystrokeChangeListener(listener);

        service.setKeyStroke(TEST_KEYSTROKE, TEST_KEY);
        assertEquals(1, listenerCallCount.get());

        service.setKeyStroke(null, TEST_KEY);
        assertEquals(2, listenerCallCount.get());
    }
}