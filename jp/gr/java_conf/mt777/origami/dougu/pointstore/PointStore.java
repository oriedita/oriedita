package jp.gr.java_conf.mt777.origami.dougu.pointstore;

import  jp.gr.java_conf.mt777.zukei2d.ten.*;
import  jp.gr.java_conf.mt777.zukei2d.senbun.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import  jp.gr.java_conf.mt777.zukei2d.takakukei.*;
import  jp.gr.java_conf.mt777.origami.dougu.bou.*;
import  jp.gr.java_conf.mt777.origami.dougu.men.*;
import  jp.gr.java_conf.mt777.kiroku.memo.*;

import java.util.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------

public class PointStore {

	int Msuu_temp;

//Ten_p tp=new Ten_p();

	int pointsTotal;               //実際に使う点の総数
	int sticksTotal;               //実際に使う棒の総数
	int facesTotal;               //実際に使う面の総数
	Point_p[] t;//点のインスタンス化
	Stick[] b;//棒のインスタンス化
	int[] Stick_moti_Menid_min;
	int[] Stick_moti_Menid_max;

	Face[] m;//面のインスタンス化

	double[] Stick_x_max;
	double[] Stick_x_min;
	double[] Stick_y_max;
	double[] Stick_y_min;

	double[] Surface_x_max;
	double[] Surface_x_min;
	double[] Surface_y_max;
	double[] Surface_y_min;

	OritaCalc oc =new OritaCalc();          //各種計算用の関数を使うためのクラスのインスタンス化
	ArrayList<Integer>[] T_renketu;//t_renketu[i][j]はt[i]に連決しているTenの番号。t[0]には、Temの数を格納。
                        
	int[][] Men_tonari;//Men_tonari[i][j]はm[i]とm[j]の境界のBouの番号。m[i]とm[j]が隣り合わないときは0を格納。

	public PointStore(){   reset();  } //コンストラクタ
	//---------------------------------------
	public void reset(){	pointsTotal =0; sticksTotal =0; facesTotal =0;    }

	//---------------------------------------
	public void settei(int Tsuu,int Bsuu,int Msuu){ //最初やリセットの後には必ず通るようにする。

		Msuu_temp=Msuu;

		t = new Point_p[Tsuu+1];
		T_renketu= new ArrayList[Tsuu+1];
	 
		T_renketu[0]=new ArrayList<>();
		for(int i=0;i<=Tsuu;i++){
			T_renketu[i]= new ArrayList<>();
			T_renketu[i].add(0);
		}
	
		for(int i=0;i<=Tsuu;i++){
			t[i]=new Point_p();
			set_T_renketu(i,0,  0);
		}

		b = new Stick[Bsuu+1];
		int[] BMmin =new int[Bsuu+1];
		int[] BMmax =new int[Bsuu+1];
		Stick_moti_Menid_min =BMmin;
		Stick_moti_Menid_max =BMmax;
		for(int i=0;i<=Bsuu;i++){
			b[i]=new Stick();
			Stick_moti_Menid_min[i]=0;
			Stick_moti_Menid_max[i]=0;
		}

		m = new Face[Msuu+1];

		Men_tonari= new int[Msuu+1][Msuu+1];

		for(int i=0;i<=Msuu;i++){
		//for(int i=0;i<=Msuu+1;i++){
			m[i]=new Face();
			for(int j=0;j<=Msuu;j++){
			//for(int j=0;j<=Msuu+1;j++){

				Men_tonari[i][j]=0;
			}

		}	 

		double[] Bxmax = new double[Bsuu+1];
		double[] Bxmin = new double[Bsuu+1];
		double[] Bymax = new double[Bsuu+1];
		double[] Bymin = new double[Bsuu+1];

		double[] Mxmax = new double[Msuu+1];
		double[] Mxmin = new double[Msuu+1];
		double[] Mymax = new double[Msuu+1];
		double[] Mymin = new double[Msuu+1];

		Stick_x_max =Bxmax;
		Stick_x_min =Bxmin;
		Stick_y_max =Bymax;
		Stick_y_min =Bymin;

		Surface_x_max = Mxmax;
		Surface_x_min = Mxmin;
		Surface_y_max = Mymax;
		Surface_y_min = Mymin;
	}

	//---------------
	private int get_T_renketu(int i,int j){
		Integer Tr= T_renketu[i].get(j);
		return Tr;
	}
  
	private void set_T_renketu(int i,int j,int tid){
		if(j+1> T_renketu[i].size()){while(j+1> T_renketu[i].size()){T_renketu[i].add(0);}}//この文がないとうまく行かない。なぜこの文でないといけないかという理由が正確にはわからない。
		T_renketu[i].set(j, tid);
	}
  
