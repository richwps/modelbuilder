/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.graphView.codec.GraphEdgeCodec;
import de.hsos.richwps.mb.graphView.codec.GraphModelCodec;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;
import org.w3c.dom.Document;

/**
 *
 * @author dziegenh
 */
public class GraphView extends JPanel {

    private Graph graph;
    private mxGraphComponent graphComponent;
    private LinkedList<ListSelectionListener> selectionListener;
    private IProcessProvider processProvider;
    private mxUndoManager undoManager;

    // TODO move values to config/constants
    private int PROCESS_WIDTH = 210;
    private int PROCESS_HEIGHT = 90;
    private int PROCESS_PORT_HEIGHT = 30;
    private int GLOBAL_PORT_WIDTH = 45;
    private int GLOBAL_PORT_HEIGHT = 45;

    public GraphView(IProcessProvider processProvider) {
        super();
        this.processProvider = processProvider;
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        selectionListener = new LinkedList<ListSelectionListener>();
    }

    /**
     * Adds an listener for mxEvent.UNDO events to the model.
     *
     * @param listener
     */
    public void addUndoEventListener(mxEventSource.mxIEventListener listener) {
        getGraph().getModel().addListener(mxEvent.UNDO, listener);
    }

    /**
     * Lazy graph init.
     *
     * @return
     */
    private mxGraphComponent getGraphComponent() {
        if (null == graphComponent) {
            Graph graph = getGraph();

            graphComponent = new GraphComponent(graph);
            graphComponent.setToolTips(true);
            graphComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
            graphComponent.getViewport().setBackground(Color.WHITE); // TODO move value to config/constants !!

//            CellMarker cellMarker = new CellMarker(graphComponent);
//            cellMarker.setHotspotEnabled(true);
//            graphComponent.getConnectionHandler().setMarker(cellMarker);
            // TODO replace with eventlistener - graph should not know graphcomponent!
//            graph.setGraphComponent(graphComponent);
        }

        return graphComponent;
    }

    /**
     * Blackboxed component getter.
     *
     * @return
     */
    public Component getGui() {
        return getGraphComponent();
    }

    /**
     * Returns the custom (JGraphX) graph.
     *
     * @return
     */
    public Graph getGraph() {
        if (null == graph) {
            graph = new Graph();

            // TODO move to config/constants
            int fontSize = 16;
            int spacing = 4;

            // graph setup
            graph.setCellsDisconnectable(true);
            graph.setAllowDanglingEdges(false);
            graph.setEdgeLabelsMovable(false);
            graph.setConnectableEdges(false);
            graph.setCellsResizable(false);
            graph.setCellsDeletable(true);
            graph.setCellsEditable(false);
            graph.setPortsEnabled(true);
            graph.setDropEnabled(false);
            graph.setAllowLoops(AppConstants.GRAPH_ALLOW_FEEDBACK_LOOPS);
            graph.setMultigraph(false);
            graph.setAutoLayout(AppConstants.GRAPH_AUTOLAYOUT);
            graph.setCellsMovable(!graph.isAutoLayout());

            // size (ratio) of the connectable port part (in percent relative to port size)
            mxConstants.DEFAULT_HOTSPOT = graph.isAutoLayout() ? 1 : .5;

            // EDGE STYLE
            mxStylesheet stylesheet = graph.getStylesheet();
            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_STROKECOLOR, "000000");
            Object edgeStyle = graph.isAutoLayout() ? mxConstants.NONE : mxConstants.EDGESTYLE_TOPTOBOTTOM;
            stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_EDGE, edgeStyle);
            graph.setStylesheet(stylesheet);

            // PROCESS STYLE
            Hashtable<String, Object> processStyle = new Hashtable<String, Object>();
            processStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            processStyle.put(mxConstants.STYLE_OPACITY, 100); // changed opcatity to 100
            processStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            processStyle.put(mxConstants.STYLE_FILLCOLOR, "#ffffff"); // changed fill color to white
            processStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // changed stroke color to black
            processStyle.put(mxConstants.STYLE_FONTSIZE, fontSize); // changed font size
            processStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD); // changed font size
            processStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f6f6f6"); // changed font size
