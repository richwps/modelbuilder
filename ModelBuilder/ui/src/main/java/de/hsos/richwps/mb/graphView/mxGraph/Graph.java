/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphProperties;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphSetup;
import de.hsos.richwps.mb.graphView.mxGraph.layout.GraphWorkflowLayout;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Extended version of the (JGraphX) mxGraph. Needs a GraphModel as model
 * instance.
 *
 * @author dziegenh Based on mxGraph example "ports"
 */
public class Graph extends mxGraph {

    private mxAnalysisGraph ag;
    private GraphWorkflowLayout graphWorkflowLayout;
//    private mxGraphComponent graphComponent;
    private boolean autoLayout = true;

    public Graph() {
        super(new GraphModel());
    }

    @Override
    public String getLabel(Object cell) {
        Object value = model.getValue(cell);
        if (null != value && value instanceof ProcessEntity) {
            return UiHelper.limitString(((ProcessEntity) value).toString(), AppConstants.PROCESS_TITLE_MAX_VIEW_LENGTH);
        }

        return super.getLabel(cell);
    }

    @Override
    public mxRectangle graphModelChanged(mxIGraphModel igm, List<mxUndoableEdit.mxUndoableChange> list) {
        mxRectangle ret = super.graphModelChanged(igm, list);
        if (autoLayout) {
            layout();
        }

        return ret;
    }

    public void setAutoLayout(boolean autoLayout) {
        this.autoLayout = autoLayout;
    }

    @Override
    public boolean isValidSource(Object o) {
        // a used input port is not a valid source
        if (getGraphModel().isInputPortUsed(o, this)) {
            return false;
        }

        return super.isValidSource(o);
    }

    @Override
    public String getEdgeValidationError(Object edge, Object source, Object target) {

        GraphModel graphModel = getGraphModel();
        // check direction and reverse it if necessery (target must be an input port)
        if (graphModel.isFlowOutput(target)) {
            Object tmp = target;
            target = source;
            source = tmp;
        }

        boolean inputOccupied = (getGraphModel().isFlowInput(source) && graphModel.isInputPortUsed(source, this))
                || (getGraphModel().isFlowInput(target) && graphModel.isInputPortUsed(target, this));
        if (!allowOutsToInMulticast() && inputOccupied) {
            return AppConstants.GRAPH_ERROR_INPUT_OCCUPIED;
        }

        boolean outputOccupied = (getGraphModel().isFlowOutput(source) && graphModel.isOutputPortUsed(source, this))
                || (getGraphModel().isFlowOutput(target) && graphModel.isOutputPortUsed(target, this));
        if (!allowOutToInsMulticast() && outputOccupied) {
            return AppConstants.GRAPH_ERROR_OUTPUT_OCCUPIED;
        }

        // Parents are used to retrieve incoming and outgoing edges.
        Object sourceParent;
        Object targetParent;

        // Global ports don't have a parent process => use themself to retrieve edges.
        if (getGraphModel().isGlobalPort(source)) {
            sourceParent = source;
        } else {
            sourceParent = graphModel.getParent(source);
        }
        if (getGraphModel().isGlobalPort(target)) {
            targetParent = target;
        } else {
            targetParent = graphModel.getParent(target);
        }

        // same port?
        boolean samePort = (source != null && target != null) && source.equals(target);
        if (samePort) {
            return "";
        }

        // same parent?
        boolean sameParent = (sourceParent != null && targetParent != null) && sourceParent.equals(targetParent);
        if (sameParent) {
            return AppConstants.GRAPH_ERROR_SINGLE_FEEDBACK;
        }

        // in-to-in or out-to-out?
        boolean inputToInput = graphModel.isFlowInput(source) && graphModel.isFlowInput(target);
        if (inputToInput) {
            return AppConstants.GRAPH_ERROR_IN_TO_IN;
        }
        boolean outputToOutput = graphModel.isFlowOutput(source) && graphModel.isFlowOutput(target);
        if (outputToOutput) {
            return AppConstants.GRAPH_ERROR_OUT_TO_OUT;
        }

        // desired target port already used?
//        boolean inputAlreadyUsed = false;
//        if (!allowOutsToInMulticast() && !inputToInput && !outputToOutput && null != graphEdge) {
//            Object[] targetIncomingEdges = mxGraphModel.getIncomingEdges(model, targetParent);
//            for (Object in : targetIncomingEdges) {
//                if (in instanceof GraphEdge) {
//                    GraphEdge inEdge = (GraphEdge) in;
//                    boolean sameTargetPortCell = inEdge.getTargetPortCell().equals(graphEdge.getTargetPortCell());
//                    boolean differentEdges = !inEdge.equals(graphEdge);
//                    if (sameTargetPortCell && differentEdges) {
//                        inputAlreadyUsed = true;
//                    }
//                }
//            }
//        }
//        if (inputAlreadyUsed) {
//            return "Port is already connected";
//        }
        // port types compatible?
        {
            Object v1 = graphModel.getValue(source);
            Object v2 = graphModel.getValue(target);
            if ((null != v1 && null != v2)
                    && (v1 instanceof ProcessPort && v2 instanceof ProcessPort)) {

                ProcessPort p1 = (ProcessPort) v1;
                ProcessPort p2 = (ProcessPort) v2;
                boolean compatible = graphModel.arePortTypesCompatible(p1, p2);
                if (!compatible) {
                    return AppConstants.GRAPH_ERROR_PORTTYPES_NOT_COMPATIBLE;
                }
            }
        }

        return super.getEdgeValidationError(edge, source, target);
    }