	//------------------------------
	private double getHeikinZahyou_x(){
		double x=0.0;
		for(int i = 1; i<= pointsTotal; i++){x=x+t[i].getx();}
		return x/((double) pointsTotal);
	}

	private double getHeikinZahyou_y(){
		double y=0.0;
		for(int i = 1; i<= pointsTotal; i++){y=y+t[i].gety();}
		return y/((double) pointsTotal);
	}
/*
	public void Iti_sitei(double x,double y){//全Tenの重心の位置を指定された座標にする。
		double xh,yh;
		xh= getHeikinZahyou_x();
		yh= getHeikinZahyou_y();
		heikou_idou(x-xh,y-yh)  ;
	}
*/
        //
	public void uragaesi(){//重心の位置を中心に左右に裏返す。
		double xh;int icol;
		xh= getHeikinZahyou_x();
		for(int i = 1; i<= pointsTotal; i++){t[i].setx(2.0*xh-t[i].getx());}
		for(int i = 1; i<= sticksTotal; i++){
			icol=b[i].getColor();
			if(icol==1){b[i].setColor(2);}
			if(icol==2){b[i].setColor(1);}
		}
	   
	}


	//public double menseki(int men_id) {return 1.0; }//   面の面積を返す


	public void heikou_idou(double x,double y) { for(int i = 0; i<= pointsTotal; i++){t[i].heikou_idou(x,y);} }

	public void set( PointStore ts) {
		pointsTotal =ts.getPointsTotal();
		sticksTotal =ts.getSticksTotal();
		facesTotal =ts.getFacesTotal();
		for(int i = 0; i<= pointsTotal; i++){
			t[i].set(ts.getTen(i));                                                         //  <<<-------
			for(int j=1;j<=ts.get_T_renketu(i,0);j++){
				set_T_renketu(i,j,  ts.get_T_renketu(i,j));
			} 
		}
		for(int i = 0; i<= sticksTotal; i++){
			b[i].set(ts.getStick(i));
			Stick_moti_Menid_min[i]=ts.get_Stick_moti_Menid_min(i);
			Stick_moti_Menid_max[i]=ts.get_Stick_moti_Menid_max(i);
		}
		for(int i = 0; i<= facesTotal; i++){
			m[i]=new Face(ts.getMen(i));
			for(int j = 0; j<= facesTotal; j++){Men_tonari[i][j] =ts.get_Men_tonari(i,j);}
		}
	}

	public void set(int i, Point tn){t[i].set(tn);}                                               //  <<<-------

	private int  get_Men_tonari(int i,int j){return  Men_tonari[i][j];}
	//
	private int get_Stick_moti_Menid_min(int i){return Stick_moti_Menid_min[i];}
	//
	private int get_Stick_moti_Menid_max(int i){return Stick_moti_Menid_max[i];}
        //
	private double get_Bou_x_max(int i){return Stick_x_max[i];}
	//
	private double get_Bou_x_min(int i){return Stick_x_min[i];}
	//
	private double get_Bou_y_max(int i){return Stick_y_max[i];}
	//
	private double get_Bou_y_min(int i){return Stick_y_min[i];}
        //
	private double get_Men_x_max(int i){return Surface_x_max[i];}
	//
	private double get_Men_x_min(int i){return Surface_x_min[i];}
	//
	private double get_Men_y_max(int i){return Surface_y_max[i];}
	//
	private double get_Men_y_min(int i){return Surface_y_min[i];}

	//点が面の内部にあるかどうかを判定する。0なら内部にない、1なら境界線上、2なら内部
	public int kantan_naibu(Point p, int n){      //0=外部、　1=境界、　2=内部
		//System.out.println("2016");
		if(p.getx()+0.5 < Surface_x_min[n]){return 0;}
		if(p.getx()-0.5 > Surface_x_max[n]){return 0;}
		if(p.gety()+0.5 < Surface_y_min[n]){return 0;}
		if(p.gety()-0.5 > Surface_y_max[n]){return 0;}
		//System.out.println("2017");
		return naibu( p,m[n]);
	}

	//点が面の内部にあるかどうかを判定する。
	public int naibu(Point p, int n){      //0=外部、　1=境界、　2=内部
		return naibu( p,m[n]);
	}

	//点が面の内部にあるかどうかを判定する。0なら内部にない、1なら境界線上、2なら内部
	private int naibu(Point p, Face mn){      //0=外部、　1=境界、　2=内部
		Takakukei tk;tk= makeTakakukei(mn);
		return tk.naibu(p);
	}

	//点がどの面の内部にあるかどうかを判定する。0ならどの面の内部にもない、マイナスなら境界線上、正の数なら内部。該当する面番号が複数ある場合は番号の小さいほうが返される。
	public int naibu(Point p){
		for(int i = 1; i<= getFacesTotal(); i++){
			if(naibu(p,i)==2){return i;}
			if(naibu(p,i)==1){return -i;}
		}
		return 0;
	}






