package de.hsos.richwps.mb.graphView;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import java.awt.Point;

/**
 * Provides functions for creating graph nodes out of entities.
 *
 * @author dziegenh
 */
public class GraphNodeCreator {

    /**
     * Creates a graph node (cell) representing a process entity.
     *
     * @param graph
     * @param process
     * @param location
     * @return
     */
    public static mxCell createNodeFromProcess(Graph graph, ProcessEntity process, Point location) {

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        int numInputs = process.getNumInputs();
        int numOutputs = process.getNumOutputs();

        mxCell processCell = null;

        try {
            double INPUT_PORT_WIDTH = GraphSetup.PROCESS_WIDTH / (double) numInputs;
            double OUTPUT_PORT_WIDTH = GraphSetup.PROCESS_WIDTH / (double) numOutputs;

            // TODO calculate process dimensions depending on num ports, length of name  etc.
            int x = location.x;
            int y = location.y;
            int w = GraphSetup.PROCESS_WIDTH;
            int h = GraphSetup.PROCESS_HEIGHT;

            String style = GraphSetup.STYLENAME_PROCESS;

            processCell = (mxCell) graph.insertVertex(parent, null, process, x, y, w, h, style);
            processCell.setConnectable(false);

            int i = 0;
            int curX = 0;
            for (ProcessPort pIn : process.getInputPorts()) {
                int curPortWidth = (int) ((i % 2 == 0) ? Math.floor(INPUT_PORT_WIDTH) : Math.ceil(INPUT_PORT_WIDTH));

                mxCell portCell = createLocalPortCell(pIn, curX, 0, curPortWidth);
                graph.addCell(portCell, processCell);

                curX += curPortWidth;
                i++;
            }

            i = 0;
            curX = 0;
            for (ProcessPort pOut : process.getOutputPorts()) {
                int curPortWidth = (int) ((i % 2 == 0) ? Math.floor(OUTPUT_PORT_WIDTH) : Math.ceil(OUTPUT_PORT_WIDTH));

                mxCell portCell = createLocalPortCell(pOut, curX, -GraphSetup.PROCESS_PORT_HEIGHT, curPortWidth);
                graph.addCell(portCell, processCell);

                curX += curPortWidth;
                i++;
            }

        } finally {
            graph.getModel().endUpdate();
        }

        return processCell;
    }

    /**
     * Creates a cell representing a local process input or output.
     *
     * @param port
     * @param x
     * @param y
     * @param width
     * @return
     */
    private static mxCell createLocalPortCell(ProcessPort port, double x, double y, double width) {
        double geomY = port.isFlowOutput() ? 1. : 0.;
        mxGeometry geo = new mxGeometry(0, geomY, width, GraphSetup.PROCESS_PORT_HEIGHT);
        geo.setRelative(true);
        geo.setOffset(new mxPoint(x, y));

        String stylename = (port.isFlowInput()) ? GraphSetup.STYLENAME_LOCAL_INPUT : GraphSetup.STYLENAME_LOCAL_OUTPUT;

        mxCell portCell = new mxCell(port, geo, stylename);
        portCell.setVertex(true);

        return portCell;
    }

    /**
     * Creates a graph node (cell) representing a global input or output port.
     *
     * @param graph
     * @param port
     * @param location
     * @return
     */
    public static mxCell createNodeFromPort(Graph graph, ProcessPort port, Point location) {

        // This method handles global ports only
        if (!port.isGlobal()) {
            throw new IllegalArgumentException(AppConstants.GRAPH_EXCEPTION_PORT_NOT_GLOBAL);
        }

        port = port.clone();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        mxCell portCell = null;

        try {
            int x = location.x;
            int y = location.y;
            int w = GraphSetup.GLOBAL_PORT_WIDTH;
            int h = GraphSetup.GLOBAL_PORT_HEIGHT;
            String style = port.isGlobalInput() ? GraphSetup.STYLENAME_GLOBAL_INPUT : GraphSetup.STYLENAME_GLOBAL_OUTPUT;
            portCell = (mxCell) graph.insertVertex(parent, null, port, x, y, w, h, style);

        } finally {
            graph.getModel().endUpdate();
        }

        return portCell;
    }

}
