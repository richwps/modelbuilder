/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.entity.ProcessPort;
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

    public TreeView(TreeNode root) {
        tree = new JTree(root) {
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof ProcessPort) {
                        ProcessPort port = (ProcessPort) userObject;
                        if (port.isGlobal()) {
                            return port.getDatatype().toString() + (port.isGlobalInput() ? " Input" : " Output");
                        }
                    }

                }

                return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus); //To change body of generated methods, choose Tools | Templates.
            }
        };
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    /**
     * Returns the Swing tree component.
     * @return
     */
    public JTree getGui() {
        return tree;
    }

    /**
     * Expands all tree rows.
     */
    public void expandAll() {
        tree.cancelEditing();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Return the selected tree node.
     * @return
     */
    public DefaultMutableTreeNode getSelectedNode() {
        Object path = tree.getSelectionPath().getLastPathComponent();
        if (path instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) path;
        }

        return null;
    }

    /**
     * Returns true if to tree root has no children.
     * @return
     */
    public boolean isEmpty() {
        TreeModel model = getGui().getModel();
        return model.getChildCount(model.getRoot()) <= 0;
    }

}
