package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Handler for transfered tree nodes (drag and drop).
 *
 * @author dziegenh
 */
public class TreenodeTransferHandler extends TransferHandler {

    private ProcessProvider processProvider;

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
            List<Object> data = new LinkedList<>();
            for (TreePath selectionPath : selectionPaths) {
                Object pathComponent = selectionPath.getLastPathComponent();
                if (pathComponent instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) pathComponent).getUserObject();

                    if (null != userObject && userObject instanceof ProcessEntity) {
                        ProcessEntity process = (ProcessEntity) userObject;
                        // load missing data if possible
                        process = processProvider.getFullyLoadedProcessEntity(process.getServer(), process.getOwsIdentifier());
                        userObject = process;
                    }

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

    public void setProcessProvider(ProcessProvider processProvider) {
        this.processProvider = processProvider;
    }

}
