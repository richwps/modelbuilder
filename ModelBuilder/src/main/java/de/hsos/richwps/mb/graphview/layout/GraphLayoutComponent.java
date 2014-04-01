/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview.layout;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import java.util.HashMap;
import java.util.LinkedList;

class CellInfo {

    int maxDownEdges = 0;
    int maxUpEdges = 0;

    void updateMaxDownIfPossible(int value) {
        maxDownEdges = Math.max(maxDownEdges, value);
    }

    void updateMaxUpIfPossible(int value) {
        maxUpEdges = Math.max(maxUpEdges, value);
    }

    @Override
    public String toString() {
        return maxUpEdges + " " + maxDownEdges;
    }

}

/**
 *
 * @author dziegenh
 */
class GraphLayoutComponent {

    private HashMap<Object, CellInfo> cellInfo;

    private boolean useMax = false;

    /**
     * Max number of connected edges concidering their direction.
     */
    private int longestPathLength = 0;

    GraphLayoutComponent(Object[] gc) {
        cellInfo = new HashMap<Object, CellInfo>(gc.length);
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
        LinkedList<Object> cells = new LinkedList<Object>();

        // TODO threshold for level decision (maxUp or maxDown) necessary?
//        double threshold = longestPathLength / 2;
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
        // TODO calculate or get bounding box width
        //mock:
        int maxWidth = 0;

        for (int l = 0; l <= longestPathLength; l++) {
            maxWidth = Math.max(getLevelWidth(model, l), maxWidth);
        }

        return maxWidth;

//        int maxLevelCells = 0;
//        for(int i=0; i<=longestPathLength; i++) {
//        }
//                maxLevelCells = Math.max(maxLevelCells, getNumCellsAtLevel(i, useMax));
//        return 150 * maxLevelCells + 50;
    }

    int getCellLevel(CellInfo cellInfo) {
        if (useMax) {
            return Math.max(longestPathLength - cellInfo.maxDownEdges, cellInfo.maxUpEdges);
        } else {
            return Math.min(longestPathLength - cellInfo.maxDownEdges, cellInfo.maxUpEdges);
        }
    }

    /**
     * Returns the number of cells of the level with the most cells
     *
     * @return
     */
//    int getMaxCellsPerLevel() {
//        // TODO !!
//        int maxCells = 0;
//        for(int l=0; l<=longestPathLength; l++)
//            maxCells = Math.max(maxCells, getNumCellsAtLevel(l));
//
//        return maxCells;
//    }
    int getCellLevel(Object cell) {
        return getCellLevel(cell);
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

}
