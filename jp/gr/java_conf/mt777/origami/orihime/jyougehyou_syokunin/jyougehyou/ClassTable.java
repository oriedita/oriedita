package jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou;


import  jp.gr.java_conf.mt777.origami.orihime.jyougehyou_syokunin.jyougehyou.touka_jyouken.*;

import java.util.*;

public class ClassTable {//This class is used to record and utilize the hierarchical relationship of faces when folded.
  int facesTotal;             //Number of faces in the unfolded view before folding

	//  jg[][]は折る前の展開図のすべての面同士の上下関係を1つの表にまとめたものとして扱う
	//　jg[i][j]が1なら面iは面jの上側。0なら下側。
	//  jg[i][j]が-50なら、面iとjは重なが、上下関係は決められていない。
        //jg[i][j]が-100なら、面iとjは重なるところがない。
	int jg[][] ;
	int jg_h[][];	
	int Touka_jyoukensuu;   //２つの隣接する面の境界線を他の面が突き抜ける状況が生じうる組み合わせ。
        Touka_jyouken tj = new Touka_jyouken();
	
	ArrayList tL = new ArrayList();
	
        int uTouka_jyoukensuu;

	ArrayList uL = new ArrayList();
	Touka_jyouken uj = new Touka_jyouken();//２つの隣接する面a,bの境界線と２つの隣接する面c,dの境界線が、平行かつ一部重なっていて、
						//さらにa,b,c,dがあるSmenで共存する場合の、境界線で突き抜けが生じうる組み合わせ


	public ClassTable(){//コンストラクタ
		reset();
	}

        //
	public void reset(){
		tL.clear(); tL.add(new Touka_jyouken());
		uL.clear(); uL.add(new Touka_jyouken());		
		Touka_jyoukensuu=0;uTouka_jyoukensuu=0;	
	}

        //
	public void jg_hozon(){for(int i = 1; i<= facesTotal; i++){for(int j = 1; j<= facesTotal; j++){jg_h[i][j]=jg[i][j];}}}

	//
	public void jg_fukugen(){for(int i = 1; i<= facesTotal; i++){for(int j = 1; j<= facesTotal; j++){jg[i][j]=jg_h[i][j];}}}

	//
        public void set(int i,int j,int jyoutai){
		jg[i][j]=jyoutai;
	//	System.out.print(i);System.out.print(":上下表:");System.out.println(j);
	}

	public int get(int i,int j){return jg[i][j];}

	public void setFacesTotal(int iM){
		facesTotal =iM;

		int j_g[][] = new int [facesTotal +1][facesTotal +1];
		int j_g_h[][] = new int [facesTotal +1][facesTotal +1];

	  jg=j_g;
	  jg_h=j_g_h;
	
		
		for(int i = 0; i<= facesTotal; i++){
			for(int j = 0; j<= facesTotal; j++){
				jg[i][j]=-100;jg_h[i][j]=-100;
			}
		}
	}


        public int getFacesTotal(){return facesTotal;}


	public int getTouka_jyoukensuu(){return Touka_jyoukensuu;}

	public Touka_jyouken getTouka_jyouken(int i){return (Touka_jyouken)tL.get(i);}
	
	
	//等価条件の追加。棒ibの境界として隣接する2つの面im1,im2が有る場合、折り畳み推定した場合に
	//棒ibの一部と重なる位置に有る面imは面im1と面im2に上下方向で挟まれることはない。このことから
	//gj[im1][im]=gj[im2][im]という等価条件が成り立つ。        
        public void addTouka_jyouken(int ai,int bi,int ci,int di){
		Touka_jyoukensuu=Touka_jyoukensuu+1;
		tL.add(new Touka_jyouken(ai,bi,ci,di));
	 }
	 
	public int get_uTouka_jyoukensuu(){return uTouka_jyoukensuu;}

	public Touka_jyouken get_uTouka_jyouken(int i){return (Touka_jyouken)uL.get(i);}
	
	
	//等価条件の追加。棒ibの境界として隣接する2つの面im1,im2が有り、
	//また棒jbの境界として隣接する2つの面im3,im4が有り、ibとjbが平行で、一部重なる場合、折り畳み推定した場合に
	//棒ibの面と面jbの面がi,j,i,j　または　j,i,j,i　と並ぶことはない。もしこれがおきたら、
	//最初から3番目で間違いが起きているので、この3番目のところがSmenで何桁目かを求めて、この桁を１進める。        
        
	public void add_uTouka_jyouken(int ai,int bi,int ci,int di){
		uTouka_jyoukensuu=uTouka_jyoukensuu+1;

	        uL.add(new Touka_jyouken(ai,bi,ci,di));
	}


}
