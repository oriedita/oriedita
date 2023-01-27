package oriedita.editor.databinding;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@ApplicationScoped
public class AngleSystemModel {
    private double angleA;
    private double angleB;
    private double angleC;
    private double angleD;
    private double angleE;
    private double angleF;

    private int angleSystemADivider;
    private int angleSystemBDivider;

    /**
     * If this is 0, then currentAngleA,B,C are used else the value is used to divide 180 degrees.
     */
    private int currentAngleSystemDivider;
    private double currentAngleA;
    private double currentAngleB;
    private double currentAngleC;
    private AngleSystemInputType angleSystemInputType;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    @Inject
    public AngleSystemModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void setCurrentABC() {
        currentAngleA = angleA;
        currentAngleB = angleB;
        currentAngleC = angleC;

        currentAngleSystemDivider = 0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getCurrentAngleA() {
        return currentAngleA;
    }

    public double getCurrentAngleB() {
        return currentAngleB;
    }

    public double getCurrentAngleC() {
        return currentAngleC;
    }

    public int getCurrentAngleSystemDivider() {
        return currentAngleSystemDivider;
    }

    public void setCurrentAngleSystemDivider(int currentAngleSystemDivider) {
        int oldCurrentAngleSystemDivider = this.currentAngleSystemDivider;
        this.currentAngleSystemDivider = currentAngleSystemDivider;
        this.pcs.firePropertyChange("currentAngleSystemDivider", oldCurrentAngleSystemDivider, currentAngleSystemDivider);
    }

    public void reset() {
        angleA = 40.0;
        angleB = 60.0;
        angleC = 80.0;
        angleD = 30.0;
        angleE = 50.0;
        angleF = 100.0;

        angleSystemADivider = 12;
        angleSystemBDivider = 8;

        angleSystemInputType = AngleSystemInputType.NONE_0;

        currentAngleSystemDivider = 8;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getAngleA() {
        return angleA;
    }

    public void setAngleA(double angleA) {
        double oldAngleA = this.angleA;
        this.angleA = angleA;
        this.pcs.firePropertyChange("angleA", oldAngleA, angleA);
    }

    public double getAngleB() {
        return angleB;
    }

    public void setAngleB(double angleB) {
        double oldAngleB = this.angleB;
        this.angleB = angleB;
        this.pcs.firePropertyChange("angleB", oldAngleB, angleB);
    }

    public double getAngleC() {
        return angleC;
    }

    public void setAngleC(double angleC) {
        double oldAngleC = this.angleC;
        this.angleC = angleC;
        this.pcs.firePropertyChange("angleC", oldAngleC, angleC);
    }

    public double getAngleD() {
        return angleD;
    }

    public void setAngleD(double angleD) {
        double oldAngleD = this.angleD;
        this.angleD = angleD;
        this.pcs.firePropertyChange("angleD", oldAngleD, angleD);
    }

    public double getAngleE() {
        return angleE;
    }

    public void setAngleE(double angleE) {
        double oldAngleE = this.angleE;
        this.angleE = angleE;
        this.pcs.firePropertyChange("angleE", oldAngleE, angleE);
    }

    public double getAngleF() {
        return angleF;
    }

    public void setAngleF(double angleF) {
        double oldAngleF = this.angleF;
        this.angleF = angleF;
        this.pcs.firePropertyChange("angleF", oldAngleF, angleF);
    }

    public int getAngleSystemADivider() {
        return angleSystemADivider;
    }

    public void setAngleSystemADivider(int angleSystemADivider) {
        int oldAngleSystemADivider = this.angleSystemADivider;
        this.angleSystemADivider = angleSystemADivider;
        this.pcs.firePropertyChange("angleSystemADivider", oldAngleSystemADivider, angleSystemADivider);
    }

    public int getAngleSystemBDivider() {
        return angleSystemBDivider;
    }

    public void setAngleSystemBDivider(int angleSystemBDivider) {
        int oldAngleSystemBDivider = this.angleSystemBDivider;
        this.angleSystemBDivider = angleSystemBDivider;
        this.pcs.firePropertyChange("angleSystemBDivider", oldAngleSystemBDivider, angleSystemBDivider);
    }

    public AngleSystemInputType getAngleSystemInputType() {
        return angleSystemInputType;
    }

    public void setAngleSystemInputType(AngleSystemInputType angleSystemInputType) {
        AngleSystemInputType oldAngleSystemInputType = this.angleSystemInputType;
        this.angleSystemInputType = angleSystemInputType;
        this.pcs.firePropertyChange("angleSystemInputType", oldAngleSystemInputType, angleSystemInputType);
    }

    public String getAngleSystemADescription() {
        return getAngleSystemDescription(angleSystemADivider);
    }

    public String getAngleSystemBDescription() {
        return getAngleSystemDescription(angleSystemBDivider);
    }

    private String getAngleSystemDescription(int divider) {
        return "180/" + divider + "=" + (double) (Math.round((180.0 / ((double) divider)) * 1000)) / 1000.0;
    }

    public void decreaseAngleSystemA() {
        int angleSystemADivider = this.angleSystemADivider + 1;

        setAngleSystemADivider(angleSystemADivider);
        setCurrentAngleSystemDivider(angleSystemADivider);
    }

    public void increaseAngleSystemA() {
        int angleSystemADivider = this.angleSystemADivider - 1;
        if (angleSystemADivider < 2) {
            angleSystemADivider = 2;
        }

        setAngleSystemADivider(angleSystemADivider);
        setCurrentAngleSystemDivider(angleSystemADivider);
    }

    public void decreaseAngleSystemB() {
        int angleSystemBDivider = this.angleSystemBDivider + 1;

        setAngleSystemBDivider(angleSystemBDivider);
        setCurrentAngleSystemDivider(angleSystemBDivider);
    }

    public void increaseAngleSystemB() {
        int angleSystemBDivider = this.angleSystemBDivider - 1;
        if (angleSystemBDivider < 2) {
            angleSystemBDivider = 2;
        }

        setAngleSystemBDivider(angleSystemBDivider);
        setCurrentAngleSystemDivider(angleSystemBDivider);
    }

    public void setCurrentDEF() {
        currentAngleA = angleD;
        currentAngleB = angleE;
        currentAngleC = angleF;

        currentAngleSystemDivider = 0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double[] getAngles() {
        return new double[]{
                currentAngleA,
                currentAngleB,
                currentAngleC,
                360 - currentAngleA,
                360 - currentAngleB,
                360 - currentAngleC,
        };
    }

    public enum AngleSystemInputType {
        NONE_0,
        DEG_1,
        DEG_2,
        DEG_3,
        DEG_4,
        DEG_5,
    }
}
