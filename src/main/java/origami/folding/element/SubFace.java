package origami.folding.element;

import origami.folding.HierarchyList;
import origami.folding.util.EquivalenceCondition;
import origami.folding.Overlapping_Permutation_generator;
import origami_editor.editor.component.BulletinBoard;

public class SubFace {//This class folds the development view and estimates the overlap information of the planes of the wire diagram.
    //Used to utilize records. Use only in the ClassTable class
    int faceIdCount;//The number of Faces (the faces of the unfolded view before folding) that overlap with SubFace (the faces of the wire diagram obtained by folding and estimating).
    int[] faceIdList;//Record the id number of the Face included in the S plane. That this is 20
    // It means that the maximum overlap of paper after folding is 20-1 = 19 sides. // This limit is currently absent 20150309
    Overlapping_Permutation_generator permutationGenerator;

    int Permutation_count = 1;

    int[] FaceId2fromTop_counted_position;// Represents the position counted from the top of the surface (FaceId).
    int[] fromTop_counted_position2FaceId;// Represents the surface at the position counted from the top.

    int[] FaceId2PermutationDigitMap;// For fast lookup
    int[] CleanMap;// For fast reset

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
    public void setNumDigits(int FIdCount) {
        faceIdCount = FIdCount;

        faceIdList = new int[faceIdCount + 1];

        FaceId2fromTop_counted_position = new int[faceIdCount + 1];//Represents the position counted from the top of the surface (faceIdList).
        fromTop_counted_position2FaceId = new int[faceIdCount + 1];//Represents the surface at the position counted from the top.

        for (int i = 0; i <= faceIdCount; i++) {
            faceIdList[i] = 0;

            FaceId2fromTop_counted_position[i] = 0;
            fromTop_counted_position2FaceId[i] = 0;
        }
        if (FIdCount > 0) {
            permutationGenerator = new Overlapping_Permutation_generator(faceIdCount);
            Permutation_first();
        }
    }

    public int getFaceIdCount() {
        return faceIdCount;
    }

    public int getFaceId(int i) {
        return faceIdList[i];
    }

    public void setFaceId(int i, int Mid) {
        faceIdList[i] = Mid;
    }

    public int get_Permutation_count() {
        return Permutation_count;
    }

    public void Permutation_first() {
        if (getFaceIdCount() > 0) {
            permutationGenerator.reset();
            Permutation_count = 1;
        }
    } //Return to the first permutation.


    public int next() {
        return 0;
    }

    // Advance the k-th digit permutation generator and change the overlapping state of the faces to the next state. Normally returns 0.
    // Return 1 if the current overlapping state of the faces is the last one.
    // In this case, the overlapping state of the faces remains the last one.
    public int next(int k) {
        Permutation_count = Permutation_count + 1;
        return permutationGenerator.next(k);
    }   //<<<<<<<<<<<<<<<<<<<ここは後で機能を強化して高速化したい。
    // ここは　class SubFace の中だよ。

    public void reset_map(int count) {
        if (FaceId2PermutationDigitMap == null || FaceId2PermutationDigitMap.length != count + 1) {
            FaceId2PermutationDigitMap = new int[count + 1];
            CleanMap = new int[count + 1];
        } else {
            // This is the fastest way to cleanup an array
            System.arraycopy(CleanMap, 0, FaceId2PermutationDigitMap, 0, count + 1);
        }
        for (int i = 1; i <= faceIdCount; i++) {
            FaceId2PermutationDigitMap[faceIdList[getPermutation(i)]] = i;
        }
    }

    //Start with the current permutation state and look for possible permutations that overlap
    public int possible_overlapping_search(HierarchyList hierarchyList) {//This should not change hierarchyList.
        int mk, ijh;
        ijh = 1;//The initial value of ijh can be anything other than 0.
        while (ijh != 0) { //If ijh == 0, you have reached the end of the digit.
            reset_map(hierarchyList.getFacesTotal());
            mk = inconsistent_digits_request(hierarchyList);

            if (mk == 1000) {
                return 1000;
            }//This SubFace is in a consistent state.

            ijh = next(mk);

            StringBuilder s0 = new StringBuilder();
            for (int i = 1; i <= faceIdCount; i++) {
                s0.append(" : ").append(getPermutation(i));
            }
            bb.rewrite(10, "Smen_kanou_kasanari_sagasi(hierarchyList) =  " + s0);
        }
        return 0;//There is no permutation that can overlap
    }

