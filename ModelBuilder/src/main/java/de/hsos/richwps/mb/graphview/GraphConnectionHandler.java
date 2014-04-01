/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphview;

import com.mxgraph.swing.handler.mxCellMarker;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 *
 * @author dziegenh
 */
public class GraphConnectionHandler extends mxConnectionHandler {

    private GraphModel graphModel;

    public GraphConnectionHandler(mxGraphComponent gc) {
        super(gc);

        marker = new mxCellMarker(graphComponent) {
            /**
             *
             */
            private static final long serialVersionUID = 103433247310526381L;

            // Overrides to return cell at location only if valid (so that
            // there is no highlight for process)
            protected Object getCell(MouseEvent e) {
                Object cell = super.getCell(e);

                if (isConnecting()) {
                    if (source != null) {
                        error = validateConnection(source.getCell(), cell);

                        if (error != null && error.length() == 0) {
                            cell = null;

                            // Enables create target inside groups
                            if (createTarget) {
                                error = null;
                            }
                        }
                    }
                }
                else {
                    // Processes can only be connected via ports, so no marker is necessary => set cell to null.
                    if(getGraphModel().isProcess(cell))
                        cell = null;
                }

                return cell;
            }

            // Sets the highlight color according to isValidConnection
            protected boolean isValidState(mxCellState state) {
                if (isConnecting()) {
                    return error == null;
                }
//                    mxCell cell ==
                Graph graph = getTheGraphCompoment().getTheGraph();
                Object cell = state.getCell();
//                boolean isPort = graph.isPort(cell);
//                if(isPort) {
//                    return
//                }
                return graph.isValidSource(cell);

//                return false;
//                    return super.isValidState(state);
            }

            // Overrides to use marker color only in highlight mode or for
            // target selection
            protected Color getMarkerColor(MouseEvent e, mxCellState state,
                    boolean isValid) {
                // TODO currently disabled; enable if marker follow our connection rules.
                return null;
//                return (isHighlighting() || isConnecting()) ? super
//                        .getMarkerColor(e, state, isValid) : null;
            }

            // Overrides to use hotspot only for source selection otherwise
            // intersects always returns true when over a cell
            protected boolean intersects(mxCellState state, MouseEvent e) {
                if (!isHighlighting() || isConnecting()) {
                    return true;
                }

                return super.intersects(state, e);
            }
        };

        marker.setHotspotEnabled(true);
    }

    private GraphModel getGraphModel() {
        if(null == graphModel) {
            graphModel = getTheGraphCompoment().getTheGraph().getGraphModel();
        }

        return graphModel;
    }

    GraphComponent getTheGraphCompoment() {
        return (GraphComponent) graphComponent;
    }

}
