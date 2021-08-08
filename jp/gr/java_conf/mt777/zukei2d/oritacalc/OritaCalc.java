 package jp.gr.java_conf.mt777.zukei2d.oritacalc;

import  jp.gr.java_conf.mt777.zukei2d.en.*;
import  jp.gr.java_conf.mt777.zukei2d.ten.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import  jp.gr.java_conf.mt777.zukei2d.senbun.*;
//import  jp.gr.java_conf.mt777.zukei2d.Ten;
import java.math.BigDecimal;

public class OritaCalc {


	//d2s ダブルをストリングに変える　小数点2桁目で四捨五入("");ｄ２ｓ
	public String d2s(double d0){
		BigDecimal bd = new BigDecimal(d0);
 
	       	//小数第2位で四捨五入
		BigDecimal bd1 =bd.setScale(1, BigDecimal.ROUND_HALF_UP);

		String sr=new String();sr=bd1.toString();
		return sr;
	}




	//ただのSystem.out.println("String");
	public void hyouji(String s0){System.out.println(s0);}


	//直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。
	public Ten kage_motome(Tyokusen t,Ten p){

		Tyokusen t1 =new Tyokusen();
		t1.set(t);
		t1.tyokkouka(p);//点p1を通って tに直行する直線u1を求める。
		return  kouten_motome(t,t1);    
	}

	//点P0とP1を通る直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。
	public Ten kage_motome(Ten p0,Ten p1,Ten p){
		Tyokusen t =new Tyokusen(p0,p1);
		return  kage_motome(t,p);    
	}

	//線分s0を含む直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。
	public Ten kage_motome(Senbun s0,Ten p){
		return  kage_motome(s0.geta() , s0.getb() , p);    
	}


	//2つの点が同じ位置(true)か異なる(false)か判定する関数----------------------------------
	public boolean hitosii(Ten p1,Ten p2){
		return hitosii( p1,p2,0.1);//ここで誤差が定義されている。
	}

	public boolean hitosii(Ten p1,Ten p2,double r){//rは誤差の許容度。rが負なら厳密判定。

		//厳密に判定。
		if(r<=0.0){
			if((p1.getx()==p2.getx())&&(p1.gety()==p2.gety()) ){return true;} 
		}
		//誤差を許容。
		if(r>0){
			if(kyori(p1,p2)<=r ){return true;}
		}
		return  false;
	}

	//２点間の距離（整数）を求める関数----------------------------------------------------
	public double kyori(Ten p0,Ten p1){
		return p0.kyori(p1);
	}
/*
	public double kyori(Ten p0,Ten p1){
		double x0=p0.getx(),y0=p0.gety();
		double x1=p1.getx(),y1=p1.gety();
		return Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0));
	}
*/ 
	//２点間a,bを指定して、ベクトルabとx軸のなす角度を求める関数。もしa=bなら-10000.0を返す----------------------------------------------------
	public double kakudo(Ten a,Ten b){
		double ax,ay,bx,by,x,y,L,c,ret;
		ax=a.getx();ay=a.gety();
		bx=b.getx();by=b.gety();
		x=bx-ax;y=by-ay;
		L= Math.sqrt(x*x+y*y); if(L<=0.0){return -10000.0;}
		c=x/L;if (c>1.0){c=1.0;}

		ret=Math.acos(c);
		if (y<0.0){ret=-ret;}
		ret=180.0*ret/Math.PI;
		if(ret<0){ret=ret+360.0;}
		return ret;
	} 
  
  
	//線分を指定して、ベクトルabとx軸のなす角度を求める関数。もしa=bなら-10000.0を返す----------------------------------------------------
	public double kakudo(Senbun s){
		return 	 kakudo(s.geta(),s.getb());
	} 
  
	//線分を指定して、ベクトルabとx軸のなす角度を求める関数。もしa=bなら-10000.0を返す----------------------------------------------------
	public double kakudozure(Senbun s,double a){
		double b;//実際の角度をaで割った時の剰余
   		b = kakudo(s)%a;
		if(a-b<b){b=a-b;}
		return b;
	}  
  
	//点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数
	public int hakononaka(Ten p1,Ten pa,Ten p2){    
		Tyokusen t =new Tyokusen(p1,p2);//p1,p2を通る直線tを求める。
		Tyokusen u1 =new Tyokusen(p1,p2); u1.tyokkouka(p1);//点p1を通って tに直行する直線u1を求める。
		Tyokusen u2 =new Tyokusen(p1,p2); u2.tyokkouka(p2);//点p2を通って tに直行する直線u2を求める。

		if(u1.dainyuukeisan(pa)*u2.dainyuukeisan(pa) ==0.0){return 1;}
		if(u1.dainyuukeisan(pa)*u2.dainyuukeisan(pa) <0.0){return 2;}
		return  0;//箱の外部にある場合
	}


	//点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数。これは 少しはみ出しても長方形内にあるとみなす。
	//具体的には線分の中に点があるかの判定の際、わずかに点が線分の外にある場合は、線分の中にあると、甘く判定する。描き職人で展開図を描くときは、この甘いほうを使わないとT字型の線分の交差分割に失敗する
	//しかし、なぜか、折り畳み推定にこの甘いほうを使うと無限ループになるようで、うまくいかない。この正確な解明は未解決20161105
	public int hakononaka_amai(Ten p1,Ten pa,Ten p2){    
		Tyokusen t =new Tyokusen(p1,p2);//p1,p2を通る直線tを求める。
		Tyokusen u1 =new Tyokusen(p1,p2); u1.tyokkouka(p1);//点p1を通って tに直行する直線u1を求める。
		Tyokusen u2 =new Tyokusen(p1,p2); u2.tyokkouka(p2);//点p2を通って tに直行する直線u2を求める。

		//if(u1.dainyuukeisan(pa)*u2.dainyuukeisan(pa) ==0.0){return 1;}

		if(u1.kyorikeisan(pa)<0.00001){return 1;}
		if(u2.kyorikeisan(pa)<0.00001){return 1;}

		if(u1.dainyuukeisan(pa)*u2.dainyuukeisan(pa) <0.0){return 2;}
		return  0;//箱の外部にある場合
	}



