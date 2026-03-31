package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.AbstractModel;

@ApplicationScoped
public class FixPrecisionModel extends AbstractModel {
    private double fixPrecision;
    private boolean fixPrecisionUseBP;
    private boolean fixPrecisionUse22_5;

    @Inject
    public FixPrecisionModel() {
        reset();
    }

    public void reset() {
        fixPrecision = 0.05;
        fixPrecisionUseBP = true;
        fixPrecisionUse22_5 = true;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getFixPrecision () {
        return fixPrecision;
    }

    public void setFixPrecision(double  fixPrecision) {
        double oldFixPrecision = this.fixPrecision;
        this.fixPrecision = fixPrecision;
        this.pcs.firePropertyChange("fixPrecision", oldFixPrecision, fixPrecision);
    }

    public boolean getFixPrecisionUseBP () {
        return fixPrecisionUseBP;
    }

    public void setFixPrecisionUseBP(boolean  fixPrecisionUseBP) {
        boolean oldFixPrecisionUseBP = this.fixPrecisionUseBP;
        this.fixPrecisionUseBP = fixPrecisionUseBP;
        this.pcs.firePropertyChange("fixPrecisionUseBP", oldFixPrecisionUseBP, fixPrecisionUseBP);
    }

    public boolean getFixPrecisionUse22_5 () {
        return fixPrecisionUse22_5;
    }

    public void setFixPrecisionUse22_5(boolean  fixPrecisionUse22_5) {
        boolean oldFixPrecisionUse22_5 = this.fixPrecisionUse22_5;
        this.fixPrecisionUse22_5 = fixPrecisionUse22_5;
        this.pcs.firePropertyChange("fixPrecisionUse22_5", oldFixPrecisionUse22_5, fixPrecisionUse22_5);
    }
}