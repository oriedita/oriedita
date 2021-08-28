package origami_editor.editor.hierarchylist_worker.subface.overlapping_permutation_generator.guidemap;

/**
 * Guide. Create in SubFace. Register the face directly above a face in front of the permutations with repetition machine
 * Image of guide map. Look at the map and decide what to do next. The number that came out by the way that each side is now. The current state of the road is not counted.
 */
public class GuideMap {
    // guide [i] [j] represents the surface that should have appeared before the surface i appeared. guide [i] [0] is the number of such faces.
    int[][] guide;
    int numDigits;

    public GuideMap(int digit) {
        numDigits = digit;

        guide = new int[digit + 11][50];
        for (int i = 0; i <= digit + 10; i++) {
            guide[i][0] = 0;
        }
    }

    public void add(int Menidid, int ueMenidid) {
        guide[Menidid][0] = guide[Menidid][0] + 1;
        guide[Menidid][guide[Menidid][0]] = ueMenidid;
    }

    public int get(int faceIndex, int i) {
        return guide[faceIndex][i];
    }
}
