package oriedita.editor.swing.dialog;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.awt.*;

/**
 * Utilities for showing file open/save dialogs.
 */
public class FileDialogUtil {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return OS.contains("win");
    }

    public static String openFileDialog(Frame owner, String title, String defaultPath, String[] filterPatterns, String filterDescription) {
        if (isWindows()) {
            return openFileDialogWin(title, defaultPath, filterPatterns, filterDescription);
        }

        return openFileDialogJava(owner, title, defaultPath, filterPatterns, FileDialog.LOAD);
    }

    public static String saveFileDialog(Frame owner, String title, String defaultPath, String[] filterPatterns, String filterDescription) {
        if (isWindows()) {
            return saveFileDialogWin(title, defaultPath, filterPatterns, filterDescription);
        }

        return openFileDialogJava(owner, title, defaultPath, filterPatterns, FileDialog.SAVE);
    }

    /**
     * Show a frame on any platform using java.awt.FileDialog
     */
    private static String openFileDialogJava(
            Frame owner,
            String title,
            String defaultPath,
            String[] filterPatterns,
            int mode
    ) {
        FileDialog fileDialog = new FileDialog(owner, title, mode);
        fileDialog.setDirectory(defaultPath);
        fileDialog.setFilenameFilter((dir, name) -> {
            for (String filterPattern : filterPatterns) {
                if (name.endsWith(filterPattern.substring(1))) {
                    return true;
                }
            }

            return false;
        });
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            return null;
        }

        return fileDialog.getDirectory() + fileDialog.getFile();
    }

    /**
     * Show a file open dialog on Windows.
     *
     * TinyFileDialogs only outperforms java.awt.FileDialog on Windows.
     */
    private static String openFileDialogWin(
            CharSequence title,
            CharSequence defaultPath,
            CharSequence[] filterPatterns,
            CharSequence filterDescription
    ) {
        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer aFilterPatterns = stack.mallocPointer(filterPatterns.length);
        for (CharSequence filterPattern : filterPatterns) {
            aFilterPatterns.put(stack.UTF8(filterPattern));
        }
        aFilterPatterns.flip();

        String file = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath + "/", aFilterPatterns, filterDescription, false);

        stack.pop();

        return file;
    }

    /**
     * Show a file save dialog on Windows.
     *
     * TinyFileDialogs only outperforms java.awt.FileDialog on Windows.
     */
    private static String saveFileDialogWin(
            CharSequence title,
            CharSequence defaultPath,
            CharSequence[] filterPatterns,
            CharSequence filterDescription
    ) {
        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer aFilterPatterns = stack.mallocPointer(filterPatterns.length);
        for (CharSequence filterPattern : filterPatterns) {
            aFilterPatterns.put(stack.UTF8(filterPattern));
        }
        aFilterPatterns.flip();

        String file = TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath + "/", aFilterPatterns, filterDescription);

        stack.pop();

        return file;
    }
}
