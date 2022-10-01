package oriedita.editor.action;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import oriedita.editor.canvas.MouseMode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(WeldJunit5Extension.class)
public class ContainerTest {

    public static final String TEST_CONTAINER_ID = "TEST123";

    private <T> void assertSetEquality(Set<T> options, Set<T> implementations) {
        List<T> differences = implementations.stream().sorted()
                .filter(element -> !options.contains(element))
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(), differences, "Too many implementations");

        List<T> differences2 = options.stream().sorted()
                .filter(element -> !implementations.contains(element))
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of(), differences2, "Too many options");

        Assertions.assertEquals(options, implementations);
    }

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.of(new Weld().containerId(TEST_CONTAINER_ID));

    /**
     * Asserts if the implementations of mousehandlers actually is equal to the values in the MouseMode enum.
     */
    @Test
    public void testAllMouseHandlers() {
        Instance<MouseModeHandler> instances = weld.select(MouseModeHandler.class, new Any.Literal());

        try {
            Set<MouseMode> implementedHandlers = instances.stream()
                    .map(MouseModeHandler::getMouseMode)
                    .collect(Collectors.toSet());

            assertSetEquality(Set.of(MouseMode.values()), implementedHandlers);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
