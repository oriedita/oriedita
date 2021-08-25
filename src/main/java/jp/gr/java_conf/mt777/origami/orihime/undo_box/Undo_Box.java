package jp.gr.java_conf.mt777.origami.orihime.undo_box;

import jp.gr.java_conf.mt777.kiroku.memo.Memo;

import java.util.LinkedList;

public class Undo_Box {
    int undoTotal = 20;//Number of times you can undo up to how many times ago

    int recordTotal = -1;//The number of times you have recorded. If you have recorded up to 5 times ago, recordTotal = 5 and this does not include the latest recorded memo [0]
    int recordPosition = 0;//If 0, the latest recording position, if n, the previous recording position n

    LinkedList<Memo> Mem = new LinkedList<>(); //Create a LinkedList object

    public Undo_Box() {
        Mem.clear();
        for (int i = 0; i <= undoTotal; i++) {
            Mem.add(new Memo());
        }
    }

    public void record(Memo m0) {
        // (1) First, shift the current recording position so that memo (recordPosition) is memo (0) and memo (recordPosition + 1) is memo (10) ,,,.
        for (int i = 1; i <= recordPosition; i++) {
            Mem.remove(0);
            Mem.add(new Memo());
        }

        recordTotal = recordTotal - recordPosition;
        recordPosition = 0;

        //(2) Addition of new record
        Mem.remove(undoTotal);
        Mem.add(0, m0);

        recordTotal = recordTotal + 1;
        if (recordTotal > undoTotal) {
            recordTotal = undoTotal;
        }
    }

    public Memo undo() {
        recordPosition = recordPosition + 1;
        if (recordPosition > recordTotal) {
            recordPosition = recordTotal;
        }

        return Mem.get(recordPosition);
    }

    public Memo redo() {
        recordPosition = recordPosition - 1;
        if (recordPosition < 0) {
            recordPosition = 0;
        }

        return Mem.get(recordPosition);
    }

    public void set_i_undo_total(int i_new) {
        if (undoTotal <= i_new) {
            for (int i = undoTotal + 1; i <= i_new; i++) {
                Mem.add(new Memo());
            }
            undoTotal = i_new;
        } else {  //When reducing the number of records
            //First, delete the Memo from Chogo to undoTotal.
            for (int i = recordTotal + 1; i <= undoTotal; i++) {
                Mem.removeLast();
            }

            //Next, delete the Memo from the beginning to just before the recording position
            for (int i = 0; i <= recordPosition - 1; i++) {
                Mem.removeFirst();
            }

            //Update recordPosition, recordTotal and undoTotal according to the above operation
            recordTotal = recordTotal - recordPosition;
            recordPosition = 0;
            undoTotal = recordTotal;

            if (recordTotal <= i_new) {
                for (int i = undoTotal + 1; i <= i_new; i++) {
                    Mem.add(new Memo());
                }
                undoTotal = i_new;
            } else {  //When reducing the number of records
                //Delete the latter half of Memo so that the number of records is i_new
                for (int i = i_new + 1; i <= recordTotal; i++) {
                    Mem.removeLast();
                }
                recordTotal = i_new;
                undoTotal = i_new;
            }
        }
    }
}
