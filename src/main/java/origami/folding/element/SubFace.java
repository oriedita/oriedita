package origami.folding.element;

import origami.folding.HierarchyList;
import origami.folding.algorithm.AdditionalEstimationAlgorithm;
import origami.folding.constraint.LayerOrderConstraint;
import origami.folding.permutation.ChainPermutationGenerator;
import origami.folding.permutation.PermutationGenerator;
import origami.folding.permutation.combination.CombinationGenerator;
import origami.folding.util.EquivalenceCondition;
import origami.folding.util.IBulletinBoard;

import java.util.*;

/**
 * This class folds the development view and estimates the overlap information
 * of the planes of the wire diagram.
 */
public class SubFace {

    //Used to utilize records. Use only in the ClassTable class
    int faceIdCount;//The number of Faces (the faces of the unfolded view before folding) that overlap with SubFace (the faces of the wire diagram obtained by folding and estimating).
    int[] faceIdList;//Record the id number of the Face included in the S plane. That this is 20
    // It means that the maximum overlap of paper after folding is 20-1 = 19 sides. // This limit is currently absent 20150309
    PermutationGenerator permutationGenerator;

    int[] FaceId2fromTop_counted_position;// Represents the position counted from the top of the surface (FaceId).
    int[] fromTop_counted_position2FaceId;// Represents the surface at the position counted from the top.

    private Map<Integer, Integer> faceIdMap; // slower, but uses much less memory
    private int[] faceIdMapArray; // faster, but uses more memory

    private List<EquivalenceCondition> uEquivalenceConditions;
    private Map<Integer, List<EquivalenceCondition>> equivalenceConditions;

    IBulletinBoard bb;

    public int swapCounter = 0;

    CombinationGenerator cg;
    int cgTotal = 0;

    public SubFace() {
        reset();
    }

    public SubFace(IBulletinBoard bb0) {
        bb = bb0;
        reset();
    }

    private void reset() {
        faceIdCount = 0;
    }

    //Initial settings for Ketasuu and permutation generators. Don't forget.
    public void setNumDigits(int FIdCount) {
        faceIdCount = FIdCount;

        faceIdList = new int[faceIdCount + 1];
        faceIdMap = new HashMap<>(faceIdCount);

        FaceId2fromTop_counted_position = new int[faceIdCount + 1];//Represents the position counted from the top of the surface (faceIdList).
        fromTop_counted_position2FaceId = new int[faceIdCount + 1];//Represents the surface at the position counted from the top.

        if (FIdCount > 0) {
            permutationGenerator = new ChainPermutationGenerator(faceIdCount);
            // Postpone the reset of the generator until the guides are set
        }
    }

    public int getFaceIdCount() {
        return faceIdCount;
    }

    public int getFaceId(int i) {
        return faceIdList[i];
    }

    public void setFaceId(int i, int faceId) {
        faceIdList[i] = faceId;
        faceIdMap.put(faceId, i);
    }

    public boolean contains(int im) {
        return faceIdMap.containsKey(im);
    }

    public boolean contains(int im1, int im2, int im3, int im4) {
        return contains(im1) && contains(im2) && contains(im3) && contains(im4);
    }

    public int get_Permutation_count() {
        return cgTotal + permutationGenerator.getCount();
    }

    public void Permutation_first() throws InterruptedException {
        if (getFaceIdCount() > 0) {
            cg = null;
            cgTotal = 0;
            permutationGenerator.reset();
        }
    } //Return to the first permutation.


    public int next() {
        return 0;
    }

    // Advance the k-th digit permutation generator and change the overlapping state of the faces to the next state. Normally returns 0.
    // Return 1 if the current overlapping state of the faces is the last one.
    // In this case, the overlapping state of the faces remains the last one.
    public int next(int k) throws InterruptedException {
        return permutationGenerator.next(k);
    }   //<<<<<<<<<<<<<<<<<<<ここは後で機能を強化して高速化したい。
    // ここは　class SubFace の中だよ。

