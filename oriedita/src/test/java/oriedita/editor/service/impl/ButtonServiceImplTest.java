package oriedita.editor.service.impl;

import oriedita.editor.FrameProviderImpl;
import oriedita.editor.service.ButtonService;
import oriedita.editor.service.ButtonServiceTest;

public class ButtonServiceImplTest extends ButtonServiceTest {

    @Override
    public ButtonService createInstance() {
        return new ButtonServiceImpl(
                new FrameProviderImpl(),
                null, null, null, null // these dependencies should only be used once a button
                                                                // is clicked, which doesn't happen in the test
        );
    }
}