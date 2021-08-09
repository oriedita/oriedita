package jp.gr.java_conf.mt777.origami.orihime;

import jp.gr.java_conf.mt777.origami.dougu.bou.*;
import jp.gr.java_conf.mt777.kiroku.memo.*;
import jp.gr.java_conf.mt777.zukei2d.ten.*;

import java.util.*;

public class File_keisiki_henkan {

	//public File_keisiki_henkan(){   } //コンストラクタ

        //---------------
	Memo obj2orihime(Memo mem){
		System.out.println("objファイルをオリヒメ用にする");
                Memo MemR=new Memo();
                int ibangou=0;
		int jtok;
		String st = "";
		Double Dd = 0.0;
		Integer Ii    = 0;
		
		ArrayList<Point> tL = new ArrayList<>();
				
		tL.add(new Point());
		
                Point tn=new Point();
		int Tenmax=0;

		ArrayList<Stick> bL = new ArrayList<>();
                bL.add(new Stick());

		Stick bu=new Stick();
		int Boumax=0;
		
		ArrayList<Integer> itempL = new ArrayList<>();
		itempL.add(0);
		
		int ia,ib,ic,id;
		double d1,d2,d3,d4;

		double xmax=-10000.0;
		double xmin=10000.0;
		double ymax=-10000.0;
		double ymin=10000.0;
		
		double ax,ay,bx,by;
		String str = "";

		int isen=0;
		for(int ig = 1; ig<=mem.getLineSize(); ig++){
                      //  System.out.print("v,f,#eの読みこみ..."); System.out.println(ig);
			if( mem.getLine(ig).length()!=0){
				StringTokenizer tk = new StringTokenizer(mem.getLine(ig)," ");
				jtok=    tk.countTokens();

				str=tk.nextToken();
				
				if (str.equals("v")){
                                       d1= Double.parseDouble(tk.nextToken());
				       d2= Double.parseDouble(tk.nextToken());
				       d3= Double.parseDouble(tk.nextToken());
				       
				       if(d1>xmax){xmax=d1;}
				       if(d1<xmin){xmin=d1;}
				       if(d2>ymax){ymax=d2;}
				       if(d2<ymin){ymin=d2;}
				       
					Tenmax=Tenmax+1;
				       tL.add( new Point(d1,d2));
				}
				if (str.equals("f")){
					itempL.clear();itempL.add(0);
					for(int i=2;i<=jtok;i++){
						int ite= Integer.parseInt(tk.nextToken());
						itempL.add(ite);
					}  
					itempL.set(0, itempL.get(jtok-1));
					for(int i=1;i<=jtok-1;i++){
						int iflg=0;
						Integer I_itempL= itempL.get(i);
						Integer Im1_itempL= itempL.get(i-1);
						for(int j=1;j<=Boumax;j++){
							bu= bL.get(j);
							if((bu.getBegin()== Im1_itempL)&&(bu.getEnd()== I_itempL)){iflg=iflg+1;}
							if((bu.getBegin()== I_itempL)&&(bu.getEnd()== Im1_itempL)){iflg=iflg+1;}
						}
						if(iflg==0){
							Boumax=Boumax+1;
							bL.add(new Stick(Im1_itempL, I_itempL,0));
						}
					}  
				}
                               
				if (str.equals("#e")){
					ia= Integer.parseInt(tk.nextToken());
					ib= Integer.parseInt(tk.nextToken());
					ic= Integer.parseInt(tk.nextToken());
					id= Integer.parseInt(tk.nextToken());
					for(int i=1;i<=Boumax;i++){
						bu= bL.get(i);
						if((bu.getBegin()==ia)&&(bu.getEnd()==ib)){bu.setColor(ic);}
						if((bu.getBegin()==ib)&&(bu.getEnd()==ia)){bu.setColor(ic);}
					}
				}
			} 
		//	str=tk.nextToken();	
		//	System.out.print("jtok=" );System.out.println(jtok);
		//	System.out.println(mem.getGyou(i));
		}				

		//  writeMemo2File(MemR) ;
		
		MemR.reset();
		MemR.addLine("<線分集合>");
                for(int i=1;i<=Boumax;i++){
			MemR.addLine("番号,"+ i);
			//System.out.println("番号,"+str.valueOf(i));
                        bu= bL.get(i);
			
			int icol; icol= bu.getColor()-1;
			bu.setColor(icol);
			if(bu.getColor()==1){icol=2;}
			if(bu.getColor()==2){icol=1;}

			if(icol!=0){bu.setColor(icol);}

                        MemR.addLine("色,"  + icol);
			
			tn= tL.get(bu.getBegin());
			d1=tn.getX();
			d2=tn.getY();
			
			tn= tL.get(bu.getEnd());
			d3=tn.getX();
			d4=tn.getY();
			
			//d1=d1-xmin+150.0;
			//d2=d2-ymin+150.0;
			//d3=d3-xmin+150.0;
			//d4=d4-ymin+150.0;
			
			MemR.addLine("座標,"+ d1 +","+ d2 +","+ d3 +","+ d4);

			//System.out.println("座標,"+str.valueOf(d1)+","+str.valueOf(d2)+","+str.valueOf(d3)+","+str.valueOf(d4)     );			
			//MemR.addGyou("座標,"+str.valueOf(t[b[i].getmae()].getx())+","
			//+str.valueOf(t[b[i].getmae()].gety())+","
			//+str.valueOf(t[b[i].getato()].getx())+","
			//+str.valueOf(t[b[i].getato()].gety())     );
			//
		}
		return MemR;
	}

