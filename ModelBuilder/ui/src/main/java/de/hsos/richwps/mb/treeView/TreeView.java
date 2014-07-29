/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author dziegenh
 */
public class TreeView {

    private JTree tree;
//    private IProcessProvider processProvider;

    public TreeView(TreeNode root) {
//        this.processProvider = processProvider;
    
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
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

    public DefaultMutableTreeNode getSelectedNode() {
        Object path = tree.getSelectionPath().getLastPathComponent();
        if(path instanceof DefaultMutableTreeNode)
            return (DefaultMutableTreeNode) path;

        return null;
    }

    public boolean isEmpty() {
        TreeModel model = getGui().getModel();
        return model.getChildCount(model.getRoot()) <= 0;
    }

}
