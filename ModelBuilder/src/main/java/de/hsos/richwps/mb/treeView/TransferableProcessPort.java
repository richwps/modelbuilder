/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author dziegenh
 */
public class TransferableProcessPort implements Transferable {

    public static DataFlavor processPortFlavor = new DataFlavor(ProcessPort.class, "Process Entity");
    private ProcessPort processPort;

    public TransferableProcessPort(ProcessPort processPort) {
        this.processPort = processPort;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{processPortFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(processPortFlavor);
    }

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
