package oriedita.editor.datatransfer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oriedita.editor.save.Save;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class SaveTransferable implements Transferable {
    private static final Logger logger = LogManager.getLogger(SaveTransferable.class);

    public static DataFlavor saveFlavor;

    static {
        try {
            saveFlavor = new DataFlavor("data/ori;class=oriedita.editor.save.Save");
        } catch (ClassNotFoundException cle) {
            logger.error("error initializing oriedita.editor.transfer.SaveTransferable", cle);
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
            if (Save.class.isAssignableFrom(flavor.getRepresentationClass())) {
                return save;
            }
        }

        throw new UnsupportedFlavorException(flavor);
    }
}
