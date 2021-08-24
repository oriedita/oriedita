package jp.gr.java_conf.mt777.origami.orihime;

import javax.swing.*;
import java.util.Objects;

public class ResourceUtil {
    public static ImageIcon createImageIcon(String url) {
        return new ImageIcon(Objects.requireNonNull(ResourceUtil.class.getClassLoader().getResource(url)));
    }
}