	//Men を多角形にする
	private Takakukei makeTakakukei(Face mn) {
		Takakukei tk =new Takakukei(mn.getTenidsuu());  
		tk.setkakusuu(mn.getTenidsuu());  
		for(int i=0;i<=mn.getTenidsuu();i++){tk.set(i,t[mn.getTenid(i)]);} 
		return tk;
	}

	//線分s0の一部でも凸多角形の面の内部（境界線は内部とみなさない）に
	//存在するとき1、しないなら0を返す。面が凹多角形の場合は結果がおかしくなるので使わないこと
	public int kantan_totu_naibu(int ib,int im){
		//バグがあるようだったが，多分取り除けた
		if(Stick_x_max[ib]+0.5 < Surface_x_min[im]){return 0;}
		if(Stick_x_min[ib]-0.5 > Surface_x_max[im]){return 0;}
		if(Stick_y_max[ib]+0.5 < Surface_y_min[im]){return 0;}
		if(Stick_y_min[ib]-0.5 > Surface_y_max[im]){return 0;}
    
		return totu_naibu(new Line(t[b[ib].getBegin()],t[b[ib].getEnd()]),m[im]);
	}

	private int totu_naibu(Line s0, Face mn){
 		Takakukei tk ;//=new Takakukei();   
		tk=  makeTakakukei(mn);
		return tk.totu_naibu(s0);
	}
  
	private int totu_naibu(int ib,int im){
		return totu_naibu(new Line(t[b[ib].getBegin()],t[b[ib].getEnd()]),m[im]);
	}

	public int totu_naibu(double d,int ib,int im){
		Line sn = new Line(t[b[ib].getBegin()],t[b[ib].getEnd()]) ;
		return totu_naibu(oc.mayoko_idou(sn, d),m[im]);
	}

	private int kantan_totu_naibu(double d,int ib,int im){
		Line sn = new Line(t[b[ib].getBegin()],t[b[ib].getEnd()]) ;
		Line snm= oc.mayoko_idou(sn, d);
		double s_x_max=snm.getax();
		double s_x_min=snm.getax();
		double s_y_max=snm.getay();
		double s_y_min=snm.getay();
		if(s_x_max<snm.getbx()){s_x_max=snm.getbx();}
		if(s_x_min>snm.getbx()){s_x_min=snm.getbx();}	
		if(s_y_max<snm.getby()){s_y_max=snm.getby();}
		if(s_y_min>snm.getby()){s_y_min=snm.getby();}	
	
		if(s_x_max+0.5 < Surface_x_min[im]){return 0;}
		if(s_x_min-0.5 > Surface_x_max[im]){return 0;}
		if(s_y_max+0.5 < Surface_y_min[im]){return 0;}
		if(s_y_min-0.5 > Surface_y_max[im]){return 0;}
	    
		return totu_naibu(snm,m[im]);
	}



	//棒を線分にする
	private Line Stick2Senbun(Stick bu){
		return new Line(t[bu.getBegin()],t[bu.getEnd()]);
	}

	//2つのBouが平行で一部または全部で重なるときは1、そうでなければ0をかえす。1点で重なる場合は0を返す。
	public int heikou_kasanai(int ib1,int ib2){
		int skh;
		skh = oc.senbun_kousa_hantei(Stick2Senbun(b[ib1]), Stick2Senbun(b[ib2])) ;
		if(skh==31){return 1;}
		if(skh==321){return 1;}
		if(skh==322){return 1;}
		if(skh==331){return 1;}
		if(skh==332){return 1;}
		if(skh==341){return 1;}
		if(skh==342){return 1;}
		if(skh==351){return 1;}
		if(skh==352){return 1;}

		if(skh==361){return 1;}
		if(skh==362){return 1;}
		if(skh==363){return 1;}
		if(skh==364){return 1;}

		if(skh==371){return 1;}
		if(skh==372){return 1;}
		if(skh==373){return 1;}
		if(skh==374){return 1;}
    
		return 0;
	}




	//面の内部の点を求める
	public Point naibuTen_motome(int n){	return naibuTen_motome(m[n]); }

	//面の内部の点を求める
	private Point naibuTen_motome(Face mn){
		Takakukei tk ;
		tk= makeTakakukei(mn);
		return tk.naibuTen_motome();
	}

	//面積求める
	public double menseki_motome(int n){	return menseki_motome(m[n]); }


	private double menseki_motome(Face mn){
		Takakukei tk;
		tk=  makeTakakukei(mn);
		return tk.menseki_motome();
	}

