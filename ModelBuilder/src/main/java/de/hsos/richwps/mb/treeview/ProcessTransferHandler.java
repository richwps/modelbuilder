/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.treeview;

import de.hsos.richwps.mb.treeview.TreeView;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author dziegenh
 */
public class ProcessTransferHandler extends TransferHandler {
    private static DataFlavor processDataFlavor;
    
    
    @Override
    public boolean canImport(TransferSupport support) {
//        return support.isDataFlavorSupported(TreeView.getProcessDataFlavor());#
        return true;
    }

    
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }
        
        System.out.println("imported !");
    
        return true;
    }
    
    
    // TODO mock!!
    public static DataFlavor getProcessDataFlavor() {
        if(null == processDataFlavor)
            processDataFlavor = new DataFlavor("mock/x-mock-mock:class=mock", "RichWPS Process Node");
        return processDataFlavor;
    }
    
    
}
