/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.handler.mxVertexHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import de.hsos.richwps.mb.AppConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import javax.swing.JToolTip;

/**
 * Only accepts our custom graph.
 *
 * @author dziegenh
 */
public class GraphComponent extends mxGraphComponent {

    private JToolTip toolTip;
    private String errorMsgColor;

    public GraphComponent(mxGraph mxgrph) {
        super((Graph) mxgrph);

        // init tooltip
        toolTip = new JToolTip();
        int errorColor = AppConstants.ERROR_MESSAGE_COLOR.getRGB();
        errorMsgColor = Integer.toHexString(errorColor).substring(2);
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

    Component getConnectionToolTip(Object source, Object target, String error) {

        String targetTooltip = "";

        // get target cell tooltip
        if (null != target) {
            targetTooltip = getGraph().getToolTipForCell(target);
        }

        // no tooltip if there is no content
        boolean noError = (null == error || error.length() < 1);
        boolean toolttipEmpty = targetTooltip.length() < 1;
        if (toolttipEmpty && noError) {
            return null;
        }

        // TODO update init capacity when the length of a typical tooltip is known
        StringBuilder sb = new StringBuilder(255);

        if (!toolttipEmpty) {
            // remove closing html tag.
            sb.append(targetTooltip.substring(0, targetTooltip.lastIndexOf("</html>")));

            // only append an horizontal line if an error msg follows
            if (!noError) {
                sb.append("<hr>");
            }

        } else {
            // insert opening html tag if there is no target tooltip
            sb.append("<html>");
        }

        // Append error message
        if (!noError) {
            sb.append("<b><span style=\"color:#");
            sb.append(errorMsgColor);
            sb.append("\">Error: ");
            sb.append(error);
            sb.append("</b></span>");
        }

        sb.append("</html>");
        toolTip.setTipText(sb.toString());

        return toolTip;
    }

}