	//---------------
	Memo cp2orihime(Memo mem){System.out.println("cpファイルをオリヒメ用にする");
		System.out.println("cpファイルをオリヒメ用にする");
                Memo MemR=new Memo();

                int ibangou=0;
		Double Dd = 0.0;
		 double d1,d2,d3,d4;
		String str;
		int icol;

                //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

		MemR.reset();
		MemR.addLine("<線分集合>");

                //  int isen=0;
		for(int ig = 1; ig<=mem.getLineSize(); ig++){
                   //     System.out.print("cpファイルの行順番..."); System.out.println(ig);
			if( mem.getLine(ig).length()!=0){
				StringTokenizer tk = new StringTokenizer(mem.getLine(ig)," ");
				str=tk.nextToken();
                    //    System.out.print("..."+str+"..."); System.out.println(ig);				
				if (str.equals("1")){ icol=0;   ibangou=ibangou+1;
                                       d1= Double.parseDouble(tk.nextToken());
				       d2= Double.parseDouble(tk.nextToken());
				       d3= Double.parseDouble(tk.nextToken());
                                       d4= Double.parseDouble(tk.nextToken());
				      // if(d1>xmax){xmax=d1;}
				      // if(d1<xmin){xmin=d1;}
				      // if(d2>ymax){ymax=d2;}
				      // if(d2<ymin){ymin=d2;}
					MemR.addLine("番号,"+ ibangou);
					System.out.println("番号,"+ ibangou);
					MemR.addLine("色,"  + icol);
					//System.out.println("色,"  +str.valueOf(icol) );

					//d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

					MemR.addLine("座標,"+ d1 +","+ d2 +","+ d3 +","+ d4);
					//System.out.println("座標,"+str.valueOf(d1)+","+str.valueOf(d2)+","+str.valueOf(d3)+","+str.valueOf(d4)     );	
				}
				if (str.equals("2")){ icol=1;   ibangou=ibangou+1;
                                       d1= Double.parseDouble(tk.nextToken());
				       d2= Double.parseDouble(tk.nextToken());
				       d3= Double.parseDouble(tk.nextToken());
                                       d4= Double.parseDouble(tk.nextToken());
					MemR.addLine("番号,"+ ibangou);
					MemR.addLine("色,"  + icol);

					//d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

					MemR.addLine("座標,"+ d1 +","+ d2 +","+ d3 +","+ d4);
				}
				if (str.equals("3")){ icol=2;   ibangou=ibangou+1;
                                       d1= Double.parseDouble(tk.nextToken());
				       d2= Double.parseDouble(tk.nextToken());
				       d3= Double.parseDouble(tk.nextToken());
                                       d4= Double.parseDouble(tk.nextToken());
					MemR.addLine("番号,"+ ibangou);
					MemR.addLine("色,"  + icol);

					//d1=d1+350.0;d2=d2+350.0;d3=d3+350.0;d4=d4+350.0;//ORIPAからオリヒメへ移すときの座標調整

					MemR.addLine("座標,"+ d1 +","+ d2 +","+ d3 +","+ d4);
				} 
			} 
		}
				
		return MemR;  
	}
	//-----------------------------------------------------------------------------------------------------

