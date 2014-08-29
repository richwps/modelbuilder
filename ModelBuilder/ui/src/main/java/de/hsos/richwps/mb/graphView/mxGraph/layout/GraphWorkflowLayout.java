/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph.layout;

import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphEdge;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import java.util.Arrays;

/**
 * Only Graph instances are accepted.
 *
 * @author dziegenh
 */
public class GraphWorkflowLayout extends mxGraphLayout {

    private mxAnalysisGraph ag;
    private Graph g;

    private int cellGap = 0;
    private int graphComponentGap = 0;

    /**
     * Only Graph instances are accapted.
     */
    public GraphWorkflowLayout(mxGraph mxgrph) {
        super((Graph) mxgrph);

        g = (Graph) mxgrph;
        ag = g.getAnalysisGraph();
    }

    /**
     * Only Graph instances are accapted.
     */
    public void setGraph(mxGraph graph) {
        this.graph = (Graph) graph;
    }

    @Override
    public void execute(Object root) {

        // A graph component contains connected cells. Graph components are not connected to each other.
        Object[][] graphComponents = mxGraphStructure.getGraphComponents(ag);
        Object[] sinkVertices = {};
        Object[] sourceVertices = {};
        GraphModel model = g.getGraphModel();

        // Abort if there's nothing to lay out
        if (null == graphComponents) {
            return;
        }

        // Abort if there is any structural error.
        try {
            sinkVertices = mxGraphStructure.getSinkVertices(ag);
            sourceVertices = mxGraphStructure.getSourceVertices(ag);
        } catch (StructuralException ex) {
            return;
        }

        int renderX = 0, renderY = 0;

        // layout all sub-trees
        model.beginUpdate();
        for (Object[] graphComponent : graphComponents) {
            GraphLayoutComponent layoutComponent = new GraphLayoutComponent(graphComponent);
            layoutComponent.setUseMax(true);

            int maxDown = 0;
            for (Object source : sourceVertices) {
                // ignore current source if it's part of another GraphComponent
                if (!Arrays.asList(graphComponent).contains(source)) {
                    continue;
                }
                maxDown = Math.max(maxDown, goDownAndCountEdges(layoutComponent, source));
            }

            int maxUp = 0;
            for (Object sink : sinkVertices) {
                // ignore current source if it's part of another GraphComponent
                if (!Arrays.asList(graphComponent).contains(sink)) {
                    continue;
                }
                maxUp = Math.max(maxUp, goUpAndCountEdges(layoutComponent, sink));
            }

            layoutComponent.setLongestPathLength(maxUp);
            int gcWidth = layoutComponent.getWidth(model);
            gcWidth += cellGap * layoutComponent.getNumCellsAtLevel(layoutComponent.getWidestLevel(model));

            // the layout levels are computed, now compute the cells's x-position on each level.
            layoutComponent.updateXPositions(model);
            CellInfoXPosComparator cellInfoXPosComparator = new CellInfoXPosComparator(layoutComponent);

            // layout each level of current graphComponent
            int levelY = cellGap;
            for (int level = 0; level <= layoutComponent.getLongestPathLength(); level++) {

                Object[] levelCells = layoutComponent.getCellsAtLevel(level);

                Arrays.sort(levelCells, cellInfoXPosComparator);

                int numLevelCells = levelCells.length;
                int levelWidth = layoutComponent.getLevelWidth(model, level);
                levelWidth += cellGap * numLevelCells;
                int levelX = cellGap + (int) (.5 * (gcWidth - levelWidth));

                for (Object cell : levelCells) {

                    mxGeometry geom = model.getGeometry(cell);
                    geom = (mxGeometry) geom.clone();
                    geom.setX(renderX + levelX);

                    geom.setY(levelY);
                    levelX += geom.getWidth() + cellGap;

                    model.setGeometry(cell, geom);

                    // TODO dev!!
//                    model.setValue(cell, layoutComponent.getCellInfo(cell).toString());
                }

                if (levelCells.length > 0) {
                    levelY += 150;  // TODO MOCK! calculate real height + gap
                }
            }

            renderX += gcWidth + graphComponentGap;

            layoutGlobaPorts(model, layoutComponent);
        }

        model.endUpdate();

    }

    void layoutGlobaPorts(GraphModel model, GraphLayoutComponent layoutComponent) {
        layoutGlobaInputPorts(model, layoutComponent);
        layoutGlobaOutputPorts(model, layoutComponent);
    }
    
