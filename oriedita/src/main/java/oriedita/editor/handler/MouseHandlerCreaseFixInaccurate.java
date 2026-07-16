package oriedita.editor.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import org.tinylog.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.FixPrecisionModel;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineSegment;
import origami.folding.util.IBulletinBoard;


@ApplicationScoped
@Handles(MouseMode.FIX_INACCURATE_107)
public class MouseHandlerCreaseFixInaccurate extends StepMouseHandler<MouseHandlerCreaseFixInaccurate.Step> {
    @Inject
    public MouseHandlerCreaseFixInaccurate(IBulletinBoard bb, FixPrecisionModel fixPrecisionModel) {
        this.bb = bb;
        this.fixPrecisionModel = fixPrecisionModel;
    }

    private int fixDataSize;
    private double[] fixData;
    private final IBulletinBoard bb;
    private final FixPrecisionModel fixPrecisionModel;

    private static class FixerResult {
        // Number of lines that were actually fixed. Used for display and to skip fixes that aren't necessary
        long numFixedLines;
        // Number of lines that are theoretically fixable. Used to compare/determine algorithms
        long numFixableLines;
        ArrayList<Double> lines;
        Type type;
        enum Type {
            BP, PURE_22_5, OTHER, EMPTY
        }

        FixerResult(long numFixedLines, long numFixableLines, ArrayList<Double> lines, Type type) {
            this.numFixedLines = numFixedLines;
            this.numFixableLines = numFixableLines;
            this.lines = lines;
            this.type = type;
        }
        FixerResult() {
            this.numFixedLines = -1;
            this.numFixableLines = -1;
            this.lines = null;
            this.type = Type.EMPTY;
        }
    }

    private static class Xform {
        boolean isSquare;
        boolean inDefaultSquare; // true if all positions lie between (-200|-200) and (200|200)
        double scale;
        double deltaX;
        double deltaY;

        public Xform(boolean isSquare, boolean inDefaultSquare, double scale, double deltaX, double deltaY) {
            this.isSquare = isSquare;
            this.inDefaultSquare = inDefaultSquare;
            this.scale = scale;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }

    @Override
    public EnumSet<MouseHandlerSettingGroup> getSettings() {
        return EnumSet.of(MouseHandlerSettingGroup.FIX_PRECISION);
    }

    public enum Step {
        SELECT_LINES
    }

    private Xform getXform(Collection<LineSegment> lines) {
        double allowedError = 1e-4;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (var ls : lines) {
            if(ls.getA().getX() < minX)
                minX = ls.getA().getX();
            if(ls.getA().getX() > maxX)
                maxX = ls.getA().getX();

            if(ls.getA().getY() < minY)
                minY = ls.getA().getY();
            if(ls.getA().getY() > maxY)
                maxY = ls.getA().getY();

            if(ls.getB().getX() < minX)
                minX = ls.getB().getX();
            if(ls.getB().getX() > maxX)
                maxX = ls.getB().getX();

            if(ls.getB().getY() < minY)
                minY = ls.getB().getY();
            if(ls.getB().getY() > maxY)
                maxY = ls.getB().getY();
        }
        boolean isSquare = Math.abs(Math.abs(minY-maxY) - Math.abs(minX-maxX)) < allowedError;
        boolean inDefaultSquare = (minX > -(200+allowedError)) &&
                (minY > -(200+allowedError)) &&
                (maxX <  (200+allowedError)) &&
                (maxY <  (200+allowedError));
        double midX = minX + Math.abs(maxX-minX)/2;
        double midY = minY + Math.abs(maxY-minY)/2;
        double scale = 400/Math.abs(maxX-minX);
        return new Xform(isSquare, inDefaultSquare, scale, midX, midY);
    }

    private ArrayList<LineSegment> doXform (Collection<LineSegment> lines, Xform xform) {
        ArrayList<LineSegment> out = new ArrayList<>();
        for (var ls : lines) {
            var ls2 = ls.withCoordinates((ls.getA().getX() - xform.deltaX) * xform.scale,
                    (ls.getA().getY() - xform.deltaY) * xform.scale,
                    (ls.getB().getX() - xform.deltaX) * xform.scale,
                    (ls.getB().getY() - xform.deltaY) * xform.scale);
            if(xform.isSquare && !xform.inDefaultSquare)
                out.add(ls2);
            else
                out.add(ls);
        }
        return out;
    }

