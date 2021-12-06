package oriedita.editor.tools;

import org.tinylog.Logger;
import oriedita.editor.App;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
            return Paths.get(System.getenv("APPDATA"), "oriedita");
        } else if (osName.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "oriedita");
        } else {
            return Paths.get(System.getProperty("user.home"), ".oriedita");
        }
    }

    public static Path getTempDir() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    public static String getVersionFromManifest() {
        try {
            File file = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.isFile()) {
                JarFile jarFile = new JarFile(file);
                Manifest manifest = jarFile.getManifest();
                Attributes attributes = manifest.getMainAttributes();
                final String version = attributes.getValue("Implementation-Version");
                jarFile.close();
                
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
            localBundle = new PropertyResourceBundle(Files.newInputStream(Paths.get(bundle + ".properties")));
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

        Logger.warn(bundle + "." + key + " does not exist");

        return null;
    }

    public static void updateBundleKey(String bundleName, String key, String value) {
        try {
            Path bundleLocation = getAppDir().resolve(bundleName + ".properties");
            if (!bundleLocation.toFile().exists() && !bundleLocation.toFile().createNewFile()) {
                throw new IOException("Could not create file");
            }
            Properties properties = new Properties();
            properties.load(Files.newInputStream(bundleLocation));

            if (value == null) {
                properties.remove(key);
            } else {
                properties.setProperty(key, value);
            }

            properties.store(Files.newOutputStream(bundleLocation), null);
        } catch (IOException e) {
            Logger.error(e, "Writing bundle key failed");
        }
    }
}
