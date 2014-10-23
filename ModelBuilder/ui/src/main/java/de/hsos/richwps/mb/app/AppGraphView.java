package de.hsos.richwps.mb.app;

import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.ModelElementsChangedListener;
import de.hsos.richwps.mb.properties.Property;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * Controlls the graph component and it's interaction with other app components.
 *
 * @author dziegenh
 */
public class AppGraphView extends GraphView {

    private final App app;
    private boolean init = false;
    private Property endpointProperty;

    public AppGraphView(App app) {
        super();
        this.app = app;
    }

    void init() {
        if (init) {
            return;
        }

        getGui().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // Delete
                    case 127:
                        if (hasSelection()) {
                            int choice = JOptionPane.showConfirmDialog(getApp().getFrame(), AppConstants.CONFIRM_DELETE_CELLS, AppConstants.CONFIRM_DELETE_CELLS_TITLE, JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                deleteSelectedCells();
                            }
                        }
                        break;

                    // Select All
                    case 65:
                        if (0 < (e.getModifiers() & KeyEvent.CTRL_MASK)) {
                            selectAll();
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {

                    // move cells
                    case 37:
                        moveSelectedCells(-AppConstants.GRAPH_GRID_SIZE, 0);    // left
                        break;
                    case 38:
                        moveSelectedCells(0, -AppConstants.GRAPH_GRID_SIZE);    // up
                        break;
                    case 39:
                        moveSelectedCells(AppConstants.GRAPH_GRID_SIZE, 0);     // right
                        break;
                    case 40:
                        moveSelectedCells(0, AppConstants.GRAPH_GRID_SIZE);     // down
                        break;

                    default:
                        break;
                }
            }
        });

        addModelElementsChangedListener(new ModelElementsChangedListener() {
            @Override
            public void modelElementsChanged(Object element, GraphView.ELEMENT_TYPE type, GraphView.ELEMENT_CHANGE_TYPE changeType) {
                getApp().updateGraphDependentActions();

                if (!type.equals(GraphView.ELEMENT_TYPE.PROCESS)) {
                    return;
                }

                switch (changeType) {
                    case ADDED:
                        getApp().getSubTreeView().addNode(element);
                        break;
                    case REMOVED:
                        getApp().getSubTreeView().removeNode(element);
                        break;
                }
            }
        });

        // register graph components for the event service.
        AppEventService.getInstance().addSourceCommand(AppConstants.INFOTAB_ID_EDITOR, AppConstants.INFOTAB_ID_EDITOR);
        AppEventService.getInstance().addSourceCommand(this, AppConstants.INFOTAB_ID_EDITOR);
        AppEventService.getInstance().addSourceCommand(this.getGraph(), AppConstants.INFOTAB_ID_EDITOR);

        // add property for endpoint selection
        String propertyEndpointName = AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT;
        String propertyEndpointType = Property.COMPONENT_TYPE_DROPDOWN;
        endpointProperty = new Property(propertyEndpointName, propertyEndpointType, null, true);
        getGraph().getGraphModel().addProperty(endpointProperty);

        init = true;
    }

    /**
     * Add the model's undoable graph edits to the UndoManager. Needs to be
     * called after a new model has been created or loaded.
     */
    void modelLoaded() {
        addUndoEventListener(new mxEventSource.mxIEventListener() {
            public void invoke(Object o, mxEventObject eo) {
                Object editProperty = eo.getProperty(PROPERTY_KEY_EDIT);
                if (eo.getProperty("edit") instanceof mxUndoableEdit) {
                    mxUndoableEdit edit = (mxUndoableEdit) editProperty;

                    // add graph edit to undo manager
                    getApp().getUndoManager().addEdit(new AppUndoableEdit(this, edit, "Graph edit"));
                    getApp().setChangesSaved(false);
                }
            }
        });
        
        updateRemotes();
        app.setChangesSaved(true);
    }

    void updateRemotes() {
        endpointProperty.setPossibleValues(Arrays.asList(app.getProcessProvider().getAllServers()));
    }

    private App getApp() {
        return this.app;
    }

}
