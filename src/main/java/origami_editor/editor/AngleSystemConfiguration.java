package origami_editor.editor;

public class AngleSystemConfiguration {
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
    private App.AngleSystemInputType angleSystemInputType;

    public AngleSystemConfiguration() {
        reset();
    }

    public void setCurrentABC() {
        currentAngleA = angleA;
        currentAngleB = angleB;
        currentAngleC = angleC;

        currentAngleSystemDivider = 0;
    }

    public double getCurrentAngleA() {
        return currentAngleA;
    }

    public void setCurrentAngleA(double currentAngleA) {
        this.currentAngleA = currentAngleA;
    }

    public double getCurrentAngleB() {
        return currentAngleB;
    }

    public void setCurrentAngleB(double currentAngleB) {
        this.currentAngleB = currentAngleB;
    }

    public double getCurrentAngleC() {
        return currentAngleC;
    }

    public void setCurrentAngleC(double currentAngleC) {
        this.currentAngleC = currentAngleC;
    }

    public int getCurrentAngleSystemDivider() {
        return currentAngleSystemDivider;
    }

    public void setCurrentAngleSystemDivider(int currentAngleSystemDivider) {
        this.currentAngleSystemDivider = currentAngleSystemDivider;
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

        angleSystemInputType = App.AngleSystemInputType.DEG_1;

        currentAngleSystemDivider = 8;
    }

    public double getAngleA() {
        return angleA;
    }

    public void setAngleA(double angleA) {
        this.angleA = angleA;
    }

    public double getAngleB() {
        return angleB;
    }

    public void setAngleB(double angleB) {
        this.angleB = angleB;
    }

    public double getAngleC() {
        return angleC;
    }

    public void setAngleC(double angleC) {
        this.angleC = angleC;
    }

    public double getAngleD() {
        return angleD;
    }

    public void setAngleD(double angleD) {
        this.angleD = angleD;
    }

    public double getAngleE() {
        return angleE;
    }

    public void setAngleE(double angleE) {
        this.angleE = angleE;
    }

    public double getAngleF() {
        return angleF;
    }

    public void setAngleF(double angleF) {
        this.angleF = angleF;
    }

    public int getAngleSystemADivider() {
        return angleSystemADivider;
    }

    public void setAngleSystemADivider(int angleSystemADivider) {
        this.angleSystemADivider = angleSystemADivider;
    }

    public int getAngleSystemBDivider() {
        return angleSystemBDivider;
    }

    public void setAngleSystemBDivider(int angleSystemBDivider) {
        this.angleSystemBDivider = angleSystemBDivider;
    }

    public App.AngleSystemInputType getAngleSystemInputType() {
        return angleSystemInputType;
    }

    public void setAngleSystemInputType(App.AngleSystemInputType angleSystemInputType) {
        this.angleSystemInputType = angleSystemInputType;
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
        angleSystemADivider++;

        currentAngleSystemDivider = angleSystemADivider;
    }

    public void increaseAngleSystemA() {
        angleSystemADivider = angleSystemADivider - 1;
        if (angleSystemADivider < 2) {
            angleSystemADivider = 2;
        }

        currentAngleSystemDivider = angleSystemADivider;
    }

    public void decreaseAngleSystemB() {
        angleSystemBDivider++;

        currentAngleSystemDivider = angleSystemBDivider;
    }

    public void increaseAngleSystemB() {
        angleSystemBDivider = angleSystemBDivider - 1;
        if (angleSystemBDivider < 2) {
            angleSystemBDivider = 2;
        }

        currentAngleSystemDivider = angleSystemBDivider;
    }

    public void setCurrentDEF() {
        currentAngleA = angleD;
        currentAngleB = angleE;
        currentAngleC = angleF;

        currentAngleSystemDivider = 0;
    }
}
