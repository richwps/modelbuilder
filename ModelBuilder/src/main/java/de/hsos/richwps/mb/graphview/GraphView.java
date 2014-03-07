/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class GraphView extends JPanel {

    private Graph graph;
    private mxGraphComponent graphComponent;

    public GraphView() {
        super();
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
    }

    public Component getGui() {
        // lazy init
        if (null == graphComponent) {
            Graph graph = getGraph();

            graphComponent = new mxGraphComponent(graph);
            graphComponent.setToolTips(true);
            graphComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        return graphComponent;
    }

    /**
     * Returns the (JGraphX) graph.
     * @return
     */
    public Graph getGraph() {
        if (null == graph) {
            graph = new Graph();
            graph.setAllowDanglingEdges(false);
            graph.setCellsResizable(false);
            graph.setCellsEditable(false);
            graph.setPortsEnabled(true);
            graph.setAllowLoops(false);

            // TODO global graph styles 
            Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
            style.put(mxConstants.STYLE_FILLCOLOR, Color.white);
        }

        return graph;
    }

    // TODO mocked controller method
    public void createNodeFromProcess(IProcessEntity process) {
        Graph graph = getGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        int numInputs = process.getNumInputs();
        int numOutputs = process.getNumOutputs();
        String name = process.toString();

        // TODO Jewgeni, start here :)
        try {
            // TODO used by "ports" example, remove if not needed
            int PORT_DIAMETER = 20;
            int PORT_RADIUS = PORT_DIAMETER / 2;

            // TODO calculate process dimensions depending on num ports, length of name  etc.
            mxCell v1 = (mxCell) graph.insertVertex(parent, null, name, 0, 0, 100, 50, "");
            v1.setConnectable(false);

            // TODO mocked inputs
            for (int i = 0; i < numInputs; i++) {
                // TODO calculate width of ports
                mxGeometry geo1 = new mxGeometry(0, 0, PORT_DIAMETER, PORT_DIAMETER);
                geo1.setRelative(true);
                geo1.setOffset(new mxPoint(i * PORT_DIAMETER, 0));

                mxCell port1 = new mxCell(null, geo1, "shape=ellipse;perimter=ellipsePerimeter");
                port1.setVertex(true);
                port1.setValue("I"+(i+1));
                graph.addCell(port1, v1);
            }

            // TODO mocked outputs
            for (int i = 0; i < numOutputs; i++) {
                // TODO calculate width of ports
                mxGeometry geo2 = new mxGeometry(0, 1, PORT_DIAMETER, PORT_DIAMETER);
                geo2.setRelative(true);
                geo2.setOffset(new mxPoint(i * PORT_DIAMETER, -PORT_DIAMETER));

                mxCell port1 = new mxCell(null, geo2, "shape=ellipse;perimter=ellipsePerimeter");
                port1.setVertex(true);
                port1.setValue("O"+(i+1));
                graph.addCell(port1, v1);
            }

        } finally {
            graph.getModel().endUpdate();
        }
    }

}
