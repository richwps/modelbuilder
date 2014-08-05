/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.Point;

/**
 *
 * @author dziegenh
 */
public class GraphNodeCreator {

    // TODO move values to config/constants
    private static int PROCESS_WIDTH = 210;
    private static int PROCESS_HEIGHT = 90;
    private static int PROCESS_PORT_HEIGHT = 30;
    private static int GLOBAL_PORT_WIDTH = 45;
    private static int GLOBAL_PORT_HEIGHT = 45;

    public static mxCell createNodeFromProcess(Graph graph, IProcessEntity process, Point location) {
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        int numInputs = process.getNumInputs();
        int numOutputs = process.getNumOutputs();

        mxCell processCell = null;

        try {
            // TODO move to config/constants
            double INPUT_PORT_WIDTH = PROCESS_WIDTH / (double) numInputs;
            double OUTPUT_PORT_WIDTH = PROCESS_WIDTH / (double) numOutputs;

            // TODO calculate process dimensions depending on num ports, length of name  etc.
            processCell = (mxCell) graph.insertVertex(parent, null, process, location.x, location.y, PROCESS_WIDTH, PROCESS_HEIGHT, GraphSetup.STYLENAME_PROCESS); // changed height
            processCell.setConnectable(false);

            int i = 0;
            int curX = 0;
            for (ProcessPort pIn : process.getInputPorts()) {
                int curPortWidth = (int) ((i % 2 == 0) ? Math.floor(INPUT_PORT_WIDTH) : Math.ceil(INPUT_PORT_WIDTH));

                mxGeometry geo1 = new mxGeometry(0, 0, curPortWidth, PROCESS_PORT_HEIGHT); // Changed position offset
                geo1.setRelative(true);
                geo1.setOffset(new mxPoint(curX, 0));

                mxCell port1 = new mxCell(null, geo1, GraphSetup.STYLENAME_LOCAL_PORT);
                port1.setVertex(true);
                port1.setValue(pIn);
                graph.addCell(port1, processCell);

                curX += curPortWidth;
                i++;
            }

            i = 0;
            curX = 0;
            for (ProcessPort pOut : process.getOutputPorts()) {
                int curPortWidth = (int) ((i % 2 == 0) ? Math.floor(OUTPUT_PORT_WIDTH) : Math.ceil(OUTPUT_PORT_WIDTH));
                mxGeometry geo2 = new mxGeometry(0, 1, curPortWidth, PROCESS_PORT_HEIGHT); // Changed position offset
                geo2.setRelative(true);
                geo2.setOffset(new mxPoint(curX, -PROCESS_PORT_HEIGHT)); // Changed position offset

                mxCell port1 = new mxCell(null, geo2, GraphSetup.STYLENAME_LOCAL_PORT);
                port1.setVertex(true);
                port1.setValue(pOut);
                graph.addCell(port1, processCell);

                curX += curPortWidth;
                i++;
            }

        } finally {
            graph.getModel().endUpdate();
        }

        return processCell;
    }

    public static mxCell createNodeFromPort(Graph graph, ProcessPort port, Point location) {

        // This method handles global ports only
        if (!port.isGlobal()) {
            throw new IllegalArgumentException(AppConstants.GRAPH_EXCEPTION_PORT_NOT_GLOBAL);
        }

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        mxCell portCell = null;

        try {
            String style = port.isGlobalInput() ? GraphSetup.STYLENAME_GLOBAL_INPUT : GraphSetup.STYLENAME_GLOBAL_OUTPUT;
            portCell = (mxCell) graph.insertVertex(parent, null, port, location.x, location.y, GLOBAL_PORT_WIDTH, GLOBAL_PORT_HEIGHT, style);

        } finally {
            graph.getModel().endUpdate();
        }

        return portCell;
    }

}
