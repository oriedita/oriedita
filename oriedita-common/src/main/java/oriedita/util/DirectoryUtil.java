package oriedita.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryUtil {
    public static Path getTempDir() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Get the application directory, which we can use to persist files.
     *
     * Windows: %APPDATA%\oriedita
     * Mac: ~/Library/Application Support/oriedita
     * Linux: ~/.oriedita
     */
    public static Path getAppDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return Paths.get(System.getenv("APPDATA"), "oriedita");
        } else if (osName.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "oriedita");
        } else {
            return Paths.get(System.getProperty("user.home"), ".oriedita");
        }
    }
}
