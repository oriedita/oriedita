package origami_editor.editor.transfer;

import origami_editor.editor.Save;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class SaveTransferable implements Transferable {
    public static DataFlavor saveFlavor;

    static {
        try {
            saveFlavor = new DataFlavor("data/ori;class=origami_editor.editor.Save");
        } catch (ClassNotFoundException cle) {
            System.err.println("error initializing origami_editor.editor.transfer.SaveTransferable");
        }
    }

    private final Save save;

    public SaveTransferable(Save save) {
        this.save = save;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{saveFlavor,};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor dataFlavor : getTransferDataFlavors()) {
            if (dataFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(saveFlavor)) {
            if (Save.class.equals(flavor.getRepresentationClass())) {
                return save;
            }
        }

        throw new UnsupportedFlavorException(flavor);
    }
}