	public int getPointsTotal(){return pointsTotal; }   //点の総数を得る
	public int getSticksTotal(){return sticksTotal; }   //棒の総数を得る
	public int getFacesTotal(){return facesTotal; }   //面の総数を得る
    
	public int getTenid(int i,int j){return m[i].getTenid(j);}  // void setTensuu(int i){Tensuu=i;}

	// void setBousuu(int i){Bousuu=i;}       
	public double getTenx(int i){return t[i].getx();}
	public double getTeny(int i){return t[i].gety();}

	public Point getTen(int i){return t[i];}   //点を得る       <<<------------tは、スーパークラスのTenのサブクラスTen_Pクラスのオブジェクト。スーパークラスの変数にサブクラスのオブジェクトを代入可能なので、このまま使う。
	private Stick getStick(int i){return b[i];}   //棒を得る

	public Point get_maeTen_from_Bou_id(int i){return t[getmae(i)];}    //棒のidから前点を得る              <<<------------　　同上
	public Point get_atoTen_from_Bou_id(int i){return t[getato(i)];}    //棒のidから後点を得る              <<<------------　　同上


	public Line get_Senbun_from_Bou_id(int i){return Stick2Senbun(getStick(i));}    //棒のidからSenbunを得る

	private Face getMen(int i){return m[i];}   //面を得る
  
	public int getmae(int i){return b[i].getBegin();} //棒のidから前点のidを得る
	public int getato(int i){return b[i].getEnd();} //棒のidから後点のidを得る
  
	public double getmaex(int i){return t[b[i].getBegin()].getx();}
	public double getmaey(int i){return t[b[i].getBegin()].gety();}
	public double getatox(int i){return t[b[i].getEnd()].getx();}
	public double getatoy(int i){return t[b[i].getEnd()].gety();}
  
	public int getTenidsuu(int i){return m[i].getTenidsuu();}
  
	public void setTen(int i, Point tn){t[i].set(tn);}                                                        //   <<<------------
	private void setTen(int i,double x,double y){t[i].set(x,y);}

	public void addTen(double x,double y){  pointsTotal = pointsTotal +1;  t[pointsTotal].set(x,y);  }   //点を加える

	public void addBou(int i,int j,int icol){
		sticksTotal = sticksTotal +1;b[sticksTotal].set(i,j,icol);}   //棒を加える
  
	//i番目の棒の色を入出力する
	private void setcolor(int i,int icol){b[i].setColor(icol);}
	public int getcolor(int i){return b[i].getColor();}

	private void t_renketu_sakusei(){
		for(int k = 1; k<= sticksTotal; k++){
			set_T_renketu(b[k].getBegin(),0, get_T_renketu(b[k].getBegin(),0)+1);
			set_T_renketu(b[k].getBegin(),    get_T_renketu(b[k].getBegin(),0), b[k].getEnd());
			set_T_renketu(b[k].getEnd(),0, get_T_renketu(b[k].getEnd(),0)+1);
			set_T_renketu(b[k].getEnd(),    get_T_renketu(b[k].getEnd(),0), b[k].getBegin());
		}
	}

	//点iと点jが棒で連結していれば1、していなければ0を返す。
	private int renketu_hantei(int i,int j){
		for(int k = 1; k<= sticksTotal; k++){
			if (
			((b[k].getBegin()==i)&&(b[k].getEnd()==j))
				||
			((b[k].getBegin()==j)&&(b[k].getEnd()==i))
			)
				{return 1;}
			}
	return 0;
	}

	//点iから点jに進んで、次に、点jから点iの右隣に進む時の点の番号を求める。
	private int getRTen(int i,int j){
		int n=0;double kakudo=876.0;   //kakudoは適当な大きい数にしておく
	 
		// if(renketu_hantei(i,j)==0){return 0;}//点iと点jが連結していない時は0を返す

		int iflg=0;
		for(int k=1;k<=get_T_renketu(i,0);k++){ if(get_T_renketu(i,k)==j){iflg=1;} }
	
		if (iflg==0){return 0;}//点iと点jが連結していない時は0を返す
 
		for(int ik=1;ik<=get_T_renketu(j,0);ik++){
			int k;
			k=get_T_renketu(j,ik);
			if(k!=i){
				if(oc.kakudo(t[j],t[i],t[j],t[k])<=kakudo){	    
					n=k;kakudo=oc.kakudo(t[j],t[i],t[j],t[k]);
				}
			} 
		}
		return n; //点jに連結している点が点iしかない時は0を返す
	}  
	//--------------------------------    
    
