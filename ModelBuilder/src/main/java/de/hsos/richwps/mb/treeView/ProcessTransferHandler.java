/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author dziegenh
 */
public class ProcessTransferHandler extends TransferHandler {

    public ProcessTransferHandler() {
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
            TreePath selectionPath = tree.getSelectionPath();
            if (null == selectionPath) {
                return null;
            }
            Object pathComponent = selectionPath.getLastPathComponent();
            if (pathComponent instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) pathComponent).getUserObject();

                if (userObject instanceof ProcessEntity) {
                    return new TransferableProcessEntity((ProcessEntity) userObject);
                }

                else if (userObject instanceof ProcessPort) {
                    return new TransferableProcessPort((ProcessPort) userObject);
                }
            }
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
        if (!info.isDrop()) {
            return false;
        }

        return true;
    }

}
