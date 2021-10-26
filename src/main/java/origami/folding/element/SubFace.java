package origami.folding.element;

import origami.folding.HierarchyList;
import origami.folding.util.EquivalenceCondition;
import origami.folding.permutation.ChainPermutationGenerator;
import origami.folding.permutation.PermutationGenerator;
import origami_editor.editor.component.BulletinBoard;

public class SubFace {//This class folds the development view and estimates the overlap information of the planes of the wire diagram.
    //Used to utilize records. Use only in the ClassTable class
    int faceIdCount;//The number of Faces (the faces of the unfolded view before folding) that overlap with SubFace (the faces of the wire diagram obtained by folding and estimating).
    int[] faceIdList;//Record the id number of the Face included in the S plane. That this is 20
    // It means that the maximum overlap of paper after folding is 20-1 = 19 sides. // This limit is currently absent 20150309
    PermutationGenerator permutationGenerator;

    int[] FaceId2fromTop_counted_position;// Represents the position counted from the top of the surface (FaceId).
    int[] fromTop_counted_position2FaceId;// Represents the surface at the position counted from the top.

    int[] faceIdMap;// For fast lookup

    BulletinBoard bb;

    public SubFace() {
        reset();
    }

    public SubFace(BulletinBoard bb0) {
        bb = bb0;
        reset();
    }

    private void reset() {
        faceIdCount = 0;
    }

    //Initial settings for Ketasuu and permutation generators. Don't forget.
    public void setNumDigits(int FIdCount, int faceTotal) {
        faceIdCount = FIdCount;

        faceIdList = new int[faceIdCount + 1];
        faceIdMap = new int[faceTotal + 1];

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
        faceIdMap[faceId] = i;
    }

    public boolean contains(int im1, int im2, int im3, int im4) {
        return faceIdMap[im1] > 0 && faceIdMap[im2] > 0 && faceIdMap[im3] > 0 && faceIdMap[im4] > 0;
    }

    public int get_Permutation_count() {
        return permutationGenerator.getCount();
    }

    public void Permutation_first() throws InterruptedException {
        if (getFaceIdCount() > 0) {
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
    public int possible_overlapping_search(HierarchyList hierarchyList) throws InterruptedException {//This should not change hierarchyList.
        int mk, ijh;
        ijh = 1;//The initial value of ijh can be anything other than 0.
        while (ijh != 0) { //If ijh == 0, you have reached the end of the digit.
            mk = inconsistent_digits_request(hierarchyList);

            if (mk == 1000) {
                return 1000;
            }//This SubFace is in a consistent state.

            ijh = next(mk);

            StringBuilder s0 = new StringBuilder();
            for (int i = 1; i <= faceIdCount; i++) {
                s0.append(" : ").append(getPermutation(i));
            }
            bb.rewrite(9, "Tested permutation count : " + permutationGenerator.getCount());
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

    //Find the number from the top in the stacking order of the surface im. Returns 0 if this SubFace does not contain Men.
    public int FaceId2PermutationDigit(int im) {
        // This is now done in two places; permutationGenerator keeps its own map, so
        // that SubFace only need to keep an immutable faceIdeMap, and no resetting is
        // required.
        return permutationGenerator.locate(faceIdMap[im]);
    }

    public int FaceIdIndex(int im) {
        return faceIdMap[im];
    }

    // ここは　class SubFace の中だよ。


    // Check from the top surface to find out at what number the boundary line penetration condition of the adjacent surface is inconsistent.
    // At this time, hierarchyList does not change. This SubFace returns 1000 if there is no contradiction in the penetration conditions.
    private int penetration_inconsistent_digits_request(EquivalenceCondition tj, int mm) {
        int M1, M2; //折り畳み推定の際の等価条件の登録は addEquivalenceCondition(im,Mid_min,im,Mid_max); による
        M1 = FaceId2PermutationDigit(tj.getB());
        M2 = FaceId2PermutationDigit(tj.getD());
        if (M1 * M2 == 0) {
            return 1000;
        }
        if ((mm - M1) * (mm - M2) < 0) {
            return mm;
        }
        return 1000;
    }

    private int penetration_inconsistent_digits_request(HierarchyList hierarchyList, int min) {
        for (int i = 1; i <= faceIdCount && i < min; i++) {
            Iterable<EquivalenceCondition> list = hierarchyList.getEquivalenceConditions(faceIdList[getPermutation(i)]);
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


    // Check from the top surface to find out at what number the two surfaces that share a part of the boundary line and the penetration conditions of the two surfaces are inconsistent.
    // At this time, hierarchyList does not change. This SubFace returns 1000 if there is no contradiction.
    private int u_penetration_inconsistent_digits_request(EquivalenceCondition uj, int min) {
        int mi1, mi2, mj1, mj2, itemp; //Registration of equivalent conditions for folding estimation is based on u_addTouka_jyouken (im1, im2, im3, im4) ;.
        mi1 = FaceId2PermutationDigit(uj.getA());
        mi2 = FaceId2PermutationDigit(uj.getB());
        if (mi2 < mi1) {
            itemp = mi1;
            mi1 = mi2;
            mi2 = itemp;
        }

        mj1 = FaceId2PermutationDigit(uj.getC());
        mj2 = FaceId2PermutationDigit(uj.getD());
        if (mj2 < mj1) {
            itemp = mj1;
            mj1 = mj2;
            mj2 = itemp;
        }

        if (mi1 * mi2 * mj1 * mj2 != 0) {
            if (mi2 < min && ((mi1 < mj1) && (mj1 < mi2)) && (mi2 < mj2)) {
                return mi2;
            }
            if (mj2 < min && ((mj1 < mi1) && (mi1 < mj2)) && (mj2 < mi2)) {
                return mj2;
            }
        }
        return min;
    }

    private int u_penetration_inconsistent_digits_request(HierarchyList hierarchyList, int min) {
        for (EquivalenceCondition ec : hierarchyList.getUEquivalenceConditions()) {
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
        return min;
    }

    // Enter the information due to the overlap of SubFace's faces in the upper and lower tables
    public void hierarchyList_at_subFace_wo_input(HierarchyList hierarchyList) {
        for (int i = 1; i < faceIdCount; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.ABOVE_1);
            }
        }
    }

    /** Prepare a guidebook for the permutation generator in SubFace. */
    public void setGuideMap(HierarchyList hierarchyList) throws InterruptedException {
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

        }

        if (faceIdCount > 0) {
            // Now we're ready to reset the generator.
            permutationGenerator.initialize();
        }
    }
}
