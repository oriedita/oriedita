package origami_editor.tools;

import origami_editor.editor.App;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

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

    public static String getVersionFromManifest() {
        try {
            File file = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.isFile()) {
                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                Attributes attributes = manifest.getMainAttributes();
                final String version = attributes.getValue("Implementation-Version");

                if (version != null) {
                    return version;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "dev";
    }

    /**
     * Reads a string from one of three locations:
     * 1. Try to find a properties file with the key in the same directory as where the jar is executed
     * 2. Try to find a properties file with the key in the directory from {@link #getAppDir}
     * 3. Try to read the key from the properties file in the jar.
     * @param bundle Properties file to load
     * @param key Key to read in properties file
     */
    public static String getBundleString(String bundle, String key) {
        ResourceBundle userBundle = null;
        ResourceBundle localBundle = null;
        ResourceBundle jarBundle = null;

        try {
            userBundle = new PropertyResourceBundle(Files.newInputStream(getAppDir().resolve(bundle + ".properties")));
        } catch (IOException ignored) {
        }

        try {
            localBundle = new PropertyResourceBundle(Files.newInputStream(Path.of(bundle + ".properties")));
        } catch (IOException ignored) {
        }

        try {
            jarBundle = ResourceBundle.getBundle(bundle);
        } catch (MissingResourceException ignored) {
        }

        if (localBundle != null && localBundle.containsKey(key)) {
            return localBundle.getString(key);
        } else if (userBundle != null && userBundle.containsKey(key)) {
            return userBundle.getString(key);
        } else if (jarBundle != null && jarBundle.containsKey(key)) {
            return jarBundle.getString(key);
        }

        System.err.println(bundle + "." + key + " does not exist");

        return null;
    }
}
