/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphProperties;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import de.hsos.richwps.mb.graphview.layout.GraphWorkflowLayout;
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
    private mxCell tempCreatedEdge;
    private mxGraphComponent graphComponent;

    public Graph() {
        super(new GraphModel());
    }

    public Graph(mxIGraphModel igm) {
        super((GraphModel) igm);
    }

    public Graph(mxStylesheet m) {
        super(new GraphModel(), m);
    }

    public Graph(mxIGraphModel igm, mxStylesheet m) {
        super((GraphModel) igm, m);
    }

    @Override
    public mxRectangle graphModelChanged(mxIGraphModel igm, List<mxUndoableEdit.mxUndoableChange> list) {
        mxRectangle ret = super.graphModelChanged(igm, list);
        layout();
        return ret;
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
    public String getEdgeValidationError(Object o, Object o1, Object o2) {
        return super.getEdgeValidationError(o, o1, o2); //To change body of generated methods, choose Tools | Templates.
    }

    GraphWorkflowLayout getGraphWorkflowLayout() {
        if (null == graphWorkflowLayout) {
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

//        fireEvent(new mxEventObject(mxEvent.REPAINT));
        // TODO replace with eventlistener - graph should not know graphcomponent!
        graphComponent.refresh();
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
        if (model.isEdge(cell)) {
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
    public Object createEdge(Object o, String string, Object o1, Object o2, Object o3, String string1) {
        tempCreatedEdge = (mxCell) super.createEdge(o, string, o1, o2, o3, string1);
        return tempCreatedEdge;
    }

    /**
     * Check and adjust added edges to meet requirements (edge direction, type
     * of connected ports etc.).
     */
    @Override
    public Object addCell(Object cell, Object o1, Integer intgr, Object source, Object target) {
        Object returnValue;// = super.addCell(cell, o1, intgr, source, target);

        GraphModel graphModel = getGraphModel();

        //Edge added?
        if (model.isEdge(cell)) {

            Object sourceParent = graphModel.getParent(source);
            Object targetParent = graphModel.getParent(target);

            boolean sameParent = (sourceParent != null && targetParent != null) && sourceParent.equals(targetParent);
            boolean inputToInput = graphModel.isInput(source) && graphModel.isInput(target);
            boolean outputToOutput = graphModel.isOutput(source) && graphModel.isOutput(target);

            // TODO this doesn't work !!!
            boolean inputAlreadyUsed = false; //mxGraphModel.getIncomingEdges(model, targetParent).length > 0;

            // Disable connections from input to output of the same process.
            // Disable connections In<->In and Out<->Out.
            if (sameParent || inputToInput || outputToOutput || inputAlreadyUsed) {
                super.addCell(cell);
                graphModel.remove(cell);
                returnValue = null;

            } else {
                // check direction and reverse it if necessery
                if (graphModel.isOutput(target)) {
                    Object tmp = target;
                    target = source;
                    source = tmp;
                }

                // user superclass to add cell
                returnValue = super.addCell(cell, o1, intgr, source, target);
            }

            // Detect and remove cycle connections if necessary.
            if (mxGraphStructure.isCyclicDirected(getAnalysisGraph())) {
                graphModel.remove(cell);
                returnValue = null;
            }

        } else {
            // Object is not an edge -> delegate to superclass
            returnValue = super.addCell(cell, o1, intgr, source, target);
        }

        // TODO layout doesn't always update ?!!
        if (graphModel.isProcess(cell) || graphModel.isEdge(cell)) {
            layout();
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

        super.setModel(igm);
    }

    /**
     * Returns the used GraphModel instance.
     */
    public GraphModel getGraphModel() {
        return (GraphModel) model;
    }

    /**
     * Provides the AnalysisGraph needed by the layout class.
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
    void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }
}
