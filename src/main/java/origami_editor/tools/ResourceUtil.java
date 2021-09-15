package origami_editor.tools;

import javax.swing.*;
import java.util.Objects;

public class ResourceUtil {
    public static ImageIcon createImageIcon(String url) {
        return new ImageIcon(Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResource(url)));
    }
}
