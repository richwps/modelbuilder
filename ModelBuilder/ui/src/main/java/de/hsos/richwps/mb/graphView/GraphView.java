/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;
import org.w3c.dom.Document;

/**
 * Boundary class for accessing and interacting with the RichWPS graph.
 * @author dziegenh
 */
public class GraphView extends JPanel {

    private Graph graph;
    private mxGraphComponent graphComponent;
    private LinkedList<ListSelectionListener> selectionListener;
    private IProcessProvider processProvider;

    public GraphView(IProcessProvider processProvider) {
        super();
        this.processProvider = processProvider;
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        selectionListener = new LinkedList<ListSelectionListener>();
        
        // initialize codecs and constants.
        GraphSetup.init();
    }

    /**
     * Lazy graph component init.
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
            GraphSetup.setup(graph);

            // selection listener adapter
            graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
                public void invoke(Object o, mxEventObject eo) {
                    ListSelectionEvent event = new ListSelectionEvent(graph, 0, graph.getSelectionCount(), true);
                    for (ListSelectionListener listener : selectionListener) {
                        listener.valueChanged(event);
                    }
                }
            });
        }

        return graph;
    }

    /**
     * Creates a graph node (cell) representing a process entity.
     * @param process
     * @param location point where the cell will be placed
     * @return
     */
    public mxCell createNodeFromProcess(IProcessEntity process, Point location) {
        return GraphNodeCreator.createNodeFromProcess(getGraph(), process, location);
    }

    /**
     * Creates a graph node (cell) representing the global port.
     * @param port
     * @param location point where the cell will be placed
     * @return
     */
    public mxCell createNodeFromPort(ProcessPort port, Point location) {
        return GraphNodeCreator.createNodeFromPort(getGraph(), port, location);
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
     * Adds a listener for selection changes.
     *
     * @param listener will be notified on selection changes.
     */
    public void addSelectionListener(ListSelectionListener listener) {
        selectionListener.add(listener);
    }

    /**
     * Removes a selection change listener.
     *
     * @param listener the listener to be removed.
     */
    public void removeSelectionListener(ListSelectionListener listener) {
        selectionListener.remove(listener);
    }

    /**
     * Return true if any modelling element is selected (cell, edge or sth).
     *
     * @return
     */
    public boolean hasSelection() {
        return getGraph().getSelectionCount() > 0;
    }

    /**
     * Returns all selected modelling elements (cells, edges etc).
     *
     * @return
     */
    public Object[] getSelection() {
        return getGraph().getSelectionCells();
    }

    /**
     * Returns currently selected process entities.
     *
     * @return selected process entities.
     */
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

    /**
     * Serializes the current model using extended JGRaphX codecs and saves the
     * result.
     *
     * @param filename the file which will contain the resulting XML.
     * @throws IOException
     */
    public void saveGraphToXml(String filename) throws IOException {
        mxCodec codec = new mxCodec();
        String xml = mxXmlUtils.getXml(codec.encode(getGraph().getGraphModel()));//.cloneMxgraphModel()));
        mxUtils.writeFile(xml, filename);
    }

    /**
     * Reads from the given file and instantiates the model etc using extended
     * JGRaphX codecs.
     * @param filename
     * @throws java.io.IOException
     */
    public void loadGraphFromXml(String filename) throws IOException {
        mxCodec codec = new mxCodec();
        Document doc = mxXmlUtils.parseXml(mxUtils.readFile(filename));
        GraphModel graphModel = (GraphModel) codec.decode(doc.getFirstChild());
        graph.setModel(graphModel);
        graphComponent.refresh();
    }

    /**
     * Removes all modelling elements and resets the graph/model properties
     * (name etc).
     */
    public void newGraph() {
        // TODO check if graph model is really clean/empty after method call
        mxGraph graph = getGraph();
        graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
        getGraph().getGraphModel().setName(null);
    }

    /**
     * Deletes all selected elements (cells and edges).
     */
    public void deleteSelectedCells() {
        getGraph().removeCells(getGraph().getSelectionCells());
    }

    /**
     * Returns the name property of the current graph/model.
     *
     * @return
     */
    public String getCurrentGraphName() {
        return getGraph().getGraphModel().getName();
    }

    /**
     * Sets the graph (model) name.
     *
     * @param name
     */
    public void setGraphName(String name) {
        getGraph().getGraphModel().setName(name);
    }

    /**
     * Arrangens the modelling elements (cells) using a custom LayoutManager.
     */
    public void layoutGraph() {
        getGraph().layout();
    }

    /**
     * Select all modelling elements (cells, edges).
     */
    public void selectAll() {
        getGraph().selectAll();
    }

    /**
     * Selects the given cells.
     * @param cells to selected.
     */
    void setCellsSelected(Object[] cells) {
        getGraph().clearSelection();
        getGraph().setSelectionCells(cells);
    }

}
