package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

/**
 * Extension of JGraphX edges (cells) to identify the edges source/target ports
 * (the super class only knows the connected process cells, not the ports).
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

    @Override
    public String toString() {
        return sourcePortCell + " - " + targetPortCell;
    }

}
