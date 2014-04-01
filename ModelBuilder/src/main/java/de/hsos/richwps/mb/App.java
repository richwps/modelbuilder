package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appview.AppFrame;
import de.hsos.richwps.mb.graphview.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphview.GraphView;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.treeview.ProcessTransferHandler;
import de.hsos.richwps.mb.treeview.TreeView;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class App {

    private AppFrame frame;
    private ProcessProvider processProvider;

    private ProcessTransferHandler processTransferHandler;

    public static void main(String[] args) {
        App app = new App();
    }

    private TreeView treeView;
    private GraphView graphView;

    public App() {
        frame = new AppFrame(this);
        initDragAndDrop();
    }

    public IProcessProvider getProcessProvider() {
        if (null == processProvider) {
            processProvider = new ProcessProvider();
        }
        return processProvider;
    }

    protected ProcessTransferHandler getProcessTransferHandler() {
        if (null == processTransferHandler) {
            processTransferHandler = new ProcessTransferHandler();
        }

        return processTransferHandler;
    }

    private void initDragAndDrop() {
        GraphDropTargetAdapter dropTargetAdapter = new GraphDropTargetAdapter(getProcessProvider(), getGraphView(), getGraphViewGui());
        dropTargetAdapter.getDropTarget().setActive(false);

        // activate graph droptarget when user starts dragging a treeView node
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(getTreeView().getGui(), DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent dge) {
                getGraphViewGui().getDropTarget().setActive(true);
            }
        });
        // deactivate graph drop target when dragging ends
        DragSource.getDefaultDragSource().addDragSourceListener(new DragSourceListener() {
            public void dragEnter(DragSourceDragEvent dsde) {
            }

            public void dragOver(DragSourceDragEvent dsde) {
            }

            public void dropActionChanged(DragSourceDragEvent dsde) {
            }

            public void dragExit(DragSourceEvent dse) {
            }

            public void dragDropEnd(DragSourceDropEvent dsde) {
                getGraphViewGui().getDropTarget().setActive(false);
            }
        });

//        getGraphView().setTransferHandler(getProcessTransferHandler());
        getTreeViewGui().setTransferHandler(getProcessTransferHandler());
    }

    /**
     * The server/process tree (north-west of the frame).
     *
     * @return
     */
    protected TreeView getTreeView() {
        if (null == treeView) {
            // TODO mock
            IProcessProvider processProvider = getProcessProvider();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(AppConstants.TREE_ROOT_NAME);
            DefaultMutableTreeNode processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);

            for (String server : processProvider.getAllServer()) {
                DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
                for (ProcessEntity process : processProvider.getServerProcesses(server)) {
                    serverNode.add(new DefaultMutableTreeNode(process));
                }
                processesNode.add(serverNode);
            }

            root.add(processesNode);
            root.add(new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME));
            root.add(new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME));

            treeView = new TreeView(getProcessProvider(), root);
            treeView.expandAll();
        }

        return treeView;
    }

    /**
     * The tree GUI component
     *
     * @return
     */
    public JTree getTreeViewGui() {
        return getTreeView().getGui();
    }

    /**
     * The graph.
     *
     * @return
     */
    protected GraphView getGraphView() {
        if (null == graphView) {
            graphView = new GraphView();
        }
        return graphView;
    }

    /**
     * The graph GUI component.
     *
     * @return
     */
    public Component getGraphViewGui() {
        return getGraphView().getGui();
    }

}
