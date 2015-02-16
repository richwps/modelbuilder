package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphNodeCreator;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class ProcessReplacer {

    public void replaceAutoMappedProcess(GraphView graphView, mxCell oldCell, ProcessEntity newProcess) {
        final ProcessEntity oldProcess = (ProcessEntity) oldCell.getValue();
        MapPortsPanel mapPortsPanel = new MapPortsPanel(oldProcess, newProcess);
        this.replaceProcess(graphView, mapPortsPanel, oldCell, newProcess);
    }

    public void replaceProcess(GraphView graphView, MapPortsPanel mapPortsPanel, mxCell oldCell, ProcessEntity newProcess) {
        Graph graph = graphView.getGraph();
        

        // insert new process cell
        Point cellLocation = oldCell.getGeometry().getPoint();
        mxCell targetCell = GraphNodeCreator.createNodeFromProcess(graph, newProcess, cellLocation);

        // restore outgoing connections
        Object[] outgoingEdges = graphView.getGraph().getOutgoingEdges(oldCell);
        for (Object edge : outgoingEdges) {
            GraphEdge outEdge = (GraphEdge) edge;

            Object outEdgeSourcePort = outEdge.getSourcePortCell().getValue();
            List<MapPortPanel> portPanelsForSourcePort = mapPortsPanel.getPortPanelsForSourcePort((ProcessPort) outEdgeSourcePort);

            for (MapPortPanel mapPortPanel : portPanelsForSourcePort) {
                mxCell targetPortCell = graphView.getCellByValue(targetCell, mapPortPanel.getTarget());

                // connect cells
                graph.insertEdge(targetCell, null, null, targetPortCell, outEdge.getTargetPortCell());
            }
        }

        // restore incoming connections
        Object[] incomingEdges = graphView.getGraph().getIncomingEdges(oldCell);
        for (Object edge : incomingEdges) {
            GraphEdge inEdge = (GraphEdge) edge;

            Object outEdgeTargetPort = inEdge.getTargetPortCell().getValue();
            List<MapPortPanel> portPanelsForSourcePort = mapPortsPanel.getPortPanelsForSourcePort((ProcessPort) outEdgeTargetPort);

            for (MapPortPanel mapPortPanel : portPanelsForSourcePort) {
                mxCell targetPortCell = graphView.getCellByValue(targetCell, mapPortPanel.getTarget());

                // connect cells
                graph.insertEdge(targetCell, null, null, targetPortCell, inEdge.getSourcePortCell());
            }
        }

        // remove old process cell
        graph.removeCells(new mxCell[]{oldCell});
    }

}