    //Based on the current upper and lower tables, the id number of the i-th surface counting from the top is stored.
    public void set_FaceId2fromTop_counted_position(HierarchyList hierarchyList) {
        for (int i = 1; i <= faceIdCount; i++) {
            FaceId2fromTop_counted_position[i] = 0;
            for (int j = 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[i], faceIdList[j]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
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

    // Check from the top side to find out at what digit the folds are inconsistent.
    // At this time, hierarchyList does not change. Here, the penetration condition of the boundary line of the adjacent surface is not checked.
    // This SubFace returns 1000 if there is no contradiction in the folds.
    private int overlapping_inconsistent_digits_request(HierarchyList hierarchyList) {
        for (int i = 1; i <= faceIdCount - 1; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                    return i;
                }
            }
        }
        return 1000;
    }

    //Find the number from the top in the stacking order of the surface im. Returns 0 if this SubFace does not contain Men.
    public int FaceId2PermutationDigit(int im) {
        return FaceId2PermutationDigitMap[im];
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
        for (int i = 1; i <= faceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.HierarchyListCondition.UNKNOWN_0);
            }

            for (int j = i + 1; j <= faceIdCount; j++) {
                hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.HierarchyListCondition.UNKNOWN_1);
            }
        }
    }


    // Enter the information due to the overlap of the SubFace surfaces in the upper and lower tables. This is used to find the valid number of SubFaces during the initial calculation preparation.
    public void hierarchyList_ni_subFace_no_manager_wo_input(HierarchyList hierarchyList) {
        for (int i = 1; i <= faceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
                    hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.HierarchyListCondition.UNKNOWN_N50);
                }
            }

            for (int j = i + 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
                    hierarchyList.set(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)], HierarchyList.HierarchyListCondition.UNKNOWN_N50);
                }
            }
        }
    }

    //上下表にSubFaceによって何個の新情報が入るかを返す。
    //Returns how many new information SubFace will put in the top and bottom tables.
    public int sinki_jyouhou_suu(HierarchyList hierarchyList) {
        int inew = 0;
        for (int i = 1; i <= faceIdCount; i++) {
            for (int j = 1; j <= i - 1; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
                    inew = inew + 1;
                }
            }

            for (int j = i + 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
                    inew = inew + 1;
                }
            }
        }
        return inew;
    }

    // hierarchyList[][] treats the hierarchical relationship between all the faces of the crease pattern before folding as one table.
    // If hierarchyList[i][j] is 1, surface i is above surface j. If it is 0, it is the lower side.
    // If hierarchyList[i][j] is -50, faces i and j overlap, but the hierarchical relationship is not determined.
    // If hierarchyList[i][j] is -100, then faces i and j do not overlap.
    public void setGuideMap(HierarchyList hierarchyList) { //I will prepare a guidebook for the permutations with repeat generator in SubFace.
        int[] ueFaceId = new int[faceIdCount + 1];
        boolean[] ueFaceIdFlg = new boolean[faceIdCount + 1];//1 if ueFaceId [] is enabled, 0 if disabled

        for (int faceIndex = 1; faceIndex <= faceIdCount; faceIndex++) {
            int ueFaceIdCount = 0;//Stores how many ueFaceId [] are from 1.

            //First, collect the SubFace id number of the upper surface in ueFaceId []
            for (int i = 1; i <= faceIdCount; i++) {
                if (hierarchyList.get(faceIdList[i], faceIdList[faceIndex]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                    ueFaceIdCount = ueFaceIdCount + 1;
                    ueFaceId[ueFaceIdCount] = i;
                    ueFaceIdFlg[ueFaceIdCount] = true;
                }
            }

            //Set ueFaceIdFlg [id] of the id number to be invalid to 0.
            for (int i = 1; i <= ueFaceIdCount - 1; i++) {
                for (int j = i + 1; j <= ueFaceIdCount; j++) {
                    if (hierarchyList.get(faceIdList[ueFaceId[i]], faceIdList[ueFaceId[j]]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                        ueFaceIdFlg[i] = false;
                    }
                    if (hierarchyList.get(faceIdList[ueFaceId[j]], faceIdList[ueFaceId[i]]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                        ueFaceIdFlg[j] = false;
                    }
                }
            }

            //Store in guidebook
            for (int i = 1; i <= ueFaceIdCount; i++) {
                if (ueFaceIdFlg[i]) {
                    permutationGenerator.addGuide(faceIndex, ueFaceId[i]);
                }
            }

        }

    }

    //According to the table above and below, the overlapping classification of pairs of faces included in this SubFace is undecided.
    public int overlapping_classification_pending(HierarchyList hierarchyList) {
        int iret = 0;
        for (int i = 1; i <= faceIdCount - 1; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.EMPTY_N100) {
                    iret++;
                }//20171021本当は-50のつもりだったが現状は-100となっている
            }
        }
        return iret;
    }

    // According to the upper and lower tables, the overlapping classification of the pairs of faces included in this SubFace is determined.
    public int overlapping_classification_determined(HierarchyList hierarchyList) {
        int iret = 0;
        for (int i = 1; i <= faceIdCount - 1; i++) {
            for (int j = i + 1; j <= faceIdCount; j++) {
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.UNKNOWN_0) {
                    iret++;
                }
                if (hierarchyList.get(faceIdList[getPermutation(i)], faceIdList[getPermutation(j)]) == HierarchyList.HierarchyListCondition.UNKNOWN_1) {
                    iret++;
                }
            }
        }
        return iret;
    }
}
