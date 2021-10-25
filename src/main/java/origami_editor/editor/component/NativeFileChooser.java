package origami_editor.editor.component;

import javax.swing.*;
import java.awt.*;

/**
 * A fileChooser which uses the native look and feel.
 */
public class NativeFileChooser extends JFileChooser {

    public NativeFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    @Override
    public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
        LookAndFeel current = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.updateComponentTreeUI(this);

        int result = super.showDialog(parent, approveButtonText);

        try {
            UIManager.setLookAndFeel(current);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        return result;
    }
}
