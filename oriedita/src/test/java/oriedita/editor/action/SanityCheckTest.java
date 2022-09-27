package oriedita.editor.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.factory.DaggerAppFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SanityCheckTest {
    private void assertSetEquality(Set<String> options, Set<String> implementations) {
        List<String> differences = implementations.stream().sorted()
                .filter(element -> !options.contains(element))
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(), differences, "Too many implementations");

        List<String> differences2 = options.stream().sorted()
                .filter(element -> !implementations.contains(element))
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(), differences2, "Too many options");

        Assertions.assertEquals(options, implementations);
    }

    /**
     * Asserts if the implementations of mousehandlers actually is equal to the values in the MouseMode enum.
     */
    @Test
    public void testAllMouseHandlers() {
        try {
            Set<MouseModeHandler> handlers = DaggerAppFactory.create().handlers();
            Set<String> implementedHandlers = handlers.stream()
                    .map(MouseModeHandler::getMouseMode)
                    .map(MouseMode::toReadableString)
                    .collect(Collectors.toSet());
            Set<String> expectedHandlers = Arrays.stream(MouseMode.values())
                    .map(MouseMode::toReadableString)
                    .collect(Collectors.toSet());

            assertSetEquality(expectedHandlers, implementedHandlers);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
