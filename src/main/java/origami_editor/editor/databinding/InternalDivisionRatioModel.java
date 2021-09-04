package origami_editor.editor.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class InternalDivisionRatioModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private double internalDivisionRatioA;
    private double internalDivisionRatioB;
    private double internalDivisionRatioC;
    private double internalDivisionRatioD;
    private double internalDivisionRatioE;
    private double internalDivisionRatioF;

    public InternalDivisionRatioModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        internalDivisionRatioA = 1.0;
        internalDivisionRatioB = 0.0;
        internalDivisionRatioC = 0.0;
        internalDivisionRatioD = 0.0;
        internalDivisionRatioE = 1.0;
        internalDivisionRatioF = 2.0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getInternalDivisionRatioS() {
        double d_internalDivisionRatio_s = internalDivisionRatioA + internalDivisionRatioB * Math.sqrt(internalDivisionRatioC);
        if (d_internalDivisionRatio_s < 0.0) {
            internalDivisionRatioB = 0.0;
        }

        return d_internalDivisionRatio_s;
    }

    public double getInternalDivisionRatioT() {
        double internalDivisionRatioT = internalDivisionRatioD + internalDivisionRatioE * Math.sqrt(internalDivisionRatioF);
        if (internalDivisionRatioT < 0.0) {
            internalDivisionRatioE = 0.0;
        }

        return internalDivisionRatioT;
    }

    public double getInternalDivisionRatioA() {
        return internalDivisionRatioA;
    }

    public void setInternalDivisionRatioA(double internalDivisionRatioA) {
        double oldInternalDivisionRatioA = this.internalDivisionRatioA;
        this.internalDivisionRatioA = internalDivisionRatioA;
        this.pcs.firePropertyChange("internalDivisionRatioA", oldInternalDivisionRatioA, internalDivisionRatioA);
    }

    public double getInternalDivisionRatioB() {
        return internalDivisionRatioB;
    }

    public void setInternalDivisionRatioB(double internalDivisionRatioB) {
        double oldInternalDivisionRatioB = this.internalDivisionRatioB;
        this.internalDivisionRatioB = internalDivisionRatioB;
        this.pcs.firePropertyChange("internalDivisionRatioB", oldInternalDivisionRatioB, internalDivisionRatioB);
    }

    public double getInternalDivisionRatioC() {
        return internalDivisionRatioC;
    }

    public void setInternalDivisionRatioC(double internalDivisionRatioC) {
        double oldInternalDivisionRatioC = this.internalDivisionRatioC;
        this.internalDivisionRatioC = Math.max(internalDivisionRatioC, 0.0);
        this.pcs.firePropertyChange("internalDivisionRatioC", oldInternalDivisionRatioC, this.internalDivisionRatioC);
    }

    public double getInternalDivisionRatioD() {
        return internalDivisionRatioD;
    }

    public void setInternalDivisionRatioD(double internalDivisionRatioD) {
        double oldInternalDivisionRatioD = this.internalDivisionRatioD;
        this.internalDivisionRatioD = internalDivisionRatioD;
        this.pcs.firePropertyChange("internalDivisionRatioD", oldInternalDivisionRatioD, internalDivisionRatioD);
    }

    public double getInternalDivisionRatioE() {
        return internalDivisionRatioE;
    }

    public void setInternalDivisionRatioE(double internalDivisionRatioE) {
        double oldInternalDivisionRatioE = this.internalDivisionRatioE;
        this.internalDivisionRatioE = internalDivisionRatioE;
        this.pcs.firePropertyChange("internalDivisionRatioE", oldInternalDivisionRatioE, internalDivisionRatioE);
    }

    public double getInternalDivisionRatioF() {
        return internalDivisionRatioF;
    }

    public void setInternalDivisionRatioF(double internalDivisionRatioF) {
        double oldInternalDivisionRatioF = this.internalDivisionRatioF;
        this.internalDivisionRatioF = Math.max(internalDivisionRatioF, 0.0);
        this.pcs.firePropertyChange("internalDivisionRatioF", oldInternalDivisionRatioF, this.internalDivisionRatioF);
    }
}
