package oriedita.editor.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.tinylog.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.handler.step.StepFactory;
import oriedita.editor.handler.step.StepGraph;
import oriedita.editor.handler.step.StepMouseHandler;
import origami.crease_pattern.element.LineSegment;
import origami.folding.util.IBulletinBoard;

@ApplicationScoped
@Handles(MouseMode.FIX_INACCURATE_107)
public class MouseHandlerCreaseFixInaccurate extends StepMouseHandler<MouseHandlerCreaseFixInaccurate.Step> {
    @Inject
    public MouseHandlerCreaseFixInaccurate(IBulletinBoard bb) {
        this.bb = bb;
    }

    private int fixDataSize;
    private double[] fixData;
    IBulletinBoard bb;

    private class FixerResult {
        // Number of lines that were actually fixed. Used for display and to skip fixes that aren't necessary
        long numFixedLines;   
        // Number of lines that are theoretically fixable. Used to compare/determine algorithms      
        long numFixableLines;       
        ArrayList<Double> lines;
        Type type;
        enum Type {
            BP, PURE_22_5, Other
        }

        FixerResult(long numFixedLines, long numFixableLines, ArrayList<Double> lines, Type type) {
            this.numFixedLines = numFixedLines;
            this.numFixableLines = numFixableLines;
            this.lines = lines;
            this.type = type;
        }
    }

    private class Xform {
        boolean isSquare;
        boolean inDefaultSquare; // All positions lie within (-200|-200) - (200|200)
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

    public enum Step {
        SELECT_LINES
    }

