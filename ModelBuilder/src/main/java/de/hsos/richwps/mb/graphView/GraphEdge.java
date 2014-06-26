/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.graphView;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 *
 * @author dziegenh
 */
public class GraphEdge extends mxCell {

    private mxCell sourcePortCell;
    private mxCell targetPortCell;

    public GraphEdge(Object value, mxGeometry mxGeometry, String style) {
        super(value, mxGeometry, style);
    }

    public GraphEdge() {
        super(null, null, null);
    }

    public mxCell getSourcePortCell() {
        return sourcePortCell;
    }

    public mxCell getTargetPortCell() {
        return targetPortCell;
    }

    public void setSourcePortCell(mxCell sourcePortCell) {
        this.sourcePortCell = sourcePortCell;
    }

    public void setTargetPortCell(mxCell targetPortCell) {
        this.targetPortCell = targetPortCell;
    }

}
