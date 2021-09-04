package origami_editor.editor.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class HistoryStateModel {
    private int historyTotal;
    private int auxHistoryTotal;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public HistoryStateModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public int getHistoryTotal() {
        return historyTotal;
    }

    public void setHistoryTotal(int historyTotal) {
        int oldHistoryTotal = this.historyTotal;
        this.historyTotal = Math.max(historyTotal, 0);
        this.pcs.firePropertyChange("historyTotal", oldHistoryTotal, this.historyTotal);
    }

    public int getAuxHistoryTotal() {
        return auxHistoryTotal;
    }

    public void setAuxHistoryTotal(int auxHistoryTotal) {
        int oldAuxHistoryTotal = this.auxHistoryTotal;
        this.auxHistoryTotal = Math.max(auxHistoryTotal, 0);
        this.pcs.firePropertyChange("auxHistoryTotal", oldAuxHistoryTotal, this.auxHistoryTotal);
    }

    public void reset() {
        historyTotal = 50;
        auxHistoryTotal = 50;

        this.pcs.firePropertyChange(null, null, null);
    }
}
