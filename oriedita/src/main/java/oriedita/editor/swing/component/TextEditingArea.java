package oriedita.editor.swing.component;

import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class TextEditingArea extends JTextArea {
    private SelectedTextModel textModel;
    private TextWorker textWorker;

    private CreasePattern_Worker cpWorker;

    private CanvasModel canvasModel;

    private CameraModel cameraModel;


    public TextEditingArea(SelectedTextModel textModel,
                           TextWorker textWorker,
                           CreasePattern_Worker cpWorker,
                           CanvasModel canvasModel,
                           CameraModel cameraModel) {
        this.textModel = textModel;
        this.textWorker = textWorker;
        this.cpWorker = cpWorker;
        this.canvasModel = canvasModel;
        this.cameraModel = cameraModel;
    }

    public void setupListeners() {
        textModel.addPropertyChangeListener(e -> {
            if (Objects.equals(e.getPropertyName(), "selectedText")) {
                if (e.getOldValue() != null && ((Text) e.getOldValue()).getText().isBlank()) {
                    textWorker.removeText((Text) e.getOldValue());
                }
            }
            update(textModel, cpWorker.camera);
        });
        canvasModel.addPropertyChangeListener(e -> {
            if (Objects.equals(e.getPropertyName(), "mouseMode")) {
                if (e.getNewValue() != MouseMode.TEXT) {
                    textModel.setSelected(false);
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateSelectedText(textModel);
                cpWorker.record();
            }
        });
        cameraModel.addPropertyChangeListener(e -> update(textModel, cpWorker.camera));

    }
    private void updateSelectedText(SelectedTextModel textModel) {
        textModel.getSelectedText().setText(getText());
        textModel.markDirty();
    }

    public void update() {
        update(textModel, cpWorker.camera);
    }
    private void update(SelectedTextModel textModel, Camera camera) {
        if (textModel.getSelectedText() == null || !textModel.isSelected()) {
            setVisible(false);
            repaint();
            return;
        }
        repaint();
        if (!Objects.equals(getText(), textModel.getSelectedText().getText())) {
            setText(textModel.getSelectedText().getText());
        }
        Rectangle bounds = textModel.getSelectedText().calculateBounds();
        requestFocusInWindow();
        setVisible(true);
        Point textPos = camera.object2TV(textModel.getSelectedText().getPos());
        setBounds((int) textPos.getX()-3, (int) textPos.getY()-10, bounds.width+30, bounds.height+30);
        repaint();
    }
}
