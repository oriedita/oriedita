package origami_editor.tools;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceUtil {
    public static ImageIcon createImageIcon(String url) {
        return new ImageIcon(Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResource(url)));
    }

    public static Path getAppDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return Paths.get(System.getenv("APPDATA"), "origami-editor");
        } else if (osName.contains("mac")) {
            return Paths.get(System.getenv("user.home"), "Library", "Application Support", "origami-editor");
        } else {
            return Paths.get(System.getProperty("user.home"), ".origami-editor");
        }
    }
}
