package oriedita.editor.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jakarta.enterprise.context.ApplicationScoped;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@ApplicationScoped
public class CollapsiblePanel extends JPanel {

    public CollapsiblePanel(String labelName, JPanel contentPanel) {
        this.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 5, 0), -1, -1));

        JLabel clickLabel = new JLabel("▾ ".concat(labelName));
        this.add(clickLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        contentPanel.setEnabled(true);
        contentPanel.setVisible(true);
        this.add(contentPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, 1, 1, null, null, null, 0, false));

        clickLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                contentPanel.setEnabled(!contentPanel.isEnabled());
                contentPanel.setVisible(!contentPanel.isVisible());
                clickLabel.setText(contentPanel.isEnabled() ? clickLabel.getText().replace('▸', '▾') : clickLabel.getText().replace('▾', '▸'));
            }
        });
    }
}