	Memo orihime2cp(Memo mem){
		System.out.println("オリヒメ用ファイルをcp用にする");
                Memo MemR=new Memo();
                int ibangou=0;
		Integer Ii    = 0;
		Double Dd = 0.0;
		 double d1,d2,d3,d4;
		String str;
		int icol=0;

                //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

		MemR.reset();
		//MemR.addGyou("<線分集合>");    

                //  int isen=0;
		for(int ig = 1; ig<=mem.getLineSize(); ig++){
                   //     System.out.print("cpファイルの行順番..."); System.out.println(ig);
			if( mem.getLine(ig).length()!=0){
				StringTokenizer tk = new StringTokenizer(mem.getLine(ig),",");
				str=tk.nextToken();
                    //    System.out.print("..."+str+"..."); System.out.println(ig);				
				if (str.equals("番号")){ibangou=ibangou+1;}  
				if (str.equals("色")){icol= Integer.parseInt(tk.nextToken())+1;}
				if (str.equals("座標")){ 
                                       d1= Double.parseDouble(tk.nextToken());
				       d2= Double.parseDouble(tk.nextToken());
				       d3= Double.parseDouble(tk.nextToken());
                                       d4= Double.parseDouble(tk.nextToken());
				      // if(d1>xmax){xmax=d1;}
				      // if(d1<xmin){xmin=d1;}
				      // if(d2>ymax){ymax=d2;}
				      // if(d2<ymin){ymin=d2;}
				     //MemR.addGyou("番号,"+str.valueOf(ibangou));
					//System.out.println("番号,"+str.valueOf(ibangou));
					
					//System.out.println("色,"  +str.valueOf(icol) );

					//d1=d1-350.0;d2=d2-350.0;d3=d3-350.0;d4=d4-350.0;//オリヒメからORIPAへ移すときの座標調整
					MemR.addLine(icol +" "+ d1 +" "+ d2 +" "+ d3 +" "+ d4);
					//MemR.addGyou("座標,"+str.valueOf(d1)+","+str.valueOf(d2)+","+str.valueOf(d3)+","+str.valueOf(d4)     );			
					//System.out.println("座標,"+str.valueOf(d1)+","+str.valueOf(d2)+","+str.valueOf(d3)+","+str.valueOf(d4)     );	
				}
			} 
		}				
		return MemR;
	}

	//-----------------------------------------------------------------------------------------------------

