/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author dziegenh
 */
public class TreenodeTransferHandler extends TransferHandler {

    public TreenodeTransferHandler() {
        super();
    }

    @Override
    public boolean canImport(TransferSupport support) {

        if (support.getComponent() instanceof JTree) {
            return false;
        }
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            TreePath[] selectionPaths = tree.getSelectionPaths();

            if (null == selectionPaths) {
                return null;
            }

            // add all selected treeNodes to the transfer data array
            List<Object> data = new LinkedList<Object>();
            for (TreePath selectionPath : selectionPaths) {
                Object pathComponent = selectionPath.getLastPathComponent();
                if (pathComponent instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) pathComponent).getUserObject();
                    data.add(userObject);
                }
            }

            return new TransferableTreeNodes(data.toArray());
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c
    ) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        return info.isDrop();
    }

}
