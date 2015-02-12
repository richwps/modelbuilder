package de.hsos.richwps.mb.graphView;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphComponent;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
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
    private final LinkedList<ListSelectionListener> selectionListener;
    private LinkedList<ModelElementsChangedListener> modelElementsChangeListener;

    // there exist no mxGraph-Constants for the following keys :(
    public static final String PROPERTY_KEY_EDIT = "edit";
    public static final String PROPERTY_KEY_CELLS = "cells";
    public static final String EVENT_NAME_UNDO = "undo";

    public GraphView() {
        super();
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        selectionListener = new LinkedList<>();

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
            GraphSetup.setupGraphComponent((GraphComponent) graphComponent);
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
                    removePortSelection();
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
     * Removes local process ports from the current selection.
     */
    protected void removePortSelection() {
        for (Object cell : getGraph().getSelectionCells()) {
            if (getGraph().getGraphModel().isLocalPort(cell)) {
                getGraph().removeSelectionCell(cell);
            }
        }
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

    /**
     * Retuns a valid location for adding new cells.
     *
     * @param start
     * @return
     */
    public Point getEmptyCellLocation(Point start) {
        Rectangle rect = new Rectangle(start.x, start.y, GraphSetup.PROCESS_WIDTH, GraphSetup.PROCESS_HEIGHT);

        while (getGraphComponent().getCells(rect).length > 0) {
            rect.y += GraphSetup.CELLS_VERTICAL_OFFSET;
        }

        return new Point(rect.x, rect.y);
    }

    /**
     * Finds the cell which contains the given value and selects it on success.
     *
     * @param value
     */
    public void selectCellByValue(Object value) {
//        if (value instanceof GraphModel) {
        getGraph().clearSelection();
//        } else {
        Object[] cells = getGraph().getChildCells(getGraph().getDefaultParent());
        for (Object cell : cells) {
            Object cellValue = getGraph().getGraphModel().getValue(cell);
            if (value.equals(cellValue)) {
                getGraph().setSelectionCell(cell);
            }
        }
//        }
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
        if (null == element) {
            return;
        }

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
        getGraph().setModel(graphModel);
        getGraphComponent().zoomTo(1., true);
        getGraphComponent().refresh();
    }

    /**
     * Removes all modelling elements and resets the graph/model properties
     * (name etc).
     */
    public Graph newGraph() {
        Graph graph = getGraph();
        graph.clearSelection();
        graph.setModel(new GraphModel());
        getGraph().removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
        return graph;
    }

    /**
     * Deletes all selected elements (cells and edges).
     */
    public void deleteSelectedCells() {
        getGraph().removeCells(getGraph().getSelectionCells());
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
     * Creates clones of the selected elements and adds them to the model.
     */
    public void duplicateSelection() {
        Object[] selection = getSelection();
        for (Object anObject : selection) {
            Object cellValue = getGraph().getModel().getValue(anObject);
            Point location = getEmptyCellLocation(new Point(0, 0));

            // create process
            if (cellValue instanceof ProcessEntity) {
                // don't clone the process, there must be only one instance of each process
                GraphNodeCreator.createNodeFromProcess(getGraph(), (ProcessEntity) cellValue, location);

                // create port
            } else if (cellValue instanceof ProcessPort) {
                // clone the port because it contains user input values
                ProcessPort port = (ProcessPort) cellValue;
                port = port.clone();
                GraphNodeCreator.createNodeFromPort(getGraph(), port, location);

            }
        }
    }

    /**
     * Selects the given cells.
     *
     * @param cells to selected.
     */
    public void setCellsSelected(Object[] cells) {
        getGraph().clearSelection();
        getGraph().setSelectionCells(cells);
    }

    /**
     * Creates and connects a global port for every non-connected local process
     * port.
     */
    public void addMissingGlobalPorts() {
        final GraphModel model = getGraph().getGraphModel();

        model.beginUpdate();

        List<mxCell> processCells = getProcessCells();
        for (mxCell aCell : processCells) {
            int childCount = aCell.getChildCount();
            for (int i = 0; i < childCount; i++) {
                mxCell aPortCell = (mxCell) aCell.getChildAt(i);

                // check prot cell connections
                boolean unused = true;
                if (model.isFlowInput(aPortCell)) {
                    unused = !model.isInputPortUsed(aPortCell, null);
                } else {
                    unused = !model.isOutputPortUsed(aPortCell, null);
                }

                // port cell must not have any connections
                if (unused) {

                    mxGeometry aPortGeom = aPortCell.getGeometry();
                    ProcessPort aPort = (ProcessPort) getGraph().getModel().getValue(aPortCell);

                    // create the global cell's value
                    ProcessPort aGlobalPort = aPort.clone();
                    aGlobalPort.setGlobal(true);

                    // compute the global cell's location
                    double portAbsoluteX = aCell.getGeometry().getX() + aPortGeom.getOffset().getX();
                    double portAbsoluteY = aCell.getGeometry().getY() - aPortGeom.getOffset().getY();
                    double globalX = portAbsoluteX + (aPortGeom.getWidth() - GraphSetup.GLOBAL_PORT_WIDTH) / 2;
                    Point cellLocation = new Point((int) globalX, (int) portAbsoluteY);
                    if (aPort.isFlowInput()) { // global port location above port
                        cellLocation.y -= 2 * GraphSetup.GLOBAL_PORT_HEIGHT;

                    } else { // global port location below port
                        cellLocation.y += GraphSetup.PROCESS_PORT_HEIGHT + 2 * GraphSetup.GLOBAL_PORT_HEIGHT;
                    }

                    // create global port cell
                    mxCell globalPortCell = GraphNodeCreator.createNodeFromPort(getGraph(), aGlobalPort, cellLocation);

                    // connect global and local port cells
                    getGraph().insertEdge(aCell.getParent(), null, null, aPortCell, globalPortCell);
                }
            }
        }

        getGraph().getModel().endUpdate();
    }

    /**
     * Finds a cell by its value.
     *
     * @param value
     * @return
     */
    public mxCell getCellByValue(Object value) {
        final GraphModel model = getGraph().getGraphModel();
        Map<String, Object> cells = model.getCells();
        for (Object cell : cells.values()) {
            Object value1 = model.getValue(cell);
            if (value1.equals(value)) {
                return (mxCell) cell;
            }
        }

        return null;
    }

    /**
     * Finds a cell by its value inside then given parent cell.
     *
     * @param value
     * @return
     */
    public mxCell getCellByValue(mxCell parent, Object value) {
        final GraphModel model = getGraph().getGraphModel();
        for (int i = 0; i < parent.getChildCount(); i++) {
            mxCell cell = (mxCell) parent.getChildAt(i);
            Object value1 = model.getValue(cell);
            if (value1.equals(value)) {
                return (mxCell) cell;
            }
        }

        return null;
    }
}