    /**
     * Returns the graph workflow layouter.
     *
     * @return
     */
    public GraphWorkflowLayout getGraphWorkflowLayout() {
        if (null == graphWorkflowLayout) {
            graphWorkflowLayout = new GraphWorkflowLayout(this);
        }

        return graphWorkflowLayout;
    }

    /**
     * Adjusts cell positions to improve the visible workflow.
     */
    public void layout() {
        getGraphModel().beginUpdate();
        getGraphWorkflowLayout().execute(null);
        getGraphModel().endUpdate();
    }

    /**
     * Uses the model to check if the cell is a process port.
     */
    @Override
    public boolean isPort(Object cell) {
        GraphModel model = getGraphModel();
        return (model.isFlowInput(cell) || model.isFlowOutput(cell)) && !model.isGlobalPort(cell);
    }

    /**
     * Implements a tooltip that shows the actual source and target of an edge.
     */
    @Override
    public String getToolTipForCell(Object cell) {
        Object value = model.getValue(cell);

        if (null != value) {
            if (value instanceof ProcessPort) {
                return ((ProcessPort) value).getToolTipText();
            } else if (value instanceof ProcessEntity) {
                return ((ProcessEntity) value).getToolTipText();
            }
        } else if (model.isEdge(cell)) {
            return convertValueToString(model.getTerminal(cell, true)) + " -> "
                    + convertValueToString(model.getTerminal(cell, false));
        }

        return super.getToolTipForCell(cell);
    }

    /**
     * Removes the folding icon and disables any folding´.
     */
    @Override
    public boolean isCellFoldable(Object cell, boolean collapse) {
        return false;
    }

    /**
     * Create temporary edge.
     */
    @Override
    public Object createEdge(Object parent, String id, Object value,
            Object source, Object target, String style) {

        mxCell edge = new GraphEdge(value, new mxGeometry(), style);
        edge.setId(id);
        edge.setEdge(true);
        edge.getGeometry().setRelative(true);

        return edge;
    }

    /**
     * Check and adjust added edges to meet requirements (edge direction, type
     * of connected ports etc.).
     */
    @Override
    public Object addCell(Object cell, Object parent, Integer index, Object source, Object target) {
        Object returnValue;

        GraphModel graphModel = getGraphModel();

        //Edge added?
        if (model.isEdge(cell)) {

            // check direction and reverse it if necessery (target must be an input port)
            if (graphModel.isFlowOutput(target)) {
                Object tmp = target;
                target = source;
                source = tmp;
            }

            returnValue = super.addCell(cell, parent, index, source, target);
            GraphEdge graphEdge = null;

            // set the edge's source and target port
            if (cell instanceof GraphEdge) {
                graphEdge = (GraphEdge) cell;
                graphEdge.setSourcePortCell((mxCell) source);
                graphEdge.setTargetPortCell((mxCell) target);
            }

            // Detect and remove cycle connections if necessary.
            if (!isAllowLoops() && mxGraphStructure.isCyclicDirected(getAnalysisGraph())) {
                graphModel.remove(cell);
                returnValue = null;
                AppEventService.getInstance().fireAppEvent("Connection loops are not allowed.", this);
            }

        } else {
            // Object is not an edge -> delegate to superclass
            returnValue = super.addCell(cell, parent, index, source, target);
        }

        if (graphModel.isProcess(cell) || graphModel.isEdge(cell)) {
            if (autoLayout) {
                layout();
            }
        }

        return returnValue;
    }

