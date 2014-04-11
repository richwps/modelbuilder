/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.treeView.TransferableProcessEntity;
import de.hsos.richwps.mb.treeView.TransferableProcessPort;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;

/**
 *
 * @author dziegenh
 */
public class GraphDropTargetAdapter extends DropTargetAdapter {

    private GraphView graphView;
    private DropTarget dropTarget;
    private IProcessProvider processProvider;

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
        try {
            Object transferProcess = transferable.getTransferData(TransferableProcessEntity.processEntityFlavor);
            ProcessEntity processEntity = (ProcessEntity) transferProcess;
            graphView.createNodeFromProcess(processEntity);
        } catch (Exception ex) {
            // TODO ignore or log ?
        }

        try {
            Object transferPort = transferable.getTransferData(TransferableProcessPort.processPortFlavor);
            ProcessPort processPort = (ProcessPort) transferPort;
            graphView.createNodeFromPort(processPort);
        } catch (Exception ex) {
            // TODO ignore or log ?
        }

    }

}
