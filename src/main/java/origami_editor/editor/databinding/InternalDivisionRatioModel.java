package origami_editor.editor.databinding;

public class InternalDivisionRatioModel {
    private double internalDivisionRatioA;
    private double internalDivisionRatioB;
    private double internalDivisionRatioC;
    private double internalDivisionRatioD;
    private double internalDivisionRatioE;
    private double internalDivisionRatioF;

    public InternalDivisionRatioModel() {
        reset();
    }

    public void reset() {
        internalDivisionRatioA = 1.0;
        internalDivisionRatioB = 0.0;
        internalDivisionRatioC = 0.0;
        internalDivisionRatioD = 0.0;
        internalDivisionRatioE = 1.0;
        internalDivisionRatioF = 2.0;
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
        this.internalDivisionRatioA = internalDivisionRatioA;
    }

    public double getInternalDivisionRatioB() {
        return internalDivisionRatioB;
    }

    public void setInternalDivisionRatioB(double internalDivisionRatioB) {
        this.internalDivisionRatioB = internalDivisionRatioB;
    }

    public double getInternalDivisionRatioC() {
        return internalDivisionRatioC;
    }

    public void setInternalDivisionRatioC(double internalDivisionRatioC) {
        this.internalDivisionRatioC = Math.max(internalDivisionRatioC, 0.0);
    }

    public double getInternalDivisionRatioD() {
        return internalDivisionRatioD;
    }

    public void setInternalDivisionRatioD(double internalDivisionRatioD) {
        this.internalDivisionRatioD = internalDivisionRatioD;
    }

    public double getInternalDivisionRatioE() {
        return internalDivisionRatioE;
    }

    public void setInternalDivisionRatioE(double internalDivisionRatioE) {
        this.internalDivisionRatioE = internalDivisionRatioE;
    }

    public double getInternalDivisionRatioF() {
        return internalDivisionRatioF;
    }

    public void setInternalDivisionRatioF(double internalDivisionRatioF) {
        this.internalDivisionRatioF = Math.max(internalDivisionRatioF, 0.0);
    }
}