    /**
     * Only GraphModel instances are accepted.
     */
    @Override
    public void setModel(mxIGraphModel igm) {
        // Explicite cast to provoke an exception if the given model is not a GraphModel
        GraphModel gm = (GraphModel) igm;
        ag = null;

        super.setModel(igm);
    }

    /**
     * Returns the used GraphModel instance.
     */
    public GraphModel getGraphModel() {
        return (GraphModel) model;
    }

    /**
     * Provides the AnalysisGraph needed by the workflow-layout class.
     *
     * @return
     */
    public mxAnalysisGraph getAnalysisGraph() {
        if (null == ag) {
            ag = new mxAnalysisGraph();
            Map<String, Object> properties = ag.getProperties();
            properties.put(mxGraphProperties.DIRECTED, "1");
            ag.setGraph(this);
        }

        return ag;
    }

    public boolean isAutoLayout() {
        return autoLayout;
    }

    /**
     * Returns true if an output can be connected to multiple inputs.
     *
     * @return
     */
    boolean allowOutToInsMulticast() {
        return AppConstants.GRAPH_ALLOW_OUT_TO_INS;
    }

    /**
     * Returns true if multiple outputs can be connected to the same input.
     *
     * @return
     */
    boolean allowOutsToInMulticast() {
        return AppConstants.GRAPH_ALLOW_OUTS_TO_IN;
    }

    /**
     * Returns true if a process output can be connected to an input of the same
     * process.
     *
     * @return
     */
    boolean allowSingleFeedbackLoop() {
        return AppConstants.GRAPH_ALLOW_SINGLE_FEEDBACK_LOOPS;
    }

    public Graph clone() throws CloneNotSupportedException {
        Graph clone = new Graph();
        clone.ag = null; // force lazy init
        clone.model = getGraphModel().clone();
        GraphSetup.setup(clone);

        for (Object cell : clone.getChildCells(clone.getDefaultParent())) {
            Object cellValue = clone.model.getValue(cell);
            if (cellValue instanceof ProcessEntity) {
                ProcessEntity value = (ProcessEntity) cellValue;
                clone.model.setValue(cell, value.clone());

            } else if (cellValue instanceof ProcessPort) {
                ProcessPort value = (ProcessPort) cellValue;
                clone.model.setValue(cell, value.clone());
            }
        }

        return clone;
    }

    /**
     * Returns a list containing only the cells which represent a ProcessEntity.
     *
     * @param cells
     * @return
     */
    private List<ProcessEntity> filterProcessEntities(Object[] cells) {
        List<ProcessEntity> processes = new LinkedList<>();
        for (Object cell : cells) {
            Object cellValue = getModel().getValue(cell);
            if (cellValue != null && cellValue instanceof ProcessEntity) {
                processes.add((ProcessEntity) cellValue);
            }
        }

        return processes;
    }

    private List<mxCell> filterProcessCells(Object[] cells) {
        List<mxCell> processCells = new LinkedList<>();
        for (Object cell : cells) {
            Object cellValue = getModel().getValue(cell);
            if (cell instanceof mxCell && cellValue != null && cellValue instanceof ProcessEntity) {
                processCells.add((mxCell) cell);
            }
        }

        return processCells;
    }

    /**
     * Returns currently selected process entities.
     *
     * @return selected process entities.
     */
    public List<ProcessEntity> getSelectedProcesses() {
        Object[] cells = getSelectionCells();
        return filterProcessEntities(cells);
    }

    /**
     * Returns all process entities which are used by the graph.
     *
     * @return selected process entities.
     */
    public List<ProcessEntity> getProcesses() {
        Object[] cells = getChildCells(getDefaultParent());
        return filterProcessEntities(cells);
    }

    public List<mxCell> getProcessCells() {
        Object[] cells = getChildCells(getDefaultParent());
        return filterProcessCells(cells);
    }

    /**
     * Returns currently selected global ports.
     *
     * @return selected global ports.
     */
    public List<ProcessPort> getSelectedGlobalPorts() {
        Object[] cells = getSelectionCells();
        return filterGlobalPorts(cells);
    }