//            processStyle.put(mxConstants.STYLE_MOVABLE, "0");
            processStyle.put(mxConstants.STYLE_SPACING_TOP, spacing); // changed textlabel v-align
            stylesheet.putCellStyle("PROCESS", processStyle);

            // GLOBAL INPUT PORT STYLE
            Hashtable<String, Object> processInputStyle = (Hashtable<String, Object>) processStyle.clone();
            stylesheet.putCellStyle("PROCESS_INPUT", processInputStyle);

            // GLOBAL OUTPUT PORT STYLE
            Hashtable<String, Object> processOutputStyle = new Hashtable<String, Object>();
            processOutputStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
            processOutputStyle.put(mxConstants.STYLE_OPACITY, 100);
            processOutputStyle.put(mxConstants.STYLE_FONTCOLOR, "#ffffff");
            processOutputStyle.put(mxConstants.STYLE_FILLCOLOR, "#808080");
            processOutputStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
            processOutputStyle.put(mxConstants.STYLE_FONTSIZE, fontSize);
            processOutputStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
            processOutputStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#000000");
//            processStyle.put(mxConstants.STYLE_MOVABLE, "0");
            processOutputStyle.put(mxConstants.STYLE_SPACING_TOP, spacing);
            stylesheet.putCellStyle("PROCESS_OUTPUT", processOutputStyle);

            // PROCESS (SUB-) PORT STYLE
            Hashtable<String, Object> portStyle = new Hashtable<String, Object>();
            portStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE); // changed shape to rect
            portStyle.put(mxConstants.STYLE_OPACITY, 100); // changed opacity to 100
            portStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
            portStyle.put(mxConstants.STYLE_FILLCOLOR, "none"); // changed fill color to white