	private Face Men_motome(int i, int j){//i番目の点、j番目の点から初めて右側の棒をたどって面を求める
		Face mtemp =new Face();
		//mtemp.reset();       
		mtemp.addTenid(i);mtemp.addTenid(j);
		int nextT=0;
	
		nextT=getRTen(mtemp.getTenid(1),mtemp.getTenid(2));
		while(nextT!=mtemp.getTenid(1)){
			if(nextT==0) {mtemp.reset(); return mtemp;}//エラー時の対応
			mtemp.addTenid(nextT);
			nextT=getRTen(mtemp.getTenid(mtemp.getTenidsuu()-1),mtemp.getTenid(mtemp.getTenidsuu()));
		}
		mtemp.align();
		return mtemp;                  
	} 
	//-------------------------------------
	public void Menhassei(){  
		int flag1;
		Face mtemp =new Face();
		facesTotal =0;
		t_renketu_sakusei();
		
		for(int i = 1; i<= sticksTotal; i++){
		//System.out.print("面発生　＝　"+i+"    ");System.out.println(Mensuu); 

			//
			mtemp=Men_motome(b[i].getBegin(),b[i].getEnd());
			flag1=0;   //　0なら面を追加する。1なら　面を追加しない。
			for(int j = 1; j<= facesTotal; j++){
				if(onaji_ka_hantei(mtemp,m[j] )==1){flag1=1;break;}
			}       

       			if(  ((flag1==0)&&(mtemp.getTenidsuu()!=0))&&
			     (menseki_motome(mtemp)>0.0)  ){addMen(mtemp); }      
			//

			mtemp=Men_motome(b[i].getEnd(),b[i].getBegin());
			flag1=0;   //　0なら面を追加する。1なら　面を追加しない。
			for(int j = 1; j<= facesTotal; j++){
				if(onaji_ka_hantei(mtemp,m[j] )==1){flag1=1;break;}
			}       

      			if(((flag1==0)&&(mtemp.getTenidsuu()!=0))&&(menseki_motome(mtemp)>0.0)){
		//System.out.println("面発生ループ内　003"); 				
				addMen(mtemp);
		//System.out.println("面発生ループ内　004");				
			 }   
		
			//-----
		//	if(Mensuu%20==0){
		//		System.out.print("今までに発生した面数　＝　");System.out.println(Mensuu);
		//	}
		}
		
		System.out.print("全面数　＝　");System.out.println(facesTotal);
		Men_tonari_sakusei();

                //Bouの両側の面の登録
		for(int ib = 1; ib<= sticksTotal; ib++){
		
			Stick_moti_Menid_min[ib]= Stick_moti_Menid_min_sagasi(ib);
			Stick_moti_Menid_max[ib]= Stick_moti_Menid_max_sagasi(ib);
		}
	}
	
	//BouやMenの座標の最大値、最小値を求める。kantan_totu_naibu関数にのみ用いる。kantan_totu_naibu関数を使うなら折り畳み推定毎にやる必要あり。
	public void BouMenMaxMinZahyou(){
	//Bouの座標の最大最小を求める（これはここでやるより、Bouが加えられた直後にやるほうがよいかも知れない。）
		for(int ib = 1; ib<= sticksTotal; ib++){
   
			Stick_x_max[ib] = t[b[ib].getBegin()].getx();
			Stick_x_min[ib] = t[b[ib].getBegin()].getx();
			Stick_y_max[ib] = t[b[ib].getBegin()].gety();
			Stick_y_min[ib] = t[b[ib].getBegin()].gety();
 
			if(Stick_x_max[ib]< t[b[ib].getEnd()].getx()){
				Stick_x_max[ib]= t[b[ib].getEnd()].getx();}
			if(Stick_x_min[ib]> t[b[ib].getEnd()].getx()){
				Stick_x_min[ib]= t[b[ib].getEnd()].getx();}
			if(Stick_y_max[ib]< t[b[ib].getEnd()].gety()){
				Stick_y_max[ib]= t[b[ib].getEnd()].gety();}
			if(Stick_y_min[ib]> t[b[ib].getEnd()].gety()){
				Stick_y_min[ib]= t[b[ib].getEnd()].gety();}
		        MenMaxMinZahyou();
		}
        }
	
	private void MenMaxMinZahyou(){	
		//Menの座標の最大最小を求める
		for(int im = 1; im<= facesTotal; im++){
			Surface_x_max[im] = t[m[im].getTenid(1)].getx();
			Surface_x_min[im] = t[m[im].getTenid(1)].getx();
			Surface_y_max[im] = t[m[im].getTenid(1)].gety();
			Surface_y_min[im] = t[m[im].getTenid(1)].gety();
			for(int i=2;i<=m[im].getTenidsuu();i++){
				if(Surface_x_max[im] < t[m[im].getTenid(i)].getx()){
					Surface_x_max[im] = t[m[im].getTenid(i)].getx();}
				if(Surface_x_min[im] > t[m[im].getTenid(i)].getx()){
					Surface_x_min[im] = t[m[im].getTenid(i)].getx();}
				if(Surface_y_max[im] < t[m[im].getTenid(i)].gety()){
					Surface_y_max[im] = t[m[im].getTenid(i)].gety();}
				if(Surface_y_min[im] > t[m[im].getTenid(i)].gety()){
					Surface_y_min[im] = t[m[im].getTenid(i)].gety();}
			}            
		}
	}




