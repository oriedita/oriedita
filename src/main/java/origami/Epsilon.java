package origami;

/**
 * Author: Mu-Tsun Tsai
 * 
 * This class organizes all epsilon constants used throughout the source code.
 * Hopefully it will eventually replace all related comparisons.
 */
public class Epsilon {

    /**
     * In the following, all constants after "factor" are the original epsilon
     * constants used by Orihime. Those epsilons are, however, too big for
     * super-complex models such as full Ryujin with shaped scales. Before we have a
     * better understanding of the purpose of these different epsilons, let's just
     * multiply all of them by a factor to fix this problem. By using a factor of
     * 0.1, that essentially means all CPs are now 10x larger than the origin.
     */
    private static final double factor = 0.1;

    // These are the constants of which purpose is uncertain.
 
    public static final double UNKNOWN_01 = factor * 0.1;
    public static final double UNKNOWN_05 = factor * 0.5;
    public static final double UNKNOWN_001 = factor * 1E-2;
    public static final double UNKNOWN_0001 = factor * 1E-3;
    public static final double UNKNOWN_1EN4 = factor * 1E-4;
    public static final double UNKNOWN_1EN5 = factor * 1E-5;
    public static final double UNKNOWN_1EN6 = factor * 1E-6;
    public static final double UNKNOWN_1EN7 = factor * 1E-7;

    // These are the constants with a known purpose.

    public static final double PARALLEL = factor * 0.5;
    public static final double QUAD_TREE_ITEM = factor * 0.5;

    /**
     * This is the smallest epsilon used in the code. Any value that is even smaller
     * is considered zero.
     */
    private static final double ZERO_COMPARISON = factor * 1E-8;

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
