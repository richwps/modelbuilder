package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.AppTreeFactory;
import de.hsos.richwps.mb.app.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.TreeView;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Abstract Controller for a TreeView.
 *
 * @author dziegenh
 */
public abstract class AbstractTreeViewController {

    private TreeView treeView;

    private GraphDropTargetAdapter dropTargetAdapter;

    private final GraphView graphView;
    private final ProcessProvider processProvider;
    private final Component graphDndProxy;
    private final TransferHandler processTransferHandler;
    private final JFrame parent;

    public AbstractTreeViewController(SemanticProxyInteractionComponents components) {
        this.graphView = components.graphView;
        this.processProvider = components.processProvider;
        this.graphDndProxy = components.graphDndProxy;
        this.processTransferHandler = components.processTransferHandler;
        this.parent = components.parent;
    }

    /**
     * Creates the tree content.
     */
    abstract void fillTree();

    protected ProcessProvider getProcessProvider() {
        return this.processProvider;
    }

    protected GraphView getGraphView() {
        return this.graphView;
    }

    protected Component getGraphDndProxy() {
        return this.graphDndProxy;
    }

    protected TransferHandler getProcessTransferHandler() {
        return this.processTransferHandler;
    }

    public TreeView getTreeView() {
        if (null == treeView) {
            this.treeView = AppTreeFactory.createTree(getGraphView(), getProcessProvider());
            ToolTipManager.sharedInstance().registerComponent(this.treeView.getGui());
        }

        return this.treeView;
    }

    protected JFrame getParent() {
        return parent;
    }

    /**
     * Initialises drag-and-drop mechanism for TreeView nodes.
     */
    public void initDnd() {
        JTree tree = getTreeView().getGui();

        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                try {
                    if (null != dropTargetAdapter) {
                        return;
                    }

                    // cancel if the event was NOT triggered by the left mouse button.
                    Iterator<InputEvent> dgeIterator = dge.iterator();
                    while (dgeIterator.hasNext()) {
                        InputEvent next = dgeIterator.next();
                        if (next instanceof MouseEvent
                                && !SwingUtilities.isLeftMouseButton((MouseEvent) next)) {
                            return;
                        }
                    }

                    getGraphDndProxy().setVisible(true);
                    dropTargetAdapter = new GraphDropTargetAdapter(getParent(), getProcessProvider(), getGraphView(), getGraphDndProxy());
                    dropTargetAdapter.getDropTarget().removeDropTargetListener(dropTargetAdapter);
                    dropTargetAdapter.getDropTarget().addDropTargetListener(dropTargetAdapter);

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
