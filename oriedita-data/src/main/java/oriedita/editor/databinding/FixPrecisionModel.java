package oriedita.editor.databinding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.AbstractModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@ApplicationScoped
public class FixPrecisionModel extends AbstractModel {
    private double fixPrecision;
    private boolean fixPrecisionUseBP;
    private boolean fixPrecisionUse22_5;

    private ArrayList<Double> fixData_22_5;
    private ArrayList<Double> fixData_BP;
    private boolean isData_22_5;
    private boolean isData_BP;

    @Inject
    public FixPrecisionModel() {
        reset();
    }

    public void reset() {
        fixPrecision = 0.05;
        fixPrecisionUseBP = true;
        fixPrecisionUse22_5 = true;
        fixData_22_5 = new ArrayList<>();
        fixData_BP = new ArrayList<>();
        isData_22_5 = false;
        isData_BP = false;

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

    public ArrayList<Double> getFixData22_5() {
        if(!isData_22_5) {
            generateFixData_22_5();
            isData_22_5 = true;
        }
        return fixData_22_5;
    }

    private void generateFixData_22_5() {
        fixData_22_5 = make(15, 200);

        // Remove duplicates
        Set<Double> set = new LinkedHashSet<>(fixData_22_5);
        fixData_22_5.clear();
        fixData_22_5.addAll(set);
    }

    public ArrayList<Double> getFixData_BP() {
        if(!isData_BP) {
            generateFixData_BP();
            isData_BP = true;
        }
        return fixData_BP;
    }

    private void generateFixData_BP() {
        fixData_BP = make(10, 1);

        // Remove duplicates
        Set<Double> set = new LinkedHashSet<>(fixData_BP);
        fixData_BP.clear();
        fixData_BP.addAll(set);
    }

    // All 22.5° positions are of form (a+b*sqrt2)/(c+d*sqrt2).
    // This generates all positions with up to a|b|c|d = ranks
    ArrayList<Double> make(int ranks, double bound) {
        ArrayList<Double> v = new ArrayList<>();
        v.add(0.0);
        v.add(bound);
        double sqrt2 = 1.414213562373095;

        for (int max = 0; max < ranks; ++max) { // increase the max over time, so that more complicated references appear later
            for (int a = 0; a <= max; ++a) {
                for (int b = 0; b <= max; ++b) {
                    for (int c = 0; c <= max; ++c) {
                        for (int d = 0; d <= max; ++d) {
                            // Some positions, despite being positive, use negative values in some variables.
                            // Makes use of binary representation of numbers to invert the variables
                            for(int i = 0; i<16; i++) {
                                a = (i & 0b1)==0 ? a : -a;      // reverse a if last bit is 1
                                b = (i & 0b10)==0 ? b : -b;     // reverse b if second to last bit is 1
                                c = (i & 0b100)==0 ? c : -c;    // etc.
                                d = (i & 0b1000)==0 ? d : -d;

                                double pos = (a + b * sqrt2) / (c + d * sqrt2);
                                if (pos < 1 && pos > 0)
                                    v.add(pos * bound);
                            }
                        }
                    }
                }
            }
        }
        return v;
    }
}