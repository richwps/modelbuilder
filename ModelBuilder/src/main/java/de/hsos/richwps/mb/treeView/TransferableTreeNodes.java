/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author dziegenh
 */
public class TransferableTreeNodes implements Transferable {

    public static DataFlavor objectArrayFlavor = new DataFlavor(Object.class, "Object");
//    public static DataFlavor processEntityFlavor = new DataFlavor(ProcessEntity.class, "Process Entity");
//    public static DataFlavor processPortFlavor = new DataFlavor(ProcessPort.class, "Process Port");

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

//    public IProcessEntity getProcessEntity() {
//        return objectArrayFlavor;
//    }

}
