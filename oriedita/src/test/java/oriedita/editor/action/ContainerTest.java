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
import oriedita.editor.action2.ActionType;
import oriedita.editor.action2.OrieditaAction;
import oriedita.editor.canvas.MouseMode;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ExtendWith(WeldJunit5Extension.class)
public class ContainerTest {

    public static final String TEST_CONTAINER_ID = "TEST123";
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.of(new Weld().containerId(TEST_CONTAINER_ID));

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

    private <T, V> void assertUniqueByField(List<T> actions, Function<T, V> getField) {
        for (T action : actions) {
            List<T> equalActions = actions.stream().filter(b -> getField.apply(b).equals(getField.apply(action))).collect(Collectors.toList());
            if (equalActions.size() > 1) {
                V actionType = getField.apply(equalActions.get(0));
                String actionClasses = equalActions.stream().map(Object::toString).collect(Collectors.joining(", "));
                Assertions.fail(MessageFormat.format("Multiple handlers found for {0}: {1}", actionType, actionClasses));
            }
        }
    }

    /**
     * Asserts if the implementation of actions is equal to the values in the OrieditaActionType enum.
     */
    @Test
    public void testAllActions() {
        Instance<OrieditaAction> instances = weld.select(OrieditaAction.class, new Any.Literal());

        try {
            Set<ActionType> implementedHandlers = instances.stream()
                    .map(OrieditaAction::getActionType)
                    .collect(Collectors.toSet());

            assertUniqueByField(instances.stream().collect(Collectors.toList()), OrieditaAction::getActionType);

            assertSetEquality(Set.of(ActionType.values()), implementedHandlers);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

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

            assertUniqueByField(instances.stream().collect(Collectors.toList()), MouseModeHandler::getMouseMode);

            assertSetEquality(Set.of(MouseMode.values()), implementedHandlers);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}
