/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.TreeView;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract Controller for a TreeView.
 * @author dziegenh
 */
public abstract class AbstractTreeViewController {

    protected App app;
    private TreeView treeView;
    private GraphDropTargetAdapter dropTargetAdapter;

    public AbstractTreeViewController(App app) {
        this.app = app;
    }

    /**
     * Creates the tree content.
     */
    abstract void fillTree();

    protected ProcessProvider getProcessProvider() {
        return app.getProcessProvider();
    }

    protected GraphView getGraphView() {
        return app.getGraphView();
    }

    protected Component getGraphDndProxy() {
        return app.getGraphDndProxy();
    }

    protected TransferHandler getProcessTransferHandler() {
        return app.getProcessTransferHandler();
    }

    TreeView getTreeView() {
        if (null == treeView) {
            this.treeView = AppTreeFactory.createTree(app.getGraphView());
        }

        return this.treeView;
    }

    /**
     * Initialises drag-and-drop mechanism for TreeView nodes.
     */
    void initDnd() {
        JTree tree = getTreeView().getGui();

        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent dge) {
                try {
                    if (null != dropTargetAdapter) {
                        return;
                    }
                    dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphDndProxy());
                    dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
                    dropTargetAdapter.getDropTarget().addDropTargetListener(dropTargetAdapter);
                    getGraphDndProxy().setVisible(true);

                } catch (TooManyListenersException ex) {
                    java.util.logging.Logger.getLogger(App.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // deactivate graph drop target when dragging ends
        DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceAdapter() {
            @Override
            public void dragDropEnd(DragSourceDropEvent dsde) {
                if (null != dropTargetAdapter && null != dropTargetAdapter.getDropTarget()) {
                    dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
                }
                getGraphDndProxy().setVisible(false);
                dropTargetAdapter = null;
            }
        });

        tree.setTransferHandler(getProcessTransferHandler());
    }

    public void updateUI() {
        getTreeView().getGui().updateUI();
        getTreeView().expandAll();
    }

    protected DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) getTreeView().getGui().getModel().getRoot();
    }
}
