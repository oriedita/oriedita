package origami;

import origami.crease_pattern.OritaCalc;

/**
 * This class organizes all epsilon constants used throughout the source code.
 * Hopefully it will eventually replace all related comparisons.
 *
 * @author Mu-Tsun Tsai
 */
public class Epsilon {

    /**
     * In the following, all constants after "factor" are the original epsilon constants used by Orihime
     * (except for those in the "modified" section).
     * Those epsilons are, however, too big for super-complex models such as full Ryujin with shaped scales.
     * Before we have a better understanding of the purpose of these different epsilons,
     * let's just multiply all of them by a factor to fix this problem.
     * By using a factor of 0.01, that essentially means all CPs are now 100x larger than the origin.
     */
    private static final double factor = 0.01;

    // These are the constants of which purpose is uncertain.
    // Most likely, they don't actually have a fixed meaning throughout the code base,
    // and in each of their use cases it just happen to require an epsilon of that magnitude.

    public static final double UNKNOWN_05 = factor * 0.5;
    public static final double UNKNOWN_001 = factor * 1E-2;
    public static final double UNKNOWN_0001 = factor * 1E-3;
    public static final double UNKNOWN_1EN4 = factor * 1E-4;
    public static final double UNKNOWN_1EN5 = factor * 1E-5;
    public static final double UNKNOWN_1EN6 = factor * 1E-6;
    public static final double UNKNOWN_1EN7 = factor * 1E-7;

    // These are the constants with a known purpose.

    public static final double PARALLEL_FOR_EDIT = factor * 0.1;
    public static final double PARALLEL_FOR_FIX = factor * 0.5; // TODO: do we need two parallel comparison standards?
    public static final double FLAT = factor * 1E-4;
    public static final double QUAD_TREE_ITEM = factor * 0.5;
    public static final double GRID_ANGLE_THRESHOLD = 1;
    public static final double VECTOR_NORMALIZE_THRESHOLD = 1E-4;
    public static final double AXIOM_THRESHOLD = 1E-6;


    /**
     * For the most part, this is the smallest epsilon used in the code. Any value
     * that is even smaller is considered zero.
     */
    private static final double ZERO_COMPARISON = factor * 1E-8;

    /**
     * Tsai: This is used only in {@link OritaCalc#isInside_sweet}. For some
     * Ryujin-type CPs, even {@link Epsilon#ZERO_COMPARISON} is not small enough and would
     * lead to false positive result, causing the model to have no solution. But on
     * the other hand, if the value is too small (say factor * 1E-12), then some
     * other CPs may have false negative result, causing invalid solutions to be
     * found. So I settled with this particular value which seems to work best.
     */
    public static final double SWEET_DISTANCE = factor * 1E-10;

    // These are the constants that has been modified from the original values.

    public static final double POINT = factor * 0.025; // Originally the value was 0.1

    /**
     * This is the default instance of the Epsilon class. In the future I expect
     * more instance are created for different purposes.
     */
    public static final Epsilon high = new Epsilon(ZERO_COMPARISON);

    ////////////////////////////////////////////////////////////////////////////////////
    // Instance
    ////////////////////////////////////////////////////////////////////////////////////

    private final double precision;

    private Epsilon(double precision) {
        this.precision = precision;
    }

    public boolean eq0(double value) {
        return -precision < value && value < precision;
    }

    public boolean le0(double value) {
        return value < precision;
    }

    public boolean gt0(double value) {
        return value > precision;
    }
}