	public Point get_men_migiue_Ten(int im){//imは面番号　。migiue	指定された番号の面を含む最小の長方形の右上の頂点を返す。　折り上がり図の裏返図の位置指定に使う。
		//Menの座標の最大最小を求める

			double x_max = t[m[im].getTenid(1)].getx();
			double x_min = t[m[im].getTenid(1)].getx();
			double y_max = t[m[im].getTenid(1)].gety();
			double y_min = t[m[im].getTenid(1)].gety();
			for(int i=2;i<=m[im].getTenidsuu();i++){
				if(x_max < t[m[im].getTenid(i)].getx()){x_max = t[m[im].getTenid(i)].getx();}
				if(x_min > t[m[im].getTenid(i)].getx()){x_min = t[m[im].getTenid(i)].getx();}
				if(y_max < t[m[im].getTenid(i)].gety()){y_max = t[m[im].getTenid(i)].gety();}             
				if(y_min > t[m[im].getTenid(i)].gety()){y_min = t[m[im].getTenid(i)].gety();}    
			}            

		return new Point(x_max,y_min);

	}




	//--------------
	//棒ibを境界として含む面(最大で2面ある)のうちでMenidの小さいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
	private int Stick_moti_Menid_min_sagasi(int ib){
		for(int im = 1; im<= facesTotal; im++){
			if(Stick_moti_hantei(im,ib)==1){return im;}
		}
		return 0;
	}      

	//棒ibを境界として含む面(最大で2面ある)のうちでMenidの大きいほうのMenidを返す。棒を境界として含む面が無い場合は0を返す
	private int Stick_moti_Menid_max_sagasi(int ib){
		for(int im = facesTotal; im>=1; im--){
			if(Stick_moti_hantei(im,ib)==1){return im;}
		}
		return 0;
	}      

	//---------------

	//Boundary of rods Boundary surface (two sides in yellow) Here, Menid of the proliferating branch of Menid was made.
	public int Stick_moti_Menid_min_motome(int ib){
		return Stick_moti_Menid_min[ib];
	}      

	//Returns the Menid with the larger Menid of the faces containing the bar ib as the boundary (there are up to two faces). Returns 0 if there is no face containing the bar as the boundary
	public int Stick_moti_Menid_max_motome(int ib){
		return Stick_moti_Menid_max[ib];
	}      

	//---------------
	private int onaji_ka_hantei(Face m, Face n ) { //同じなら1、違うなら0を返す
 
		if (m.getTenidsuu()!=n.getTenidsuu()){return 0;}
 
		for (int i=1;i<=m.getTenidsuu();i++){
			if (m.getTenid(i)!=n.getTenid(i)){return 0;}
		}
 
		return 1;
 
	}
	
	//Men[im]の境界にTen[it]が含まれるなら1、含まれないなら0を返す
	public int Ten_moti_hantei(int im,int it) { 
		for(int i=1;i<=m[im].getTenidsuu();i++){
			if(it==m[im].getTenid(i)){return 1;}
		}
		return  0;
	}

	//Men[im]の境界にBou[ib]が含まれるなら1、含まれないなら0を返す
	private int Stick_moti_hantei(int im, int ib) {
		for(int i=1;i<=m[im].getTenidsuu()-1;i++){
			if((b[ib].getBegin()==m[im].getTenid(i))&& (b[ib].getEnd()==m[im].getTenid(i+1))){return 1;}
			if((b[ib].getEnd()==m[im].getTenid(i))&& (b[ib].getBegin()==m[im].getTenid(i+1))){return 1;}
		}
		if((b[ib].getBegin()==m[im].getTenid(m[im].getTenidsuu()))&& (b[ib].getEnd()==m[im].getTenid(1))){return 1;}
		if((b[ib].getEnd()==m[im].getTenid(m[im].getTenidsuu()))&& (b[ib].getBegin()==m[im].getTenid(1))){return 1;}
	  
		return  0;
	}

