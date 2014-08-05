/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dziegenh
 */
public class SubTreeViewController extends AbstractTreeViewController {

    public SubTreeViewController(App app) {
        super(app);
    }

    @Override
    void fillTree() {
        DefaultMutableTreeNode root = getRoot();

        if (root.getChildCount() > 0) {
            root.removeAllChildren();
        }

        for (IProcessEntity process : app.getGraphView().getUsedProcesses()) {
            root.add(new DefaultMutableTreeNode(process));
        }

        updateUI();
    }

    void addNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode root = getRoot();

        if (null == root) {
            return;
        }

        // cancel if node already exists.
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getUserObject().equals(node.getUserObject())) {
                return;
            }
        }
        
        root.add(node);
        updateUI();
    }
}
