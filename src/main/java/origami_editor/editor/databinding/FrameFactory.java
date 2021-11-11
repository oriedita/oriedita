package origami_editor.editor.databinding;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class FrameFactory {
    @Bean
    @Qualifier("mainFrame")
    public JFrame mainFrame() {
        return new JFrame();
    }
}
