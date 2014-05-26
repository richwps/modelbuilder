/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxVertexHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * Only accepts our custom graph.
 *
 * @author dziegenh
 */
public class GraphComponent extends mxGraphComponent {

    public GraphComponent(mxGraph mxgrph) {
        super((Graph) mxgrph);
    }

    @Override
    protected mxConnectionHandler createConnectionHandler() {
        // custom connection handler is necessary to use the extended anonymous cellMarker class.
        return new GraphConnectionHandler(this);
    }

    Graph getTheGraph() {
        return (Graph) graph;
    }

    @Override
    public mxCellHandler createHandler(mxCellState state) {
        if (graph.getModel().isVertex(state.getCell())) {
            return new mxVertexHandler(this, state) {
                @Override
                public void paint(Graphics g) {
                    Rectangle bounds = getState().getRectangle();

                    if (g.hitClip(bounds.x, bounds.y, bounds.width, bounds.height)) {
                        Graphics2D g2 = (Graphics2D) g;

                        // Hide existing border using the background color
                        g.setColor(getViewport().getBackground());
                        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

                        // Draw selection border
                        Stroke stroke = g2.getStroke();
                        g2.setStroke(getSelectionStroke());
                        g.setColor(getSelectionColor());
                        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
                        g2.setStroke(stroke);
                    }

                    super.paint(g);
                }
            };
        }

        return super.createHandler(state);
    }


}
