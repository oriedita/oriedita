package oriedita.util;

import oriedita.util.ResourceUtil;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class VersionUtil {
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
}
