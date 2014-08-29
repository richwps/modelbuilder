package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxConnectorShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dziegenh
 */
public class GraphEdgeShape extends mxConnectorShape {

    public GraphEdgeShape() {
        super();
    }

    @Override
    public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
        super.paintShape(canvas, state);
    }

    @Override
    protected void paintPolyline(mxGraphics2DCanvas canvas,
            List<mxPoint> points, Map<String, Object> style) {

        // use custom canvas if available
        if (canvas instanceof GraphCanvas) {
            GraphCanvas gCanvas = (GraphCanvas) canvas;
            
            boolean rounded = isRounded(style)
                    && canvas.getScale() > mxConstants.MIN_SCALE_FOR_ROUNDED_LINES;
            
            gCanvas.paintPolyline(points.toArray(new mxPoint[points.size()]),
                    rounded,
                    true);
        } else {
            super.paintPolyline(canvas, points, style);
        }
    }

}
