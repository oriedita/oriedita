package oriedita.editor.swing.dialog;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.awt.*;

public class FileDialog {
    public static String openFileDialog(
            Frame owner,
            CharSequence aTitle,
            CharSequence aDefaultPathAndFile,
            CharSequence[] filterPatterns,
            CharSequence aSingleFilterDescription,
            boolean aAllowMultipleSelects
    ) {
        owner.setEnabled(false);

        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer aFilterPatterns = stack.mallocPointer(filterPatterns.length);
        for (CharSequence filterPattern : filterPatterns) {
            aFilterPatterns.put(stack.UTF8(filterPattern));
        }
        aFilterPatterns.flip();

        String file = TinyFileDialogs.tinyfd_openFileDialog(aTitle, aDefaultPathAndFile + "/", aFilterPatterns, aSingleFilterDescription, aAllowMultipleSelects);

        stack.pop();
        owner.setEnabled(true);

        return file;
    }

    public static String saveFileDialog(
            Frame owner,
            CharSequence aTitle,
            CharSequence aDefaultPathAndFile,
            CharSequence[] filterPatterns,
            CharSequence aSingleFilterDescription
    ) {
        owner.setEnabled(false);

        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer aFilterPatterns = stack.mallocPointer(filterPatterns.length);
        for (CharSequence filterPattern : filterPatterns) {
            aFilterPatterns.put(stack.UTF8(filterPattern));
        }
        aFilterPatterns.flip();

        String file = TinyFileDialogs.tinyfd_saveFileDialog(aTitle, aDefaultPathAndFile + "/", aFilterPatterns, aSingleFilterDescription);

        stack.pop();
        owner.setEnabled(true);

        return file;
    }
}
