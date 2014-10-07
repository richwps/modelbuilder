package de.hsos.richwps.mb.graphView.mxGraph.layout;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Stores temporary information about a cell's logical position inside the graph
 * for layouting.
 *
 * @author dziegenh
 */
class CellInfo {

    int xPos = -1;

    int maxDownEdges = 0;
    int maxUpEdges = 0;

    void updateMaxDownIfPossible(int value) {
        maxDownEdges = Math.max(maxDownEdges, value);
    }

    void updateMaxUpIfPossible(int value) {
        maxUpEdges = Math.max(maxUpEdges, value);
    }

    /**
     * Sets the X-Position only if it has not already been set.
     *
     * @param x
     * @return
     */
    boolean updateXPosIfPossible(int x) {
        if (-1 == xPos) {
            xPos = x;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "" + xPos;
//        return maxUpEdges + " " + maxDownEdges + ", x: " + xPos;
    }

}

/**
 *
 * @author dziegenh
 */
class GraphLayoutComponent {

    private final HashMap<Object, CellInfo> cellInfo;

    private boolean useMax = false;

    /**
     * Max number of connected edges concidering their direction.
     */
    private int longestPathLength = 0;

    GraphLayoutComponent(Object[] gc) {
        cellInfo = new HashMap<>(gc.length);
        for (Object cell : gc) {
            cellInfo.put(cell, new CellInfo());
        }
    }

    public void setUseMax(boolean useMax) {
        this.useMax = useMax;
    }

    CellInfo getCellInfo(Object cell) {
        return cellInfo.get(cell);
    }

    public void setLongestPathLength(int value) {
        this.longestPathLength = value;
    }

    public int getLongestPathLength() {
        return longestPathLength;
    }

    Object[] getCellsAtLevel(int level) {
        LinkedList<Object> cells = new LinkedList<>();

        for (Object curCell : cellInfo.keySet()) {
            CellInfo curCellInfo = getCellInfo(curCell);
            int curLevel = getCellLevel(curCellInfo);
            if (curLevel == level) {
                cells.add(curCell);
            }
        }

        return cells.toArray();
    }

    int getWidestLevel(mxGraphModel model) {
        int maxWidth = 0;
        int level = 0;

        int curWidth = 0;
        for (int l = 0; l <= longestPathLength; l++) {
            curWidth = getLevelWidth(model, l);
            if (curWidth > maxWidth) {
                maxWidth = curWidth;
                level = l;
            }
        }

        return level;
    }

    int getWidth(mxGraphModel model) {
        int maxWidth = 0;

        for (int l = 0; l <= longestPathLength; l++) {
            maxWidth = Math.max(getLevelWidth(model, l), maxWidth);
        }

        return maxWidth;
    }

    int getCellLevel(CellInfo cellInfo) {
        if (useMax) {
            return Math.max(longestPathLength - cellInfo.maxDownEdges, cellInfo.maxUpEdges);
        } else {
            return Math.min(longestPathLength - cellInfo.maxDownEdges, cellInfo.maxUpEdges);
        }
    }

    int getCellLevel(Object cell) {
        return getCellLevel(getCellInfo(cell));
    }

    int getNumCellsAtLevel(int level) {
        return getCellsAtLevel(level).length;
    }

    int getLevelWidth(mxGraphModel model, int level) {
        int curWidth = 0;
        Object[] cellsAtLevel = getCellsAtLevel(level);
        for (Object cell : cellsAtLevel) {
            mxGeometry geom = model.getGeometry(cell);
            curWidth += geom.getWidth();
        }

        return curWidth;
    }

    void updateXPositions(GraphModel model) {

        // step 1: sort output ports
        // step 2: sort ports
        Object[] cells = getCellsAtLevel(longestPathLength);
        resetLevelXPos();

//        Arrays.sort(cells, new CellXComparator());
        EdgeTargetXComparator edgeTargetComparator = new EdgeTargetXComparator(model);
        EdgeSourceXComparator edgeSourceComparator = new EdgeSourceXComparator(model);

        int xPos = 0;
        for (Object cell : cells) {

            // TODO do this at ALL levels!! ( => move to updateIncomingEdges() )
            if (model.isGlobalOutputPort(cell)) {
                Object[] inEdges = mxGraphModel.getIncomingEdges(model, cell);
                for (Object inEdge : inEdges) {
                    if (inEdge instanceof GraphEdge) {
//                        GraphEdge gInEdge =  (GraphEdge) inEdge;
                        mxCell edgeCell = (mxCell) inEdge;

//                        updateEdge(model, edgeCell);
                        if (model.isProcess(edgeCell.getSource())) {
                            mxCell inEdgeSrc = (mxCell) edgeCell.getSource();
                            Object[] inEdgeSrcOutgoingEdges = mxGraphModel.getOutgoingEdges(model, inEdgeSrc);
                            Arrays.sort(inEdgeSrcOutgoingEdges, edgeSourceComparator);
                            for (Object edge : inEdgeSrcOutgoingEdges) {
                                CellInfo cellInfo1 = getCellInfo(((mxCell) edge).getTarget());
                                if (null != cellInfo1) {
                                    int level = getCellLevel(cellInfo1);
                                    if (cellInfo1.updateXPosIfPossible(curLevelXPos[level])) {
                                        curLevelXPos[level] += 1;
                                    }
                                }
                            }

                        }
                    }
                }
            }
            cellInfo.get(cell).updateXPosIfPossible(xPos++);
//            if (cellInfo.get(cell).updateXPosIfPossible(xPos++)) {
            updateIncomingEdges(model, edgeTargetComparator, cell);
//            }
        }
    }

