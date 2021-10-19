package origami_editor.editor.component;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class RecentFileList extends JPanel {

    private final JList<File> list;
    private final FileListModel listModel;
    private final JFileChooser fileChooser;

    public RecentFileList(JFileChooser chooser, List<File> recentFiles) {
        fileChooser = chooser;
        listModel = new FileListModel(recentFiles);
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new FileListCellRenderer());

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && chooser.getSelectedFile() != null) {
                    chooser.approveSelection();
                }
            }
        });

        setLayout(new BorderLayout(5,5));
        add(new JLabel("Recent files", JLabel.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(list));

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File file = list.getSelectedValue();
                // You might like to check to see if the file still exists...
                fileChooser.setSelectedFile(file);
            }
        });
    }

    public void clearList() {
        listModel.clear();
    }

    public void add(File file) {
        listModel.add(file);
    }

    public static class FileListModel extends AbstractListModel<File> {

        private final List<File> files;

        public FileListModel(List<File> files) {
            this.files = files;
            fireIntervalAdded(this, 0, files.size() - 1);
        }

        public void add(File file) {
            if (!files.contains(file)) {
                if (files.isEmpty()) {
                    files.add(file);
                } else {
                    files.add(0, file);
                }
                fireIntervalAdded(this, 0, 0);
            }
        }

        public void clear() {
            int size = files.size() - 1;
            if (size >= 0) {
                files.clear();
                fireIntervalRemoved(this, 0, size);
            }
        }

        @Override
        public int getSize() {
            return files.size();
        }

        @Override
        public File getElementAt(int index) {
            return files.get(index);
        }
    }

    public static class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File file = (File) value;
                Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
                setIcon(ico);
                setToolTipText(file.getParent());
                setText(file.getName());
            }
            return this;
        }

    }

}
