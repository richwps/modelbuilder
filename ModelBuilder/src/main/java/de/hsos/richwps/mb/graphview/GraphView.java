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
import com.mxgraph.view.mxStylesheet;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;
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
        if (null == graphComponent) {
            Graph graph = getGraph();

//            graphComponent = new GraphComponent(graph);
            graphComponent = new mxGraphComponent(graph);
            graphComponent.setToolTips(true);
            graphComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
            graphComponent.getViewport().setBackground(Color.WHITE);


            // TODO replace with eventlistener - graph should not know graphcomponent!
            graph.setGraphComponent(graphComponent);
        }

        return graphComponent;
    }

    /**
     * Returns the custom (JGraphX) graph.
     *
     * @return
     */
    public Graph getGraph() {
        if (null == graph) {
            graph = new Graph();
            graph.setCellsDisconnectable(true);
            graph.setAllowDanglingEdges(false);
            graph.setEdgeLabelsMovable(false);
            graph.setConnectableEdges(false);
            graph.setCellsResizable(false);
            graph.setCellsDeletable(true);
            graph.setCellsEditable(false);
            graph.setPortsEnabled(true);
            graph.setAllowLoops(false);
            graph.setMultigraph(false);

            mxConstants.DEFAULT_HOTSPOT = 1;

            mxStylesheet stylesheet = graph.getStylesheet();
            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_STROKECOLOR, "000000");
//            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_OPACITY, 50);
            graph.setStylesheet(stylesheet);

            Hashtable<String, Object> processStyle = new Hashtable<String, Object>();
            processStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            processStyle.put(mxConstants.STYLE_OPACITY, 100); // changed opcatity to 100
            processStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            processStyle.put(mxConstants.STYLE_FILLCOLOR, "#ffffff"); // changed fill color to white
            processStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // changed stroke color to black
            processStyle.put(mxConstants.STYLE_FONTSIZE, 20); // changed font size
            processStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD); // changed font size
            processStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f6f6f6"); // changed font size
            processStyle.put(mxConstants.STYLE_MOVABLE, "0"); // changed font size
            processStyle.put(mxConstants.STYLE_SPACING_TOP, 5); // changed textlabel v-align
            stylesheet.putCellStyle("PROCESS", processStyle);

            Hashtable<String, Object> portStyle = new Hashtable<String, Object>();
            portStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE); // changed shape to rect
            portStyle.put(mxConstants.STYLE_OPACITY, 100); // changed opacity to 100
            portStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            portStyle.put(mxConstants.STYLE_FILLCOLOR, "none"); // changed fill color to white
//            portStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f9f9f9"); // changed font size
            portStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // changed stroke color to black
            portStyle.put(mxConstants.STYLE_FONTSIZE, 20); // changed font size
            portStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD); // changed font size
            portStyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE); // changed textlabel v-align
            portStyle.put(mxConstants.STYLE_SPACING_TOP, 5); // changed textlabel v-align
            stylesheet.putCellStyle("PORT", portStyle);
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

        try {
            // TODO used by "ports" example, remove if not needed
            int PROCESS_WIDTH = 180;
            int PROCESS_HEIGHT = 90;
            int PORT_HEIGHT = 30;
            int INPUT_PORT_WIDTH = PROCESS_WIDTH / numInputs;
            int OUTPUT_PORT_WIDTH = PROCESS_WIDTH / numOutputs;

            // TODO calculate process dimensions depending on num ports, length of name  etc.
            mxCell v1 = (mxCell) graph.insertVertex(parent, null, name, 0, 0, PROCESS_WIDTH, PROCESS_HEIGHT, "PROCESS"); // changed height
            v1.setConnectable(false);

            // TODO mocked inputs must later be replaced with real input information
            for (int i = 0; i < numInputs; i++) {
                // TODO calculate width of ports
                mxGeometry geo1 = new mxGeometry(0, 0, INPUT_PORT_WIDTH, PORT_HEIGHT); // Changed position offset
                geo1.setRelative(true);
                geo1.setOffset(new mxPoint(i * INPUT_PORT_WIDTH, 0));

                mxCell port1 = new mxCell(null, geo1, "PORT");
                port1.setVertex(true);
                port1.setValue("In " + (i + 1));   // later, ports will have names!
                graph.addCell(port1, v1);
            }

            // TODO mocked outputs must later be replaced with real output information
            for (int i = 0; i < numOutputs; i++) {
                // TODO calculate width of ports
                mxGeometry geo2 = new mxGeometry(0, 1, OUTPUT_PORT_WIDTH, PORT_HEIGHT); // Changed position offset
                geo2.setRelative(true);
                geo2.setOffset(new mxPoint(i * OUTPUT_PORT_WIDTH, -PORT_HEIGHT)); // Changed position offset

                mxCell port1 = new mxCell(null, geo2, "PORT");
                port1.setVertex(true);
                port1.setValue("Out " + (i + 1));   // later, ports will have names!
                graph.addCell(port1, v1);
            }

        } finally {
            graph.getModel().endUpdate();
        }
    }

}
