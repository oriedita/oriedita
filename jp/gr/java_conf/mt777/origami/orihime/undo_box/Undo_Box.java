package jp.gr.java_conf.mt777.origami.orihime.undo_box;

import java.util.LinkedList;

import jp.gr.java_conf.mt777.kiroku.memo.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Undo_Box {
    int i_undo_suu = 20;//最大何回前までundoできるかという数

    int i_kiroku_suu = -1;//何回前まで記録しているかという数。5回前まで記録しているならi_kiroku_suu=5でこれには最新の記録分のmemo[0]は含まない
    int i_kiroku_iti = 0;//0なら、最新の記録位置、ｎならｎ前の記録位置

    LinkedList<Memo> Mem = new LinkedList<>(); //LinkedListのオブジェクトを作成
    //Memo memo[] = new Memo[21];//一時記録用に使う。memo[0] は最新の記録、memo[1]は一つ前の記録。

    int i_tokeiban_iti = -1;//記録は順次、 i_undo_suu+1の時刻のある時計盤の各時刻に格納するイメージ。最新の格納場所がi_tokeiban_iti

    //---------------------------------
    public Undo_Box() {  //コンストラクタ
        Mem.clear();
        for (int i = 0; i <= i_undo_suu; i++) {
            Mem.add(new Memo());
        }

        //for(int i=0;i<=20;i++){memo[i]  = new Memo();}
    }

    //---------------------------------
    public void reset() {

    }

    //---------------------------------
    public void kiroku(Memo m0) {
        //（１）最初に現在の記録位置のmemo(i_kiroku_iti)がmemo(0)、memo(i_kiroku_iti　+　1)がmemo(10)、、、になるようにずらす
        for (int i = 1; i <= i_kiroku_iti; i++) {
            Mem.remove(0);
            Mem.add(new Memo());
        }

        //i_tokeiban_iti=i_tokeiban_iti-i_kiroku_iti;
        i_kiroku_suu = i_kiroku_suu - i_kiroku_iti;
        i_kiroku_iti = 0;


        //（２）新しい記録の追加
        Mem.remove(i_undo_suu);
        Mem.add(0, m0);

        //i_tokeiban_iti=i_tokeiban_iti+1; if(i_tokeiban_iti>i_undo_suu){i_tokeiban_iti=0;}
        //memo[i_tokeiban_iti].set(m0);

        i_kiroku_suu = i_kiroku_suu + 1;
        if (i_kiroku_suu > i_undo_suu) {
            i_kiroku_suu = i_undo_suu;
        }
    }

    //---------------------------------
/*
	public void kiroku_old001(Memo m0){
		for(int i=0;i<=i_kiroku_suu-i_kiroku_iti;i++){
			memo[i].set(memo[i+i_kiroku_iti]);
		}

		i_kiroku_suu=i_kiroku_suu-i_kiroku_iti;
		i_kiroku_iti=0;

		for(int i=i_undo_suu;i>=1;i--){
			memo[i].set(memo[i-1]);
		}
		memo[0].set(m0);

		i_kiroku_suu=i_kiroku_suu+1;if(i_kiroku_suu>i_undo_suu){i_kiroku_suu=i_undo_suu;}
	}

//---------------------------------
	public void kiroku_old(Memo m0){
		//i_kiroku_iti
		for(int i=0;i<=i_kiroku_suu-i_kiroku_iti;i++){
			Memo ma = new Memo();
			ma.set(memo[i+i_kiroku_iti]);
			memo[i].set(ma);
			//	memo[i].set(memo[i+i_kiroku_iti]);
		}

		i_kiroku_suu=i_kiroku_suu-i_kiroku_iti;
		i_kiroku_iti=0;

		for(int i=10;i<=1;i=i-1){
			Memo ma = new Memo();
			//Memo mb = new Memo();

			ma.set(memo[i-1]);
			memo[i].set(ma);

			//memo[i].set(memo[i-1])	;
		}

		memo[10].set(memo[9]);
		memo[9].set(memo[8]);
		memo[8].set(memo[7]);
		memo[7].set(memo[6]);
		memo[6].set(memo[5]);
		memo[5].set(memo[4]);
		memo[4].set(memo[3]);
		memo[3].set(memo[2]);
		memo[2].set(memo[1]);
		memo[1].set(memo[0]);
		memo[0].set(m0);

		i_kiroku_suu=i_kiroku_suu+1;if(i_kiroku_suu>10){i_kiroku_suu=10;}

		//System.out.println("i_kiroku_suu="+i_kiroku_suu);
		//System.out.println("i_kiroku_iti="+i_kiroku_iti);
		//for(int i=0;i<=10;i++){	System.out.println("memo"+i+":"+memo[i].getGyousuu());}
	}
*/
    //-----------------------------
    public Memo undo() {
        i_kiroku_iti = i_kiroku_iti + 1;
        if (i_kiroku_iti > i_kiroku_suu) {
            i_kiroku_iti = i_kiroku_suu;
        }
        return (Memo) Mem.get(i_kiroku_iti);
        //int i_t=i_tokeiban_iti-i_kiroku_iti;if(i_t<0){i_t=i_t+i_undo_suu+1;}
        //return memo[i_t];
    }

    //-----------------------------
    public Memo redo() {
        i_kiroku_iti = i_kiroku_iti - 1;
        if (i_kiroku_iti < 0) {
            i_kiroku_iti = 0;
        }
        return (Memo) Mem.get(i_kiroku_iti);
        //int i_t=i_tokeiban_iti-i_kiroku_iti;if(i_t<0){i_t=i_t+i_undo_suu+1;}
        //return memo[i_t];
    }

    //-----------------------------
    public void set_i_undo_suu(int i_new) {
        if (i_undo_suu <= i_new) {
            for (int i = i_undo_suu + 1; i <= i_new; i++) {
                Mem.add(new Memo());
            }
            i_undo_suu = i_new;
        } else if (i_undo_suu > i_new) {  //記録数を少なくする場合
            //最初に記録数の長後からi_undo_suuまでのMemoを消す
            for (int i = i_kiroku_suu + 1; i <= i_undo_suu; i++) {
                Mem.removeLast();
            }

            //次に先頭から記録位置直前までのMemoを消す
            for (int i = 0; i <= i_kiroku_iti - 1; i++) {
                Mem.removeFirst();
            }

            //上記の操作に応じてi_kiroku_itiとi_kiroku_suuとi_undo_suuとを更新
            i_kiroku_suu = i_kiroku_suu - i_kiroku_iti;
            i_kiroku_iti = 0;
            i_undo_suu = i_kiroku_suu;

            if (i_kiroku_suu <= i_new) {
                for (int i = i_undo_suu + 1; i <= i_new; i++) {
                    Mem.add(new Memo());
                }
                i_undo_suu = i_new;
            } else if (i_kiroku_suu > i_new) {  //記録数を少なくする場合
                //記録数がi_newになるように後半のMemoを消す
                for (int i = i_new + 1; i <= i_kiroku_suu; i++) {
                    Mem.removeLast();
                }
                i_kiroku_suu = i_new;
                i_undo_suu = i_new;

            }

        }

        int i_undo_suu = 20;//最大何回前までundoできるかという数

        int i_kiroku_suu = -1;//何回前まで記録しているかという数。5回前まで記録しているならi_kiroku_suu=5でこれには最新の記録分のmemo[0]は含まない
        int i_kiroku_iti = 0;//0なら、最新の記録位置、ｎならｎ前の記録位置

    }
    //-----------------------------
    //public Memo getMemo(){
    //	return m1;
    //}
    //--------------------------------------------
    //public void test1(){//デバック等のテスト用
    //	System.out.println("Undo_Box");
    //}
    //--------------------------------------------
}
