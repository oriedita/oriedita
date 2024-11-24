package oriedita.editor.tools;

import org.tinylog.Logger;

import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
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
        return Paths.get(System.getProperty("java.io.tmpdir")).resolve("oriedita");
    }

    public static String getVersionFromManifest() {
        try {
            File file = new File(ResourceUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
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

    private static ResourceBundle emptyResourceBundle = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][];
        }
    };
    private static Map<String, ResourceBundle> userBundleCache = new ConcurrentHashMap<>();
    private static Map<String, ResourceBundle> localBundleCache = new ConcurrentHashMap<>();


    /**
     * Reads a string from one of three locations:
     * 1. Try to find a properties file with the key in the same directory as where the jar is executed
     * 2. Try to find a properties file with the key in the directory from {@link #getAppDir}
     * 3. Try to read the key from the properties file in the jar.
     *
     * @param bundle Properties file to load
     * @param key    Key to read in properties file
     */
    public static String getBundleString(String bundle, String key) {
        ResourceBundle localBundle = localBundleCache.computeIfAbsent(bundle, (b) -> {
            try {
                return new PropertyResourceBundle(Files.newInputStream(Paths.get(bundle + ".properties")));
            } catch (IOException e) {
                return emptyResourceBundle;
            }
        });

        if (localBundle.containsKey(key)) {
            return localBundle.getString(key);
        }

        ResourceBundle userBundle = userBundleCache.computeIfAbsent(bundle, (b) -> {
            try {
                return new PropertyResourceBundle(Files.newInputStream(getAppDir().resolve(b + ".properties")));
            } catch (IOException e) {
                return emptyResourceBundle;
            }
        });

        if (userBundle.containsKey(key)) {
            return userBundle.getString(key);
        }

        try {
            ResourceBundle jarBundle = ResourceBundle.getBundle(bundle);

            if (jarBundle.containsKey(key)) {
                return jarBundle.getString(key);
            }
        } catch (MissingResourceException ignored) {
        }

        Logger.debug(bundle + "." + key + " does not exist");

        return null;
    }

    public static String getDefaultHotkeyBundleString(String key) {
        ResourceBundle jarBundle = null;
        try {
            jarBundle = ResourceBundle.getBundle("hotkey");
        } catch (MissingResourceException ignored) {
        }

        if (jarBundle != null && jarBundle.containsKey(key)) {
            return jarBundle.getString(key);
        }

        Logger.debug(key + " does not exist (DEFAULT)");
        return null;
    }

    public static void updateBundleKey(String bundleName, String key, String value) {
        try {
            localBundleCache.remove(bundleName);
            userBundleCache.remove(bundleName);
            ResourceBundle.clearCache();

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

    public static void clearBundle(String bundleName) {
        try {
            localBundleCache.remove(bundleName);
            userBundleCache.remove(bundleName);
            ResourceBundle.clearCache();

            Path bundleLocation = getAppDir().resolve(bundleName + ".properties");
            if (!bundleLocation.toFile().exists() && !bundleLocation.toFile().createNewFile()) {
                throw new IOException("Could not create file");
            }
            Properties properties = new Properties();
            properties.load(Files.newInputStream(bundleLocation));

            properties.clear();

            properties.store(Files.newOutputStream(bundleLocation), null);
        } catch (IOException e) {
            Logger.error(e, "Clearing bundle failed");
        }
    }
}