    /**
     * Resets the edge's geometry.
     *
     * @param model
     * @param edge
     */
    void updateEdge(GraphModel model, Object edge) {
        if (!model.isEdge(edge)) {
            return;
        }

        ((mxCell) edge).getGeometry().setPoints(null);
    }

    /**
     * Recursively update source cells of incoming edges.
     *
     * @param model
     * @param edgeComparator
     * @param cell
     */
    void updateIncomingEdges(GraphModel model, EdgeTargetXComparator edgeComparator, Object cell) {
        Object[] edges = mxGraphModel.getIncomingEdges(model, cell);
        Arrays.sort(edges, edgeComparator);

        for (Object edge : edges) {
            if (model.isEdge(edge)) {

                updateEdge(model, edge);

                mxICell source = ((mxCell) edge).getSource();
                if (cellInfo.containsKey(source)) {
                    CellInfo curCellInfo = cellInfo.get(source);
                    int cellLevel = getCellLevel(curCellInfo);
                    if (curCellInfo.updateXPosIfPossible(curLevelXPos[cellLevel])) {
                        curLevelXPos[cellLevel] += 1;
                    }
                }
                updateIncomingEdges(model, edgeComparator, source);
            }
        }
    }

    int[] curLevelXPos;

    void resetLevelXPos() {
        curLevelXPos = new int[longestPathLength + 1];

        for (int i = 0; i < longestPathLength; i++) {
            curLevelXPos[i] = 0;
        }
    }

}

class EdgeTargetXComparator implements Comparator<Object> {

    mxGraphModel model;
    CellXComparator xComparator = new CellXComparator();

    public EdgeTargetXComparator(mxGraphModel model) {
        this.model = model;
    }

    @Override
    public int compare(Object edge1, Object edge2) {
        if (!(edge1 instanceof GraphEdge) || !(edge2 instanceof GraphEdge)) {
            return 0;
        }

        GraphEdge gEdge1 = (GraphEdge) edge1;
        GraphEdge gEdge2 = (GraphEdge) edge2;

        mxCell edge1Target = gEdge1.getTargetPortCell();
        mxCell edge2Target = gEdge2.getTargetPortCell();

        return xComparator.compare(edge1Target, edge2Target);
    }

}

class EdgeSourceXComparator implements Comparator<Object> {

    mxGraphModel model;
    CellXComparator xComparator = new CellXComparator();

    public EdgeSourceXComparator(mxGraphModel model) {
        this.model = model;
    }

    @Override
    public int compare(Object edge1, Object edge2) {
        if (!(edge1 instanceof GraphEdge) || !(edge2 instanceof GraphEdge)) {
            return 0;
        }

        GraphEdge gEdge1 = (GraphEdge) edge1;
        GraphEdge gEdge2 = (GraphEdge) edge2;

        mxCell edge1Source = gEdge1.getSourcePortCell();
        mxCell edge2Source = gEdge2.getSourcePortCell();

        return xComparator.compare(edge1Source, edge2Source);
    }

}

class CellXComparator implements Comparator<Object> {

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

class CellInfoXPosComparator implements Comparator<Object> {

    GraphLayoutComponent component;

    public CellInfoXPosComparator(GraphLayoutComponent component) {
        this.component = component;
    }

    @Override
    public int compare(Object cell1, Object cell2) {
        CellInfo cell1Info = component.getCellInfo(cell1);
        CellInfo cell2Info = component.getCellInfo(cell2);

        if (null == cell1Info || null == cell2Info) {
            return 0;
        }

        return cell1Info.xPos - cell2Info.xPos;
    }

}
