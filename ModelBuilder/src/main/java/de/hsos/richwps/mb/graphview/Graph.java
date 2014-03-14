/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * Extended version of the (JGraphX) mxGraph. Needs a GraphModel as model
 * instance.
 *
 * @author dziegenh Based on mxGraph example "ports"
 */
class Graph extends mxGraph {

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

    /**
     * Adjusts cell positions to improve the visible workflow.
     */
    public void layout() {

        // TODO find well-working layout or create custom layout
//        mxCompactTreeLayout layout = new mxCompactTreeLayout(this);      //0
//        mxFastOrganicLayout layout =  new mxFastOrganicLayout(this);     //0
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(this);    //0
//        mxParallelEdgeLayout layout = new mxParallelEdgeLayout(this);    //0
//        mxStackLayout layout = new mxStackLayout(this);                  //0
//        mxPartitionLayout layout = new mxPartitionLayout(this);          //0
//        mxEdgeLabelLayout layout = new mxEdgeLabelLayout(this);          //0
        mxOrganicLayout layout = new mxOrganicLayout(this);              //1

        layout.execute(((mxCell) getModel().getRoot()).getChildAt(0));

        // TODO adjust zoom and center view
    }

    /**
     * Ports are not used as terminals for edges, they are only used to compute
     * the graphical connection point.
     */
    @Override
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
        return super.createEdge(o, string, o1, o2, o3, string1); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Check and adjust added edges to meet requirements (edge direction, type
     * of connected ports etc.).
     */
    @Override
    public Object addCell(Object cell, Object o1, Integer intgr, Object source, Object target) {
        Object returnValue;

        //Edge added?
        if (model.isEdge(cell)) {

            GraphModel graphModel = getGraphModel();
            Object o2Parent = graphModel.getParent(source);
            Object o3Parent = graphModel.getParent(target);

            boolean sameParent = (o2Parent != null && o3Parent != null) && o2Parent.equals(o3Parent);
            boolean inputToInput = graphModel.isInput(source) && graphModel.isInput(target);
            boolean outputToOutput = graphModel.isOutput(source) && graphModel.isOutput(target);

            // Disable connections from input to output of the same process.
            // Disable connections In<->In and Out<->Out.
            if (sameParent || inputToInput || outputToOutput) {
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

                returnValue = super.addCell(cell, o1, intgr, source, target);
            }

            // Object is not an edge
        } else {
            returnValue = super.addCell(cell, o1, intgr, source, target);
        }

        layout();
        getView().invalidate();
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
    GraphModel getGraphModel() {
        return (GraphModel) model;
    }

}
