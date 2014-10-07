package de.hsos.richwps.mb.app;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Controller for the "used elements" tree.
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

        for (mxCell cell : app.getGraphView().getProcessCells()) {
            if (cell.getValue() instanceof ProcessEntity) {
                addNode(cell.getValue());
            }
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
//            if (child.getUserObject().equals(userObject)) {
            if (userObjectsEqual(child.getUserObject(), userObject)) {
                node = child;
                // cancel loop
                i = root.getChildCount();
            }
        }

        return node;
    }

    private boolean userObjectsEqual(Object o1, Object o2) {
        if (null == o1 || null == o2) {
            return o1 == o2;
        }

        if (o1 instanceof ProcessEntity && o2 instanceof ProcessEntity) {
            ProcessEntity pe1 = (ProcessEntity) o1;
            return pe1.equals(o2);
        }

        return o1.equals(o2);
    }

}
