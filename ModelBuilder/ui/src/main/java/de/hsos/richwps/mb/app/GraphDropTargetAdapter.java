package de.hsos.richwps.mb.app;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphSetup;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.TransferableProcessEntity;
import de.hsos.richwps.mb.treeView.TransferableProcessPort;
import de.hsos.richwps.mb.treeView.TransferableTreeNodes;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;

/**
 * Adapter for performing drop actions depending on the transferred object(s).
 *
 * @author dziegenh
 */
public class GraphDropTargetAdapter extends DropTargetAdapter {

    private DropTarget dropTarget;

    private List<mxCell> createdNodes;
    private final ProcessProvider processProvider;
    private final JFrame parent;
    private final GraphView graphView;

    public GraphDropTargetAdapter(JFrame parent, ProcessProvider processProvider, GraphView graphView, Component graphDndProxy) {
        this.parent = parent;
        this.processProvider = processProvider;
        this.graphView = graphView;
        
        dropTarget = new DropTarget(graphDndProxy, DnDConstants.ACTION_COPY, this, true, null);
    }

    public DropTarget getDropTarget() {
        return dropTarget;
    }

    /**
     * Performs an action depending on the transfered Object. Uses DataFlavors
     * to get the transfer object.
     *
     * @param dtde
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();

        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        createdNodes = new LinkedList<mxCell>();

        try {
            Object[] transferObjects = (Object[]) transferable.getTransferData(TransferableTreeNodes.objectArrayFlavor);
            createNodesFromTransferObject(transferObjects, dtde.getLocation());

        } catch (Exception ex) {
            // ignore
        }

        try {
            Object transferProcess = transferable.getTransferData(TransferableProcessEntity.processEntityFlavor);
            createNodesFromTransferObject(transferProcess, dtde.getLocation());

        } catch (Exception ex) {
            // ignore
        }

        try {
            Object transferPort = transferable.getTransferData(TransferableProcessPort.processPortFlavor);
            createNodesFromTransferObject(transferPort, dtde.getLocation());

        } catch (Exception ex) {
            // ignore
        }

        parent.setCursor(Cursor.getDefaultCursor());
        graphView.setCellsSelected(createdNodes.toArray());
    }

    /**
     * Creates a graph node (cell) depending on the given transfer object. Useds
     * recursively if the object is an array.
     *
     * @param o
     * @param location
     * @return
     */
    protected boolean createNodesFromTransferObject(Object o, Point location) {

        location.x = (int) Math.floor(location.x / AppConstants.GRAPH_GRID_SIZE) * AppConstants.GRAPH_GRID_SIZE;
        location.y = (int) Math.floor(location.y / AppConstants.GRAPH_GRID_SIZE) * AppConstants.GRAPH_GRID_SIZE;

        location = graphView.getEmptyCellLocation(location);

        mxCell node = null;

        if (o instanceof ProcessEntity) {
            ProcessEntity processEntity = (ProcessEntity) o;

            // try to update ProcessEntity using SemanticProxy
            String server = processEntity.getServer();
            String identifier = processEntity.getOwsIdentifier();
            ProcessEntity spProcess = processProvider.getFullyLoadedProcessEntity(server, identifier);
            if (null != spProcess) {
                processEntity = spProcess;
            }

            node = graphView.createNodeFromProcess(processEntity, location);
        }

        if (o instanceof ProcessPort) {
            ProcessPort processPort = (ProcessPort) o;
            node = graphView.createNodeFromPort(processPort, location);
        }

        if (o instanceof Object[]) {
            Object[] objects = (Object[]) o;
            for (Object object : objects) {
                if (createNodesFromTransferObject(object, location)) {
                    // if node creation was successful, set a different location for next node
                    location.y += GraphSetup.CELLS_VERTICAL_OFFSET;
                }
            }
            return true;
        }

        if (null != node) {
            createdNodes.add(node);
            return true;
        }

        return false;
    }

}