    private Xform getXform(Collection<LineSegment> lines) {
        double allowedErrror = 0.001;
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
        boolean isSquare = Math.abs(Math.abs(minY-maxY) - Math.abs(minX-maxX)) < allowedErrror;
        boolean inDefaultSqaure = (minX > -(200+allowedErrror)) && 
                                  (minY > -(200+allowedErrror)) && 
                                  (maxX <  (200+allowedErrror)) && 
                                  (maxY <  (200+allowedErrror));               
        double midX = minX + Math.abs(maxX-minX)/2; 
        double midY = minY + Math.abs(maxY-minY)/2; 
        double scale = 400/Math.abs(maxX-minX);
        return new Xform(isSquare, inDefaultSqaure, scale, midX, midY);
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

    private double undoXformCalc(double pos, double allowedErrror) {
        double close = (double)Math.round(pos);
            if(Math.abs(close - pos) < allowedErrror) 
                return close;
            else                                       
                return pos;
    }

    private ArrayList<Double> undoXform (ArrayList<Double> lines, Xform xform) {
        double allowedErrror = 0.000000000001;
        if(xform.isSquare && !xform.inDefaultSquare) {
            ArrayList<Double> out = new ArrayList<>();
            for (int i = 0; i<lines.size(); i+=4) {
                // The rescaling introduced a slight error, fix near intetgers to make the save file prettier.
                double pos = lines.get(i+0)/xform.scale + xform.deltaX;
                out.add((Double)undoXformCalc(pos,allowedErrror));

                pos = lines.get(i+1)/xform.scale + xform.deltaY;
                out.add((Double)undoXformCalc(pos,allowedErrror));

                pos = lines.get(i+2)/xform.scale + xform.deltaX;
                out.add((Double)undoXformCalc(pos,allowedErrror));

                pos = lines.get(i+3)/xform.scale + xform.deltaY;
                out.add((Double)undoXformCalc(pos,allowedErrror));
            }
            return out;
        }
        else
            return lines;
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
        FixerResult result = fix(toFix);

        
        if((result.type == FixerResult.Type.PURE_22_5) && !xform.inDefaultSquare && !xform.isSquare)
            bb.write("WARNING: Fix may be bad. Try to fix 22.5° CPs inside the default square or as square CP");

        result.lines = undoXform(result.lines, xform);

        int i = 0;
        var fls = d.getFoldLineSet();
        for(LineSegment ls : selectedLines) {
            fls.deleteLine(ls);
            LineSegment ls2 = ls.withCoordinates(result.lines.get(i+0), 
                                                 result.lines.get(i+1), 
                                                 result.lines.get(i+2), 
                                                 result.lines.get(i+3));
            fls.addLine(ls2);
            i += 4;
        }

        fls.divideLineSegmentWithNewLines(fls.getTotal() - lines.size(), fls.getTotal());

        // Record new state and display changed line number when 1+ lines changed
        if(result.numFixedLines > 0) {
            d.record();
            bb.write("Fixed " + result.numFixedLines + " lines");
            new Thread(() -> {
                try {
                    if((result.type == FixerResult.Type.PURE_22_5) && !xform.inDefaultSquare && !xform.isSquare) {
                        Thread.sleep(15000);
                        bb.clear();
                    }
                    else {
                        Thread.sleep(5000);
                        bb.clear();
                    }
                } catch (InterruptedException ex) {}
            }).start();
        }
        else {
            bb.write("No fix needed");
            new Thread(() -> {
                try {
                        Thread.sleep(5000);
                        bb.clear();
                } catch (InterruptedException ex) {}
            }).start();
        }
        d.check4();
    }

    private FixerResult fix(ArrayList<Double> toFix) {

        double precision22_5 = 0.0004;

        // Fix BP first
        FixerResult resultBP = fixBP(toFix);
        // Exit early if it's probably boxpleated
        if(resultBP.numFixableLines > (toFix.size()/4 * .9))
            return resultBP;

        // Fix 22.5
        loadData("fixData_22_5.bin");
        FixerResult result22_5 = fixWithData(toFix, precision22_5);

        /* To load external file, keep just in case we decide against packing the 60mb generic fix file into the jar
        if (genericFix) {
            // Check if fixDataGeneric.bin exists, download if not
            File f = new File(dataFilePath);
            if (!f.exists()) {
                try {
                    URI uri = new URI(downloadPath);
                    URL in = uri.toURL();
                    try (ReadableByteChannel readableByteChannel = Channels.newChannel(in.openStream());
                         FileOutputStream fileOutputStream = new FileOutputStream(dataFilePath)) {
                        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    }
                } catch (IOException | URISyntaxException ioe3) {
                    // Failed to download
                }
            }
            try {
                mapData("fixDataGeneric.bin");
            } catch (IOException ioe_generic) {
                // Failed to access file
            }

            result2 = fixGeneric(toFix, precisionGeneric);
        }*/

        if (resultBP.numFixableLines > result22_5.numFixableLines)
            return resultBP;
        else
            return result22_5;
    }

    // Map data into an array
    private void loadData(String file){ 
        try {
            var stream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file));
            byte[] bytes = stream.readAllBytes();

            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            DoubleBuffer db = byteBuffer.asDoubleBuffer();
            fixDataSize = db.remaining();
            fixData = new double[fixDataSize];
            db.get(fixData);
        } catch (IOException | NullPointerException e) {
            Logger.error(e);
        }
    }