	//点pが指定された線分とどの部所で近い(r以内)かどうかを判定する関数　---------------------------------
	//0=近くない、1=a点に近い、2=b点に近い、3=柄の部分に近い
	public int senbun_busyo_sagasi(Ten p,Senbun s0,double r) {
		if( r >       kyori( p,s0.geta())){ return 1; }//a点に近いかどうか 
		if( r >       kyori( p,s0.getb())){ return 2; }//b点に近いかどうか
		if( r> kyori_senbun( p,s0 )){ return 3; }//柄の部分に近いかどうか
		return 0;
	}




	//点p0と、二点p1,p2を両端とする線分との間の距離を求める関数----------------------------------------------------
	public double kyori_senbun(Ten p0,Ten p1,Ten p2){
		// Ten p1 = new Ten();   p1.set(s.geta());
		// Ten p2 = new Ten();   p2.set(s.getb());    

		//p1とp2が同じ場合
		if(kyori(p1,p2)==0.0){ return kyori( p0, p1); }

		//p1とp2が異なる場合
		Tyokusen t =new Tyokusen(p1,p2);//p1,p2を通る直線tを求める。
		Tyokusen u =new Tyokusen(p1,p2); u.tyokkouka(p0);//点p0を通って tに直行する直線uを求める。
  
		if(hakononaka(p1,kouten_motome(t,u),p2)>=1){return t.kyorikeisan(p0);}//tとuの交点がp1とp2の間にある場合。
		return Math.min(kyori(p0,p1),kyori(p0,p2));//tとuの交点がp1とp2の間にない場合。
	}

	//点p0と、線分sとの間の距離を求める関数----------------------------------------------------
	public double kyori_senbun(Ten p0,Senbun s){
		Ten p1 = new Ten();   p1.set(s.geta());
		Ten p2 = new Ten();   p2.set(s.getb());    
		return kyori_senbun(p0, p1, p2);
	}

	//２つの線分が、交差するかどうかを判定する関数----------------------------------------------------
		// 0=交差しない、　
		// 1=２つの線分が平行でなく、一点で十字路型で交差する、
		// 2番代=２つの線分が平行でなく、一点でＴ字路型またはくの字型で交差する
		// 3=２つの線分が平行で、交差する
		// 4=線分s1と線分s2が点で、交差する
		// 5=線分s1が点で、交差する
		// 6=線分s2が点で、交差する
		//注意！p1とp2が同じ、またはp3とp4が同じ場合は結果がおかしくなるがが、
		//この関数自体にはチェック機構をつけていないので、気づきにくいことがある。
	public int senbun_kousa_hantei(Senbun s1,Senbun s2){   
		//return senbun_kousa_hantei( s1,s2,0.001,0.001) ;
		return senbun_kousa_hantei( s1,s2,0.01,0.01) ;

		//return senbun_kousa_hantei( s1,s2,0.000001,0.000001) ;
	}


	public int senbun_kousa_hantei_amai(Senbun s1,Senbun s2){   
		//return senbun_kousa_hantei_amai( s1,s2,0.000001,0.000001) ;
		return senbun_kousa_hantei_amai( s1,s2,0.01,0.01) ;
	}


