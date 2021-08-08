package jp.gr.java_conf.mt777.origami.dougu.bou;

public class Bou {
	int mae;
	int ato;
	int icol;//0なら山谷なし。1なら、山。2なら谷。
 
	//コンストラクタ
	public Bou(){mae=0;ato=0;icol=0;}
	//コンストラクタ
	public Bou(int ma,int at,int ic){mae=ma;ato=at;icol=ic;}

	//入力
	public void set(Bou bu){mae=bu.getmae();ato=bu.getato();icol=bu.getcolor();}
	public void set(int i,int j,int k){mae=i;ato=j;icol=k;}
	public void setmae(int i){mae=i;}	
	public void setato(int i){ato=i;}	
	public void setcolor(int i){icol=i;}
	
	//出力
	public int getmae(){return mae;}
	public int getato(){return ato;}	
	public int getcolor(){return icol;}
	
	public void reset(){mae=0;ato=0;icol=0;}
}
