package de.hsos.richwps.mb.app;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUndoableEdit;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortKey;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.ModelElementsChangedListener;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

                    // Duplicate selected
                    case 68:
                        if (0 < (e.getModifiers() & KeyEvent.CTRL_MASK)) {
                            duplicateSelection();
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
        endpointProperty = createEndpointProperty();
        getGraph().getGraphModel().addProperty(endpointProperty);

        init = true;
    }

    public void newGraph(String remote) {
        Graph graph = super.newGraph();
        endpointProperty = createEndpointProperty();
        graph.getGraphModel().addProperty(endpointProperty);
        endpointProperty.setValue(remote);
    }

    protected Property createEndpointProperty() {
        String propertyEndpointName = GraphModel.PROPERTIES_KEY_OWS_ENDPOINT;
        String propertyEndpointType = Property.COMPONENT_TYPE_DROPDOWN;
        Property property = new Property(propertyEndpointName, propertyEndpointType, null, true);

        // don't persist the list of endpoints
        property.setPossibleValuesTransient(true);

        return property;
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
        
        if (null != loadedEndpointProperty && null != loadedEndpointProperty.getValue()) {
            String loadedValue = loadedEndpointProperty.getValue().toString();

            Collection endpoints = endpointProperty.getPossibleValues();

            // no endpoints set: create new list containing the loaded endpoint
            if (null == endpoints) {
                LinkedList<String> newEndpoints = new LinkedList<>();
                newEndpoints.add(loadedValue);
                endpointProperty.setPossibleValues(newEndpoints);

                // endoints exist but don't contain the loaded endpoint: add it
            } else if (!endpoints.contains(loadedValue)) {
                endpoints = new LinkedList<>(endpoints);
                endpoints.add(loadedValue);
                endpointProperty.setPossibleValues(endpoints);
            }

            endpointProperty.setValue(loadedValue);
            model.setProperty(endpointProperty.getPropertiesObjectName(), endpointProperty);
        }

        // set correct process instances as cell values
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
     * Set correct process and port instances using the ProcessProvider
     */
    private void updateProcessCellInstances() {
        Graph graph = getGraph();
        GraphModel graphModel = graph.getGraphModel();
        ProcessProvider processProvider = app.getProcessProvider();

        boolean mappingError = false;

        // update process cell values
        List<mxCell> processCells = getProcessCells();
        for (mxCell aCell : processCells) {
            ProcessEntity process = (ProcessEntity) graphModel.getValue(aCell);
            ProcessEntity loadedProcess = processProvider.getFullyLoadedProcessEntity(process);

            graphModel.setValue(aCell, loadedProcess);

            HashMap<ProcessPortKey, ProcessPort> loadedPorts = getProcessPorts(loadedProcess);

            // update the processes' port cells values
            int childCount = graphModel.getChildCount(aCell);
            for (int c = 0; c < childCount; c++) {
                Object aChild = graphModel.getChildAt(aCell, c);
                Object childValue = graphModel.getValue(aChild);

                // find the port instance for this cell
                if (null != childValue && childValue instanceof ProcessPort) {
                    ProcessPort aPort = (ProcessPort) childValue;

                    ProcessPortKey key = new ProcessPortKey();
                    key.setOwsIdentifier(aPort.getOwsIdentifier());
                    key.setDatatype(aPort.getDatatype());
                    key.setInput(aPort.isFlowInput());

                    // mapping error: no port instance found
                    if (!loadedPorts.containsKey(key)) {
                        String msg = AppConstants.LOAD_MODEL_MAPPING_ERROR_UNKNOWNPORT;
                        msg += String.format(
                                AppConstants.LOAD_MODEL_MAPPING_ERROR_FORMAT,
                                aPort.getOwsIdentifier(),
                                loadedProcess.getOwsIdentifier(),
                                loadedProcess.getServer()
                        );
                        msg = msg + '\n';

                        AppEventService.getInstance().fireAppEvent(msg, AppConstants.INFOTAB_ID_EDITOR, AppEvent.PRIORITY.URGENT);

                        mappingError = true;

                    } else {

                        graphModel.setValue(aChild, loadedPorts.get(key));

                        // remove port instance from map to identity unmapped ports
                        loadedPorts.remove(key);
                    }
                    
                    
                } else if(null == childValue) {
                    // no cell value => use next fitting port
                    mxGeometry geom = graphModel.getGeometry(aChild);
                    System.out.println(geom.getY());
                    
                    
                }
            }

            // mapping error: unmapped port instances
            for (ProcessPort aPort : loadedPorts.values()) {
                mappingError = true;

                // show mapping error msg
                String msg = AppConstants.LOAD_MODEL_MAPPING_ERROR_MISSINGPORT;
                msg += String.format(
                        AppConstants.LOAD_MODEL_MAPPING_ERROR_FORMAT,
                        aPort.getOwsIdentifier(),
                        loadedProcess.getOwsIdentifier(),
                        loadedProcess.getServer()
                );
                msg = msg + '\n';
                AppEventService.getInstance().fireAppEvent(msg, AppConstants.INFOTAB_ID_EDITOR, AppEvent.PRIORITY.URGENT);
            }
        }

        if (mappingError) {
            JOptionPane.showMessageDialog(app.getFrame(), AppConstants.LOAD_MODEL_MAPPING_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private HashMap<ProcessPortKey, ProcessPort> getProcessPorts(ProcessEntity process) {
        HashMap<ProcessPortKey, ProcessPort> result = new HashMap<>();

        List<ProcessPort> inputs = process.getInputPorts();
        List<ProcessPort> outputs = process.getOutputPorts();

        for (ProcessPort aPort : inputs) {
            ProcessPortKey key = new ProcessPortKey();
            key.setOwsIdentifier(aPort.getOwsIdentifier());
            key.setDatatype(aPort.getDatatype());
            key.setInput(aPort.isFlowInput());

            result.put(key, aPort);
        }

        for (ProcessPort aPort : outputs) {
            ProcessPortKey key = new ProcessPortKey();
            key.setOwsIdentifier(aPort.getOwsIdentifier());
            key.setDatatype(aPort.getDatatype());
            key.setInput(aPort.isFlowInput());

            result.put(key, aPort);
        }

        return result;
    }

}
