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
        long numFixedLines;
        ArrayList<Double> lines;

        FixerResult(long numFixedLines, ArrayList<Double> lines) {
            this.numFixedLines = numFixedLines;
            this.lines = lines;
        }
    }

    public enum Step {
        SELECT_LINES
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

    private void fixWrapper(Collection<LineSegment> lines) {
        if (lines.isEmpty()) {return;}

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

        int i = 0;
        LineSegment ls2;
        var fls = d.getFoldLineSet();
        // Delete selected lines
        for(LineSegment ls : lines) {
            fls.deleteLine(ls);
            ls2 = ls.withCoordinates(result.lines.get(i), 
                                     result.lines.get(i+1), 
                                     result.lines.get(i+2), 
                                     result.lines.get(i+3));
            fls.addLine(ls2);
            i += 4;
        }

        fls.divideLineSegmentWithNewLines(fls.getTotal() - lines.size(), fls.getTotal());

        // Record new state and display nuimber of lines changed
        // only if any lines changed.
        if(result.numFixedLines > 0) {
            d.record();
            bb.write("Fixed " + result.numFixedLines + " lines");
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    bb.clear();
                } catch (InterruptedException ex) {}
            }).start();
        }
        d.check4();
    }

    private FixerResult fix(ArrayList<Double> toFix) {

        double precisionGeneric = 0.0004;
        // Fix BP first
        FixerResult result1 = fixBP(toFix);
        // Fix 22.5
        loadData("fixData_22_5.bin");
        FixerResult result2 = fixWithData(toFix, precisionGeneric);

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

        if (result1.numFixedLines > result2.numFixedLines)
            return result1;
        else
            return result2;
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

        // Fixing
        int gridSize = 0;
        double currentValue, nearestInt;
        final double basePrecision = 0.0013; // Arbitrary value. Derived from testing
        // Since the fixing involves scaling, the precision needs to be adjusted
        double precision = (basePrecision * gridSize) / 200.0;

        // Grid search
        int gridSizeSearch = 0;
        long numLinesFixedWithPrevBestGrid = 0;
        boolean endGridSearch = false;
        final float gridSearchEndPercent = .9f; // Arbitrary value, keep under 1.0f
        final float neccesaryImprovementGrid = 1.15f; // Arbitrary value

        // Fixed lines counter logic
        boolean isLineFixed = false;
        long fixedLineCounter = 0;

        // Automatic grid search algorithm
        for (int gridIteration = 1; gridIteration <= 16; gridIteration++) {
            // Reset here because it shouldn't be overwritten at the end of grid search
            fixedLineCounter = 0;

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
                if (Math.abs(currentValue - nearestInt) <= precision) {
                    // Actual fixing happens later so we only need to
                    // increment the line counter.
                    if (!isLineFixed) {
                        isLineFixed = true;
                        fixedLineCounter++;
                    }
                }
            }

            // Only overwrites old grid solution if the new one has 10% more matches (arbitrary value)
            if (fixedLineCounter > (numLinesFixedWithPrevBestGrid) * neccesaryImprovementGrid) {
                gridSize = gridSizeSearch;
                numLinesFixedWithPrevBestGrid = fixedLineCounter;
            }

            // Ends grid search prematurely if it finds a close match
            if (fixedLineCounter > ((toFix.size()/4) * gridSearchEndPercent))
                endGridSearch = true;

            // Resets value for next iteration/actual fixing
            isLineFixed = false;

            if (endGridSearch)
                break;
        }

        // Fixing algorithm
        for (int i = 0; i < toFix.size(); i++) {
            currentValue = toFix.get(i);
  
            // Scales the position for fixing
            currentValue = currentValue / 200 * gridSize;

            // Round to nearest integer
            nearestInt = (double) Math.round(currentValue);
            if (Math.abs(currentValue - nearestInt) <= precision)
                currentValue = nearestInt;

            // Scale back
            currentValue = currentValue * 200 / gridSize;
            outLines.add(currentValue);
        }

        return new FixerResult(fixedLineCounter, outLines);
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
        long fixedLineCounter = 0;


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
                if (Math.abs(currentValue - prevFixedPositions.get(j)) <= precision) {
                    currentValue = prevFixedPositions.get(j);
                    // Increment fixed lines if not already fixed
                    if (!isLineFixed) {
                        isLineFixed = true;
                        fixedLineCounter++;
                    }
                    skipSlow = true;
                    break;
                }
            }
            // If the position wasn't previously fixed already go through all possible positions
            if (skipSlow == false) {
                for (int j = 0; j < fixDataSize; j++) {
                    if (Math.abs(currentValue - fixData[j]) <= precision) {
                        currentValue = fixData[j];

                        // Write fixed value into alreadyFixed database
                        prevFixedPositions.add(fixData[j]);
                        // Increment fixed lines if not already fixed
                        if (!isLineFixed) {
                            isLineFixed = true;
                            fixedLineCounter++;
                        }
                        break;
                    }
                }
            }
            // Re-invert negative values
            if (isNegative)
                currentValue *= -1;

            outLines.add(currentValue);
        }
        return new FixerResult(fixedLineCounter, outLines);
    }
}