    private double undoXformHelper(double pos, double allowedError) {
        double close = (double)Math.round(pos);
        if(Math.abs(close - pos) < allowedError)
            return close;
        else
            return pos;
    }

    private ArrayList<FixerResult> undoXform (ArrayList<FixerResult> results, Xform xform) {
        ArrayList<FixerResult> outResults = new ArrayList<>();
        double allowedError = 1e-11;
        double pos;
        for(FixerResult r : results) {
            if (xform.isSquare && !xform.inDefaultSquare) {
                ArrayList<Double> outResultLines = new ArrayList<>();
                for (int i = 0; i < r.lines.size(); i += 4) {
                    // The rescaling introduced a slight error, fix near integers to make the save file prettier.
                    pos = r.lines.get(i) / xform.scale + xform.deltaX;
                    outResultLines.add(undoXformHelper(pos, allowedError));

                    pos = r.lines.get(i + 1) / xform.scale + xform.deltaY;
                    outResultLines.add(undoXformHelper(pos, allowedError));

                    pos = r.lines.get(i + 2) / xform.scale + xform.deltaX;
                    outResultLines.add(undoXformHelper(pos, allowedError));

                    pos = r.lines.get(i + 3) / xform.scale + xform.deltaY;
                    outResultLines.add(undoXformHelper(pos, allowedError));
                }
                outResults.add(new FixerResult(r.numFixedLines, r.numFixableLines, outResultLines, r.type));
            }
            else
                outResults.add(r);
        }
        return outResults;
    }

    @Override
    protected StepGraph<Step> initStepGraph(StepFactory stepFactory) {
        var st = new StepGraph<>(Step.SELECT_LINES);
        st.addNode(stepFactory.createBoxSelectLinesNode(Step.SELECT_LINES,
                lines -> {
                    fixWrapper(lines);
                    return Step.SELECT_LINES;
                }, l -> l.getColor().isFoldingLine()));
        return st;
    }