    //Start with the current permutation state and look for possible permutations that overlap
    public int possible_overlapping_search(HierarchyList hierarchyList) throws InterruptedException {// This should not change hierarchyList.
        int mk, ijh;
        ijh = 1;//The initial value of ijh can be anything other than 0.
        while (ijh != 0) { //If ijh == 0, you have reached the end of the digit.
            mk = inconsistent_digits_request(hierarchyList);

            if (permutationGenerator.getCount() > 2000 && cg == null) {
                cg = new CombinationGenerator(this, faceIdMapArray, hierarchyList);
                if (!cg.process()) return 0;
                cg.addGuide(permutationGenerator);
            }

            if (mk == 1000) {
                return 1000;
            }//This SubFace is in a consistent state.

            ijh = next(mk);

            if (ijh == 0 && cg != null) {
                cgTotal += permutationGenerator.getCount();
                permutationGenerator.reset();
                if (!cg.process()) return 0;
                cg.addGuide(permutationGenerator);
                ijh = 1;
            }

            StringBuilder s0 = new StringBuilder();
            for (int i = 1; i <= faceIdCount; i++) {
                s0.append(" : ").append(getPermutation(i));
            }
            bb.rewrite(9, "Tested permutation count : " + get_Permutation_count());
            bb.rewrite(10, "Testing permutation " + s0);
        }
        return 0;//There is no permutation that can overlap
    }

