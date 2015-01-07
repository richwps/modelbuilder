package de.hsos.richwps.mb.app;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.app.view.LoadingScreen;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.ModelElementsChangedListener;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.UIManager;

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

                if (getApp().hasSubTreeView()) {
                    switch (changeType) {
                        case ADDED:
                            getApp().getSubTreeView().addNode(element);
                            break;
                        case REMOVED:
                            getApp().getSubTreeView().removeNode(element);
                            break;
                    }
                }
            }
        });

        // register graph components for the event service.
        AppEventService.getInstance().addSourceCommand(AppConstants.INFOTAB_ID_EDITOR, AppConstants.INFOTAB_ID_EDITOR);
        AppEventService.getInstance().addSourceCommand(this, AppConstants.INFOTAB_ID_EDITOR);
        AppEventService.getInstance().addSourceCommand(this.getGraph(), AppConstants.INFOTAB_ID_EDITOR);

        // add property for endpoint selection
        String propertyEndpointName = GraphModel.PROPERTIES_KEY_OWS_ENDPOINT;
        String propertyEndpointType = Property.COMPONENT_TYPE_DROPDOWN;
        endpointProperty = new Property(propertyEndpointName, propertyEndpointType, null, true);
        getGraph().getGraphModel().addProperty(endpointProperty);

        init = true;
    }

    public void newGraph(String remote) {
        Graph graph = super.newGraph();
        String remotePropertyKey = GraphModel.PROPERTIES_KEY_OWS_ENDPOINT;
        Property remoteProperty = new Property(remotePropertyKey, Property.COMPONENT_TYPE_TEXTFIELD, remote);
        graph.getGraphModel().addProperty(remoteProperty);
    }

    /**
     * Add the model's undoable graph edits to the UndoManager and sets the
     * correct cell value instances. Needs to be called after a new model has
     * been created or loaded.
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

        GraphModel model = getGraph().getGraphModel();
        Property loadedEndpointProperty = getModelEndpointProperty(model.getProperties());

        updateRemotes();

        if (null != loadedEndpointProperty) {
            endpointProperty.setValue(loadedEndpointProperty.getValue());
            model.setProperty(loadedEndpointProperty.getPropertiesObjectName(), endpointProperty);
        }

        // set the cells process instances
        updateProcessCellInstances();

        app.setChangesSaved(true);
    }

    private Property getModelEndpointProperty(Collection<? extends IObjectWithProperties> modelProperties) {
        Property foundProperty = null;

        for (IObjectWithProperties p : modelProperties) {

            // property found
            if (p instanceof Property && p.getPropertiesObjectName().equals(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT)) {
                foundProperty = (Property) p;

                // current property is a propertygroup -> recursive search
            } else if (p instanceof PropertyGroup) {
                Property subProperty = getModelEndpointProperty(p.getProperties());
                if (null != subProperty) {
                    foundProperty = subProperty;
                }
            }
        }

        return foundProperty;
    }

    void updateRemotes() {
        endpointProperty.setPossibleValues(Arrays.asList(app.getProcessProvider().getAllServers()));
    }

    private App getApp() {
        return this.app;
    }

    /**
     * Set correct process instances using the ProcessProvider
     */
    private void updateProcessCellInstances() {
        List<mxCell> processCells = getProcessCells();
        GraphModel graphModel = getGraph().getGraphModel();
        ProcessProvider processProvider = app.getProcessProvider();

        for (mxCell aCell : processCells) {
            ProcessEntity process = (ProcessEntity) graphModel.getValue(aCell);

            String server = process.getServer();
            String identifier = process.getOwsIdentifier();

            ProcessEntity loadedProcess = processProvider.getFullyLoadedProcessEntity(server, identifier);

            graphModel.setValue(aCell, loadedProcess);
        }
    }
}
