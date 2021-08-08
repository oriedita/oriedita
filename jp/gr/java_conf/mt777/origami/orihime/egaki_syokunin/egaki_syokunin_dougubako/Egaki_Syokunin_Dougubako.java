package jp.gr.java_conf.mt777.origami.orihime.egaki_syokunin.egaki_syokunin_dougubako;

import jp.gr.java_conf.mt777.origami.dougu.orisensyuugou.*;

import  jp.gr.java_conf.mt777.zukei2d.ten.*;
import  jp.gr.java_conf.mt777.zukei2d.senbun.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Egaki_Syokunin_Dougubako {


	OritaCalc oc =new OritaCalc(); //各種計算用の関数を使うためのクラスのインスタンス化
	PolygonStore ori_s;


	public Egaki_Syokunin_Dougubako(PolygonStore o_s ){  //コンストラクタ
		ori_s=o_s;
	}

//ベクトルab(=s0)を点aからb方向に、最初に他の折線と交差するところまで延長する
	Line kousaten_made_nobasi_line =new Line();
	Point kousaten_made_nobasi_point =new Point();
	int kousaten_made_nobasi_flg=0;//abを伸ばした最初の交点の状況
	int kousaten_made_nobasi_orisen_fukumu_flg=0;//abを直線化したのが、既存の折線を含むなら3
	Line kousaten_made_nobasi_saisyono_line = new Line();//abを直線化したのと、最初にぶつかる既存の折線



/*
	public void kousaten_made_nobasi_keisan(Ten a,Ten b) {//ベクトルab(=s0)を点aからb方向に、最初に他の折線と交差するところまで延長する//他の折線と交差しないなら、Ten aを返す
			Senbun s0=new Senbun();s0.set(a,b);
			Senbun add_sen=new Senbun();add_sen.set(s0);
			Ten kousa_ten =new Ten(1000000.0,1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
			double kousa_ten_kyori=kousa_ten.kyori(add_sen.geta());
			Tyokusen tyoku1 =new Tyokusen(add_sen.geta(),add_sen.getb());
			int i_kousa_flg;

			kousaten_made_nobasi_flg=0;
			kousaten_made_nobasi_orisen_fukumu_flg=0;
			for (int i=1; i<=ori_s.getsousuu(); i++ ){
				
				i_kousa_flg=tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
				if(i_kousa_flg==3){kousaten_made_nobasi_orisen_fukumu_flg=3;}
				if((i_kousa_flg==1||i_kousa_flg==21)||i_kousa_flg==22){

					kousa_ten.set(oc.kouten_motome(tyoku1,ori_s.get(i)));

					if(kousa_ten.kyori(add_sen.geta())>0.00001     ){

						if(kousa_ten.kyori(add_sen.geta())<kousa_ten_kyori   ){
							double d_kakudo=oc.kakudo(add_sen.geta(),add_sen.getb(),add_sen.geta(),kousa_ten);

							if(d_kakudo<1.0||d_kakudo>359.0){

								kousa_ten_kyori=kousa_ten.kyori(add_sen.geta());
								add_sen.set(add_sen.geta(),kousa_ten);
							
								kousaten_made_nobasi_flg=i_kousa_flg;
								kousaten_made_nobasi_saisyono_senbun.set(ori_s.get(i));

							}
					
						}

					}

				}
	
			}
		//return add_sen.getb();

		kousaten_made_nobasi_senbun.set(add_sen);//System.out.println("kousaten_made_nobasi_senbun.set 20201129 kousaten_made_nobasi_keisan");
		kousaten_made_nobasi_ten.set(add_sen.getb());
	}

*/

// -------------------
	public void kousaten_made_nobasi_keisan_fukumu_senbun_musi(Point a, Point b) {//ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する//他の折線と交差しないなら、Ten aを返す
			Line s0=new Line();s0.set(a,b);
			Line add_sen=new Line();add_sen.set(s0);
			Point kousa_point =new Point(1000000.0,1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
			double kousa_ten_kyori= kousa_point.kyori(add_sen.geta());
			StraightLine tyoku1 =new StraightLine(add_sen.geta(),add_sen.getb());
			int i_kousa_flg;

			kousaten_made_nobasi_flg=0;
			kousaten_made_nobasi_orisen_fukumu_flg=0;
			for (int i=1; i<=ori_s.getsousuu(); i++ ){
				
				i_kousa_flg=tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
				//if(i_kousa_flg==3){kousaten_made_nobasi_orisen_fukumu_flg=3;}
				if((i_kousa_flg==1||i_kousa_flg==21)||i_kousa_flg==22){

					kousa_point.set(oc.kouten_motome(tyoku1,ori_s.get(i)));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

					if(kousa_point.kyori(add_sen.geta())>0.00001     ){

						if(kousa_point.kyori(add_sen.geta())<kousa_ten_kyori   ){
							double d_kakudo=oc.kakudo(add_sen.geta(),add_sen.getb(),add_sen.geta(), kousa_point);

							if(d_kakudo<1.0||d_kakudo>359.0){

								kousa_ten_kyori= kousa_point.kyori(add_sen.geta());
								add_sen.set(add_sen.geta(), kousa_point);
							
								kousaten_made_nobasi_flg=i_kousa_flg;
								kousaten_made_nobasi_saisyono_line.set(ori_s.get(i));

							}
					
						}

					}

				}
	
			}
		//return add_sen.getb();

		kousaten_made_nobasi_line.set(add_sen);//System.out.println("kousaten_made_nobasi_senbun.set 20201129 kousaten_made_nobasi_keisan_fukumu_senbun_musi");
		kousaten_made_nobasi_point.set(add_sen.getb());
	}



// -------------------
	public void kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(Point a, Point b) {//ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する//他の折線と交差しないなら、Ten aを返す
			Line s0=new Line();s0.set(a,b);
			Line add_sen=new Line();add_sen.set(s0);
			Point kousa_point =new Point(1000000.0,1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
			double kousa_ten_kyori= kousa_point.kyori(add_sen.geta());
			StraightLine tyoku1 =new StraightLine(add_sen.geta(),add_sen.getb());
			int i_kousa_flg;

			kousaten_made_nobasi_flg=0;
			kousaten_made_nobasi_orisen_fukumu_flg=0;
			for (int i=1; i<=ori_s.getsousuu(); i++ ){

//System.out.println("000 20201129 col = "+ori_s.get(i).getcolor());  
				if(ori_s.get(i).getcolor()<3){//System.out.println("  kousaten_made_nobasi_keisan_fukumu_senbun_musi_new 20201128");
//0=この直線は与えられた線分と交差しない、
//1=X型で交差する、
//21=線分のa点でT型で交差する、
//22=線分のb点でT型で交差する、
//3=線分は直線に含まれる。
				i_kousa_flg=tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
				//if(i_kousa_flg==3){kousaten_made_nobasi_orisen_fukumu_flg=3;}
				if((i_kousa_flg==1||i_kousa_flg==21)||i_kousa_flg==22){

					kousa_point.set(oc.kouten_motome(tyoku1,ori_s.get(i)));//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す

					if(kousa_point.kyori(add_sen.geta())>0.00001     ){

						if(kousa_point.kyori(add_sen.geta())<kousa_ten_kyori   ){
							double d_kakudo=oc.kakudo(add_sen.geta(),add_sen.getb(),add_sen.geta(), kousa_point);

							if(d_kakudo<1.0||d_kakudo>359.0){
//System.out.println("20201129 col = "+ori_s.get(i).getcolor());  
								kousa_ten_kyori= kousa_point.kyori(add_sen.geta());
								add_sen.set(add_sen.geta(), kousa_point);
							
								kousaten_made_nobasi_flg=i_kousa_flg;
								kousaten_made_nobasi_saisyono_line.set(ori_s.get(i));

							}
					
						}

					}

				}
			}
			}
		//return add_sen.getb();

		kousaten_made_nobasi_line.set(add_sen);//System.out.println("kousaten_made_nobasi_senbun.set 20201129 kousaten_made_nobasi_keisan_fukumu_senbun_musi_new");
		kousaten_made_nobasi_point.set(add_sen.getb());
	}



/*
	折線集合の関数//点pに最も近い線分の、点pに近い方の端点を、頂点とした場合、何本の黒い線分が出ているか（頂点とr以内に端点がある線分の数）	
	public int tyouten_syuui_sensuu_black(Ten p,double r) {

		Ten q = new Ten();   q.set(mottomo_tikai_Ten(p));//qは点pに近い方の端点
		Ten p_temp = new Ten(); 


		int i_return;i_return=0;

		for(int i=1;i<=sousuu;i++){
			p_temp.set(geta(i));if(q.kyori2jyou(getb(i))<q.kyori2jyou(geta(i)) ) {p_temp.set(getb(i)); }			
			if(q.kyori2jyou(p_temp)<r*r) { if(getcolor(i)==0 ) {  i_return=i_return+1; }}

		}

		return i_return;
	}

*/

// -------------------
public int get_kousaten_made_nobasi_flg_new(Point a, Point b){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
	//kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a,b);
	return kousaten_made_nobasi_flg;
}



// -------------------
public int get_kousaten_made_nobasi_flg(Point a, Point b){//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
	kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_flg;
}
// -------------------
/*
public int get_kousaten_made_nobasi_orisen_fukumu_flg(Ten a,Ten b){//abを直線化したのが、既存の折線を含むなら3
	kousaten_made_nobasi_keisan(a,b);
	return kousaten_made_nobasi_orisen_fukumu_flg;
}*/

// -------------------
public Line get_kousaten_made_nobasi_senbun(Point a, Point b){
	kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_line;
}
// -------------------
public Line get_kousaten_made_nobasi_senbun_new(){
	//kousaten_made_nobasi_keisan_fukumu_senbun_musi_new(a,b);
	return kousaten_made_nobasi_line;
}
// -------------------
public Line get_kousaten_made_nobasi_saisyono_senbun(Point a, Point b){
	kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_saisyono_line;
}

// -------------------
public Line get_kousaten_made_nobasi_saisyono_senbun_new(){
	//kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_saisyono_line;
}


// -------------------
public Point get_kousaten_made_nobasi_ten(Point a, Point b){
	kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_point;
}

public Point get_kousaten_made_nobasi_ten_new(){
	//kousaten_made_nobasi_keisan_fukumu_senbun_musi(a,b);
	return kousaten_made_nobasi_point;
}



//e_s_dougubako





/*
一値分解する関数itti_bunkai();

public void itti_bunkai(){//（１）2点a,bを指定







}

（１）2点a,bを指定
（２）aを基点とするベクトルabが最初にぶつかる折線との交点cを求める。abと重なる折線は無視
（３）
cが既存の折線の柄の部分だった場合、その線で鏡映し、cをaとし、bを鏡映線の先の点として再帰的に。
cが点だった場合、すでに通過していた点なら、return;


cからベクトルacと一値性を持つベクトルを求める、




















*/






/*


	public Ten kousaten_made_nobasi(Ten a,Ten b) {//ベクトルab(=s0)を点aからb方向に、他の折線と交差するところまで延長する新しいTenを返す//他の折線と交差しないなら、Ten aを返す
			Senbun s0=new Senbun();s0.set(a,b);

			Senbun add_sen=new Senbun();add_sen.set(s0);
			Ten kousa_ten =new Ten(1000000.0,1000000.0); //この方法だと、エラーの原因になりうる。本当なら全線分のx_max、y_max以上の点を取ればいい。今後修正予定20161120
			double kousa_ten_kyori=kousa_ten.kyori(add_sen.geta());


			Tyokusen tyoku1 =new Tyokusen(add_sen.geta(),add_sen.getb());
			int i_kousa_flg;
			for (int i=1; i<=ori_s.getsousuu(); i++ ){
				i_kousa_flg=tyoku1.senbun_kousa_hantei_kuwasii(ori_s.get(i));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。

				if((i_kousa_flg==1||i_kousa_flg==21)||i_kousa_flg==22){
					kousa_ten.set(oc.kouten_motome(tyoku1,ori_s.get(i)));
					if(kousa_ten.kyori(add_sen.geta())>0.00001     ){

						if(kousa_ten.kyori(add_sen.geta())<kousa_ten_kyori   ){

							double d_kakudo=oc.kakudo(add_sen.geta(),add_sen.getb(),add_sen.geta(),kousa_ten);
							if(d_kakudo<1.0||d_kakudo>359.0){
								//i_kouten_ari_nasi=1;
								kousa_ten_kyori=kousa_ten.kyori(add_sen.geta());
								add_sen.set(add_sen.geta(),kousa_ten);
							}
						}
					}
				}
			}
		return add_sen.getb();
	}


*/
































	//--------------------------------------------
	public void test1(){//デバック等のテスト用

		System.out.println("_________");

	}
    
	//--------------------------------------------

	//メモ
		//icol=0 black
		//icol=1 red
		//icol=2 blue
		//icol=3 cyan
		//icol=4 orange
		//icol=5 mazenta
		//icol=6 green
		//icol=7 yellow
		//icol=8 new Color(210,0,255) //紫



}
