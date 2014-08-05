/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.graphView.mxGraph;

import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import de.hsos.richwps.mb.appEvents.AppEventService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 *
 * @author dziegenh
 */
public class GraphConnectionHandler extends mxConnectionHandler {

    private GraphModel graphModel;
    private Popup tipWindow;

    private Object target;
//    private Object targetParent;

    private void hideToolTip() {
        if (null != tipWindow) {
            tipWindow.hide();
            tipWindow = null;
        }
    }

    public GraphConnectionHandler(final mxGraphComponent gc) {
        super(gc);

        marker = new CellMarker(graphComponent) {
            /**
             *
             */
            private static final long serialVersionUID = 103433247310526381L;

            // Overrides to return cell at location only if valid (so that
            // there is no highlight for invalid cells that have no error
            // message when the mouse is released)
            protected Object getCell(MouseEvent e) {
                target = super.getCell(e);

                if (isConnecting()) {
                    if (source != null) {
                        error = validateConnection(source.getCell(), target);
                        if (null != target) {
//                            if (getGraphModel().isProcess(target)) {
//                                targetParent = getGraphModel().getParent(target);
//                            } else {
//                                targetParent = target;
//                            }

                        }

                        if (error != null && error.length() == 0) {
                            target = null;

                            // Enables create target inside groups
                            if (createTarget) {
                                error = null;
                            }
                        }

                    }
                } else if (!isValidSource(target)) {
                    if (!getGraphModel().isFlowInput(target)) {
                        target = null;
                    }
                }

                return target;
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
                if (state != null && isHighlighting() && !isConnecting()) {
                    boolean flowInput = getGraphModel().isFlowInput(state.getCell());
                    // TODO marker is red, but connecting is still possible !!
                    if (flowInput && getGraphModel().isInputPortUsed(state.getCell(), null)) {
                        isValid = false;
                    }
                }
                return (isHighlighting() || isConnecting()) ? super
                        .getMarkerColor(e, state, isValid) : null;
            }

        };

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

    @Override
    public void mouseReleased(MouseEvent me) {
        if (null != error && error.length() > 0) {
            AppEventService.getInstance().fireAppEvent(error, graphComponent.getGraph());
        }

        if (null != tipWindow) {
            tipWindow.hide();
            tipWindow = null;
        }

        // Reset error msg to avoid a message box popping up.
        error = null;
        super.mouseReleased(me);
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Object tmpTarget = target;

        super.mouseDragged(me);

        if (tmpTarget != target) {
            hideToolTip();
        }

        if (isConnecting()) {
            updateToolTip(me.getLocationOnScreen());
        }
    }

    void updateToolTip(Point loc) {

        if (null == tipWindow && null != source) {

            Component toolTip = getTheGraphCompoment().getConnectionToolTip(source, target, error);
            if (null == toolTip) {
                return;
            }

            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            tipWindow = popupFactory.getPopup(getTheGraphCompoment(), toolTip, loc.x, loc.y);
            tipWindow.show();
        }
    }

}
