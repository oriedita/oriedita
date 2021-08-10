package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator;

import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator.annaisyo.*;

public class Overlapping_Permutation_generator {//重複順列発生機
    //重複順列を発生させるクラスを改造して、Smen構成に応じた、面の重なり順列を効率良く発生させるクラス。

    int[] ij;//重複順列を格納する。
    int numDigits = 0;//重複順列の桁数。たとえば、桁数が5なら、1から5までの数字でできる順列を発生する
    int i_traveler;//Traveler's position
    int[] map;//Map image. The number that came out by the way that each side is now. The current state of the road is not counted.
    //Annaisyo ann = new Annaisyo();//案内書のイメージ。
    GuideMap guides; //なぜか、この行の書き方ではコンパイルはokなのに実行中にエラーがでるが、下の行の書き方だとうまくいく
    //  Annaisyo ann = new Annaisyo(0);


    public Overlapping_Permutation_generator(int k) {  //コンストラクタ
        numDigits = k;
        i_traveler = 0;
        int[] ij0 = new int[k + 10];//重複順列を格納する。
        int[] map0 = new int[k + 10];  //地図のイメージ。各面がいまいる道しるべまでに出てきた数。今の道しるべの状態は数えない。
        ij = ij0;
        map = map0;
        guides = new GuideMap(k + 10);

//System.out.println("20150309@@@@@@@44444444444444444444@@@@@@@@@" );
        Permutation_first();
//System.out.println("20150309@@@@@@@55555555555555555555@@@@@@@@@" );
    }

    //---------------
    //private void set_i_tabibito(int i){i_tabibito=i;}

    // ----------------------------------------------------------------
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
                ij[i_traveler] = 0;//桁数大きくなる方に進む
            }

            ij[i_traveler] = mitisirube_tatekae(ij[i_traveler]);

            if (ij[i_traveler] <= numDigits) {//桁数大きくなる方に進む
                i_traveler = i_traveler + 1;
                ij[i_traveler] = 0;
                if (i_traveler == numDigits + 1) {
                    break;
                }
            } else {//桁数小さくなる方に戻る
                i_traveler = i_traveler - 1;
                ireturn = i_traveler;
            }
        }


//System.out.println("susumu("+idousuru_digit+")");
//			for(int i=1;i<=Ketasuu;i++){System.out.print(ij[i] );System.out.print(",");}System.out.println();

        return ireturn;
    }

    //
    private int mitisirube_tatekae(int ig) {

        for (int i = 1; i <= numDigits; i++) {
            map[i] = 0;
        }

        for (int i = 1; i <= i_traveler - 1; i++) {
            map[ij[i]] = map[ij[i]] + 1;
        }

        //ここはバグがないか検討を要する。
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
    //
    //int getAnnai(int iM,int i){return ann.get(iM,i);}

}
