package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.entity.ProcessEntity;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transfer oject for drag-and-drop operations of processes.
 *
 * @author dziegenh
 */
public class TransferableProcessEntity implements Transferable {

    public static DataFlavor processEntityFlavor = new DataFlavor(ProcessEntity.class, "Process Entity");
    private ProcessEntity processEntity;

    public TransferableProcessEntity(ProcessEntity processEntity) {
        this.processEntity = processEntity;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{processEntityFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(processEntityFlavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!flavor.equals(processEntityFlavor)) {
            throw new UnsupportedFlavorException(flavor);
        }

        return processEntity;
    }

    public ProcessEntity getProcessEntity() {
        return processEntity;
    }

}
