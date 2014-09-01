/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.util.mxPoint;
import de.hsos.richwps.mb.graphView.GraphSetup;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class GraphCanvas extends mxInteractiveCanvas {

    private double normalize(double x) {
        if (x == 0) {
            return 0;
        }
        return x / Math.abs(x);
    }

    private static int BEAUTIFY_WIDTH = 2;

    public void paintPolyline(mxPoint[] points, boolean rounded, boolean beautifyIntersections) {

        // Adds a border to the line by cloning all points and shifting them in x and y direction.
        // The border improves distinguishing the edges at intersections.
        if (beautifyIntersections && points.length > 1) {

            // init point shift arrays
            int numPointArrays = 2 * BEAUTIFY_WIDTH;
            List<mxPoint[]> pArrays = new LinkedList<>();
            for (int a = 0; a < numPointArrays; a++) {
                pArrays.add(new mxPoint[points.length]);
                pArrays.get(a)[0] = new mxPoint(points[0].getX(), points[0].getY());
            }

            int i = 0;
            for (; i < points.length - 1; i++) {
                mxPoint p1 = points[i];
                mxPoint p2 = points[i + 1];

                double dx = normalize(p2.getX() - p1.getX());
                double dy = normalize(p2.getY() - p1.getY());

                // move both line endpoints (p1 and p2) orthogonal to their connecting line
                int offset = 1;
                for (int a = 0; a < numPointArrays; a += 2) {
                    mxPoint[] rightShifted = pArrays.get(a);
                    mxPoint[] leftShifted = pArrays.get(a + 1);

                    int moveToOtherDirection = (0 == i ? 1 : 0);

                    // point #i already exists
                    rightShifted[i].setX(rightShifted[i].getX() + offset * dy + moveToOtherDirection * dx);
                    rightShifted[i].setY(rightShifted[i].getY() - offset * dx + moveToOtherDirection * dy);
                    leftShifted[i].setX(leftShifted[i].getX() - offset * dy + moveToOtherDirection * dx);
                    leftShifted[i].setY(leftShifted[i].getY() + offset * dx + +moveToOtherDirection * dy);

                    // point #i+1 has to be created
                    rightShifted[i + 1] = new mxPoint(p2.getX() + offset * dy, p2.getY() - offset * dx);
                    leftShifted[i + 1] = new mxPoint(p2.getX() - offset * dy, p2.getY() + offset * dx);

                    offset++;
                }
            }

            // draw shifted lines
            g.setColor(GraphSetup.GRAPH_EDGE_SHIFTED_COLOR);
            int a = 0;
            float ratio;
            for (mxPoint[] pArray : pArrays) {
                paintPolyline(pArray, rounded);
                if (1 == a % 2) {
                    ratio = ((float) (a+1))/pArrays.size();
//                    ratio = (float) Math.log(w);
//Logger.log(""+w+": "+ratio);
//Logger.log(UiHelper.mixColors(GraphSetup.GRAPH_EDGE_SHIFTED_COLOR, GraphSetup.GRAPH_BG_COLOR, ratio));
                    g.setColor(UiHelper.mixColors(GraphSetup.GRAPH_EDGE_SHIFTED_COLOR, GraphSetup.GRAPH_BG_COLOR, ratio));
                }
                a++;
            }

        }
        g.setColor(GraphSetup.GRAPH_EDGE_COLOR);

        paintPolyline(points, rounded);
    }

}
