package origami.folding.algorithm.swapping;

import origami.folding.element.SubFace;

/**
 * The original Orihime algorithm chooses an initial SubFace ordering to perform
 * the exhaustive search, but it is very commonly the case that the ordering is
 * not optimal, leading to a phenomenon where the search reaches the same
 * dead-end at a certain depth over and over. The idea of swapping algorithm is
 * to swap the order of the SubFace reaching a dead-end to a earlier position,
 * and doing so generally improves ths searching performance.
 * 
 * @author Mu-Tsun Tsai
 */
public class SubFaceSwappingAlgorithm extends SwappingAlgorithm<SubFace> {

    private int lastLow;

    @Override
    protected void onAfterProcess(SubFace[] s, int low, int max) {
        lastLow = low;

		// To further improve performance
		reverseSwap(s, low, low, max, s[low].swapCounter - 1);
    }

    @Override
    protected void onBeforeSwap(int high, int low) {
        System.out.println("swapper.swap(s, " + high + ", " + low + ");");
    }

    @Override
    protected void onSwapOver(SubFace s) {
        s.clearTempGuide();
    }
    
    public boolean shouldEstimate(int s) {
        if (lastLow == 0) return true;
        if (s == lastLow) {
            lastLow = 0;
            return true;
        }
        return false;
    }
}
