package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_jyunretu_hasseiki.annaisyo;

public class Annaisyo {//案内書。Smenの中で作成する。重複順列作成機の前で、ある面の直ぐ上の面を登録する
    //案内図のイメージ。地図といっしょに見て次どうするかを判断する。各面がいまいる道しるべまでに出てきた数。今の道しるべの状態は数えない。
    //annai[i][j]は面iが出る前に出ているはずの面を表す。annaisyo[i][0]は、そういった面の数。
    int[][] annai;
    int ketasuu;

    public Annaisyo(int keta) {//コンストラクタ
        ketasuu = keta;


        annai = new int[keta + 11][50];
        for (int i = 0; i <= keta + 10; i++) {
            annai[i][0] = 0;
        }
    }

    public void add(int Menidid, int ueMenidid) {
        annai[Menidid][0] = annai[Menidid][0] + 1;
        annai[Menidid][annai[Menidid][0]] = ueMenidid;
    }

    public int get(int iMen, int i) {
        //  System.out.print("ketasuu = ");System.out.print(ketasuu); System.out.print(" : iMen = ");System.out.println(iMen);
        return annai[iMen][i];
    }

}
