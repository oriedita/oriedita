package oriedita.editor.service.impl;

import oriedita.editor.FrameProviderImpl;
import oriedita.editor.action.ActionService;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.ButtonServiceTest;

import java.util.List;

public class ButtonServiceImplTest extends ButtonServiceTest {

    @Override
    public ButtonService createInstance() {
        return new ButtonServiceImpl(
                new FrameProviderImpl(),
                null, // these dependencies should only be used once a button
                null, // is clicked, which doesn't happen in the test
                null,
                new TestActionService()
        );
    }

    /**
     * Empty stub for test.
     */
    private static class TestActionService implements ActionService {
        @Override
        public void registerAction(OrieditaAction orieditaAction) {

        }

        @Override
        public List<OrieditaAction> getAllRegisteredActions() {
            return List.of();
        }
    }
}