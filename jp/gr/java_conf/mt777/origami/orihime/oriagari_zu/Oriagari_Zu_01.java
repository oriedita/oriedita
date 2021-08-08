package jp.gr.java_conf.mt777.origami.orihime.oriagari_zu;

//import java.awt.MouseInfo;
//import java.awt.PointerInfo;

//import java.awt.BorderLayout;

//画像出力のため追加開始　20170107
//import java.io.*;
//画像出力のため追加終わり

import jp.gr.java_conf.mt777.origami.orihime.*;


// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
//public class Oriagari_Zu_01 {
public class Oriagari_Zu_01 extends Oriagari_Zu {//Oriagari_Zuが基本となる折上がり予測アルゴリズム
/*
	ap orihime_ap;

	OritaCalc oc =new OritaCalc(); 
	Moji_sousa ms =new Moji_sousa(); //文字列操作用の関数を集めたクラス

	double r=3.0;                   //基本枝構造の直線の両端の円の半径、枝と各種ポイントの近さの判定基準


	public double d_oriagarizu_syukusyaku_keisuu=1.0;//折り上がり図の縮尺係数
	public double d_oriagarizu_kaiten_hosei=0.0;//折り上がり図の回転表示角度の補正角度

	public Kihonshi_Syokunin ks2       = new Kihonshi_Syokunin(r);    //基本枝職人。ts2の持つ点集合をts3に渡す前に、
                                                             //ts2の持つ点集合は棒が重なっていたりするかもしれないので、
                                                             //いったんks2に渡して線分集合として整理する。

	public Tenkaizu_Syokunin ts1       = new Tenkaizu_Syokunin(r);    //展開図職人。入力された線分集合を最初に折って針金状の点集合の折り上がり図を作る
	public Tenkaizu_Syokunin ts2       = new Tenkaizu_Syokunin(r);    //展開図職人。ts1の作った針金状の点集合の折り上がり図を保持し、線分集合にするなどの働きをする。  
	public Tenkaizu_Syokunin ts3       = new Tenkaizu_Syokunin(r);    //展開図職人。ts1の作った針金状の点集合を整理し。新たに面を認識するなどの働きを持つ。  

	public Jyougehyou_Syokunin js     ;
	//public Jyougehyou_Syokunin js      = new Jyougehyou_Syokunin();
	//public Jyougehyou_Syokunin(ap ap0){ orihime_ap=ap0;    reset();	}    //コンストラクタ

        public Camera camera_of_oriagarizu	= new Camera();
        public Camera camera_of_oriagari_omote	= new Camera();
        public Camera camera_of_oriagari_ura	= new Camera();
        public Camera camera_of_touka_omote	= new Camera();
        public Camera camera_of_touka_ura	= new Camera();

	public Color oriagarizu_F_color=new Color(255,255,50);//折り上がり図の表面の色
	public Color oriagarizu_B_color=new Color(233,233,233);//折り上がり図の裏面の色
	public Color oriagarizu_L_color=Color.black;//折り上がり図の線の色

	public  int hyouji_flg_backup=4;//表示様式hyouji_flgの一時的バックアップ用
 		//int hyouji_flg_backup=4;//表示様式hyouji_flgの一時的バックアップ用
	public int hyouji_flg=0;//折り上がり図の表示様式の指定。1なら展開図整理、2なら針金図。3なら透過図。4なら実際に折り紙を折った場合と同じ。
	public int i_suitei_meirei=0;//折り畳み推定をどの段階まで行うかの指示
	public int i_suitei_dankai=0;//折り畳み推定がどの段階までできたかの表示
	//public int i_suitei_jissi_umu=0;//int i_suitei_jissi_umuは、折り畳み推定の計算を実施したかどうかを表す。int i_suitei_jissi_umu=0なら実施しない。1なら実施した。


	//表示用の値を格納する変数
	public int ip1=-1;//上下表職人の初期設定時に、折った後の表裏が同じ面が
		//隣接するという誤差があれが0を、無ければ1000を格納する変数。
		//ここでの初期値は(0か1000)以外の数ならなんでもいい。	
	public int ip2=-1;//上下表職人が折り畳み可能な重なり方を探した際に、
		//可能な重なり方がなければ0を、可能な重なり方があれば1000を格納する変数。
		//ここでの初期値は(0か1000)以外の数ならなんでもいい。	
	//int ip3a=1;
	public int ip3=1;//ts1が折り畳みを行う際の基準面を指定するのに使う。

	public int ip4=0;//これは、ts1の最初に裏返しをするかどうかを指定する。0ならしない。1なら裏返す。

	public int ip5=-1;	//上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
			//さらに別の紙の重なりをさがす時の最初のjs.susumu(Smensuu)の結果。
			//0なら新たにsusumu余地がなかった。0以外なら変化したSmenのidの最も小さい番号

	public int ip6=-1;	//上下表職人が一旦折り畳み可能な紙の重なりを示したあとで、
			//さらに別の紙の重なりをさがす時の js.kanou_kasanari_sagasi()の結果。
			//0なら可能な重なりかたとなる状態は存在しない。
                         //1000なら別の重なり方が見つかった。                           

	public int betu_sagasi_flg=0;     //これは「別の重なりを探す」ことが有効の場合は１、無効の場合は０をとる。
	public int hakkenn_sita_kazu=0;    //折り重なり方で、何通り発見したかを格納する。


	//public int i_AS_matome =100;//折畳み推定の別解をまとめて出す個数//20171217 ver3.030では使われていない。


	public int toukazu_toukado=16;//透過図をカラー描画する際の透過度


	public int i_oriagari_sousa_mode=1;//1=変形時は針金図になり、変形後に上下表を再計算する、旧来からのモード、2=変形時も折り上がり図のままで、基本的に変形後に上下表は再計算しないモード
















	//public Keijiban keijiban =new Keijiban(this);
	public Keijiban keijiban;



	public boolean w_image_jikkoutyuu = false;//折畳みまとめ実行の。単一回のイメージ書き出しが実行中ならtureになる。
	public boolean matome_write_image_jikkoutyuu = false;//matome_write_imageが実行中ならtureになる。これは、複数の折りあがり形の予測の書き出しがかすれないように使う。20170613

	String fname_and_number;//まとめ書き出しに使う。





	//各種変数の定義
	String c=new String();                //文字列処理用のクラスのインスタンス化
	public String text_kekka=new String();                //結果表示用文字列のクラスのインスタンス化 




	int i_fold_type=0;//=0は通常の展開図の全折線を対象とした折り畳み推定、=1はselectされた折線を対象とした折り畳み推定、	

	
	public int i_toukazu_color=0;//透過図をカラーにするなら１、しないなら０


*/
// **************************************************
//コンストラクタ

	public Oriagari_Zu_01(App app0){
		super(app0);

	}


/*
	public Oriagari_Zu_01(ap ap0){ 
super("default");
		orihime_ap=ap0; 

		js      = new Jyougehyou_Syokunin(ap0);
		keijiban =new Keijiban(ap0);

		//カメラの設定 ------------------------------------------------------------------
		oriagari_camera_syokika();
		//カメラの設定はここまで----------------------------------------------------

text_kekka="";

	}
*/
	//----------------------------------------------------------


}
