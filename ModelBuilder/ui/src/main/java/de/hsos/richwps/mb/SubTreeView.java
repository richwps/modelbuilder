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
public class SubTreeView extends AbstractTreeView {

    public SubTreeView(App app) {
        super(app);
    }

    @Override
    void fillTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getTreeView().getGui().getModel().getRoot();

        if(root.getChildCount() > 0) {
            root.removeAllChildren();
        }

        for (IProcessEntity process : app.getGraphView().getUsedProcesses()) {
            root.add(new DefaultMutableTreeNode(process));
        }

        getTreeView().getGui().updateUI();
        getTreeView().expandAll();
    }
}
