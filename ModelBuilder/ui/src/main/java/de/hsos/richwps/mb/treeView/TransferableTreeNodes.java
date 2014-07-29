package de.hsos.richwps.mb.treeView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transferable array of Objects.
 * @author dziegenh
 */
public class TransferableTreeNodes implements Transferable {

    public static DataFlavor objectArrayFlavor = new DataFlavor(Object.class, "Object");

    private Object[] payload;

    public TransferableTreeNodes(Object[] nodes) {
        this.payload = nodes;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{objectArrayFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(objectArrayFlavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!flavor.equals(objectArrayFlavor)) {
            throw new UnsupportedFlavorException(flavor);
        }

        return payload;
    }

}
