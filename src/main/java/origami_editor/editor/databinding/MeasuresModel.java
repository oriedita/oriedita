package origami_editor.editor.databinding;

public class MeasuresModel {
    private double measuredLength1;
    private double measuredLength2;
    private double measuredAngle1;
    private double measuredAngle2;
    private double measuredAngle3;

    public MeasuresModel() {
        reset();
    }

    public void reset() {
        measuredLength1 = 0.0;
        measuredLength2 = 0.0;
        measuredAngle1 = 0.0;
        measuredAngle2 = 0.0;
        measuredAngle3 = 0.0;
    }

    public double getMeasuredLength1() {
        return measuredLength1;
    }

    public void setMeasuredLength1(double measuredLength1) {
        this.measuredLength1 = measuredLength1;
    }

    public double getMeasuredLength2() {
        return measuredLength2;
    }

    public void setMeasuredLength2(double measuredLength2) {
        this.measuredLength2 = measuredLength2;
    }

    public double getMeasuredAngle1() {
        return measuredAngle1;
    }

    public void setMeasuredAngle1(double measuredAngle1) {
        if (measuredAngle1 > 180.0) {
            this.measuredAngle1 = measuredAngle1 - 360.0;
        } else {
            this.measuredAngle1 = measuredAngle1;
        }
    }

    public double getMeasuredAngle2() {
        return measuredAngle2;
    }

    public void setMeasuredAngle2(double measuredAngle2) {
        if (measuredAngle2 > 180.0) {
            this.measuredAngle2 = measuredAngle2 - 360.0;
        } else {
            this.measuredAngle2 = measuredAngle2;
        }
    }

    public double getMeasuredAngle3() {
        return measuredAngle3;
    }

    public void setMeasuredAngle3(double measuredAngle3) {
        if (measuredAngle3 > 180.0) {
            this.measuredAngle3 = measuredAngle3 - 360.0;
        } else {
            this.measuredAngle3 = measuredAngle3;
        }
    }
}
