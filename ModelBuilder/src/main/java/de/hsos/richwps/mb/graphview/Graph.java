/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author dziegenh
 * Based on mxGraph example "ports"
 */
public class Graph extends mxGraph {

    public Graph() {
        getModel().addListener(null, new mxIEventListener() {

            // TODO find better way to auto-layout and to react on model changes
            public void invoke(Object o, mxEventObject eo) {
                if(!(o instanceof mxGraphModel) || !o.equals(getModel()))
                    return;

                if(eo.getName().equals("undo")) {
                    layout();
                }
            }
        });
    }

    // TODO find well-working layout
    public void layout() {
//        mxCompactTreeLayout layout1 = new mxCompactTreeLayout(this);      //0
//        mxFastOrganicLayout layout =  new mxFastOrganicLayout(this);     //0
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(this);    //0
//        mxParallelEdgeLayout layout = new mxParallelEdgeLayout(this);    //0
//        mxStackLayout layout = new mxStackLayout(this);                  //0
//        mxPartitionLayout layout = new mxPartitionLayout(this);          //0
//        mxEdgeLabelLayout layout = new mxEdgeLabelLayout(this);          //0
        mxOrganicLayout layout = new mxOrganicLayout(this);              //1

        layout.execute(getDefaultParent());
    }

    // Ports are not used as terminals for edges, they are
    // only used to compute the graphical connection point
    @Override
    public boolean isPort(Object cell) {
        mxGeometry geo = getCellGeometry(cell);

        return (geo != null) ? geo.isRelative() : false;
    }

    // Implements a tooltip that shows the actual
    // source and target of an edge
    @Override
    public String getToolTipForCell(Object cell) {
        if (model.isEdge(cell)) {
            return convertValueToString(model.getTerminal(cell, true)) + " -> "
                    + convertValueToString(model.getTerminal(cell, false));
        }

        return super.getToolTipForCell(cell);
    }

    // Removes the folding icon and disables any folding
    @Override
    public boolean isCellFoldable(Object cell, boolean collapse) {
        return false;
    }



}
