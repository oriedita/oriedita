package jp.gr.java_conf.mt777.origami.orihime.undo_box;

import java.util.LinkedList;

import jp.gr.java_conf.mt777.kiroku.memo.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Undo_Box {
    int i_undo_total = 20;//Number of times you can undo up to how many times ago

    int i_record_total = -1;//The number of times you have recorded. If you have recorded up to 5 times ago, i_record_total = 5 and this does not include the latest recorded memo [0]
    int i_record_position = 0;//If 0, the latest recording position, if n, the previous recording position n

    LinkedList<Memo> Mem = new LinkedList<>(); //Create a LinkedList object

    //---------------------------------
    public Undo_Box() {  //コンストラクタ
        Mem.clear();
        for (int i = 0; i <= i_undo_total; i++) {
            Mem.add(new Memo());
        }
    }

    //---------------------------------
    public void record(Memo m0) {
        // (1) First, shift the current recording position so that memo (i_record_position) is memo (0) and memo (i_record_position + 1) is memo (10) ,,,.
        for (int i = 1; i <= i_record_position; i++) {
            Mem.remove(0);
            Mem.add(new Memo());
        }

        i_record_total = i_record_total - i_record_position;
        i_record_position = 0;


        //(2) Addition of new record
        Mem.remove(i_undo_total);
        Mem.add(0, m0);

        i_record_total = i_record_total + 1;
        if (i_record_total > i_undo_total) {
            i_record_total = i_undo_total;
        }
    }

    //-----------------------------
    public Memo undo() {
        i_record_position = i_record_position + 1;
        if (i_record_position > i_record_total) {
            i_record_position = i_record_total;
        }
        return Mem.get(i_record_position);
    }

    //-----------------------------
    public Memo redo() {
        i_record_position = i_record_position - 1;
        if (i_record_position < 0) {
            i_record_position = 0;
        }
        return Mem.get(i_record_position);
    }

    //-----------------------------
    public void set_i_undo_total(int i_new) {
        if (i_undo_total <= i_new) {
            for (int i = i_undo_total + 1; i <= i_new; i++) {
                Mem.add(new Memo());
            }
            i_undo_total = i_new;
        } else if (i_undo_total > i_new) {  //記録数を少なくする場合
            //最初に記録数の長後からi_undo_suuまでのMemoを消す
            for (int i = i_record_total + 1; i <= i_undo_total; i++) {
                Mem.removeLast();
            }

            //次に先頭から記録位置直前までのMemoを消す
            for (int i = 0; i <= i_record_position - 1; i++) {
                Mem.removeFirst();
            }

            //上記の操作に応じてi_kiroku_itiとi_kiroku_suuとi_undo_suuとを更新
            i_record_total = i_record_total - i_record_position;
            i_record_position = 0;
            i_undo_total = i_record_total;

            if (i_record_total <= i_new) {
                for (int i = i_undo_total + 1; i <= i_new; i++) {
                    Mem.add(new Memo());
                }
                i_undo_total = i_new;
            } else if (i_record_total > i_new) {  //記録数を少なくする場合
                //記録数がi_newになるように後半のMemoを消す
                for (int i = i_new + 1; i <= i_record_total; i++) {
                    Mem.removeLast();
                }
                i_record_total = i_new;
                i_undo_total = i_new;

            }

        }

        int i_undo_suu = 20;//最大何回前までundoできるかという数

        int i_kiroku_suu = -1;//何回前まで記録しているかという数。5回前まで記録しているならi_kiroku_suu=5でこれには最新の記録分のmemo[0]は含まない
        int i_kiroku_iti = 0;//0なら、最新の記録位置、ｎならｎ前の記録位置

    }
}
