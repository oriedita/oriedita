package origami_editor.editor.component;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class AppFileChooser extends JFileChooser {
    public AppFileChooser(String defaultDirectory, List<File> recentFileList) {
        super(defaultDirectory);

        RecentFileList recentFileList1 = new RecentFileList(this, recentFileList);
        setAccessory(recentFileList1);
    }
}
