/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView;

import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 *
 * @author dziegenh
 */
public class GraphConnectionHandler extends mxConnectionHandler {

    private GraphModel graphModel;

    public GraphConnectionHandler(final mxGraphComponent gc) {
        super(gc);

        marker = new CellMarker(graphComponent) {
            /**
             *
             */
            private static final long serialVersionUID = 103433247310526381L;
            private Popup tipWindow;

            // Overrides to return cell at location only if valid (so that
            // there is no highlight for invalid cells that have no error
            // message when the mouse is released)
            protected Object getCell(MouseEvent e) {
                Object cell = super.getCell(e);

                if (isConnecting()) {
                    if (source != null) {
                        error = validateConnection(source.getCell(), cell);

                        if (null != cell) {
//                            Logger.log(cell);
                            Object parent = getGraphModel().getParent(cell);

                            // TODO popups don't work !!
                            if (null == parent) {
                                if (null != tipWindow) {
                                    tipWindow.hide();
                                }
                            } else {
                                Point loc = getGraphModel().getGeometry(parent).getPoint();
                                if (null == tipWindow) {
                                    PopupFactory popupFactory = PopupFactory.getSharedInstance();
                                    tipWindow = popupFactory.getPopup(gc, new JLabel("asdfg"), loc.x, loc.y);
                                }
                                tipWindow.show();
                            }
                        }

                        if (error != null && error.length() == 0) {
                            cell = null;

                            // Enables create target inside groups
                            if (createTarget) {
                                error = null;
                            }
                        }

                    }
                } else if (!isValidSource(cell)) {
                    cell = null;
                }

                return cell;
            }

            // Sets the highlight color according to isValidConnection
            protected boolean isValidState(mxCellState state) {
                if (isConnecting()) {
                    return error == null;
                } else {
                    return super.isValidState(state);
                }
            }

            // Overrides to use marker color only in highlight mode or for
            // target selection
            protected Color getMarkerColor(MouseEvent e, mxCellState state,
                    boolean isValid) {
                return (isHighlighting() || isConnecting()) ? super
                        .getMarkerColor(e, state, isValid) : null;
            }

            // Overrides to use hotspot only for source selection otherwise
            // intersects always returns true when over a cell
//			protected boolean intersects(mxCellState state, MouseEvent e)
//			{
//				if (!isHighlighting() || isConnecting())
//				{
//					return true;
//				}
//
//				return super.intersects(state, e);
//			}
        };

//        marker = new mxCellMarker(graphComponent) {
//            /**
//             *
//             */
//            private static final long serialVersionUID = 103433247310526381L;
//
//            // Overrides to return cell at location only if valid (so that
//            // there is no highlight for process)
//            protected Object getCell(MouseEvent e) {
//                Object cell = super.getCell(e);
//
//                if (isConnecting()) {
//                    if (source != null) {
//                        error = validateConnection(source.getCell(), cell);
//
//                        if (error != null && error.length() == 0) {
//                            cell = null;
//
//                            // Enables create target inside groups
//                            if (createTarget) {
//                                error = null;
//                            }
//                        }
//                    }
//                }
//                else {
//                    // Processes can only be connected via ports, so no marker is necessary => set cell to null.
//                    if(getGraphModel().isProcess(cell))
//                        cell = null;
//                }
//
//                return cell;
//            }
//
//            // Sets the highlight color according to isValidConnection
//            protected boolean isValidState(mxCellState state) {
//                if (isConnecting()) {
//                    return error == null;
//                }
////                    mxCell cell ==
//                Graph graph = getTheGraphCompoment().getTheGraph();
//                Object cell = state.getCell();
////                boolean isPort = graph.isPort(cell);
////                if(isPort) {
////                    return
////                }
//                return graph.isValidSource(cell);
//
////                return false;
////                    return super.isValidState(state);
//            }
//
//            // Overrides to use marker color only in highlight mode or for
//            // target selection
//            protected Color getMarkerColor(MouseEvent e, mxCellState state,
//                    boolean isValid) {
//                // TODO currently disabled; enable if marker follow our connection rules.
//                return null;
////                return (isHighlighting() || isConnecting()) ? super
////                        .getMarkerColor(e, state, isValid) : null;
//            }
//
//            // Overrides to use hotspot only for source selection otherwise
//            // intersects always returns true when over a cell
//            protected boolean intersects(mxCellState state, MouseEvent e) {
//                if (!isHighlighting() || isConnecting()) {
//                    return true;
//                }
//
//                return super.intersects(state, e);
//            }
//        };
        marker.setHotspotEnabled(true);
    }

    private GraphModel getGraphModel() {
        if (null == graphModel) {
            graphModel = getTheGraphCompoment().getTheGraph().getGraphModel();
        }

        return graphModel;
    }

    GraphComponent getTheGraphCompoment() {
        return (GraphComponent) graphComponent;
    }

}
