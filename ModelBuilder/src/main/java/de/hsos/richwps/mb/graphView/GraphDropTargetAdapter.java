/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.treeView.TransferableProcessEntity;
import de.hsos.richwps.mb.treeView.TransferableProcessPort;
import de.hsos.richwps.mb.treeView.TransferableTreeNodes;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class GraphDropTargetAdapter extends DropTargetAdapter {

    private GraphView graphView;
    private DropTarget dropTarget;
    private IProcessProvider processProvider;

    private List<mxCell> createdNodes;

    public GraphDropTargetAdapter(IProcessProvider processProvider, GraphView graphView, Component c) {
        this.graphView = graphView;
        this.processProvider = processProvider;
        dropTarget = new DropTarget(c, DnDConstants.ACTION_COPY, this, true, null);
    }

    public DropTarget getDropTarget() {
        return dropTarget;
    }

    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();

        createdNodes = new LinkedList<mxCell>();

        try {
            Object[] transferObjects = (Object[]) transferable.getTransferData(TransferableTreeNodes.objectArrayFlavor);
            createNodesFromTransferObject(transferObjects, dtde.getLocation());

        } catch (Exception ex) {
            // TODO ignore or log ?
        }

        try {
            Object transferProcess = transferable.getTransferData(TransferableProcessEntity.processEntityFlavor);
            createNodesFromTransferObject(transferProcess, dtde.getLocation());

        } catch (Exception ex) {
            // TODO ignore or log ?
        }

        try {
            Object transferPort = transferable.getTransferData(TransferableProcessPort.processPortFlavor);
            createNodesFromTransferObject(transferPort, dtde.getLocation());

        } catch (Exception ex) {
            // TODO ignore or log ?
        }

        graphView.setCellsSelected(createdNodes.toArray());
    }

    protected boolean createNodesFromTransferObject(Object o, Point location) {

        mxCell node = null;

        if (o instanceof ProcessEntity) {
            ProcessEntity processEntity = (ProcessEntity) o;
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
                    location.y += 100; // TODO get magic number from config/graph
                }
            }
            return true;
        }

        // can't handle given object
        if(null != node) {
            createdNodes.add(node);
            return true;
        }

        return false;
    }

}
