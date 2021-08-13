package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator;

import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator.annaisyo.*;

public class Overlapping_Permutation_generator {//Permutations with repeat generator
    //A class that efficiently generates overlapping permutations of faces according to the SubFace configuration by modifying the class that generates permutations with duplicates.

    int[] ij;//Store permutations with duplicates.
    int numDigits;//The number of digits in the permutations with repetition. For example, if the number of digits is 5, it will generate a permutation made up of numbers from 1 to 5.
    int i_traveler;//Traveler's position
    int[] map;//Map image. The number that came out by the way that each side is now. The current state of the road is not counted.
    GuideMap guides; //For some reason, this line is ok to compile, but I get an error during execution, but the line below works fine.


    public Overlapping_Permutation_generator(int k) {
        numDigits = k;
        i_traveler = 0;
        int[] ij0 = new int[k + 10];//Store permutations with duplicates.
        int[] map0 = new int[k + 10];  //Map image. The number that came out by the way that each side is now. The current state of the road is not counted.
        ij = ij0;
        map = map0;
        guides = new GuideMap(k + 10);

        Permutation_first();
    }

    // Go from the current permutation to the next permutation.
    // Advance the kth digit by one, and set all k-1 digits and below to 1. The return value is the number of digits changed as a permutation
    // Return 0 if the current permutation is the last one.
    public int next(int idousuru_digit) {
        //Traveler movement When moving in the direction of increasing the number of digits
        // Follow the value of each ij [] as a guide. When the number of digits returns from the larger one to the smaller one, the value of each ij [] is changed.
        // The map is a guide for each road, and contains information on the guides with less than that number of digits (including that). Specifically, the number of each group that came out so far.
        // i_tabibito == numDigits goes back one digit. When I got back, I immediately corrected the location (road guide) and the map there.
        // If i_tabibito <numDigits, ij [i_tabibito] == Gsuu will return by one digit. When I got back, I immediately corrected the location (road guide) and the map there.
        // If i_tabibito <numDigits, ij [i_tabibito] <Gsuu, proceed in the direction of increasing by one digit. As soon as you proceed, correct the location (road guide) and map there. The guide should be as young as possible.
        i_traveler = idousuru_digit;
        int ireturn = idousuru_digit;

        while (i_traveler <= numDigits) {

            if (i_traveler == 0) {
                i_traveler = i_traveler + 1;
                ij[i_traveler] = 0;//Proceed to the larger number of digits
            }

            ij[i_traveler] = guide_rebuild(ij[i_traveler]);

            if (ij[i_traveler] <= numDigits) {//Proceed to the larger number of digits
                i_traveler = i_traveler + 1;
                ij[i_traveler] = 0;
                if (i_traveler == numDigits + 1) {
                    break;
                }
            } else {//Return to the one with the smaller number of digits
                i_traveler = i_traveler - 1;
                ireturn = i_traveler;
            }
        }

        return ireturn;
    }

    private int guide_rebuild(int ig) {

        for (int i = 1; i <= numDigits; i++) {
            map[i] = 0;
        }

        for (int i = 1; i <= i_traveler - 1; i++) {
            map[ij[i]] = map[ij[i]] + 1;
        }

        //It is necessary to consider whether there are any bugs here.
        int ignew = ig;
        while (true) {
            ignew = ignew + 1;
            if (ignew > numDigits) {
                break;
            }
            int exit_flg = 1;
            if (map[ignew] == 1) {
                exit_flg = 0;
            }

            for (int i = 1; i <= guides.get(ignew, 0); i++) {
                if (map[guides.get(ignew, i)] == 0) {
                    exit_flg = 0;
                }
            }

            if (exit_flg == 1) {
                break;
            }

        }

        return ignew;
    }

    public int getPermutation(int i) {
        return ij[i];
    }

    //Make the permutation the very first
    public void Permutation_first() {
        next(0);
    }

    //Handing over of guide information
    public void addGuide(int iM, int i) {
        guides.add(iM, i);
    }
}
