package de.hsos.richwps.mb.graphView.mxGraph.layout;

import com.mxgraph.model.mxCell;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import java.util.Comparator;

public class CellXComparator implements Comparator<Object> {

    @Override
    public int compare(Object cell1, Object cell2) {
        if (!(cell1 instanceof mxCell) || !(cell2 instanceof mxCell)) {
            return 0;
        }

        mxCell mxCell1 = (mxCell) cell1;
        mxCell mxCell2 = (mxCell) cell2;

        double c1x = Graph.getAbsoluteCellX(mxCell1);
        double c2x = Graph.getAbsoluteCellX(mxCell2);
        double diff = (c1x - c2x);

        // better check value explicit instead of just casting to int
        if (diff < 0) {
            return -1;
        } else if (diff == 0) {
            return 0;
        } else {
            return 1;
        }
    }

}