	//------------------------------------------------------
	private void Men_tonari_sakusei(){
		System.out.println("面となり作成　開始");
		for(int im = 1; im<= facesTotal -1; im++){
			for(int in = im+1; in<= facesTotal; in++){
				Men_tonari[im][in]=0;Men_tonari[in][im]=0;
				int ima,imb,ina,inb;
				for(int iim=1;iim<=m[im].getTenidsuu();iim++){
					ima=m[im].getTenid(iim);
					if(iim==m[im].getTenidsuu()){imb=m[im].getTenid(1);}
					else{imb=m[im].getTenid(iim+1);}
					//imb=m[im].getTenid((iim+1)%m[im].getTenidsuu());
					
					for(int iin=1;iin<=m[in].getTenidsuu();iin++){
						ina=m[in].getTenid(iin);
						 
					if(iin==m[in].getTenidsuu()){inb=m[in].getTenid(1);}
					else{inb=m[in].getTenid(iin+1);}
						 
						 
						//inb=m[in].getTenid((iin+1)%m[in].getTenidsuu());
						if(((ima==ina)&&(imb==inb))||((ima==inb)&&(imb==ina))){
							int ib;ib=Bou_sagasi(ima,imb);
							Men_tonari[im][in]= ib;
							Men_tonari[in][im]= ib;
						} 
					}
				}

			} 
		}
		System.out.println("面となり作成　終了");
	}
	
	//点t1とt2を含むBouの番号を返す
	private int Bou_sagasi(int t1,int t2){
		for(int i = 1; i<= sticksTotal; i++){
			if(( b[i].getBegin()==t1)&&( b[i].getEnd()==t2)){return i;}
			if(( b[i].getBegin()==t2)&&( b[i].getEnd()==t1)){return i;}
		}
		return 0;
	}
	
	//Men[im]とMen[ib]が隣接するならその境界にある棒のid番号を返す。隣接しないなら0を返す
	public int Men_tonari_hantei(int im,int in) { 
		/*
		for(int i=1;i<=Bousuu;i++){
       	        	if(( Bou_moti_hantei( im,i)==1)&&( Bou_moti_hantei( in,i)==1)){return i;}
		}
		return 0;
		*/
		return Men_tonari[im][in];
	}

	//
	private void addMen(Face mtemp){
		facesTotal = facesTotal +1;
//System.out.println("点集合：addMen 1   Mensuu = "+Mensuu+"  Msuu = "+Msuu_temp );

		m[facesTotal].reset();
		//for (int i=0; i<50; i++ ){m[Mensuu].setTenid(i,mtemp.getTenid(i));}
//System.out.println("点集合：addMen 2   Mensuu = "+  Mensuu    );
                for (int i=1; i<=mtemp.getTenidsuu(); i++ ){m[facesTotal].addTenid(mtemp.getTenid(i));}
//System.out.println("点集合：addMen 3   Mensuu = "+  Mensuu );
		m[facesTotal].setcolor(mtemp.getcolor());
		//m[Mensuu].setTenidsuu(mtemp.getTenidsuu());

	}
	
	//与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の番号を返す。もし、一定の距離以内にTenがないなら0を返す。
	public int mottomo_tikai_Tenid(Point p, double r){
		int ireturn=0;double rmin=1000000.0; double rtemp;
		for(int i = 1; i<= pointsTotal; i++){
			rtemp=oc.kyori(p,t[i]);
			if(rtemp<r){
				if(rtemp<rmin){rmin=rtemp;ireturn=i;}
			}
		}
	        return ireturn;
	}
 



	//与えられた座標と一定の距離より近い近傍にあって、かつ最も近い点の距離を返す。もし、一定の距離以内にTenがないなら1000000.0を返す。
	public double mottomo_tikai_Ten_kyori(Point p, double r){
		int ireturn=0;double rmin=1000000.0; double rtemp;
		for(int i = 1; i<= pointsTotal; i++){
			rtemp=oc.kyori(p,t[i]);
			if(rtemp<r){
				if(rtemp<rmin){rmin=rtemp;ireturn=i;}
			}
		}
	        return rmin;
	}




    	//一定の距離より近い位置関係にあるTen同士の位置を、共に番号の若い方の位置にする。
	public void Ten_awase(double r){
		//int ireturn=0;double rmin=10000.0; double rtemp;
		//System.out.println(" Ten_awase  Tensuu="+Tensuu );



		for(int i = 1; i<= pointsTotal -1; i++){
			for(int j = i+1; j<= pointsTotal; j++){
				if(oc.kyori(t[i],t[j])<r){   t[j].set(t[i]);	}
		//System.out.println(" Ten_awase  r="+r+" , i="+i+" , j="+j+" , kyori="+oc.kyori(t[i],t[j]) );


			}
		}
		//  return ireturn;
	}

