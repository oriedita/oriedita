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

    private ArrayList<Double> fixData;
    private boolean isData;

    @Inject
    public FixPrecisionModel() {
        reset();
    }

    public void reset() {
        fixPrecision = 0.05;
        fixPrecisionUseBP = true;
        fixPrecisionUse22_5 = true;
        fixData = new ArrayList<>();
        isData = false;

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

    public ArrayList<Double> getFixData() {
        if(!isData) {
            generateFixData();
            isData = true;
        }
        return fixData;
    }

    private void generateFixData() {
        fixData = makePure22_5();

        // Remove duplicates
        Set<Double> set = new LinkedHashSet<>(fixData);
        fixData.clear();
        fixData.addAll(set);
    }

    ArrayList<Double> makePure22_5() {
        // Ranks
        int max = 15;
        ArrayList<Double> v = new ArrayList<>();
        // Initial values. Assumes (-200|-200) - (200|200) canvas. Negative values don't need to be stored
        v.add(0.0);
        v.add(200.0);

        for (int n = 0; n < max; ++n) {
            for (int a = 0; a <= n; ++a) {
                for (int b = 0; b <= n; ++b) {
                    for (int c = 0; c <= n; ++c) {
                        for (int d = 0; d <= n; ++d) {
                            calc(a, b,  c,  d, v);  calc(-a, b,  c,  d, v);  calc(a, -b,  c,  d, v); calc(-a, -b,  c,  d, v);
                            calc(a, b, -c,  d, v);  calc(-a, b, -c,  d, v);  calc(a, -b, -c,  d, v); calc(-a, -b, -c,  d, v);
                            calc(a, b,  c, -d, v);  calc(-a, b,  c, -d, v);  calc(a, -b,  c, -d, v); calc(-a, -b,  c, -d, v);
                            calc(a, b, -c, -d, v);  calc(-a, b, -c, -d, v);  calc(a, -b, -c, -d, v); calc(-a, -b, -c, -d, v);
                        }
                    }
                }
            }
        }
        return v;
    }

    // Helper
    void calc(double a, double b, double c, double d, ArrayList<Double> v)
    {
        double r2 = 1.414213562373095;
        double pos = (a + b * r2) / (c + d * r2);

        if (pos < 1 && pos > 0)
            v.add(pos * 200);
    }
}