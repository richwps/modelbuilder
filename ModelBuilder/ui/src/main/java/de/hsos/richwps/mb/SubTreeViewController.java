/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dziegenh
 */
public class SubTreeViewController extends AbstractTreeViewController {

    public SubTreeViewController(App app) {
        super(app);
    }

    private HashMap<Object, Integer> nodeCount = new HashMap<>();

    @Override
    void fillTree() {
        DefaultMutableTreeNode root = getRoot();
        if (root.getChildCount() > 0) {
            root.removeAllChildren();
        }

        for (ProcessEntity process : app.getGraphView().getUsedProcesses()) {
//            root.add(new DefaultMutableTreeNode(process));
            addNode(process);
        }

        updateUI();
    }

    void addNode(Object userObject) {
        DefaultMutableTreeNode root = getRoot();

        if (null == root) {
            return;
        }

        // check if node already exists.
        DefaultMutableTreeNode node = getNodeByUserObject(userObject);
        if (null == node) {
            node = new DefaultMutableTreeNode(userObject);
        }

        // cancel if node already exists.
        if (incNodeCount(node) > 1) {
            return;
        }

        root.add(node);
        updateUI();
    }

    void removeNode(Object nodeValue) {
        DefaultMutableTreeNode root = getRoot();

        if (null == root) {
            return;
        }

        DefaultMutableTreeNode node = getNodeByUserObject(nodeValue);
        if (null == node) {
            return;
        }

        // cancel if node is still in use.
        if (decNodeCount(node) > 0) {
            return;
        }

        root.remove(node);
        updateUI();
    }

    private Integer getNodeCount(DefaultMutableTreeNode node) {
        Integer count = nodeCount.get(node.getUserObject());

        if (null != count) {
            return count;
        }

        count = new Integer(0);
        nodeCount.put(node.getUserObject(), count);
        return count;
    }

    private int incNodeCount(DefaultMutableTreeNode node) {
        Integer count = getNodeCount(node);
        count = new Integer(count.intValue() + 1);
        nodeCount.put(node.getUserObject(), count);
        return count.intValue();
    }

    private int decNodeCount(DefaultMutableTreeNode node) {
        Integer count = getNodeCount(node);
        count = new Integer(count.intValue() - 1);
        nodeCount.put(node.getUserObject(), count);
        return count.intValue();
    }

    private DefaultMutableTreeNode getNodeByUserObject(Object userObject) {
        DefaultMutableTreeNode root = getRoot();
        DefaultMutableTreeNode node = null;

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getUserObject().equals(userObject)) {
                node = child;
                // cancel loop
                i = root.getChildCount();
            }
        }

        return node;
    }

}
