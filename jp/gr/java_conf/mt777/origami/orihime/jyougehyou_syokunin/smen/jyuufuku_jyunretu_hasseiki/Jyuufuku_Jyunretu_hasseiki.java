package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_jyunretu_hasseiki;

import jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.smen.jyuufuku_jyunretu_hasseiki.annaisyo.*;

public class Jyuufuku_Jyunretu_hasseiki {//重複順列発生機
    //重複順列を発生させるクラスを改造して、Smen構成に応じた、面の重なり順列を効率良く発生させるクラス。

    int[] ij;//重複順列を格納する。
    int Ketasuu = 0;//重複順列の桁数。たとえば、桁数が5なら、1から5までの数字でできる順列を発生する
    int i_tabibito;//旅人の位置
    int[] tizu;//地図のイメージ。各面がいまいる道しるべまでに出てきた数。今の道しるべの状態は数えない。
    //Annaisyo ann = new Annaisyo();//案内書のイメージ。
    Annaisyo ann; //なぜか、この行の書き方ではコンパイルはokなのに実行中にエラーがでるが、下の行の書き方だとうまくいく
    //  Annaisyo ann = new Annaisyo(0);


    public Jyuufuku_Jyunretu_hasseiki(int k) {  //コンストラクタ
        Ketasuu = k;
        i_tabibito = 0;
        int[] ij0 = new int[k + 10];//重複順列を格納する。
        int[] tizu0 = new int[k + 10];  //地図のイメージ。各面がいまいる道しるべまでに出てきた数。今の道しるべの状態は数えない。
        ij = ij0;
        tizu = tizu0;
        Annaisyo a = new Annaisyo(k + 10);
        ann = a;

//System.out.println("20150309@@@@@@@44444444444444444444@@@@@@@@@" );
        Jyunretu_1banme();
//System.out.println("20150309@@@@@@@55555555555555555555@@@@@@@@@" );
    }

    //---------------
    //private void set_i_tabibito(int i){i_tabibito=i;}

    // ----------------------------------------------------------------
    //現状の順列から次の順列に進む。
    //k桁目を1つ進めて、k-1桁以下はみな1にする。返り値は順列として変わった桁数
    //現状の順列が最後のものだった場合は0をreturnする。

    public int susumu(int idousuru_keta) {
        //旅人移動     桁数が大きくなる方向に進むときは
        //各ij[]の値を道しるべに進む。桁数が大きいところから小さいほうに戻ってきたときは各ij[]の値を変える。
        //地図は各道しるべで，桁数がそこ以下（そこも含む）の道しるべの情報を含む。具体的には、そこまで出てきた各グループごとの個数。
        //i_tabibito==Ketasuuなら1桁戻る。もどったら、そこで直ちにそこの位置（道しるべ）と地図の修正。
        //i_tabibito< Ketasuuなら、ij[i_tabibito]==Gsuuなら1桁減る方向に戻る。もどったら、そこで直ちにそこの位置（道しるべ）と地図の修正。
        //i_tabibito< Ketasuuなら、ij[i_tabibito]< Gsuuなら1桁増える方向に進む。進んだら、そこで直ちにそこの位置（道しるべ）と地図の修正。道しるべは可能な限り若い番号にする。
        i_tabibito = idousuru_keta;
        int ireturn = idousuru_keta;

        while (i_tabibito <= Ketasuu) {

            if (i_tabibito == 0) {
                i_tabibito = i_tabibito + 1;
                ij[i_tabibito] = 0;//桁数大きくなる方に進む
            }

            ij[i_tabibito] = mitisirube_tatekae(ij[i_tabibito]);

            if (ij[i_tabibito] <= Ketasuu) {//桁数大きくなる方に進む
                i_tabibito = i_tabibito + 1;
                ij[i_tabibito] = 0;
                if (i_tabibito == Ketasuu + 1) {
                    break;
                }
            } else {//桁数小さくなる方に戻る
                i_tabibito = i_tabibito - 1;
                ireturn = i_tabibito;
            }
        }


//System.out.println("susumu("+idousuru_keta+")");
//			for(int i=1;i<=Ketasuu;i++){System.out.print(ij[i] );System.out.print(",");}System.out.println();

        return ireturn;
    }

    //
    private int mitisirube_tatekae(int ig) {

        for (int i = 1; i <= Ketasuu; i++) {
            tizu[i] = 0;
        }

        for (int i = 1; i <= i_tabibito - 1; i++) {
            tizu[ij[i]] = tizu[ij[i]] + 1;
        }

        //ここはバグがないか検討を要する。
        int ignew = ig;
        while (true) {
            ignew = ignew + 1;
            if (ignew > Ketasuu) {
                break;
            }
            int nukedasi_flg = 1;
            if (tizu[ignew] == 1) {
                nukedasi_flg = 0;
            }

            for (int i = 1; i <= ann.get(ignew, 0); i++) {
                if (tizu[ann.get(ignew, i)] == 0) {
                    nukedasi_flg = 0;
                }
            }

            if (nukedasi_flg == 1) {
                break;
            }

        }

        return ignew;
    }

    public int getJyunretu(int i) {
        return ij[i];
    }

    //順列を一番最初のものにする
    public void Jyunretu_1banme() {
        susumu(0);
    }

    //案内書情報の受け渡し
    public void addAnnai(int iM, int i) {
        ann.add(iM, i);
    }
    //
    //int getAnnai(int iM,int i){return ann.get(iM,i);}

}