    void layoutGlobaInputPorts(GraphModel model, GraphLayoutComponent layoutComponent) {

        mxCell xReferenceCell = null;

        for (Object portCell : g.getGlobalInputPortCells()) {
            mxCell cell = (mxCell) portCell;

            xReferenceCell = null;

            // get x position of connected reference port cell
            Object[] edges = mxGraphModel.getOutgoingEdges(model, portCell);
            if (edges.length > 0 && edges[0] instanceof GraphEdge) {
                GraphEdge gEdge = (GraphEdge) edges[0];
                xReferenceCell = gEdge.getTargetPortCell();
            }

            // set new x position
            setCellCenteredToReference(cell, xReferenceCell, layoutComponent);
        }

    }

    void layoutGlobaOutputPorts(GraphModel model, GraphLayoutComponent layoutComponent) {

        mxCell xReferenceCell = null;

        for (Object portCell : g.getGlobalOutputPortCells()) {
            mxCell cell = (mxCell) portCell;

            xReferenceCell = null;

            // get x position of connected reference port cell
            Object[] edges = mxGraphModel.getIncomingEdges(model, portCell);
            if (edges.length > 0 && edges[0] instanceof GraphEdge) {
                GraphEdge gEdge = (GraphEdge) edges[0];
                xReferenceCell = gEdge.getSourcePortCell();
            }

            // set new x position
            setCellCenteredToReference(cell, xReferenceCell, layoutComponent);
        }

    }

    void setCellCenteredToReference(mxCell cell, mxCell referenceCell, GraphLayoutComponent layoutComponent) {
        if (null != referenceCell) {
            mxGeometry geom = cell.getGeometry();
            double oldX = geom.getX();

            // calculate center relative to reference cell
            double refX = Graph.getAbsoluteCellX(referenceCell);
            double refWidth = referenceCell.getGeometry().getWidth();
            double cellWidth = geom.getWidth();
            geom.setX(refX + (refWidth - cellWidth) / 2);
            cell.setGeometry(geom);

            // reset if an intersection was produced
            boolean resetX = false;
            Object[] cellsALevel = layoutComponent.getCellsAtLevel(layoutComponent.getCellLevel(cell));
            for (Object aLevelCell : cellsALevel) {
                if (!aLevelCell.equals(cell) && ((mxCell) aLevelCell).getGeometry().contains(geom.getX(), geom.getY())) {
                    resetX = true;
                }
            }

            if (resetX) {
                geom.setX(oldX);
                cell.setGeometry(geom);
            }

        }
    }


    /**
     *
     * @param graphComponent
     * @param source
     * @return
     */
    private int goDownAndCountEdges(GraphLayoutComponent graphComponent, Object source) {
        Object[] outgoingEdges = mxGraphModel.getOutgoingEdges(g.getModel(), source);

        CellInfo cellInfo = graphComponent.getCellInfo(source);

        if (null == cellInfo || null == outgoingEdges || 0 == outgoingEdges.length) {
            return 1;
        }

        int maxEdges = 0;

        for (Object edge : outgoingEdges) {
            mxCell edgeCell = (mxCell) edge;
            maxEdges = Math.max(maxEdges, goDownAndCountEdges(graphComponent, edgeCell.getTarget()));
        }

        cellInfo.updateMaxDownIfPossible(maxEdges);

        return maxEdges + 1;
    }

    /**
     *
     * @param graphComponent
     * @param sink
     * @return
     */
    private int goUpAndCountEdges(GraphLayoutComponent graphComponent, Object sink) {
        Object[] incomingEdges = mxGraphModel.getIncomingEdges(g.getModel(), sink);

        CellInfo cellInfo = graphComponent.getCellInfo(sink);

        if (cellInfo == null || null == incomingEdges || 0 == incomingEdges.length) {
            return 1;
        }

        int maxEdges = 0;

        for (Object edge : incomingEdges) {
            mxCell edgeCell = (mxCell) edge;
            maxEdges = Math.max(maxEdges, goUpAndCountEdges(graphComponent, edgeCell.getSource()));
        }

        cellInfo.updateMaxUpIfPossible(maxEdges);

        return maxEdges + 1;
    }

    /**
     *
     * @param cellGap
     */
    public void setCellGap(int cellGap) {
        this.cellGap = cellGap;
    }

    /**
     *
     * @param graphComponentGap
     */
    public void setGraphComponentGap(int graphComponentGap) {
        this.graphComponentGap = graphComponentGap;
    }

}
