package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.entity.ProcessPort;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transfer oject for drag-and-drop operations of process ports.
 *
 * @author dziegenh
 */
public class TransferableProcessPort implements Transferable {

    public static DataFlavor processPortFlavor = new DataFlavor(ProcessPort.class, "Process Port");
    private ProcessPort processPort;

    public TransferableProcessPort(ProcessPort processPort) {
        this.processPort = processPort;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{processPortFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(processPortFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!flavor.equals(processPortFlavor)) {
            throw new UnsupportedFlavorException(flavor);
        }

        return processPort;
    }

    public ProcessPort getProcessPort() {
        return processPort;
    }

}
