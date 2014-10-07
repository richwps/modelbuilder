package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.swing.handler.mxCellMarker;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import java.awt.event.MouseEvent;

/**
 * Custom cell markers enable the use of different hot spots for cells.
 *
 * @author dziegenh
 */
public class CellMarker extends mxCellMarker {

    public static double DEFAULT_PORT_HOTSPOT = 1.;

    protected double portHotspot = CellMarker.DEFAULT_PORT_HOTSPOT;

    public CellMarker(mxGraphComponent gc) {
        super(gc);
    }

    public void setPortHotspot(double hotspot) {
        this.portHotspot = hotspot;
    }

    @Override
    protected boolean intersects(mxCellState cs, MouseEvent me) {
        boolean isPort = graphComponent.getGraph().isPort(cs.getCell());

        double tmpHotspot = hotspot;

        // Use different hotspot value if cell is a (process-) port.
        if (isPort) {
            hotspot = portHotspot;
        }

        boolean intersects = super.intersects(cs, me);

        hotspot = tmpHotspot;

        return intersects;
    }

}