//            portStyle.put(mxConstants.STYLE_GRADIENTCOLOR, "#f9f9f9"); // changed font size
            portStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // changed stroke color to black
            portStyle.put(mxConstants.STYLE_FONTSIZE, fontSize); // changed font size
            portStyle.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD); // changed font size
            portStyle.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE); // changed textlabel v-align
            portStyle.put(mxConstants.STYLE_SPACING_TOP, spacing); // changed textlabel v-align
            stylesheet.putCellStyle("PORT", portStyle);

            // selection listener adapter
            graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
                public void invoke(Object o, mxEventObject eo) {
                    ListSelectionEvent event = new ListSelectionEvent(graph, 0, graph.getSelectionCount(), true);
                    for (ListSelectionListener listener : selectionListener) {
                        listener.valueChanged(event);
                    }
                }
            });

            // TODO refactor when the real Process Model is implemented!
            mxCodecRegistry.addPackage("de.hsos.richwps.mb.semanticProxy.entity");
            mxCodecRegistry.register(new mxObjectCodec(new de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity()));
            mxCodecRegistry.register(new mxObjectCodec(new de.hsos.richwps.mb.semanticProxy.entity.ProcessPort()));
            mxCodecRegistry.register(new GraphEdgeCodec(new de.hsos.richwps.mb.graphView.GraphEdge()));
            mxCodecRegistry.register(new GraphModelCodec(new de.hsos.richwps.mb.graphView.GraphModel()));

            // TODO move magic numbers etc. to config !!
            mxSwingConstants.VERTEX_SELECTION_COLOR = AppConstants.SELECTION_BG_COLOR;
            float strokeWidth = 1.5f;
            mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(strokeWidth,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[]{
                        3 * strokeWidth, 3 * strokeWidth}, 0.0f);
        }

        return graph;
    }

    public mxCell createNodeFromProcess(IProcessEntity process, Point location) {
        Graph graph = getGraph();
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
            processCell = (mxCell) graph.insertVertex(parent, null, process, location.x, location.y, PROCESS_WIDTH, PROCESS_HEIGHT, "PROCESS"); // changed height
            processCell.setConnectable(false);

            int i = 0;
            int curX = 0;
            for (ProcessPort pIn : process.getInputPorts()) {
                int curPortWidth = (int) ((i % 2 == 0) ? Math.floor(INPUT_PORT_WIDTH) : Math.ceil(INPUT_PORT_WIDTH));

                mxGeometry geo1 = new mxGeometry(0, 0, curPortWidth, PROCESS_PORT_HEIGHT); // Changed position offset
                geo1.setRelative(true);
                geo1.setOffset(new mxPoint(curX, 0));

                mxCell port1 = new mxCell(null, geo1, "PORT");
                port1.setVertex(true);
//                port1.setValue("In " + (i + 1));   // later, ports will have names!
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

                mxCell port1 = new mxCell(null, geo2, "PORT");
                port1.setVertex(true);
//                port1.setValue("Out " + (i + 1));   // later, ports will have names!
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

    public mxCell createNodeFromPort(ProcessPort port, Point location) {

        // This method handles global ports only
        if (!port.isGlobal()) {
            return null;
        }

        Graph graph = getGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();

        mxCell portCell = null;

        try {
            String style = port.isGlobalInput() ? "PROCESS_INPUT" : "PROCESS_OUTPUT";
            portCell = (mxCell) graph.insertVertex(parent, null, port, location.x, location.y, GLOBAL_PORT_WIDTH, GLOBAL_PORT_HEIGHT, style);

        } finally {
            graph.getModel().endUpdate();
        }

        return portCell;
    }

    public void addSelectionListener(ListSelectionListener listener) {
        selectionListener.add(listener);
    }

    public void removeSelectionListener(ListSelectionListener listener) {
        selectionListener.remove(listener);
    }

    public boolean hasSelection() {
        return getGraph().getSelectionCount() > 0;
    }

    public List<IProcessEntity> getSelectedProcesses() {
        Object[] cells = getGraph().getSelectionCells();
        List<IProcessEntity> processes = new LinkedList<IProcessEntity>();
        for (Object cell : cells) {
            Object cellValue = getGraph().getModel().getValue(cell);
            if (cellValue != null && cellValue instanceof IProcessEntity) {
                processes.add((IProcessEntity) cellValue);
            }
        }

        return processes;
    }

    public Object[] getSelection() {
        return getGraph().getSelectionCells();
    }

    public String getCurrentGraphName() {
        return getGraph().getGraphModel().getName();
    }

    public void saveGraphToXml(String filename) throws IOException {
        mxCodec codec = new mxCodec();
        String oldName = getGraph().getGraphModel().getName();
        // TODO check for missing ProcessEntities !!!

        // Set graph name in order to let the GraphModelCodec use it as an attribute
        setGraphName(filename);
        try {
            String xml = mxXmlUtils.getXml(codec.encode(getGraph().getGraphModel()));//.cloneMxgraphModel()));
            mxUtils.writeFile(xml, filename);
        
        } catch (IOException e) {
            // if an error occured, reset graphModel name
            setGraphName(oldName);
            throw e;
        }

    }

    public void loadGraphFromXml(String filename) throws Exception {
        mxCodec codec = new mxCodec();
        Document doc = mxXmlUtils.parseXml(mxUtils.readFile(filename));
        GraphModel graphModel = (GraphModel) codec.decode(doc.getFirstChild());
        graph.setModel(graphModel);
        graphComponent.refresh();
    }

    public void newGraph() {
        // TODO check if graph model is really clean/empty after method call
        mxGraph graph = getGraph();
        graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
        getGraph().getGraphModel().setName(null);
    }

    public void deleteSelectedCells() {
        getGraph().removeCells(getGraph().getSelectionCells());
    }

    // TODO mocked: graph name is currently derived from filename. create possibility to let the user set the name!
    private void setGraphName(String filename) {
        int pathEnd = filename.lastIndexOf(File.separator) + 1;
        int extensionBegin = filename.length() - 4;
        // the name is the pure filename without path and extension.
        getGraph().getGraphModel().setName(filename.substring(pathEnd, extensionBegin));
    }

    public void layoutGraph() {
        getGraph().layout();
    }

    void setCellsSelected(Object[] cells) {
        getGraph().clearSelection();
        getGraph().setSelectionCells(cells);
    }

}