	Memo orihime2svg(Memo mem){//これはes1.getMemo_for_kakidasi()を入力して展開図の生データのsvgを出力
		System.out.println("オリヒメ用ファイルをsvg用にする");
                Memo MemR=new Memo();
                int ibangou=0;
		Integer Ii    = 0;
		Double Dd = 0.0;
		 double d1,d2,d3,d4;
		String str = "";
		String str_stroke = "";
		String str_strokewidth = "";
		str_strokewidth = "1";
		int icol=0;

                //オリヒメ　0.Contour, 1.Mountain, 2.Valley　、ORIPA 1.Contour, 2.Mountain, 3.Valley

		MemR.reset();

		MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

		//MemR.addGyou("<線分集合>");    

                //  int isen=0;
		for(int ig = 1; ig<=mem.getLineSize(); ig++){
                 //     System.out.print("cpファイルの行順番..."); System.out.println(ig);
			if( mem.getLine(ig).length()!=0){
			 	StringTokenizer tk = new StringTokenizer(mem.getLine(ig),",");
				str=tk.nextToken();
                    //    System.out.print("..."+str+"..."); System.out.println(ig);				
				if (str.equals("番号")){ibangou=ibangou+1;}  
				if (str.equals("色")){icol= Integer.parseInt(tk.nextToken());}
			 		str_stroke="black";
					if(icol==0){str_stroke="black";
					}else if(icol==1){str_stroke="red";
					}else if(icol==2){str_stroke="blue";
					}


				if (str.equals("座標")){ 
					d1= Double.parseDouble(tk.nextToken());
					d2= Double.parseDouble(tk.nextToken());
					d3= Double.parseDouble(tk.nextToken());
					d4= Double.parseDouble(tk.nextToken());




					MemR.addLine(    "<line x1=\"" + d1 + "\"" +
							      " y1=\"" + d2 + "\"" +
							      " x2=\"" + d3 + "\"" +
							      " y2=\"" + d4 + "\"" +
							  " stroke=\"" + str_stroke	 + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);
						//	<line x1="0" y1="10" x2="200" y2="10" stroke="black" y="10" stroke-width="1" />
				}
			} 
		
		}				

		MemR.addLine("</svg>");
		return MemR;
	}

/*
<svg xmlns="http://www.w3.org/2000/svg"
     xmlns:xlink="http://www.w3.org/1999/xlink">
  <circle cx="100" cy="100" r="100" fill="red" />
  <rect x="130" y="130" width="300" height="200" fill="blue" />
</svg>


<line x1="0" y1="10" x2="200" y2="10"
      stroke="black" y="10" stroke-width="1" />
<line x1="0" y1="30" x2="200" y2="30"
      stroke="black" y="10" stroke-width="2" />
<line x1="0" y1="50" x2="200" y2="50"
      stroke="black" y="10" stroke-width="4" />
<line x1="0" y1="70" x2="200" y2="70"
      stroke="black" y="10" stroke-width="6" />





*/

	Memo orihime2svg(Memo mem_tenkaizu,Memo mem_oriagarizu){
		System.out.println("svg画像出力");
                Memo MemR=new Memo();
/*
                int ibangou=0;
		Integer Ii    = new Integer(0);
		Double Dd = new Double(0.0);
		 double d1,d2,d3,d4;
		String str =new String(); 
		String str_stroke =new String(); 
		String str_strokewidth =new String(); 
		str_strokewidth = "1";
		int icol=0;
*/
		MemR.reset();

		MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

		MemR.addMemo(mem_tenkaizu);
		MemR.addMemo(mem_oriagarizu);


/*
		//MemR.addGyou("<線分集合>");    

                //  int isen=0;
		for(int ig=1;ig<=mem.getGyousuu();ig++){
                 //     System.out.print("cpファイルの行順番..."); System.out.println(ig);
			if( mem.getGyou(ig).length()!=0){
			 	StringTokenizer tk = new StringTokenizer(mem.getGyou(ig),",");
				str=tk.nextToken();
                    //    System.out.print("..."+str+"..."); System.out.println(ig);				
				if (str.equals("番号")){ibangou=ibangou+1;}  
				if (str.equals("色")){icol=Ii.parseInt(tk.nextToken());} 
			 		str_stroke="black";
					if(icol==0){str_stroke="black";
					}else if(icol==1){str_stroke="red";
					}else if(icol==2){str_stroke="blue";
					}


				if (str.equals("座標")){ 
					d1=Dd.parseDouble(tk.nextToken());
					d2=Dd.parseDouble(tk.nextToken());
					d3=Dd.parseDouble(tk.nextToken());
					d4=Dd.parseDouble(tk.nextToken());				       




					MemR.addGyou(    "<line x1=\"" + str.valueOf(d1) + "\"" +
							      " y1=\"" + str.valueOf(d2) + "\"" + 
							      " x2=\"" + str.valueOf(d3) + "\"" +
							      " y2=\"" + str.valueOf(d4) + "\"" +
							  " stroke=\"" + str_stroke	 + "\"" +
						    " stroke-width=\"" + str_strokewidth + "\"" +" />"
													);
						//	<line x1="0" y1="10" x2="200" y2="10" stroke="black" y="10" stroke-width="1" />
				}
			} 
		
		}				
*/
		MemR.addLine("</svg>");
		return MemR;
	}



	//----------------

}