    private void fixWrapper(Collection<LineSegment> selectedLines) {
        if (selectedLines.isEmpty()) return;

        // Transform the selected lines to the middle for easier fixing
        Xform xform = getXform(selectedLines);
        ArrayList<LineSegment> lines = doXform(selectedLines, xform);

        // Holds the values to be fixed
        ArrayList<Double> toFix = new ArrayList<>();
        for (LineSegment s : lines) {
            toFix.add(s.getA().getX());
            toFix.add(s.getA().getY());
            toFix.add(s.getB().getX());
            toFix.add(s.getB().getY());
        }

        // Fixing
        ArrayList<FixerResult> results = fix(toFix);

        results = undoXform(results, xform);

        // Extract the best result and calculate the number of actually fixed Lines
        long maxLines = 0;
        FixerResult result = new FixerResult();
        for(FixerResult r : results) {
            if(r.numFixableLines > maxLines) {
                maxLines = r.numFixableLines;
                result = r;
            }
        }
        result.numFixedLines = getNumFixed(result, new ArrayList<>(selectedLines));

        if(result.type == FixerResult.Type.EMPTY || result.numFixableLines == 0 || result.lines.isEmpty())
        {
            bb.write("No lines fixed");
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    bb.clear();
                } catch (InterruptedException iex) {
                    Logger.info(iex);
                }
            }).start();
            return;
        }
        // If it's a non-square 22.5° CP the xform doesn't know where to place it within the default square,
        // so it can't be fixed properly.
        boolean isBadFix = (result.type == FixerResult.Type.PURE_22_5) && !xform.inDefaultSquare && !xform.isSquare;
        if(isBadFix)
            bb.write("WARNING: Fix may be bad. Try to fix 22.5° CPs inside the default square or as square CP");

        int i = 0;
        var fls = d.getFoldLineSet();
        for(LineSegment ls : selectedLines) {
            fls.deleteLine(ls);
            LineSegment ls2 = ls.withCoordinates(result.lines.get(i),
                    result.lines.get(i+1),
                    result.lines.get(i+2),
                    result.lines.get(i+3));
            fls.addLine(ls2);
            i += 4;
        }

        fls.divideLineSegmentWithNewLines(fls.getTotal() - lines.size(), fls.getTotal());

        // Record new state and display changed line number when one or more lines changed
        if(result.numFixedLines > 0) {
            d.record();
            bb.write("Fixed " + result.numFixedLines + " lines");
            // Needs to be copied to final for lamba expression
            new Thread(() -> {
                try {
                    // Keep the warning message for longer
                    if(isBadFix) {
                        Thread.sleep(15000);
                        bb.clear();
                    }
                    else {
                        Thread.sleep(5000);
                        bb.clear();
                    }
                } catch (InterruptedException iex) {
                    Logger.info(iex);
                }
            }).start();
        }
        else {
            bb.write("No fix available");
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    bb.clear();
                } catch (InterruptedException iex) {
                    Logger.info(iex);
                }
            }).start();
        }
        d.check4();
    }

    private long getNumFixed(FixerResult r, ArrayList<LineSegment> origLines) {
        int i = 0;
        long num = 0;
        double allowedError = 1e-11;
        for(LineSegment l : origLines) {
            if(Math.abs(l.determineAX() - r.lines.get(i)) > allowedError)
                num++;
            else if(Math.abs(l.determineAY() - r.lines.get(i+1)) > allowedError)
                num++;
            else if(Math.abs(l.determineBX() - r.lines.get(i+2)) > allowedError)
                num++;
            else if(Math.abs(l.determineBY() - r.lines.get(i+3)) > allowedError)
                num++;
            i+=4;
        }
        return num;
    }

    private ArrayList<FixerResult> fix(ArrayList<Double> toFix) {

        ArrayList<FixerResult> results = new ArrayList<>();

        // Fix BP first
        if(fixPrecisionModel.getFixPrecisionUseBP()) {
            results.add(fixBP(toFix));

            // Exit early if it's probably box-pleated
            if (results.get(0).numFixableLines > (toFix.size() / 4.0 * .9))
                return results;
        }

        // Fix 22.5
        if(fixPrecisionModel.getFixPrecisionUse22_5()) {
            double precision22_5 = fixPrecisionModel.getFixPrecision()/100.0;
            getFixData();
            results.add(fixWithData(toFix, precision22_5));
        }
        return results;
    }

    private void getFixData(){
        ArrayList<Double> tmp = fixPrecisionModel.getFixData();
        fixDataSize = tmp.size();
        fixData = new double[fixDataSize];

        for (int i = 0, n = fixDataSize; i < n; i++)
            fixData[i] = tmp.get(i);
    }

    // Fix BP
    private FixerResult fixBP(ArrayList<Double> toFix) {
        // Output
        ArrayList<Double> outLines = new ArrayList<>();

        // Fixing
        int gridSize = 0;
        double currentValue, nearestInt;
        final double basePrecision = 0.0013; // Arbitrary value. Derived from testing
        // Since the fixing involves scaling, the precision needs to be adjusted
        double precision = 0;

        // Grid search
        int gridSizeSearch = 0;
        long numLinesFixedWithPrevBestGrid = 0;
        boolean endGridSearch = false;
        final float gridSearchEndPercent = .9f; // Arbitrary value, keep below 1.0f
        final float necessaryImprovementGrid = 1.15f; // Arbitrary value

        // Fixed lines counter logic
        boolean isLineFixed = false;
        long numFixableLines = 0;

        // Automatic grid search algorithm
        for (int gridIteration = 1; gridIteration <= 16; gridIteration++) {
            // Reset here because it shouldn't be overwritten at the end of grid search
            numFixableLines = 0;

            switch (gridIteration) {
                case 1  -> gridSizeSearch = 1024; // Base 2
                case 2  -> gridSizeSearch = 1536; // Base 3
                case 3  -> gridSizeSearch = 1280; // Base 5
                case 4  -> gridSizeSearch = 1792; // Base 7
                case 5  -> gridSizeSearch = 1152; // Base 9
                case 6  -> gridSizeSearch = 1408; // Base 11
                case 7  -> gridSizeSearch = 1664; // Base 13
                case 8  -> gridSizeSearch = 1920; // Base 15
                case 9  -> gridSizeSearch = 1088; // Base 17
                case 10 -> gridSizeSearch = 1216; // Base 19
                case 11 -> gridSizeSearch = 1344; // Base 21
                case 12 -> gridSizeSearch = 1472; // Base 23
                case 13 -> gridSizeSearch = 1600; // Base 25
                case 14 -> gridSizeSearch = 1728; // Base 27
                case 15 -> gridSizeSearch = 1856; // Base 29
                case 16 -> gridSizeSearch = 1984; // Base 31
            }

            // Since the fixing happens at different sizes, the precision needs to be adjusted
            precision = (basePrecision * gridSizeSearch) / 200.0;

            for (int i = 0; i < toFix.size(); i++) {
                currentValue = toFix.get(i);
                // Reset line counter
                if ((i % 4) == 0)
                    isLineFixed = false;

                // Scales the position for gridSearch
                currentValue = currentValue / 200 * gridSizeSearch;

                // Round to nearest integer
                nearestInt = (double)Math.round(currentValue);
                if (Math.abs(currentValue - nearestInt) > precision)
                    continue;
                // Actual fixing happens later so we only need to increment the line counter.
                if (!isLineFixed) {
                    isLineFixed = true;
                    numFixableLines++;
                }
            }

            // Only overwrites old grid solution if the new one has 10% more matches (arbitrary value)
            if (numFixableLines > (numLinesFixedWithPrevBestGrid) * necessaryImprovementGrid) {
                gridSize = gridSizeSearch;
                numLinesFixedWithPrevBestGrid = numFixableLines;
            }

            // Ends grid search prematurely if it finds a close match
            if (numFixableLines > ((toFix.size()/4.0) * gridSearchEndPercent))
                endGridSearch = true;

            // Resets value for next iteration/actual fixing
            isLineFixed = false;

            if (endGridSearch)
                break;
        }

        // Fixing algorithm
        for (Double fix : toFix) {
            currentValue = fix;

            // Scales the position for fixing
            currentValue = currentValue / 200 * gridSize;

            // Round to nearest integer
            nearestInt = (double)Math.round(currentValue);
            if (Math.abs(currentValue - nearestInt) < precision)
                currentValue = nearestInt;

            // Scale back
            currentValue = currentValue * 200 / gridSize;
            outLines.add(currentValue);
        }
        return new FixerResult(0, numFixableLines, outLines, FixerResult.Type.BP);
    }

    // Fix with given data file
    private FixerResult fixWithData(ArrayList<Double> inLines, double precision) {
        ArrayList<Double> outLines = new ArrayList<>();

        // For storing already used positions
        ArrayList<Double> prevFixedPositions = new ArrayList<>();

        // Variables for the fixing algorithm
        double currentValue;
        boolean isNegative;
        boolean skipSlow;

        // Fixed lines counter logic
        boolean isLineFixed = false;
        long numFixableLines = 0;

        for (int i = 0; i < inLines.size(); i++) {
            currentValue = inLines.get(i);
            skipSlow = false;
            isNegative = false;
            // On first position of line reset values
            if (((i % 4) == 0))
                isLineFixed = false;

            // 22_5_Data.bin only holds positive values
            if (currentValue < 0) {
                isNegative = true;
                currentValue *= -1;
            }

            // Check the already fixed positions first
            for (Double prevFixedPosition : prevFixedPositions) {
                // Skip if error too big
                if (Math.abs(currentValue - prevFixedPosition) > precision)
                    continue;
                if (!isLineFixed) {
                    isLineFixed = true;
                    numFixableLines++;
                }
                currentValue = prevFixedPosition;
                skipSlow = true;
                break;
            }
            // If the position wasn't previously fixed already go through all possible positions
            if (!skipSlow) {
                for (int j = 0; j < fixDataSize; j++) {
                    if (Math.abs(currentValue - fixData[j]) > precision)
                        continue;
                    if (!isLineFixed) {
                        isLineFixed = true;
                        numFixableLines++;
                    }
                    currentValue = fixData[j];
                    prevFixedPositions.add(fixData[j]);
                    break;
                }
            }
            // Re-invert negative values
            if (isNegative)
                currentValue *= -1;

            outLines.add(currentValue);
        }
        return new FixerResult(0, numFixableLines, outLines, FixerResult.Type.PURE_22_5);
    }
}