	//Tenが一定の距離よりBouに近い位置関係にあるとき、Tenの位置を、bouの上にのるようにする。
	public void Ten_Bou_awase(double r){
		//int ireturn=0;double rmin=10000.0; double rtemp;
		for(int ib = 1; ib<= sticksTotal; ib++){
		//   Senbun s =new Senbun();
		//     s.set( Bou2Senbun(b[ib])) ;
			for(int i = 1; i<= pointsTotal -1; i++){
				if( oc.kyori_senbun(t[i],t[b[ib].getBegin()],t[b[ib].getEnd()])<r){
					//Tyokusen ty =new Tyokusen(t[b[ib].getmae()],t[b[ib].getato()]);
					//t[i].set( oc.kage_motome(ty,t[i]));
					t[i].set( oc.kage_motome(t[b[ib].getBegin()],t[b[ib].getEnd()],t[i]));
				}
			}
		}
		//  return ireturn;
	}

	//--------------------
	public int get_ten_sentakusuu(){
		int r_int=0;
		for(int i = 1; i<= pointsTotal; i++){

			if(t[i].get_ten_sentaku()==1){r_int=r_int+1;}

		}
		return r_int;
	}

	//--------------------
	public void set_ten_sentaku_1(int i){t[i].set_ten_sentaku_1();}

	//--------------------
	public void set_ten_sentaku_0(int i){t[i].set_ten_sentaku_0();}

	//--------------------
	public void set_all_ten_sentaku_0(){
		for(int i = 1; i<= pointsTotal; i++){
			t[i].set_ten_sentaku_0();
		}
	}


	//--------------------
	public void change_ten_sentaku(int i){
		if      (t[i].get_ten_sentaku()==1){ t[i].set_ten_sentaku_0();
		}else if(t[i].get_ten_sentaku()==0){ t[i].set_ten_sentaku_1();
		}
	}

	//--------------------
	public byte get_ten_sentaku(int i){return t[i].get_ten_sentaku();}


	//--------------------
	public void sentaku_ten_move(Point p){
		for(int i = 1; i<= pointsTotal; i++){

			if(t[i].get_ten_sentaku()==1){set(i,p);}

		}

	}

	//--------------------
	public void sentaku_ten_move(Point ugokasu_maeno_sentaku_point, Point pa, Point pb){
		Point p_u =new Point();
		p_u.set(ugokasu_maeno_sentaku_point.getx(), ugokasu_maeno_sentaku_point.gety());
		p_u.idou(pa.tano_Ten_iti(pb));

		for(int i = 1; i<= pointsTotal; i++){
			if(t[i].get_ten_sentaku()==1){
				set(i,p_u);
			}
		}
	}

	//--------------------


/*

		for(int i=1;i<=Tensuu;i++){
			if(t[i].get_ten_sentaku()==1){
				set(i,p_u);
			}
		}
*/











	//線分集合の全線分の情報を Memoとして出力する。 //undo,redoの記録用に使う
	public Memo getMemo(){              
		String str= "";//文字列処理用のクラスのインスタンス化

		Memo memo1 = new Memo();
		memo1.reset();

		memo1.addGyou("<点>");

                for(int i = 1; i<= pointsTotal; i++){
			memo1.addGyou("番号,"  + i);
			memo1.addGyou(    "座標,"  + t[i].getx() +","+ t[i].gety());
		}
		memo1.addGyou("</点>");


		return memo1;
	}

// -----------------------------------------------------
	public void setMemo(Memo memo1){
		//最初に点の総数を求める

	        int yomiflg=0;//0なら読み込みを行わない。1なら読み込む。
                int ibangou=0;
		Double Dd = 0.0;
		Integer Ii    = 0;

                int iten=0;

		String str = "";
		double ax,ay;

		for(int i=1;i<=memo1.getGyousuu();i++){

			StringTokenizer tk = new StringTokenizer(memo1.getGyou(i),",");
			//jtok=    tk.countTokens();
			
			str=tk.nextToken();	
			if(      str.equals("<点>" )){yomiflg=1;
			}else if(str.equals("</点>")){yomiflg=0;
			}
			if((yomiflg==1)&&(str.equals("番号"))){
			         iten=iten+1;
			}
		}
		//sousuu =isen;
		//最初に補助線分の総数が求められた

		for(int i=1;i<=memo1.getGyousuu();i++){



			StringTokenizer tk = new StringTokenizer(memo1.getGyou(i),",");
			//jtok=    tk.countTokens();
			str=tk.nextToken();	
	      			  //  	System.out.println("::::::::::"+ str+"<<<<<" );
			if(str.equals("<点>")){yomiflg=1;}
			if((yomiflg==1)&&(str.equals("番号"))){
				str=tk.nextToken();ibangou= Integer.parseInt(str);
			}
			if((yomiflg==1)&&(str.equals("座標"))){
				str=tk.nextToken();ax= Double.parseDouble(str);
				str=tk.nextToken();ay= Double.parseDouble(str);
				t[ibangou].set(ax,ay);
			}


		}


	}







//-----------------------------


















}

