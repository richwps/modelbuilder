/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.treeView.TreeView;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

class TreeViewMouseAdapter extends MouseAdapter {

    private GraphView graphView;
    private TreeView treeView;

    TreeViewMouseAdapter(GraphView graphView, TreeView treeView) {
        this.graphView = graphView;
        this.treeView = treeView;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (2 == e.getClickCount()) {
            DefaultMutableTreeNode node = treeView.getSelectedNode();
            if (null != node) {
                Object nodeObject = node.getUserObject();

                Point location = graphView.getEmptyCellLocation(new Point(0, 0));

                if (nodeObject instanceof ProcessEntity) {
                    graphView.createNodeFromProcess((IProcessEntity) nodeObject, location);
                } else if (nodeObject instanceof ProcessPort) {
                    graphView.createNodeFromPort((ProcessPort) nodeObject, location);
                }
            }
        }
    }
}

/**
 *
 * @author dziegenh
 */
class AppTreeFactory {

    static TreeView createTree(GraphView graphView) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(AppConstants.TREE_ROOT_NAME);

        // Java 1.7 Bugfix: add dummy node - otherwise the tree doesn't work?!
        root.add(new DefaultMutableTreeNode(""));

        TreeView treeView = new TreeView(root);
        treeView.getGui().addMouseListener(new TreeViewMouseAdapter(graphView, treeView));
        treeView.getGui().setBorder(new EmptyBorder(2, 2, 2, 2));
        DefaultTreeCellRenderer cellRenderer = new DefaultTreeCellRenderer();
        cellRenderer.setBackgroundSelectionColor(AppConstants.SELECTION_BG_COLOR);
        cellRenderer.setLeafIcon(UIManager.getIcon(AppConstants.ICON_PROCESS_KEY));
        treeView.getGui().setCellRenderer(cellRenderer);

        return treeView;
    }

}
