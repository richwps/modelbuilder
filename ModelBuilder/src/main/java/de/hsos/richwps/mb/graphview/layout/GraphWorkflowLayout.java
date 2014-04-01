/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview.layout;

import com.mxgraph.analysis.StructuralException;
import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.graphview.Graph;
import de.hsos.richwps.mb.graphview.GraphModel;
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

        // A graph component contains connected cells. Graph components are not connected to each other!
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
//            getSinkAndSourceVertices(ag, sinkVertices, sourceVertices);
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

            // layout each level of current graphComponent
            int levelY = cellGap;
            for (int level = 0; level <= layoutComponent.getLongestPathLength(); level++) {

                Object[] levelCells = layoutComponent.getCellsAtLevel(level);
                int numLevelCells = levelCells.length;
                int levelWidth = layoutComponent.getLevelWidth(model, level);
                levelWidth += cellGap * numLevelCells;
                int levelX = cellGap + (int) (.5 * (gcWidth - levelWidth));

                for (Object cell : levelCells) {

                    CellInfo ci = layoutComponent.getCellInfo(cell);

                    // TODO just dev: show cell info as value
//                     .setValue(cell, model.getValue(cell));
                    mxGeometry geom = model.getGeometry(cell);
                    geom = (mxGeometry) geom.clone();
                    geom.setX(renderX + levelX);

                    // TODO MOCK!! calculate REAL values for gaps and positions!!
                    geom.setY(levelY); //150 * layoutComponent.getCellLevel(cell, useMax));
                    levelX += geom.getWidth() + cellGap;

                    model.setGeometry(cell, geom);
                }

                if (levelCells.length > 0) {
                    levelY += 150;  // TODO MOCK! calculate real height + gap
                }
            }

            renderX += gcWidth + graphComponentGap;
        }

        model.endUpdate();
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

//    private void getSinkAndSourceVertices(mxAnalysisGraph ag, Object[] sinkVertices, Object[] sourceVertices) {
//        for(graph.get)
//    }
}
