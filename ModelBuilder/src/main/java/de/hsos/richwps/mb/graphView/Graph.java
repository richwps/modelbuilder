/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphProperties;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.layout.GraphWorkflowLayout;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
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
    // TODO this method is used in combination with the red/green cell markers -> check if it can be used to indicate our connection rules.
    // be careful: method is also called while connecting cells => don't do complex calculations here
    public boolean isValidSource(Object o) {
        // a used input port is not a valid source
//        if(getGraphModel().isInput(o) && getGraphModel().getEdgeCount(o) > 0)
//            return false;

        return super.isValidSource(o);
    }

    @Override
    // TODO this method is used in combination with the red/green cell markers -> check if it can be used to indicate our connection rules.
    public String getEdgeValidationError(Object edge, Object source, Object target) {

        GraphModel graphModel = getGraphModel();
        // check direction and reverse it if necessery (target must be an input port)
        if (graphModel.isFlowOutput(target)) {
            Object tmp = target;
            target = source;
            source = tmp;
        }

        boolean inputOccupied = (getGraphModel().isFlowInput(source) && isInputPortUsed(source))
                || (getGraphModel().isFlowInput(target) && isInputPortUsed(target));
        if (!allowOutsToInMulticast() && inputOccupied) {
            return AppConstants.GRAPH_ERROR_INPUT_OCCUPIED;
        }

        boolean outputOccupied = (getGraphModel().isFlowOutput(source) && isOutputPortUsed(source))
                || (getGraphModel().isFlowOutput(target) && isOutputPortUsed(target));
        if (!allowOutToInsMulticast() && outputOccupied) {
            return AppConstants.GRAPH_ERROR_OUTPUT_OCCUPIED;
        }

        GraphEdge graphEdge = null;

        if (edge instanceof GraphEdge) {
            graphEdge = (GraphEdge) edge;
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

    GraphWorkflowLayout getGraphWorkflowLayout() {
        if (null == graphWorkflowLayout) {
            // TODO move magic numbers to config/constants
            graphWorkflowLayout = new GraphWorkflowLayout(this);
            graphWorkflowLayout.setCellGap(10);
            graphWorkflowLayout.setGraphComponentGap(50);
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

        // TODO check if refresh is still necessary.
        // TODO if so: replace with eventlistener - graph should not know graphcomponent!
//        graphComponent.refresh();
    }

    /**
     * Do not use this method, it has side effects if a cell is intepreted as a
     * port. Use GraphModel.isInput() and GraphModel.isOutput() instead.
     */
    @Override
    @Deprecated
    public boolean isPort(Object cell) {
        mxGeometry geo = getCellGeometry(cell);

        return (geo != null) ? geo.isRelative() : false;

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

    boolean isInputPortUsed(Object o) {
        Object parent;
        if (getGraphModel().isGlobalPort(o)) {
            parent = o;
        } else {
            parent = getModel().getParent(o);
        }
        Object[] targetIncomingEdges = mxGraphModel.getIncomingEdges(model, parent);
        for (Object in : targetIncomingEdges) {
            if (in instanceof GraphEdge) {
                GraphEdge inEdge = (GraphEdge) in;
                mxCell targetCell = inEdge.getTargetPortCell();
                if (null != targetCell && targetCell.equals(o)) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean isOutputPortUsed(Object o) {
        Object parent;
        if (getGraphModel().isGlobalPort(o)) {
            parent = o;
        } else {
            parent = getModel().getParent(o);
        }
        Object[] sourceOutgoingEdges = mxGraphModel.getOutgoingEdges(model, parent);
        for (Object out : sourceOutgoingEdges) {
            if (out instanceof GraphEdge) {
                GraphEdge outEdge = (GraphEdge) out;
                mxCell sourcePort = outEdge.getSourcePortCell();
                if (null != sourcePort && sourcePort.equals(o)) {
                    return true;
                }
            }
        }

        return false;
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

//                AppEventService.getInstance().fireAppEvent("Reversed direction of connection", this, "editor");
            }

//            String error = getEdgeValidationError(cell, source, target);
//            if (null != error) {
//                // TODO check if add/remove is still necessary (for auto-layout)
////                super.addCell(cell);
////                graphModel.remove(cell);
//                returnValue = null;
//                AppEventService.getInstance().fireAppEvent(error, this);
//
//            } else {
            returnValue = super.addCell(cell, parent, index, source, target);
            GraphEdge graphEdge = null;

            // set the edge's source and target port
            if (cell instanceof GraphEdge) {
                graphEdge = (GraphEdge) cell;
                graphEdge.setSourcePortCell((mxCell) source);
                graphEdge.setTargetPortCell((mxCell) target);
            }
//            }

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

    // TODO replace with eventlistener - graph should not know graphcomponent!
//    void setGraphComponent(mxGraphComponent graphComponent) {
//        this.graphComponent = graphComponent;
//    }
    boolean isAutoLayout() {
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

}
