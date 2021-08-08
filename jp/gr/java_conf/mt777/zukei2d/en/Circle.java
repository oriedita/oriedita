package jp.gr.java_conf.mt777.zukei2d.en;

import java.awt.*;

import  jp.gr.java_conf.mt777.zukei2d.senbun.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.tyokusen.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;


//import java.util.*;

public class Circle {//点の座標や方向ベクトルなどをあらわすときに用いる

	double x,y,r;//中心の座標と半径

	int icol;//色の指定　0=black,1=blue,2=red.
	int tpp=0;//特注プロパティパラメーター
	//Color tpp_color =new Color(100, 200, 200);//特注ある場合の色
	Color tpp_color =new Color(100, 200, 200);//特注ある場合の色

	public Circle(){x=0.0;y=0.0;r=0.0;icol=0;}//コンストラクタ
	public Circle(double i, double j, double k, int m){x=i;y=j;r=k;icol=m;} //コンストラクタ
	//public En(double a,Ten p,double b,Ten q){x=a*p.getx()+b*q.getx();y=a*p.gety()+b*q.gety();} //コンストラクタ
	public Circle(Point tc, double k, int m){x=tc.getx();y=tc.gety();r=k;icol=m;} //コンストラクタ
	public Circle(Line s0, int m){//コンストラクタ 線分を直径とする円
		x=(s0.getax()+s0.getbx())/2.0;
		y=(s0.getay()+s0.getby())/2.0;
		r=s0.getnagasa()/2.0;
		icol=m;
	} 

	
  
	public void set(Circle e){ x=e.getx();y=e.gety();r=e.getr();icol=e.getcolor();tpp=e.get_tpp();tpp_color=e.get_tpp_color();}
	public void set(double i,double j,double k,int m){ x=i;y=j;r=k;icol=m;}
	public void set(Point tc, double k, int m){x=tc.getx();y=tc.gety();r=k;icol=m;}
	public void set(double i,double j,double k){ x=i;y=j;r=k;}
	public void set(Line s0, int m){
		x=(s0.getax()+s0.getbx())/2.0;
		y=(s0.getay()+s0.getby())/2.0;
		r=s0.getnagasa()/2.0;
		icol=m;
	} 

	public void setx(double xx){x=xx;}
	public void sety(double yy){y=yy;}
	public void setr(double rr){r=rr;}

	public double getx(){return x;}
	public double gety(){return y;}  
	public double getr(){return r;}  

	public void reset(){ x=0.0;y=0.0;r=0.0;icol=0;}

	public void setcolor(int i){icol=i;}
	public int getcolor(){return icol;}


	public void set_tpp(int i){tpp=i;}
	public int get_tpp(){return tpp;}


	public void set_tpp_color(Color c0){tpp_color=c0;}
	//public void set_tpp_color(int iR,int iG,int iB){tpp_color=new Color(iR,iG,iB);}
	public Color get_tpp_color(){return tpp_color;}
	//public Color get_tpp_color(){return new Color(tpp_color.getRed(),tpp_color.getGreen(),tpp_color.getBlue());}

// int getix(){return (int)x;}
// int getiy(){return (int)y;}

	public void heikou_idou(double x1,double y1) { x=x+x1; y=y+y1; }

	public Point get_tyuusin(){
		Point rten =new Point(getx(),gety());
		return rten;
	}

/*
	//他の点との距離（double）を求める関数----------------------------------------------------
	public double kyori(Ten p){
		//double x1=p.getx(),y1=p.gety();
		//return Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));

		double x1=p.getx()-x,y1=p.gety()-y;
		return Math.sqrt(x1*x1+y1*y1);

	}



	//他の点との距離の2乗（double）を求める関数----------------------------------------------------
	public double kyori2jyou(Ten p){
		double x1=p.getx()-x,y1=p.gety()-y;
		return x1*x1+y1*y1;
	}



        //自Tenを基準としてみたとき、他の点の位置をTenで返す。
	public Ten tano_Ten_iti(Ten taten){
		Ten rten =new Ten();
		rten.setx(taten.getx()-x);
		rten.sety(taten.gety()-y);
		return rten;
	}

	public void idou(Ten addten){
		x=x+addten.getx();
		y=y+addten.gety();
	}
*/

	//他の点を反転する関数----------------------------------------------------
	public Point hanten(Point t0){//t0と(x,y)が同じ位置のときはエラーとなる。
		double	x1=t0.getx()-x,y1=t0.gety()-y;
		double d1=Math.sqrt(x1*x1+y1*y1);
		double d2,x2,y2,x3,y3;


		if(Math.abs(d1-r)<0.0000001){return t0;}
		d2=r*r/d1;
		x2=d2*x1/d1;
		y2=d2*y1/d1;
		x3=x2+x;
		y3=y2+y;
		return new Point(x3,y3);
	}

	//他の円を円に反転する関数----------------------------------------------------
	public Circle hanten(Circle e0){//e0の円周が(x,y)を通らないとき用　//e0の円周が(x,y)を通るときはエラーとなる。またe0の円周の内部に(x,y)がくるときもおかしな結果になるっぽい。
		double	x1=e0.getx()-x,y1=e0.gety()-y;
		double d1=Math.sqrt(x1*x1+y1*y1);
		double da1=d1-e0.getr();
		double db1=d1+e0.getr();

		double xa1,ya1;
		double xa0,ya0;
		double xb1,yb1;
		double xb0,yb0;

		if(d1<0.000001){
			xa1=da1;	ya1=0.0;
			xa0=xa1+x;	ya0=ya1+y;
			xb1=db1;	yb1=0.0;
			xb0=xb1+x;	yb0=yb1+y;
		}else {
			xa1=da1*x1/d1;	ya1=da1*y1/d1;
			xa0=xa1+x;	ya0=ya1+y;
			xb1=db1*x1/d1;	yb1=db1*y1/d1;
			xb0=xb1+x;	yb0=yb1+y;
		}
		
		int ic=5;//if(e0.getcolor()==5){ic=3;}

		return new Circle(new Line(   hanten(new Point(xa0,ya0)),hanten(new Point(xb0,yb0)) )   ,ic);
	}

	//(x,y)を通る他の円を線分に反転する関数----------------------------------------------------
	public Line hanten_En2Senbun(Circle e0){//e0の円周が(x,y)を通るとき用　//e0の円周が(x,y)を通らないときはおかしな結果になる。
		double	x1=e0.getx()-x,y1=e0.gety()-y;
		Point th=new Point();th.set(hanten(new Point(x1*2.0+x,y1*2.0+y)));
		Point t1=new Point();t1.set(th.getx()-x,th.gety()-y);
		Point tha=new Point();tha.set(th.getx()+3.0*y1,th.gety()-3.0*x1);
		Point thb=new Point();thb.set(th.getx()-3.0*y1,th.gety()+3.0*x1);
		return new Line( tha,thb,3 ) ;
	}


	//(x,y)を通らない線分を他の円に反転する関数----------------------------------------------------
	public Circle hanten_Senbun2En(Line s0){//s0が(x,y)を通るときはおかしな結果になる。
		StraightLine ty= new StraightLine(s0);
		Point t0=new Point(); t0.set(ty.kage_motome(get_tyuusin()));
		return new Circle(new Line(   hanten(t0),get_tyuusin() )   ,5);
	}







}
