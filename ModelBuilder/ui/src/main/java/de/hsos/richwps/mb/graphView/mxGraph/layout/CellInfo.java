package de.hsos.richwps.mb.graphView.mxGraph.layout;
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
    }

}
