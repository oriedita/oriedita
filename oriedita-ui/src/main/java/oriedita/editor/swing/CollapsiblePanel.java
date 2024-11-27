package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jakarta.enterprise.context.ApplicationScoped;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@ApplicationScoped
public class CollapsiblePanel extends JPanel {

    public CollapsiblePanel(String labelName, JPanel contentPanel) {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(UIManager.getColor("TabbedPane.light"));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UIManager.getColor("TabbedPane.background"));
                setCursor(Cursor.getDefaultCursor());
            }
        };

        this.setLayout(new GridLayoutManager(2, 1, new Insets(5,0,0,0), -1, -1));
        this.setBackground(UIManager.getColor("TabbedPane.background"));

        JLabel clickLabel = new JLabel("▾ ".concat(labelName));
        this.add(clickLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        contentPanel.setEnabled(true);
        contentPanel.setVisible(true);
        this.add(contentPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        clickLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                contentPanel.setEnabled(!contentPanel.isEnabled());
                contentPanel.setVisible(!contentPanel.isVisible());
                clickLabel.setText(contentPanel.isEnabled() ? clickLabel.getText().replace('▸', '▾') : clickLabel.getText().replace('▾', '▸'));
            }
        });
        clickLabel.addMouseListener(adapter);
    }
}
