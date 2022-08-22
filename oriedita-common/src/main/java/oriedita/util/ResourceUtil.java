package oriedita.util;

import org.tinylog.Logger;
import oriedita.util.DirectoryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ResourceUtil {

    public enum Bundle {
        name, hotkey, tooltip, icons, help
    }

    private static final Map<String, ResourceBundle> userBundle = new HashMap<>();
    private static final Map<String, ResourceBundle> localBundle = new HashMap<>();
    private static final Map<String, ResourceBundle> jarBundle = new HashMap<>();

    public static void preloadBundles() {
        for (Bundle bundleVal: Bundle.values()) {
            String bundle = bundleVal.toString();
            try {
                userBundle.put(bundle, new PropertyResourceBundle(Files.newInputStream(DirectoryUtil.getAppDir().resolve(bundle + ".properties"))));
            } catch (IOException ignored) {
            }

            try {
                localBundle.put(bundle, new PropertyResourceBundle(Files.newInputStream(Paths.get(bundle + ".properties"))));
            } catch (IOException ignored) {
            }

            try {
                jarBundle.put(bundle, ResourceBundle.getBundle(bundle));
            } catch (MissingResourceException ignored) {
            }
        }
    }

    /**
     * Reads a string from one of three locations:
     * 1. Try to find a properties file with the key in the same directory as where the jar is executed
     * 2. Try to find a properties file with the key in the directory from {@link DirectoryUtil#getAppDir}
     * 3. Try to read the key from the properties file in the jar.
     * @param bundleVal Properties file to load
     * @param key Key to read in properties file
     */
    public static String getBundleString(Bundle bundleVal, String key) {
        String bundle = bundleVal.toString();
        if (localBundle.get(bundle) != null && localBundle.get(bundle).containsKey(key)) {
            return localBundle.get(bundle).getString(key);
        } else if (userBundle.get(bundle) != null && userBundle.get(bundle).containsKey(key)) {
            return userBundle.get(bundle).getString(key);
        } else if (jarBundle.get(bundle) != null && jarBundle.get(bundle).containsKey(key)) {
            return jarBundle.get(bundle).getString(key);
        }

        Logger.debug("{}.{} does not exist", bundle, key);

        return null;
    }

    public static void updateBundleKey(String bundleName, String key, String value) {
        try {
            Path bundleLocation = DirectoryUtil.getAppDir().resolve(bundleName + ".properties");
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

            // Update userBundle
            userBundle.put(bundleName, new PropertyResourceBundle(Files.newInputStream(bundleLocation)));
        } catch (IOException e) {
            Logger.error(e, "Writing bundle key failed");
        }
    }
}
