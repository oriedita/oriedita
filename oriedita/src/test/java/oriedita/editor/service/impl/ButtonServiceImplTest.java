package oriedita.editor.service.impl;

import jakarta.enterprise.inject.Instance;
import oriedita.editor.FrameProviderImpl;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.save.SaveTest;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.ButtonServiceTest;


public class ButtonServiceImplTest extends ButtonServiceTest {

    Instance<OrieditaAction> instances = new SaveTest.MockInstance<>(null);

    @Override
    public ButtonService createInstance() {
        return new ButtonServiceImpl(
                new FrameProviderImpl(), instances,
                null, null, null // these dependencies should only be used once a button
                                                                // is clicked, which doesn't happen in the test
        );
    }
}