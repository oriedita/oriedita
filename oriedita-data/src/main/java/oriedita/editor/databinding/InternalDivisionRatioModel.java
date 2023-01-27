package oriedita.editor.databinding;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.AbstractModel;

@ApplicationScoped
public class InternalDivisionRatioModel extends AbstractModel {
    private final MeasuresModel measuresModel;
    private double internalDivisionRatioA;
    private double internalDivisionRatioB;
    private double internalDivisionRatioC;
    private double internalDivisionRatioD;
    private double internalDivisionRatioE;
    private double internalDivisionRatioF;

    private String displayInternalDivisionRatioA;
    private String displayInternalDivisionRatioB;
    private String displayInternalDivisionRatioC;
    private String displayInternalDivisionRatioD;
    private String displayInternalDivisionRatioE;
    private String displayInternalDivisionRatioF;

    @Inject
    public InternalDivisionRatioModel(MeasuresModel measuresModel) {
        this.measuresModel = measuresModel;
        reset();
    }

    public void reset() {
        internalDivisionRatioA = 1.0;
        internalDivisionRatioB = 0.0;
        internalDivisionRatioC = 0.0;
        internalDivisionRatioD = 0.0;
        internalDivisionRatioE = 1.0;
        internalDivisionRatioF = 2.0;

        displayInternalDivisionRatioA = "1.0";
        displayInternalDivisionRatioB = "0.0";
        displayInternalDivisionRatioC = "0.0";
        displayInternalDivisionRatioD = "0.0";
        displayInternalDivisionRatioE = "1.0";
        displayInternalDivisionRatioF = "2.0";

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

    public String getDisplayInternalDivisionRatioA() {
        return displayInternalDivisionRatioA;
    }

    public void setDisplayInternalDivisionRatioA(String displayInternalDivisionRatioA) {
        String oldDisplayInternalDivisionRatioA = this.displayInternalDivisionRatioA;
        this.displayInternalDivisionRatioA = displayInternalDivisionRatioA;
        this.pcs.firePropertyChange("displayInternalDivisionRatioA", oldDisplayInternalDivisionRatioA, displayInternalDivisionRatioA);
    }

    public String getDisplayInternalDivisionRatioB() {
        return displayInternalDivisionRatioB;
    }

    public void setDisplayInternalDivisionRatioB(String displayInternalDivisionRatioB) {
        String oldDisplayInternalDivisionRatioB = this.displayInternalDivisionRatioB;
        this.displayInternalDivisionRatioB = displayInternalDivisionRatioB;
        this.pcs.firePropertyChange("displayInternalDivisionRatioB", oldDisplayInternalDivisionRatioB, displayInternalDivisionRatioB);
    }

    public String getDisplayInternalDivisionRatioC() {
        return displayInternalDivisionRatioC;
    }

    public void setDisplayInternalDivisionRatioC(String displayInternalDivisionRatioC) {
        String oldDisplayInternalDivisionRatioC = this.displayInternalDivisionRatioC;
        this.displayInternalDivisionRatioC = displayInternalDivisionRatioC;
        this.pcs.firePropertyChange("displayInternalDivisionRatioC", oldDisplayInternalDivisionRatioC, displayInternalDivisionRatioC);
    }

    public String getDisplayInternalDivisionRatioD() {
        return displayInternalDivisionRatioD;
    }

    public void setDisplayInternalDivisionRatioD(String displayInternalDivisionRatioD) {
        String oldDisplayInternalDivisionRatioD = this.displayInternalDivisionRatioD;
        this.displayInternalDivisionRatioD = displayInternalDivisionRatioD;
        this.pcs.firePropertyChange("displayInternalDivisionRatioD", oldDisplayInternalDivisionRatioD, displayInternalDivisionRatioD);
    }

    public String getDisplayInternalDivisionRatioE() {
        return displayInternalDivisionRatioE;
    }

    public void setDisplayInternalDivisionRatioE(String displayInternalDivisionRatioE) {
        String oldDisplayInternalDivisionRatioE = this.displayInternalDivisionRatioE;
        this.displayInternalDivisionRatioE = displayInternalDivisionRatioE;
        this.pcs.firePropertyChange("displayInternalDivisionRatioE", oldDisplayInternalDivisionRatioE, displayInternalDivisionRatioE);
    }

    public String getDisplayInternalDivisionRatioF() {
        return displayInternalDivisionRatioF;
    }

    public void setDisplayInternalDivisionRatioF(String displayInternalDivisionRatioF) {
        String oldDisplayInternalDivisionRatioF = this.displayInternalDivisionRatioF;
        this.displayInternalDivisionRatioF = displayInternalDivisionRatioF;
        this.pcs.firePropertyChange("displayInternalDivisionRatioF", oldDisplayInternalDivisionRatioF, displayInternalDivisionRatioF);
    }

    public void commit() {
        setInternalDivisionRatioA(measuresModel.string2double(getDisplayInternalDivisionRatioA(), getInternalDivisionRatioA()));
        setInternalDivisionRatioB(measuresModel.string2double(getDisplayInternalDivisionRatioB(), getInternalDivisionRatioB()));
        setInternalDivisionRatioC(measuresModel.string2double(getDisplayInternalDivisionRatioC(), getInternalDivisionRatioC()));
        setInternalDivisionRatioD(measuresModel.string2double(getDisplayInternalDivisionRatioD(), getInternalDivisionRatioD()));
        setInternalDivisionRatioE(measuresModel.string2double(getDisplayInternalDivisionRatioE(), getInternalDivisionRatioE()));
        setInternalDivisionRatioF(measuresModel.string2double(getDisplayInternalDivisionRatioF(), getInternalDivisionRatioF()));

        setDisplayInternalDivisionRatioA(String.valueOf(getInternalDivisionRatioA()));
        setDisplayInternalDivisionRatioB(String.valueOf(getInternalDivisionRatioB()));
        setDisplayInternalDivisionRatioC(String.valueOf(getInternalDivisionRatioC()));
        setDisplayInternalDivisionRatioD(String.valueOf(getInternalDivisionRatioD()));
        setDisplayInternalDivisionRatioE(String.valueOf(getInternalDivisionRatioE()));
        setDisplayInternalDivisionRatioF(String.valueOf(getInternalDivisionRatioF()));
    }
}