    // Fix BP
    private FixerResult fixBP(ArrayList<Double> toFix) {
        // Output
        ArrayList<Double> outLines = new ArrayList<>();

        // Don't fix positions that are less than this much off
        double allowedError = 0.00000000001;

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
        final float gridSearchEndPercent = .9f; // Arbitrary value, keep under 1.0f
        final float neccesaryImprovementGrid = 1.15f; // Arbitrary value

        // Fixed lines counter logic
        boolean isLineFixed = false;
        long numFixableLines = 0;
        long numFixedLines = 0;

        // Automatic grid search algorithm
        for (int gridIteration = 1; gridIteration <= 16; gridIteration++) {
            // Reset here because it shouldn't be overwritten at the end of grid search
            numFixableLines = 0;

            switch (gridIteration) {
                // Ordered by likelihood
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
                nearestInt = (double) Math.round(currentValue);
                if (Math.abs(currentValue - nearestInt) > precision)
                    continue;
                // Actual fixing happens later so we only need to
                // increment the line counter.
                if (!isLineFixed) {
                    isLineFixed = true;
                    numFixableLines++;
                }
            }

            // Only overwrites old grid solution if the new one has 10% more matches (arbitrary value)
            if (numFixableLines > (numLinesFixedWithPrevBestGrid) * neccesaryImprovementGrid) {
                gridSize = gridSizeSearch;
                numLinesFixedWithPrevBestGrid = numFixableLines;
            }

            // Ends grid search prematurely if it finds a close match
            if (numFixableLines > ((toFix.size()/4) * gridSearchEndPercent))
                endGridSearch = true;

            // Resets value for next iteration/actual fixing
            isLineFixed = false;

            if (endGridSearch)
                break;
        }

        // Fixing algorithm
        for (int i = 0; i < toFix.size(); i++) {
            currentValue = toFix.get(i);

            // Reset line counter logic
            if ((i % 4) == 0)
                    isLineFixed = false;
  
            // Scales the position for fixing
            currentValue = currentValue / 200 * gridSize;

            // Round to nearest integer
            nearestInt = (double) Math.round(currentValue);
            if (Math.abs(currentValue - nearestInt) < precision) {
                if(Math.abs(currentValue - nearestInt) > allowedError) {
                    if (!isLineFixed) {
                        isLineFixed = true;
                        numFixedLines++;
                    }
                    currentValue = nearestInt;
                }
            }           

            // Scale back
            currentValue = currentValue * 200 / gridSize;
            outLines.add(currentValue);
        }

        return new FixerResult(numFixedLines, numFixableLines, outLines, FixerResult.Type.BP);
    }

    // Fix with given data file
    private FixerResult fixWithData(ArrayList<Double> inLines, double precision) {
        ArrayList<Double> outLines = new ArrayList<>();

        // For storing already used positions
        ArrayList<Double> prevFixedPositions = new ArrayList<>();

        // Don't fix positions that are less than this much off
        double allowedError = 0.00000000001;

        // Variables for the fixing algorithm
        double currentValue;
        boolean isNegative;
        boolean skipSlow;
        
        // Fixed lines counter logic
        boolean isLineFixed = false;
        long numFixableLines = 0;
        long numFixedLines = 0;

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
            for (int j = 0; j < prevFixedPositions.size(); j++) {
                if (Math.abs(currentValue - prevFixedPositions.get(j)) > precision)
                    continue;
                if (Math.abs(currentValue - prevFixedPositions.get(j)) > allowedError) {
                    currentValue = prevFixedPositions.get(j);
                    if (!isLineFixed) {
                        isLineFixed = true;
                        numFixableLines++;  
                        numFixedLines++;
                        skipSlow = true;
                        break;
                    }
                }
                else if (!isLineFixed) {
                    isLineFixed = true;
                    numFixableLines++;      
                    break;
                }

            }
            // If the position wasn't previously fixed already go through all possible positions
            if (skipSlow == false) {
                for (int j = 0; j < fixDataSize; j++) {
                    if (Math.abs(currentValue - fixData[j]) > precision)
                        continue;
                    if (Math.abs(currentValue - fixData[j]) > allowedError) {
                        currentValue = fixData[j];
                        prevFixedPositions.add(fixData[j]);
                        if (!isLineFixed) {
                            isLineFixed = true;
                            numFixableLines++;  
                            numFixedLines++;
                            break;
                        }
                    }
                    else if (!isLineFixed) {
                        isLineFixed = true;
                        numFixableLines++;      
                        break;
                    }
                }
            }
            // Re-invert negative values
            if (isNegative)
                currentValue *= -1;

            outLines.add(currentValue);
        }
        return new FixerResult(numFixedLines, numFixableLines, outLines, FixerResult.Type.PURE_22_5);
    }
}
