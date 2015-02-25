package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.MbTreeCellRenderer;
import de.hsos.richwps.mb.treeView.Tree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author dziegenh
 */
public class SelectProcessTree extends Tree {

    SelectProcessTree(ProcessProvider processProvider, TreeNode serverTreeNode) {

        super(processProvider, new DefaultMutableTreeNode());

        // setup cell renderer
        final MbTreeCellRenderer cellRenderer = new MbTreeCellRenderer();
        cellRenderer.setBackgroundSelectionColor(AppConstants.SELECTION_BG_COLOR);
        cellRenderer.setLeafIcon(UIManager.getIcon(AppConstants.ICON_PROCESS_KEY));
        cellRenderer.setPortIconBaseKey(AppConstants.ICON_PORT_BASE_KEY);
        setCellRenderer(cellRenderer);

        setRootVisible(false);

//        // add server nodes
        setModel(new DefaultTreeModel(serverTreeNode));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    void setServerTreeNode(TreeNode serverTreeNode) {
        setModel(new DefaultTreeModel(serverTreeNode));
        updateUI();
    }

}