    //Based on the current upper and lower tables, the id number of the i-th surface counting from the top is stored.
    public void set_FaceId2fromTop_counted_position(HierarchyList hierarchyList) {
        for (int i = 1; i <= faceIdCount; i++) {
            FaceId2fromTop_counted_position[i] = 0;
            for (int j = 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[i], faceIdList[j]) == HierarchyList.ABOVE_1) {
                    FaceId2fromTop_counted_position[i] = FaceId2fromTop_counted_position[i] + 1;
                }
            }
            FaceId2fromTop_counted_position[i] = faceIdCount - FaceId2fromTop_counted_position[i];
        }

        for (int iban = 1; iban <= faceIdCount; iban++) {
            for (int i = 1; i <= faceIdCount; i++) {
                if (FaceId2fromTop_counted_position[i] == iban) {
                    fromTop_counted_position2FaceId[iban] = i;
                }
            }
        }
    }

    public int fromTop_count_FaceId(int iban) {
        return faceIdList[fromTop_counted_position2FaceId[iban]];
    }

    //Based on the current top and bottom table, the id number of the i-th surface counting from the top is returned. If you do not use the completed table above and below, the result may be strange.
    private int get_fromTop_count_itino_FaceId(int iban, HierarchyList hierarchyList) {
        set_FaceId2fromTop_counted_position(hierarchyList);
        return faceIdList[fromTop_counted_position2FaceId[iban]];
    }

    private int getPermutation(int i) {
        return permutationGenerator.getPermutation(i);
    }

    public void clearTempGuide() {
        permutationGenerator.clearTempGuide();
    }

    // Check from the top side to find out at what digit the folds are inconsistent.
    // At this time, hierarchyList does not change. Here, the penetration condition of the boundary line of the adjacent surface is not checked.
    // This SubFace returns 1000 if there is no contradiction in the folds.
    private int overlapping_inconsistent_digits_request(HierarchyList hierarchyList) {
        for (int i = 1; i < faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                int I = getPermutation(i);
                int J = getPermutation(j);
                if (hierarchyList.get(faceIdList[I], faceIdList[J]) == HierarchyList.BELOW_0) {
                    // Add a temporary guide to the generator, so that before the current SubFace
                    // runs out of permutation, it won't generate another permutation violating the
                    // same relation over and over. For some CPs this speeds things up like crazy.
                    permutationGenerator.addGuide(J, I);
                    return i;
                }
            }
        }
        return 1000;
    }

    public int FaceId2PermutationDigit(int im) {
        // This function is called very frequently in the search process, and the
        // performance between arrays and hash maps is quite obvious. The problem is
        // that arrays use a lot more memory, but fortunately, only the valid subfaces
        // need to call this method. So the array map is used only here.
        return permutationGenerator.locate(faceIdMapArray[im]);
    }

    public int FaceIdIndex(int im) {
        return faceIdMap.get(im); // Here we assume that im exists.
    }

    // ここは　class SubFace の中だよ。


    // Check from the top surface to find out at what number the boundary line penetration condition of the adjacent surface is inconsistent.
    // At this time, hierarchyList does not change. This SubFace returns 1000 if there is no contradiction in the penetration conditions.
    private int penetration_inconsistent_digits_request(EquivalenceCondition tj, int mm) {
        int M1, M2; //折り畳み推定の際の等価条件の登録は addEquivalenceCondition(im,Mid_min,im,Mid_max); による
        M1 = FaceId2PermutationDigit(tj.getB());
        M2 = FaceId2PermutationDigit(tj.getD());
        if (M1 < mm && mm < M2) {
            return mm;
        }
        return 1000;
    }

    private int penetration_inconsistent_digits_request(HierarchyList hierarchyList, int min) {
        for (int i = 1; i <= faceIdCount && i < min; i++) {
            Iterable<EquivalenceCondition> list = equivalenceConditions.getOrDefault(faceIdList[getPermutation(i)], null);
            // list could be null, be careful.
            if (list != null) {
                for (EquivalenceCondition ec : list) {
                    if (penetration_inconsistent_digits_request(ec, i) < min) {
                        return i;
                    }
                }
            }
        }
        return min;
    }

    private int customConstraintsInconsistentDigitRequest(HierarchyList hierarchyList, int min) {
        for (LayerOrderConstraint lc: hierarchyList.getCustomConstraints()) {
            min = customConstraintInconsistentDigitRequest(lc, min);
        }
        return min;
    }

    private int customConstraintInconsistentDigitRequest(LayerOrderConstraint lc, int min) {
        int a = FaceId2PermutationDigit(lc.getFaceId());
        for (Integer faceID : lc.getOverlapping()) {
            int b = FaceId2PermutationDigit(faceID);
            if (lc.getType() == LayerOrderConstraint.Type.TOP) {
                if (b < min && a < b) {
                    return b;
                }
            }
            if (lc.getType() == LayerOrderConstraint.Type.BOTTOM) {
                if (a < min && b < a) {
                    return a;
                }
            }
        }
        return min;
    }

    // Check from the top surface to find out at what number the two surfaces that share a part of the boundary line and the penetration conditions of the two surfaces are inconsistent.
    // At this time, hierarchyList does not change. This SubFace returns 1000 if there is no contradiction.
    private int u_penetration_inconsistent_digits_request(EquivalenceCondition uj, int min) {
        int a, b, c, d; //Registration of equivalent conditions for folding estimation is based on u_addTouka_jyouken (im1, im2, im3, im4) ;.
        a = FaceId2PermutationDigit(uj.getA());
        b = FaceId2PermutationDigit(uj.getB());
        c = FaceId2PermutationDigit(uj.getC());
        d = FaceId2PermutationDigit(uj.getD());

        if (b < min && a < c && c < b && b < d) { // acbd is not allowed
            return b;
        }
        if (d < min && c < a && a < d && d < b) { // cadb is not allowed
            return d;
        }
        return min;
    }


    private int u_penetration_inconsistent_digits_request(HierarchyList hierarchyList, int min) {
        for (EquivalenceCondition ec : uEquivalenceConditions) {
            min = u_penetration_inconsistent_digits_request(ec, min);
        }
        return min;
    }

    // Check from the top side to find out at what number the contradiction occurs.
    // At this time, hierarchyList does not change. This SubFace returns 1000 if there is no contradiction.
    private int inconsistent_digits_request(HierarchyList hierarchyList) {
        int min;
        min = overlapping_inconsistent_digits_request(hierarchyList);
        min = penetration_inconsistent_digits_request(hierarchyList, min);
        min = u_penetration_inconsistent_digits_request(hierarchyList, min);
        min = customConstraintsInconsistentDigitRequest(hierarchyList, min);
        return min;
    }



    // Enter the information due to the overlap of SubFace's faces in the upper and lower tables
    public void enterStackingOfSubFace(AdditionalEstimationAlgorithm AEA) {
        for (int i = 1; i < faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                AEA.inferAbove(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]);
            }
        }
    }

    // Enter the information due to the overlap of SubFace's faces in the upper and lower tables
    public void enterStackingOfSubFace(HierarchyList hierarchyList) {
        for (int i = 1; i < faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.ABOVE_1);
            }
        }
    }

    /** Prepare a guidebook for the permutation generator in SubFace. */
    public void setGuideMap(HierarchyList hierarchyList) {
        if (Thread.interrupted()) return;
        
        // We setup faceIdMapArray only for valid subfaces to save memory.
        faceIdMapArray = new int[hierarchyList.getFacesTotal() + 1];
        for (int k : faceIdMap.keySet()) {
            faceIdMapArray[k] = faceIdMap.get(k);
        }

        int[] ueFaceId = new int[faceIdCount + 1];
        boolean[] ueFaceIdFlg = new boolean[faceIdCount + 1];//1 if ueFaceId [] is enabled, 0 if disabled

        for (int faceIndex = 1; faceIndex <= faceIdCount; faceIndex++) {
            int ueFaceIdCount = 0;//Stores how many ueFaceId [] are from 1.

            //First, collect the SubFace id number of the upper face in ueFaceId []
            for (int i = 1; i <= faceIdCount; i++) {
                if (hierarchyList.get(faceIdList[i], faceIdList[faceIndex]) == HierarchyList.ABOVE_1) {
                    ueFaceIdCount = ueFaceIdCount + 1;
                    ueFaceId[ueFaceIdCount] = i;
                    ueFaceIdFlg[ueFaceIdCount] = true;
                }
            }

            // Remove guides that are redundant, i.e. finding transitive reduction.
            for (int i = 1; i <= ueFaceIdCount - 1; i++) {
                for (int j = 1; j <= ueFaceIdCount; j++) {
                    if (hierarchyList.get(faceIdList[ueFaceId[i]], faceIdList[ueFaceId[j]]) == HierarchyList.ABOVE_1) {
                        ueFaceIdFlg[i] = false;
                        break;
                    }
                }
            }

            // Add guides
            for (int i = 1; i <= ueFaceIdCount; i++) {
                if (ueFaceIdFlg[i]) {
                    permutationGenerator.addGuide(ueFaceId[i], faceIndex);
                }
            }
            if (Thread.interrupted()) return;
        }

        if (faceIdCount > 0) {
            equivalenceConditions = new HashMap<>();
            for (EquivalenceCondition ec : hierarchyList.getEquivalenceConditions()) {
                if (fastContains(ec)) {
                    equivalenceConditions.computeIfAbsent(ec.getA(), k -> new ArrayList<>()).add(ec);
                }
                if (Thread.interrupted()) return;
            }

            uEquivalenceConditions = new ArrayList<>();
            for (EquivalenceCondition ec : hierarchyList.getUEquivalenceConditions()) {
                if (fastContains(ec)) uEquivalenceConditions.add(ec);
                if (Thread.interrupted()) return;
            }

            try {
                // Now we're ready to reset the generator.
                permutationGenerator.initialize();
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    private boolean fastContains(EquivalenceCondition ec) {
        int a = faceIdMapArray[ec.getA()];
        int b = faceIdMapArray[ec.getB()];
        int c = faceIdMapArray[ec.getC()];
        int d = faceIdMapArray[ec.getD()];
        return a != 0 && b != 0 && c != 0 && d != 0;
    }

    public Iterable<EquivalenceCondition> getEquivalenceConditions() {
        List<EquivalenceCondition> result = new ArrayList<>();
        for (int i = 1; i <= faceIdCount; i++) {
            List<EquivalenceCondition> list = equivalenceConditions.getOrDefault(faceIdList[i], null);
            if (list != null) {
                for (EquivalenceCondition ec : list) result.add(ec);
            }
        }
        return result;
    }

    private boolean uecSorted = false;

    public Iterable<EquivalenceCondition> getUEquivalenceConditions() {
        if(!uecSorted) {
            uEquivalenceConditions.sort(
                    Comparator.comparingInt(EquivalenceCondition::getA).thenComparingInt(EquivalenceCondition::getB)
                            .thenComparingInt(EquivalenceCondition::getC).thenComparingInt(EquivalenceCondition::getD));
            uecSorted = true;
        }
        return uEquivalenceConditions;
    }
}
