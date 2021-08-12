package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_permutation_generator.annaisyo;

public class GuideMap {//案内書。Smenの中で作成する。重複順列作成機の前で、ある面の直ぐ上の面を登録する
    //案内図のイメージ。地図といっしょに見て次どうするかを判断する。各面がいまいる道しるべまでに出てきた数。今の道しるべの状態は数えない。
    //annai[i][j]は面iが出る前に出ているはずの面を表す。annaisyo[i][0]は、そういった面の数。
    // Image of guide map. Look at the map and decide what to do next. The number that came out by the way that each side is now. The current state of the road is not counted.
    // guide [i] [j] represents the surface that should have appeared before the surface i appeared. guide [i] [0] is the number of such faces.
    int[][] guide;
    int numDigits;

    public GuideMap(int digit) {//コンストラクタ
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

    public int get(int iMen, int i) {
        return guide[iMen][i];
    }

}
