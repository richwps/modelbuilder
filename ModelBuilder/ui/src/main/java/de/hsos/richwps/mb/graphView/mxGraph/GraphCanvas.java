/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import de.hsos.richwps.mb.graphView.GraphSetup;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class GraphCanvas extends mxInteractiveCanvas {

    public void paintPolyline(mxPoint[] points, boolean rounded, boolean beautifyIntersections) {

        if (beautifyIntersections) {
            List<mxPoint> pointList = Arrays.asList(points); //new LinkedList<>();
            mxPoint[] clone = points.clone();

            Color tmpColor = g.getColor();
            g.setColor(GraphSetup.GRAPH_BG_COLOR);

            // TODO shorten the white polylines and calculate correct translation parameters!
            super.paintPolyline(mxUtils.translatePoints(pointList, 1, 1).toArray(clone), rounded);
//            super.paintPolyline(mxUtils.translatePoints(pointList, 2, 2).toArray(clone), rounded);
            super.paintPolyline(mxUtils.translatePoints(pointList, -1, -1).toArray(clone), rounded);
//            super.paintPolyline(mxUtils.translatePoints(pointList, -2, -2).toArray(clone), rounded);
            
            g.setColor(tmpColor);
        }

        super.paintPolyline(points, rounded);
    }

}
