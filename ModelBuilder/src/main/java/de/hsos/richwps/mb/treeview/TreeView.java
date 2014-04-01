/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeview;

import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author dziegenh
 */
public class TreeView {

    private JTree tree;
    private IProcessProvider processProvider;

    public TreeView(IProcessProvider processProvider, TreeNode root) {
        this.processProvider = processProvider;
    
        tree = new JTree(root);

        tree.setRootVisible(false);
        tree.setDragEnabled(true);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//        tree.setTransferHandler(new ProcessTransferHandler());
    }

    public JTree getGui() {
        return tree;
    }

    public void expandAll() {
        tree.cancelEditing();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

}