    private List<ProcessPort> filterGlobalPorts(Object[] cells) {
        List<ProcessPort> ports = new LinkedList<>();
        GraphModel model = getGraphModel();
        for (Object cell : cells) {
            if (model.isGlobalPort(cell)) {
                ports.add((ProcessPort) model.getValue(cell));
            }
        }

        return ports;
    }

    private List<Object> filterGlobalPortCells(Object[] cells) {
        List<Object> portCells = new LinkedList<>();
        GraphModel model = getGraphModel();
        for (Object cell : cells) {
            if (model.isGlobalPort(cell)) {
                portCells.add(cell);
            }
        }

        return portCells;
    }

    /**
     * Returns all global input and output ports which are used by the graph.
     *
     * @return
     */
    public List<ProcessPort> getGlobalPorts() {
        Object[] cells = getChildCells(getDefaultParent());
        return filterGlobalPorts(cells);
    }

    public List<Object> getGlobalPortCells() {
        Object[] cells = getChildCells(getDefaultParent());
        return filterGlobalPortCells(cells);
    }

    /**
     * Returns all global input ports which are used by the graph.
     *
     * @return
     */
    public List<ProcessPort> getGlobalInputPorts() {
        List<ProcessPort> inputs = new LinkedList<>();
        Object[] cells = getChildCells(getDefaultParent());
        for (Object cell : cells) {
            if (getGraphModel().isGlobalInputPort(cell)) {
                inputs.add((ProcessPort) getGraphModel().getValue(cell));
            }
        }

        return inputs;
    }

    public List<Object> getGlobalInputPortCells() {
        List<Object> inputs = new LinkedList<>();
        Object[] cells = getChildCells(getDefaultParent());
        for (Object cell : cells) {
            if (getGraphModel().isGlobalInputPort(cell)) {
                inputs.add(cell);
            }
        }

        return inputs;
    }

    public List<Object> getGlobalOutputPortCells() {
        List<Object> outputs = new LinkedList<>();
        Object[] cells = getChildCells(getDefaultParent());
        for (Object cell : cells) {
            if (getGraphModel().isGlobalOutputPort(cell)) {
                outputs.add(cell);
            }
        }

        return outputs;
    }

    /**
     * Returns all global output ports which are used by the graph.
     *
     * @return
     */
    public List<ProcessPort> getGlobalOutputPorts() {
        List<ProcessPort> outputs = new LinkedList<>();
        Object[] cells = getChildCells(getDefaultParent());
        for (Object cell : cells) {
            if (getGraphModel().isGlobalOutputPort(cell)) {
                outputs.add((ProcessPort) getGraphModel().getValue(cell));
            }
        }

        return outputs;
    }

    /**
     * Returns the input and output ports of all processes which are used by the
     * graph.
     *
     * @return
     */
    public List<ProcessPort> getAllProcessPorts() {
        List<ProcessPort> allProcessPorts = new LinkedList<>();

        Object[] rootCells = getChildCells(getDefaultParent());
        for (ProcessEntity process : filterProcessEntities(rootCells)) {
            allProcessPorts.addAll(process.getInputPorts());
            allProcessPorts.addAll(process.getOutputPorts());
        }

        return allProcessPorts;
    }

    public List<ProcessPort> getAllFlowOutputPorts() {
        List<ProcessPort> outputs = new LinkedList<>();

        Object[] cells = getChildCells(getDefaultParent());
        for (Object cell : cells) {
            Object value = getGraphModel().getValue(cell);
            if (value instanceof ProcessPort) {
                ProcessPort port = (ProcessPort) value;
                if (port.isFlowOutput()) {
                    outputs.add(port);
                }
            } else if (value instanceof ProcessEntity) {
                ProcessEntity process = (ProcessEntity) value;
                for (ProcessPort port : process.getOutputPorts()) {
                    outputs.add(port);
                }
            }
        }

        return outputs;
    }

    public static double getAbsoluteCellX(mxCell cell) {
        double x = 0;
        mxGeometry geom = cell.getGeometry();
        if (geom.isRelative()) {
            x = geom.getOffset().getX();
            x += cell.getParent().getGeometry().getX();
        } else {
            x = geom.getX();
        }

        return x;
    }

    public int getGraphComponentsCount() {
        return mxGraphStructure.getGraphComponents(ag).length;
    }

}
