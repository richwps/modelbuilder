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
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphComponent;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
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
 *
 * @author dziegenh
 */
public class GraphView extends JPanel {

    private Graph graph;
    private mxGraphComponent graphComponent;
    private LinkedList<ListSelectionListener> selectionListener;
    private LinkedList<ModelElementsChangedListener> modelElementsChangeListener;
    private IProcessProvider processProvider;

    // there exist no mxGraph-Constants for the property keys :(
    public static final String PROPERTY_KEY_EDIT = "edit";
    public static final String PROPERTY_KEY_CELLS = "cells";

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
    mxGraphComponent getGraphComponent() {
        if (null == graphComponent) {
            Graph graph = getGraph();

            graphComponent = new GraphComponent(graph);
            graphComponent.setToolTips(true);
            graphComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
            graphComponent.getViewport().setBackground(Color.WHITE); // TODO move value to config/constants !!
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

    protected void moveSelectedCells(int dx, int dy) {
        Object[] cells = getSelection();
        getGraph().moveCells(cells, dx, dy);
    }

    /**
     * Returns true if the current graph doesn't contain any modelling element
     * (cell).
     *
     * @return
     */
    public boolean isEmpty() {
        return getGraph().getGraphModel().getChildCount(getGraph().getDefaultParent()) < 1;
    }

    /**
     * Creates a graph node (cell) representing a process entity.
     *
     * @param process
     * @param location point where the cell will be placed
     * @return
     */
    public mxCell createNodeFromProcess(ProcessEntity process, Point location) {
        return GraphNodeCreator.createNodeFromProcess(getGraph(), process, location);
    }

    /**
     * Creates a graph node (cell) representing the global port.
     *
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

    public Point getEmptyCellLocation(Point start) {
        while (null != getGraphComponent().getCellAt(start.x, start.y)) {
            start.y += GraphSetup.CELLS_VERTICAL_OFFSET;
        }

        return start;
    }

    public void selectCellByValue(Object value) {
        if(value instanceof GraphModel) {
            getGraph().clearSelection();
        }

        else {
            Object[] cells = getGraph().getChildCells(getGraph().getDefaultParent());
            for(Object cell : cells) {
                Object cellValue = getGraph().getGraphModel().getValue(cell);
                if(value.equals(cellValue)) {
                    getGraph().setSelectionCell(cell);
                }
            }
        }
    }

    // constants for model element change listener
    public static enum ELEMENT_TYPE {

        PROCESS, GLOBAL_PORT
    }

    public static enum ELEMENT_CHANGE_TYPE {

        ADDED, REMOVED
    }

    private List<ModelElementsChangedListener> getModelElementsChangedListeners() {
        if (null == modelElementsChangeListener) {
            modelElementsChangeListener = new LinkedList<ModelElementsChangedListener>();

            // listen to cells added/removed and fire custom event
            mxEventSource.mxIEventListener listener = new mxEventSource.mxIEventListener() {
                @Override
                public void invoke(Object o, mxEventObject eo) {
                    ELEMENT_CHANGE_TYPE change_type = null;
//                    ELEMENT_TYPE type = null;
                    GraphModel model = getGraph().getGraphModel();

                    if (eo.getName().equals(mxEvent.CELLS_ADDED)) {
                        change_type = ELEMENT_CHANGE_TYPE.ADDED;
                    } else if (eo.getName().equals(mxEvent.CELLS_REMOVED)) {
                        change_type = ELEMENT_CHANGE_TYPE.REMOVED;
                    } else {
                        return;
                    }

                    Object[] cells = (Object[]) eo.getProperty(PROPERTY_KEY_CELLS);
                    if (null == cells) {
                        return;
                    }

                    for (Object cell : cells) {
                        if (model.isProcess(cell)) {
                            Object value = ((mxCell) cell).getValue();
                            fireModelElementsChanged(value, ELEMENT_TYPE.PROCESS, change_type);

                        } else if (model.isGlobalPort(cell)) {
                            Object value = ((mxCell) cell).getValue();
                            fireModelElementsChanged(value, ELEMENT_TYPE.GLOBAL_PORT, change_type);
                        }
                    }
                }
            };
            getGraph().addListener(mxEvent.CELLS_ADDED, listener);
            getGraph().addListener(mxEvent.CELLS_REMOVED, listener);
        }

        return modelElementsChangeListener;
    }

    void fireModelElementsChanged(Object element, ELEMENT_TYPE type, ELEMENT_CHANGE_TYPE changeType) {
        for (ModelElementsChangedListener listener : getModelElementsChangedListeners()) {
            listener.modelElementsChanged(element, type, changeType);
        }
    }

    /**
     * Adds a listener for model changes.
     *
     * @param listener
     */
    public void addModelElementsChangedListener(ModelElementsChangedListener listener) {
        getModelElementsChangedListeners().add(listener);
    }

    /**
     * removes a l
     *
     * @param listener
     */
    public void removeModeElementslChangedListener(ModelElementsChangedListener listener) {
        getModelElementsChangedListeners().remove(listener);
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

    public ELEMENT_TYPE getSelectedElementType() {
        Object[] cells = getSelection();

        if (cells.length == 1 && cells[0] instanceof mxCell) {
            GraphModel model = getGraph().getGraphModel();

            if (model.isProcess(cells[0])) {
                return ELEMENT_TYPE.PROCESS;
            }
            if (model.isGlobalPort(cells[0])) {
                return ELEMENT_TYPE.GLOBAL_PORT;
            }
        }

        return null;
    }

    /**
     * Returns currently selected process entities.
     *
     * @return selected process entities.
     */
    public List<ProcessEntity> getSelectedProcesses() {
        return getGraph().getSelectedProcesses();
    }

    /**
     * Returns all process entities which are used by the graph.
     *
     * @return selected process entities.
     */
    public List<ProcessEntity> getUsedProcesses() {
        return getGraph().getProcesses();
    }

    public List<mxCell> getProcessCells() {
        return getGraph().getProcessCells();
    }

    /**
     * Returns currently selected global ports.
     *
     * @return selected global ports.
     */
    public List<ProcessPort> getSelectedGlobalPorts() {
        return getGraph().getSelectedGlobalPorts();
    }

    /**
     * Returns all global ports which are used by the graph.
     *
     * @return selected global ports.
     */
    public List<ProcessPort> getUsedGlobalPorts() {
        return getGraph().getGlobalPorts();
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
        String xml = mxXmlUtils.getXml(codec.encode(getGraph().getGraphModel()));
        mxUtils.writeFile(xml, filename);
    }

    /**
     * Reads from the given file and instantiates the model etc using extended
     * JGRaphX codecs.
     *
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
    public String getGraphName() {
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
     *
     * @param cells to selected.
     */
    void setCellsSelected(Object[] cells) {
        getGraph().clearSelection();
        getGraph().setSelectionCells(cells);
    }
}
