package jp.gr.java_conf.mt777.zukei2d.oritaoekaki;

import  jp.gr.java_conf.mt777.zukei2d.senbun.*;
import  jp.gr.java_conf.mt777.zukei2d.oritacalc.*;
import jp.gr.java_conf.mt777.zukei2d.ten.Point;

import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import  jp.gr.java_conf.mt777.zukei2d.*;

public class OritaOekaki { //お絵かき用
	OritaCalc oc =new OritaCalc();

	//太線描画用
	public void widthLine(Graphics g, Point a, Point b, double width, int icolor){
		Line s=new Line(a,b);
		widthLine(g,s,width, icolor);
	}

	public void widthLine(Graphics g, Line s, double r, int icolor){
		if(icolor==0){g.setColor(Color.black);}
		if(icolor==1){g.setColor(Color.red);}
		if(icolor==2){g.setColor(Color.blue);}
		if(icolor==3){g.setColor(Color.green);}
		if(icolor==4){g.setColor(Color.orange);}
		Line sp=new Line(); Line sm=new Line();
	 	sp= oc.mayoko_idou(s,r);
		sm= oc.mayoko_idou(s,-r);

		int x[] = new int[5];
		int y[] = new int[5];

		x[0]=(int)sp.getax(); y[0]=(int)sp.getay();
		x[1]=(int)sp.getbx(); y[1]=(int)sp.getby();
		x[2]=(int)sm.getbx(); y[2]=(int)sm.getby();
		x[3]=(int)sm.getax(); y[3]=(int)sm.getay();

		//  g.setColor(new Color(red,green,blue));
		// g.setColor(Color.yellow);
		g.fillPolygon(x,y,4);  
	}

	//指定されたTenを中心に十字を描く
	public void jyuuji(Graphics g, Point t, double nagasa, double haba, int icolor){

//System.out.println("   O 20170201_1");
		Point tx0=new Point();
        Point tx1=new Point();
		Point ty0=new Point();
        Point ty1=new Point();
		tx0.setx(t.getx()-nagasa); tx0.sety(t.gety()       );
		tx1.setx(t.getx()+nagasa); tx1.sety(t.gety()       );
		ty0.setx(t.getx()       ); ty0.sety(t.gety()-nagasa);
		ty1.setx(t.getx()       ); ty1.sety(t.gety()+nagasa);
//System.out.println("   O 20170201_2");
		widthLine(g,tx0,tx1,haba,icolor);
//System.out.println("   O 20170201_3");
		widthLine(g,ty0,ty1,haba,icolor);
//System.out.println("   O 20170201_4");
	}

	//指定されたTenを中心に指差し図を描く
	public void yubisasi1(Graphics g, Line s_tv, double nagasa, double haba, int icolor){
		Graphics2D g2 = (Graphics2D)g;
	//	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

				g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
g.drawLine((int)s_tv.getax(),(int)s_tv.getay(),(int)s_tv.getbx(),(int)s_tv.getby()); //直線
}

	//指定されたTenを中心に指差し図を描く
	public void yubisasi2(Graphics g, Line s_tv, double nagasa, double haba, int icolor){
		Graphics2D g2 = (Graphics2D)g;
	//	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

				g.setColor(new Color(255, 165, 0, 100));//g.setColor(Color.ORANGE);
g.drawLine((int)s_tv.getax(),(int)s_tv.getay(),(int)s_tv.getbx(),(int)s_tv.getby()); //直線

}

	//指定されたTenを中心に指差し図を描く
	public void yubisasi3(Graphics g, Line s_tv, double nagasa, double haba, int icolor){
		Graphics2D g2 = (Graphics2D)g;
	//	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

		g.setColor(new Color(255, 200, 0, 50));//g.setColor(Color.yellow);
		g.drawLine((int)s_tv.getax(),(int)s_tv.getay(),(int)s_tv.getbx(),(int)s_tv.getby()); //直線
	}


	//指定されたTenを中心に指差し図を描く
	public void yubisasi4(Graphics g, Line s_tv, int icolor_toukado){

//System.out.println("OO.yubisasi4 "+s_tv.getax()+", "+s_tv.getay()+" - "+s_tv.getbx()+", "+s_tv.getby());

		Graphics2D g2 = (Graphics2D)g;
	//	g2.setStroke(new BasicStroke(15.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定

		g.setColor(new Color(255, 0, 147, icolor_toukado));
		//g.setColor(Color.PINK);

		g.drawLine((int)s_tv.getax(),(int)s_tv.getay(),(int)s_tv.getbx(),(int)s_tv.getby()); //直線
	}




}