	public int senbun_kousa_hantei(Senbun s1,Senbun s2,double rhit,double rhei){    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度  
		double x1max=s1.getax();
		double x1min=s1.getax();
		double y1max=s1.getay();
		double y1min=s1.getay();
		if(x1max<s1.getbx()){x1max=s1.getbx();}
		if(x1min>s1.getbx()){x1min=s1.getbx();}
		if(y1max<s1.getby()){y1max=s1.getby();}
		if(y1min>s1.getby()){y1min=s1.getby();}
		double x2max=s2.getax();
		double x2min=s2.getax();
		double y2max=s2.getay();
		double y2min=s2.getay();
		if(x2max<s2.getbx()){x2max=s2.getbx();}
		if(x2min>s2.getbx()){x2min=s2.getbx();}
		if(y2max<s2.getby()){y2max=s2.getby();}
		if(y2min>s2.getby()){y2min=s2.getby();}
    
		if(x1max+rhit+0.1<x2min){return 0;}
		if(x1min-rhit-0.1>x2max){return 0;}    
		if(y1max+rhit+0.1<y2min){return 0;}
		if(y1min-rhit-0.1>y2max){return 0;}    
    
		//System.out.println("###########");

		Ten p1 = new Ten();   p1.set(s1.geta());
		Ten p2 = new Ten();   p2.set(s1.getb());    
		Ten p3 = new Ten();   p3.set(s2.geta());
		Ten p4 = new Ten();   p4.set(s2.getb()); 

		Tyokusen t1 =new Tyokusen(p1,p2);
		Tyokusen t2 =new Tyokusen(p3,p4);
		//System.out.print("　　線分交差判定での平行判定の結果　＝　");
		//System.out.println (heikou_hantei(t1,t2,rhei)); 
		// heikou_hantei(t1,t2,rhei)

		//例外処理　線分s1と線分s2が点の場合
		if(((p1.getx()==p2.getx())&&(p1.gety()==p2.gety()))
		&&
		((p3.getx()==p4.getx())&&(p3.gety()==p4.gety()))){    
			if((p1.getx()==p3.getx())&&(p1.gety()==p3.gety())){return 4;}
			return 0;
		}

		//例外処理　線分s1が点の場合
		if((p1.getx()==p2.getx())&&(p1.gety()==p2.gety())){    
			if((hakononaka(p3,p1,p4)>=1)&&(t2.dainyuukeisan(p1)==0.0)){return 5;}
			return 0;
		}

		//例外処理　線分s2が点の場合
		if((p3.getx()==p4.getx())&&(p3.gety()==p4.gety())){    
			if((hakononaka(p1,p3,p2)>=1)&&(t1.dainyuukeisan(p3)==0.0)){return 6;}
			return 0;
		}

		// System.out.println("AAAAAAAAAAAA");
		if (heikou_hantei(t1,t2,rhei)==0){    //２つの直線が平行でない
			Ten pk = new Ten();   pk.set(kouten_motome(t1,t2));    //<<<<<<<<<<<<<<<<<<<<<<<
			if( (hakononaka(p1,pk,p2)>=1)
			&& (hakononaka(p3,pk,p4)>=1) ){
				if( hitosii(p1,p3,rhit)){return 21;}//L字型
				if( hitosii(p1,p4,rhit)){return 22;}//L字型
				if( hitosii(p2,p3,rhit)){return 23;}//L字型
				if( hitosii(p2,p4,rhit)){return 24;}//L字型
				if( hitosii(p1,pk,rhit)){return 25;}//T字型 s1が縦棒
				if( hitosii(p2,pk,rhit)){return 26;}//T字型 s1が縦棒
				if( hitosii(p3,pk,rhit)){return 27;}//T字型 s2が縦棒
				if( hitosii(p4,pk,rhit)){return 28;}//T字型 s2が縦棒
				return 1;					// <<<<<<<<<<<<<<<<< return 1;
			}
			return 0;
		}

		if (heikou_hantei(t1,t2,rhei)==1){ //２つの直線が平行で、y切片は一致しない
			// System.out.println("BBBBBBBBBBB");    
			return 0;
		}

		// ２つの線分が全く同じ
		if( hitosii(p1,p3,rhit)&& hitosii(p2,p4,rhit))  {return 31;} 
		if( hitosii(p1,p4,rhit)&& hitosii(p2,p3,rhit))  {return 31;} 

		// System.out.println("###########");
    
		//２つの直線が平行で、y切片も一致する
		if (heikou_hantei(t1,t2,rhei)==2){ 
			if(hitosii(p1,p3,rhit)){ //2つの線分の端点どうしが1点で重なる場合
				if(hakononaka(p1,p4,p2)==2){return 321;}
				if(hakononaka(p3,p2,p4)==2){return 322;}
				if(hakononaka(p2,p1,p4)==2){return 323;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p1,p4,rhit)){
				if(hakononaka(p1,p3,p2)==2){return 331;}
				if(hakononaka(p4,p2,p3)==2){return 332;}
				if(hakononaka(p2,p1,p3)==2){return 333;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p2,p3,rhit)){
				if(hakononaka(p2,p4,p1)==2){return 341;}
				if(hakononaka(p3,p1,p4)==2){return 342;}
				if(hakononaka(p1,p2,p4)==2){return 343;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p2,p4,rhit)){
				if(hakononaka(p2,p3,p1)==2){return 351;}
				if(hakononaka(p4,p1,p3)==2){return 352;}
				if(hakononaka(p1,p2,p3)==2){return 353;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			//2つの線分の端点どうしが重ならない場合
			if((hakononaka(p1,p3,p4)==2)&&(hakononaka(p3,p4,p2)==2)){return 361;}//線分(p1,p2)に線分(p3,p4)が含まれる
			if((hakononaka(p1,p4,p3)==2)&&(hakononaka(p4,p3,p2)==2)){return 362;}//線分(p1,p2)に線分(p3,p4)が含まれる

			if((hakononaka(p3,p1,p2)==2)&&(hakononaka(p1,p2,p4)==2)){return 363;}//線分(p3,p4)に線分(p1,p2)が含まれる      
			if((hakononaka(p3,p2,p1)==2)&&(hakononaka(p2,p1,p4)==2)){return 364;}//線分(p3,p4)に線分(p1,p2)が含まれる      


      
			if((hakononaka(p1,p3,p2)==2)&&(hakononaka(p3,p2,p4)==2))  {return 371;}//線分(p1,p2)のP2側と線分(p3,p4)のP3側が部分的に重なる  
			if((hakononaka(p1,p4,p2)==2)&&(hakononaka(p4,p2,p3)==2))  {return 372;}//線分(p1,p2)のP2側と線分(p4,p3)のP4側が部分的に重なる

			if((hakononaka(p3,p1,p4)==2)&&(hakononaka(p1,p4,p2)==2))  {return 373;}//線分(p3,p4)のP4側と線分(p1,p2)のP1側が部分的に重なる
			if((hakononaka(p4,p1,p3)==2)&&(hakononaka(p1,p3,p2)==2))  {return 374;}//線分(p4,p3)のP3側と線分(p1,p2)のP1側が部分的に重なる

			return 0;
		}  
		return -1;//ここは何らかのエラーの時に通る。
    
	}








	//senbun_kousa_hantei_amaiの甘いところは、具体的にはreturn 21からreturn 28までの前提になる	if( (hakononaka(p1,pk,p2)>=1)&& (hakononaka(p3,pk,p4)>=1) )のかわりに
	// (hakononaka_amai(p1,pk,p2)>=1)&& (hakononaka_amai(p3,pk,p4)を使っていること。hakononaka_amaiは
	//点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数。これは 少しはみ出しても長方形内にあるとみなす。
	//具体的には線分の中に点があるかの判定の際、わずかに点が線分の外にある場合は、線分の中にあると、甘く判定する。描き職人で展開図を描くときは、この甘いほうを使わないとT字型の線分の交差分割に失敗する
	//しかし、なぜか、折り畳み推定にこの甘いほうを使うと無限ループになるようで、うまくいかない。この正確な解明は未解決20161105

	public int senbun_kousa_hantei_amai(Senbun s1,Senbun s2,double rhit,double rhei){    //r_hitosiiとr_heikouhanteiは、hitosiiとheikou_hanteiのずれの許容程度  
		double x1max=s1.getax();
		double x1min=s1.getax();
		double y1max=s1.getay();
		double y1min=s1.getay();
		if(x1max<s1.getbx()){x1max=s1.getbx();}
		if(x1min>s1.getbx()){x1min=s1.getbx();}
		if(y1max<s1.getby()){y1max=s1.getby();}
		if(y1min>s1.getby()){y1min=s1.getby();}
		double x2max=s2.getax();
		double x2min=s2.getax();
		double y2max=s2.getay();
		double y2min=s2.getay();
		if(x2max<s2.getbx()){x2max=s2.getbx();}
		if(x2min>s2.getbx()){x2min=s2.getbx();}
		if(y2max<s2.getby()){y2max=s2.getby();}
		if(y2min>s2.getby()){y2min=s2.getby();}
    
		if(x1max+rhit+0.1<x2min){return 0;}
		if(x1min-rhit-0.1>x2max){return 0;}    
		if(y1max+rhit+0.1<y2min){return 0;}
		if(y1min-rhit-0.1>y2max){return 0;}    
    
		//System.out.println("###########");
    
		Ten p1 = new Ten();   p1.set(s1.geta());
		Ten p2 = new Ten();   p2.set(s1.getb());    
		Ten p3 = new Ten();   p3.set(s2.geta());
		Ten p4 = new Ten();   p4.set(s2.getb()); 

		Tyokusen t1 =new Tyokusen(p1,p2);
		Tyokusen t2 =new Tyokusen(p3,p4);
		//System.out.print("　　線分交差判定での平行判定の結果　＝　");
		//System.out.println (heikou_hantei(t1,t2,rhei)); 
		// heikou_hantei(t1,t2,rhei)

		//例外処理　線分s1と線分s2が点の場合
		if(((p1.getx()==p2.getx())&&(p1.gety()==p2.gety()))
		&&
		((p3.getx()==p4.getx())&&(p3.gety()==p4.gety()))){    
			if((p1.getx()==p3.getx())&&(p1.gety()==p3.gety())){return 4;}
			return 0;
		}

		//例外処理　線分s1が点の場合
		if((p1.getx()==p2.getx())&&(p1.gety()==p2.gety())){    
			if((hakononaka(p3,p1,p4)>=1)&&(t2.dainyuukeisan(p1)==0.0)){return 5;}
			return 0;
		}

		//例外処理　線分s2が点の場合
		if((p3.getx()==p4.getx())&&(p3.gety()==p4.gety())){    
			if((hakononaka(p1,p3,p2)>=1)&&(t1.dainyuukeisan(p3)==0.0)){return 6;}
			return 0;
		}

		// System.out.println("AAAAAAAAAAAA");
		if (heikou_hantei(t1,t2,rhei)==0){    //２つの直線が平行でない
			Ten pk = new Ten();   pk.set(kouten_motome(t1,t2));    //<<<<<<<<<<<<<<<<<<<<<<<
			if( (hakononaka_amai(p1,pk,p2)>=1)
			&& (hakononaka_amai(p3,pk,p4)>=1) ){
				if( hitosii(p1,p3,rhit)){return 21;}//L字型
				if( hitosii(p1,p4,rhit)){return 22;}//L字型
				if( hitosii(p2,p3,rhit)){return 23;}//L字型
				if( hitosii(p2,p4,rhit)){return 24;}//L字型
				if( hitosii(p1,pk,rhit)){return 25;}//T字型 s1が縦棒
				if( hitosii(p2,pk,rhit)){return 26;}//T字型 s1が縦棒
				if( hitosii(p3,pk,rhit)){return 27;}//T字型 s2が縦棒
				if( hitosii(p4,pk,rhit)){return 28;}//T字型 s2が縦棒
				return 1;
			}
			return 0;
		}

		if (heikou_hantei(t1,t2,rhei)==1){ //２つの直線が平行で、y切片は一致しない
			// System.out.println("BBBBBBBBBBB");    
			return 0;
		}
    
		// ２つの線分が全く同じ
		if( hitosii(p1,p3,rhit)&& hitosii(p2,p4,rhit))  {return 31;} 
		if( hitosii(p1,p4,rhit)&& hitosii(p2,p3,rhit))  {return 31;} 

		// System.out.println("###########");
    
		//２つの直線が平行で、y切片も一致する
		if (heikou_hantei(t1,t2,rhei)==2){ 
			if(hitosii(p1,p3,rhit)){ //2つの線分の端点どうしが1点で重なる場合
				if(hakononaka(p1,p4,p2)==2){return 321;}//長い線分に短い線分が含まれる
				if(hakononaka(p3,p2,p4)==2){return 322;}//長い線分に短い線分が含まれる
				if(hakononaka(p2,p1,p4)==2){return 323;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p1,p4,rhit)){
				if(hakononaka(p1,p3,p2)==2){return 331;}//長い線分に短い線分が含まれる
				if(hakononaka(p4,p2,p3)==2){return 332;}//長い線分に短い線分が含まれる
				if(hakononaka(p2,p1,p3)==2){return 333;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p2,p3,rhit)){
				if(hakononaka(p2,p4,p1)==2){return 341;}//長い線分に短い線分が含まれる
				if(hakononaka(p3,p1,p4)==2){return 342;}//長い線分に短い線分が含まれる
				if(hakononaka(p1,p2,p4)==2){return 343;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			if(hitosii(p2,p4,rhit)){
				if(hakononaka(p2,p3,p1)==2){return 351;}//長い線分に短い線分が含まれる
				if(hakononaka(p4,p1,p3)==2){return 352;}//長い線分に短い線分が含まれる
				if(hakononaka(p1,p2,p3)==2){return 353;}//2つの線分は1点で重なるだけで、それ以外では重ならない
			}

			//2つの線分の端点どうしが重ならない場合
			if((hakononaka(p1,p3,p4)==2)&&(hakononaka(p3,p4,p2)==2)){return 361;}//線分(p1,p2)に線分(p3,p4)が含まれる
			if((hakononaka(p1,p4,p3)==2)&&(hakononaka(p4,p3,p2)==2)){return 362;}//線分(p1,p2)に線分(p3,p4)が含まれる

			if((hakononaka(p3,p1,p2)==2)&&(hakononaka(p1,p2,p4)==2)){return 363;}//線分(p3,p4)に線分(p1,p2)が含まれる      
			if((hakononaka(p3,p2,p1)==2)&&(hakononaka(p2,p1,p4)==2)){return 364;}//線分(p3,p4)に線分(p1,p2)が含まれる      
      
			if((hakononaka(p1,p3,p2)==2)&&(hakononaka(p3,p2,p4)==2))  {return 371;}
			if((hakononaka(p1,p4,p2)==2)&&(hakononaka(p4,p2,p3)==2))  {return 372;}
			if((hakononaka(p3,p1,p4)==2)&&(hakononaka(p1,p4,p2)==2))  {return 373;}
			if((hakononaka(p4,p1,p3)==2)&&(hakononaka(p1,p3,p2)==2))  {return 374;}
   
			return 0;
		}  
		return -1;//ここは何らかのエラーの時に通る。
    
	}

















	//２つの直線が平行かどうかを判定する関数。
	public int heikou_hantei(Tyokusen t1,Tyokusen t2){
		return  heikou_hantei( t1, t2,0.1) ;
	}

	//２つの線分が平行かどうかを判定する関数。
	public int heikou_hantei(Senbun s1,Senbun s2,double r){
		return  heikou_hantei( Senbun2Tyokusen(s1), Senbun2Tyokusen(s2),r) ;
	}

  
	public int heikou_hantei(Tyokusen t1,Tyokusen t2,double r){//rは誤差の許容度。rが負なら厳密判定。
		//0=平行でない、1=平行で２直線が一致しない、2=平行で２直線が一致する
		double a1=t1.geta(),b1=t1.getb(),c1=t1.getc();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
 		double a2=t2.geta(),b2=t2.getb(),c2=t2.getc();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

		//System.out.print("平行判定のr　＝　");System.out.println(r);
		//厳密に判定----------------------------------------
		if(r<=0.0){
			//２直線が平行の場合
			if(a1*b2-a2*b1==0){
				//２直線は同一の場合
				if((a1*a1+b1*b1)*c2*c2==(a2*a2+b2*b2)*c1*c1){return 2;}//厳密に判定。 
				//２直線が異なる場合
				else{return 1;}
			}
		}

		//誤差を許容----------------------------------------
		if(r>0){	
			//２直線が平行の場合
			if(Math.abs(a1*b2-a2*b1)<r){
				//２直線は同一の場合


				//原点（0、0）と各直線との距離を比較
				//double kyoriT=Math.abs(c1/Math.sqrt(a1*a1+b1*b1)-c2/Math.sqrt(a2*a2+b2*b2));//20181027、ver3.049までのバグありの処理
				//double kyoriT=Math.abs(   Math.abs(  c1/Math.sqrt(a1*a1+b1*b1)  )  -   Math.abs(  c2/Math.sqrt(a2*a2+b2*b2)  )      );//20181027、ver3.050以降のバグ無しの処理
				double kyoriT=t2.kyorikeisan(t1.kage_motome(new Ten(0.0,0.0)));//t1上の点とt2との距離//t1.kage_motome(new Ten(0.0,0.0))   は点（0,0）のt1上の影を求める（t1上の点ならなんでもいい）//20181115修正



				if(kyoriT<r){//誤差を許容。
					return 2;
				}
				//２直線が異なる場合
				else{return 1;}
			}
		}

		//２直線が非平行の場合-------------------------------------------------
		return 0;
	}


	//２つの直線の交点を求める関数
	public Ten kouten_motome(Tyokusen t1,Tyokusen t2){
		double a1=t1.geta(),b1=t1.getb(),c1=t1.getc();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
		double a2=t2.geta(),b2=t2.getb(),c2=t2.getc();//直線t2, a2*x+b2*y+c2=0の各係数を求める。

//System.out.println("   "+(b1*c2-b2*c1)/(a1*b2-a2*b1)+"::::::::"+(a2*c1-a1*c2)/(a1*b2-a2*b1));

		return new Ten( (b1*c2-b2*c1)/(a1*b2-a2*b1) , (a2*c1-a1*c2)/(a1*b2-a2*b1) );
	}





	//２つの直線の交点を求める関数(複製)
	public Ten kouten_motome_01(Tyokusen t1,Tyokusen t2){
		double a1=t1.geta(),b1=t1.getb(),c1=t1.getc();//直線t1, a1*x+b1*y+c1=0の各係数を求める。
		double a2=t2.geta(),b2=t2.getb(),c2=t2.getc();//直線t2, a2*x+b2*y+c2=0の各係数を求める。
		return new Ten( (b1*c2-b2*c1)/(a1*b2-a2*b1) , (a2*c1-a1*c2)/(a1*b2-a2*b1) );
	}





	public Tyokusen Senbun2Tyokusen(Senbun s){//線分を含む直線を得る
		Tyokusen t =new Tyokusen(s.geta(),s.getb());
		return t;
	}

	//２つの線分を直線とみなして交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
	public Ten kouten_motome(Senbun s1,Senbun s2){
		return  kouten_motome(Senbun2Tyokusen(s1),Senbun2Tyokusen(s2));
	}

	//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
	public Ten kouten_motome(Tyokusen t1,Senbun s2){
		return  kouten_motome(t1,Senbun2Tyokusen(s2));
	}

	//線分を直線とみなして他の直線との交点を求める関数。線分としては交差しなくても、直線として交差している場合の交点を返す
	public Ten kouten_motome(Senbun s1,Tyokusen t2){
		return  kouten_motome(Senbun2Tyokusen(s1),t2);
	}





	//線分を真横に平行移動する関数（元の線分は変えずに新しい線分を返す）
	public Senbun mayoko_idou(Senbun s,double d) {
		Tyokusen t =new Tyokusen(s.geta(),s.getb());
		Tyokusen ta =new Tyokusen(s.geta(),s.getb());	     
		Tyokusen tb =new Tyokusen(s.geta(),s.getb());	     
		ta.tyokkouka(s.geta());tb.tyokkouka(s.getb());
		Tyokusen td =new Tyokusen(s.geta(),s.getb());
		td.heikouidou(d);
	     
		Senbun sreturn =new Senbun(kouten_motome_01(ta,td),kouten_motome_01(tb,td));

		return sreturn;
	}
//------------------------------------
	//点aを中心に点bをd度回転した点を返す関数（元の点は変えずに新しい点を返す）
	public Ten ten_kaiten(Ten a,Ten b,double d) {

double Mcd=Math.cos(d*Math.PI/180.0);
double Msd=Math.sin(d*Math.PI/180.0);

double bx1=Mcd*(b.getx()-a.getx())-Msd*(b.gety()-a.gety())+a.getx();
double by1=Msd*(b.getx()-a.getx())+Mcd*(b.gety()-a.gety())+a.gety();

//double ax1=s0.getax();
//double ay1=s0.getay();	     
		Ten t_return =new Ten( bx1,by1);

		return t_return;
	}

//------------------------------------
	//点aを中心に点bをd度回転しabの距離がr倍の点を返す関数（元の点は変えずに新しい点を返す）
	public Ten ten_kaiten(Ten a,Ten b,double d,double r) {

		double Mcd=Math.cos(d*Math.PI/180.0);
		double Msd=Math.sin(d*Math.PI/180.0);

		double bx1=r*(Mcd*(b.getx()-a.getx())-Msd*(b.gety()-a.gety()))+a.getx();
		double by1=r*(Msd*(b.getx()-a.getx())+Mcd*(b.gety()-a.gety()))+a.gety();

		Ten t_return =new Ten( bx1,by1);

		return t_return;
	}

//------------------------------------
	//点aを中心に点bを元にしてabの距離がr倍の点を返す関数（元の点は変えずに新しい点を返す）20161224 未検証
	public Ten ten_bai(Ten a,Ten b,double r) {
		double bx1=r*(b.getx()-a.getx())+a.getx();
		double by1=r*(b.gety()-a.gety())+a.gety();
     
		Ten t_return =new Ten( bx1,by1);

		return t_return;
	}


//------------------------------------

	//線分abをcを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
	public Senbun Senbun_kaiten(Senbun s0,Ten c,double d,double r) {
		Senbun s_return =new Senbun(ten_kaiten(s0.geta(),c,d,r),ten_kaiten(s0.getb(),c,d,r));
		return s_return;
	}


// ------------------------------------

	//線分abをaを中心にd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
	public Senbun Senbun_kaiten(Senbun s0,double d) {
//s0.getax(),s0.getay()

//(Math.cos(d*3.14159265/180.0),-Math.sin(d*3.14159265/180.0) )  (s0.getbx()-s0.getax()) + (s0.getax())
//(Math.sin(d*3.14159265/180.0), Math.cos(d*3.14159265/180.0) )  (s0.getby()-s0.getay())   (s0.getay())

//double Mcd=Math.cos(d*3.14159265/180.0);
//double Msd=Math.sin(d*3.14159265/180.0);
double Mcd=Math.cos(d*Math.PI/180.0);
double Msd=Math.sin(d*Math.PI/180.0);

double bx1=Mcd*(s0.getbx()-s0.getax())-Msd*(s0.getby()-s0.getay())+s0.getax();
double by1=Msd*(s0.getbx()-s0.getax())+Mcd*(s0.getby()-s0.getay())+s0.getay();

double ax1=s0.getax();
double ay1=s0.getay();	     
		Senbun s_return =new Senbun( ax1, ay1,bx1,by1);

		return s_return;
	}



	//線分abをaを中心にr倍してd度回転した線分を返す関数（元の線分は変えずに新しい線分を返す）
	public Senbun Senbun_kaiten(Senbun s0,double d,double r) {
		//s0.getax(),s0.getay()

		//(Math.cos(d*3.14159265/180.0),-Math.sin(d*3.14159265/180.0) )  (s0.getbx()-s0.getax()) + (s0.getax())
		//(Math.sin(d*3.14159265/180.0), Math.cos(d*3.14159265/180.0) )  (s0.getby()-s0.getay())   (s0.getay())

		double Mcd=Math.cos(d*Math.PI/180.0);
		double Msd=Math.sin(d*Math.PI/180.0);

		double bx1=   r* (Mcd*(s0.getbx()-s0.getax())-Msd*(s0.getby()-s0.getay()) ) +  s0.getax();
		double by1=   r* (Msd*(s0.getbx()-s0.getax())+Mcd*(s0.getby()-s0.getay()) ) +  s0.getay();

		double ax1=s0.getax();
		double ay1=s0.getay();	     
		Senbun s_return =new Senbun( ax1, ay1,bx1,by1);

		return s_return;
	}




	//線分abをaを中心にr倍した線分を返す関数（元の線分は変えずに新しい線分を返す）
	public Senbun Senbun_bai(Senbun s0,double r) {

		double bx1=   r* (s0.getbx()-s0.getax())  +  s0.getax();
		double by1=   r* (s0.getby()-s0.getay())  +  s0.getay();

		double ax1=s0.getax();
		double ay1=s0.getay();	     
		Senbun s_return =new Senbun( ax1, ay1,bx1,by1);

		return s_return;
	}





	//線分Aの、線分Jを軸とした対照位置にある線分Bを求める関数
	public Senbun sentaisyou_senbun_motome(Senbun s0,Senbun jiku){
		Ten p_a = new Ten();  p_a.set(s0.geta()); 
		Ten p_b = new Ten();  p_b.set(s0.getb());
		Ten jiku_a = new Ten();  jiku_a.set(jiku.geta()); 
		Ten jiku_b = new Ten();  jiku_b.set(jiku.getb()); 
    
		Senbun s1 = new Senbun();  
		s1.set( sentaisyou_ten_motome(jiku_a,jiku_b,p_a), sentaisyou_ten_motome(jiku_a,jiku_b,p_b)  );

		return s1;
	}



	//直線t0に関して、点pの対照位置にある点を求める関数
	public Ten sentaisyou_ten_motome(Tyokusen t0,Ten p){
		Ten p1 = new Ten();  // p1.set(s.geta());
		Ten p2 = new Ten();  // p2.set(s.getb());    

		Tyokusen s1 =new Tyokusen();s1.set(t0);
		Tyokusen s2 =new Tyokusen();s2.set(t0);
    
		s2.tyokkouka(p);//点pを通って s1に直行する直線s2を求める。

		p1= kouten_motome(s1,s2) ;
		p2.set(  2.0*p1.getx()-p.getx(),  2.0*p1.gety()-p.gety()            );
		return p2;
	}

	//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める関数
	public Ten sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){
		Ten p1 = new Ten();  // p1.set(s.geta());
		Ten p2 = new Ten();  // p2.set(s.getb());    

		Tyokusen s1 =new Tyokusen(t1,t2);
		Tyokusen s2 =new Tyokusen(t1,t2);
    
		s2.tyokkouka(p);//点pを通って s1に直行する直線s2を求める。

		p1= kouten_motome(s1,s2) ;
		p2.set(  2.0*p1.getx()-p.getx(),  2.0*p1.gety()-p.gety()            );
		return p2;
	}

	//角度を-180.0度より大きく180.0度以下に押さえる関数
	public double kakudo_osame_m180_180(double kakudo){
		while (kakudo<=-180.0){
			kakudo=kakudo+360.0;
		}
		while (kakudo>180.0){
			kakudo=kakudo-360.0;
		}
		return kakudo;
	}

	//角度を0.0度以上360.0度未満に押さえる関数
	public double kakudo_osame_0_360(double kakudo){
		while (kakudo<0.0){
			kakudo=kakudo+360.0;
		}
		while (kakudo>=360.0){
			kakudo=kakudo-360.0;
		}
		return kakudo;
	}


	//角度を0.0度以上kmax度未満に押さえる関数(円錐の頂点の伏見定理などで使う)
	public double kakudo_osame_0_kmax(double kakudo,double kmax){
		while (kakudo<0.0){
			kakudo=kakudo+kmax;
		}
		while (kakudo>=kmax){
			kakudo=kakudo-kmax;
		}
		return kakudo;
	}


	//線分s1とs2のなす角度
	public double kakudo(Senbun s1,Senbun s2){
		Ten a=new Ten();a.set(s1.geta());
		Ten b=new Ten();b.set(s1.getb());
		Ten c=new Ten();c.set(s2.geta());
		Ten d=new Ten();d.set(s2.getb());

		return	kakudo_osame_0_360(kakudo(c,d)-kakudo(a,b));
	} 


	//ベクトルabとcdのなす角度
	public double kakudo(Ten a,Ten b,Ten c,Ten d){
		return	kakudo_osame_0_360(kakudo(c,d)-kakudo(a,b));
	} 
  
	//三角形の内心を求める
	public Ten naisin(Ten ta,Ten tb,Ten tc){
		double A,B,C,XA,XB,XC,YA,YB,YC,XD,YD,XE,YE,G,H,K,L,P,Q,XN,YN;
		Ten tn = new Ten();
		 XA=ta.getx();
		YA=ta.gety();
		XB=tb.getx();
		YB=tb.gety();
		XC=tc.getx();
		YC=tc.gety();
	 
		A=Math.sqrt( (XC-XB)*(XC-XB)+(YC-YB)*(YC-YB) );
		B=Math.sqrt( (XA-XC)*(XA-XC)+(YA-YC)*(YA-YC) );
		C=Math.sqrt( (XB-XA)*(XB-XA)+(YB-YA)*(YB-YA) );
		XD=(C*XC+B*XB)/(B+C) ;YD=(C*YC+B*YB)/(B+C);
		XE=(C*XC+A*XA)/(A+C) ;YE=(C*YC+A*YA)/(A+C);
		G=XD-XA ;H=YD-YA; K=XE-XB; L=YE-YB;
		P=G*YA-H*XA;Q=K*YB-L*XB;
		XN=(G*Q-K*P)/(H*K-G*L);YN=(L*P-H*Q)/(G*L-H*K);
       
		tn.set(XN,YN);

		return tn;
	}
// -------------------------------
	//内分点を求める。
	public Ten naibun(Ten a,Ten b,double d_naibun_s,double d_naibun_t){
		Ten r_ten=new Ten(-10000.0,-10000.0);
		if(kyori(a,b)<0.000001){return r_ten;}

		if((d_naibun_s==0.0)&&(d_naibun_t==0.0)){return r_ten;}
		if((d_naibun_s==0.0)&&(d_naibun_t!=0.0)){return a;}
		if((d_naibun_s!=0.0)&&(d_naibun_t==0.0)){return b;}
		if((d_naibun_s!=0.0)&&(d_naibun_t!=0.0)){
			Senbun s_ab =new Senbun(a,b);
			double nx=(d_naibun_t * s_ab.getax() + d_naibun_s * s_ab.getbx()) /(d_naibun_s+d_naibun_t)  ;
			double ny=(d_naibun_t * s_ab.getay() + d_naibun_s * s_ab.getby()) /(d_naibun_s+d_naibun_t)  ;
			r_ten.set(nx,ny);
			return r_ten;
   		}
		return r_ten;
	}
// -------------------------------
	//中間点を求める。
	public Ten tyuukanten(Ten a,Ten b){
		Ten r_ten=new Ten(  (a.getx()+b.getx())/2.0   , (a.gety()+b.gety())/2.0   );

		return r_ten;
	}
// -------------------------------
	public Tyokusen en_to_en_no_kouten_wo_tooru_tyokusen(En e1,En e2){
double x1=e1.getx();
double y1=e1.gety();
double r1=e1.getr();
double x2=e2.getx();
double y2=e2.gety();
double r2=e2.getr();

double a=2.0*x1-2.0*x2;
double b=2.0*y1-2.0*y2;
double c=x2*x2-x1*x1+y2*y2-y1*y1+r1*r1-r2*r2;

		Tyokusen r_t=new Tyokusen(a,b,c);

		return r_t;
	}

// -------------------------------
	public Senbun en_to_en_no_kouten_wo_musubu_senbun(En e1,En e2){

//System.out.println(" 20170301  e1="+e1.getx() +",    "+ e1.gety() +",    "+e1.getr());
//System.out.println(" 20170301  e2="+e2.getx() +",    "+ e2.gety() +",    "+e2.getr());

Tyokusen t0 = new Tyokusen();t0.set(en_to_en_no_kouten_wo_tooru_tyokusen(e1,e2));
Tyokusen t1 = new Tyokusen(e1.get_tyuusin(),e2.get_tyuusin());
Ten kouten_t0t1=new Ten();kouten_t0t1.set( kouten_motome(t0,t1));
double nagasa_a=t0.kyorikeisan(e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ

//double nagasa_a=kyori(kouten_t0t1,e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ
double nagasa_b=Math.sqrt(e1.getr()*e1.getr()- nagasa_a*nagasa_a  ); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

//Senbun r_s=new Senbun();

Senbun r_s=new Senbun(
kouten_t0t1.getx()+t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.gety()-t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.getx()-t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.gety()+t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() )
);
/*
double ax,ay,bx,by;
//System.out.println(" 20170301 nagasa_b "+nagasa_b);
//System.out.println(" 20170301  "+nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta()));


//ax=kouten_t0t1.getx()+t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
ax=kouten_t0t1.getx()+t0.getb()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
ay=kouten_t0t1.gety()-t0.geta()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
bx=kouten_t0t1.getx()-t0.getb()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
by=kouten_t0t1.gety()+t0.geta()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
Ten ta=new Ten(ax+1.0,ay);
Ten tb=new Ten(bx,by+2.0);
r_s.set(ax,ay,bx,by);
*/

		return r_s;
	}


// --------qqqqqqqqqqqqqqq-----------------------
	public Senbun en_to_tyokusen_no_kouten_wo_musubu_senbun(En e1,Tyokusen t0){

//System.out.println(" 20170301  e1="+e1.getx() +",    "+ e1.gety() +",    "+e1.getr());
//System.out.println(" 20170301  e2="+e2.getx() +",    "+ e2.gety() +",    "+e2.getr());

//En e2 = new En(sentaisyou_ten_motome(t0,e1.get_tyuusin()),e1.getr(),3);
//Tyokusen t1 = new Tyokusen(e1.get_tyuusin(),e2.get_tyuusin());
Ten kouten_t0t1=new Ten();kouten_t0t1.set( kage_motome(t0,e1.get_tyuusin()));
double nagasa_a=t0.kyorikeisan(e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ

//double nagasa_a=kyori(kouten_t0t1,e1.get_tyuusin());  //t0とt1の交点からe1の中心までの長さ
double nagasa_b=Math.sqrt(e1.getr()*e1.getr()- nagasa_a*nagasa_a  ); //t0とt1の交点からe1とe2の交点までの長さ
//t0と平行な方向ベクトルは(t0.getb() , -t0.geta())
//t0と平行な方向ベクトルで長さがnagasa_bのものは(t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ) , -t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ))

//Senbun r_s=new Senbun();

Senbun r_s=new Senbun(
kouten_t0t1.getx()+t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.gety()-t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.getx()-t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() ),
kouten_t0t1.gety()+t0.geta()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() )
);
/*
double ax,ay,bx,by;
//System.out.println(" 20170301 nagasa_b "+nagasa_b);
//System.out.println(" 20170301  "+nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta()));


//ax=kouten_t0t1.getx()+t0.getb()*nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
ax=kouten_t0t1.getx()+t0.getb()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
ay=kouten_t0t1.gety()-t0.geta()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
bx=kouten_t0t1.getx()-t0.getb()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
by=kouten_t0t1.gety()+t0.geta()*  0.2;//  nagasa_b/Math.sqrt(t0.getb()*t0.getb()+ t0.geta()*t0.geta() );
Ten ta=new Ten(ax+1.0,ay);
Ten tb=new Ten(bx,by+2.0);
r_s.set(ax,ay,bx,by);
*/

		return r_s;
	}

	//点p0と、円e0の円周との間の距離を求める関数----------------------------------------------------
	public double kyori_ensyuu(Ten p0,En e0){
		
		 
		return Math.abs(kyori(p0,e0.get_tyuusin())-e0.getr());
	}

	//Minを返す関数
	public double min(double d1,double d2,double d3,double d4){
		double min_d=d1;
		if(min_d>d2){min_d=d2;}
		if(min_d>d3){min_d=d3;}	
		if(min_d>d4){min_d=d4;}
		return min_d;
	}


	public Senbun nitoubunsen(Ten t1,Ten t2,double d0){

//s_step[i].geta(),s_step[j].geta(),200.0
		//double bx1=   r* (s0.getbx()-s0.getax())  +  s0.getax();
		//double by1=   r* (s0.getby()-s0.getay())  +  s0.getay();

		//double ax1=s0.getax();
		//double ay1=s0.getay();	     


	Ten tm =new Ten((t1.getx()+t2.getx())/2.0,(t1.gety()+t2.gety())/2.0);

	double bai=d0/kyori(t1,t2);

	Senbun s1 =new Senbun(); s1.set(Senbun_kaiten(new Senbun(tm,t1),90.0,bai));
	Senbun s2 =new Senbun(); s2.set(Senbun_kaiten(new Senbun(tm,t2),90.0,bai));

		Senbun s_return =new Senbun( s1.getb(), s2.getb());
//Senbun s_return =new Senbun(t1, t2);
		return s_return;
	}



//--------------------------------------------------------
	public int Senbun_kasanari_hantei(Senbun s1,Senbun s2){//0は重ならない。1は重なる。20201012追加

						int i_senbun_kousa_hantei=senbun_kousa_hantei(s1,s2,0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 31  ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 321 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 322 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 331 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 332 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 341 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 342 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 351 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 352 ){ i_jikkou=1;}

						if(i_senbun_kousa_hantei== 361 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 362 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 363 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 364 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 371 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 372 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 373 ){ i_jikkou=1;}
						if(i_senbun_kousa_hantei== 374 ){ i_jikkou=1;}
return i_jikkou;
}
//--------------------------------------------------------
	public int Senbun_X_kousa_hantei(Senbun s1,Senbun s2){//0はX交差しない。1は交差する。20201017追加

						int i_senbun_kousa_hantei=senbun_kousa_hantei(s1,s2,0.0001,0.0001);
						int i_jikkou=0;
						if(i_senbun_kousa_hantei== 1  ){ i_jikkou=1;}

return i_jikkou;
}


//--------------------------------------------------------



















}
