package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentQosTarget;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentQosTargets;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.DatatypeProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.UndoablePropertyChangeAction;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.IPropertyChangedByUIListener;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextFieldDouble;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import java.awt.Window;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Controlls the properties view and it's interaction with other app components.
 *
 * @author dziegenh
 */
public class AppPropertiesView extends PropertiesView {

    private AppProperties appProperties;
    private AppGraphView graphView;
    private AppUndoManager undoManager;
    private AppActionProvider actionProvider;
    private DatatypeProvider datatypeProvider;

    private final ListSelectionListener listSelectionListener;
    private final App app;

    public AppPropertiesView(App app) {
        super(AppConstants.PROPERTIES_PANEL_TITLE);

        this.app = app;

        // update properties view depending on the graph selection
        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (getGraphView().hasSelection()) {
                    GraphView.ELEMENT_TYPE type = getGraphView().getSelectedElementType();
                    if (null != type && type.equals(GraphView.ELEMENT_TYPE.GLOBAL_PORT)) {
                        setObjectsWithProperties(getGraphView().getSelectedGlobalPorts());
                    } else {
                        setObjectsWithProperties(getGraphView().getSelectedProcesses());
                    }
                } else {
                    setObjectWithProperties(getGraphView().getGraph().getGraphModel());
                }
            }
        };
    }

    public void setDatatypeProvider(DatatypeProvider datatypeProvider) {
        this.datatypeProvider = datatypeProvider;
    }

    public void setParentFrame(Window parentFrame) {
        super.setParentWindow(parentFrame);
    }

    public void setUndoManager(AppUndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public void setActionProvider(AppActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public void setGraphView(AppGraphView graphView) {
        this.graphView = graphView;
    }

    public AppGraphView getGraphView() {
        return graphView;
    }

    public ListSelectionListener getListSelectionListener() {
        return listSelectionListener;
    }

    /**
     * The property components whose changes are observed.
     */
    private List<AbstractPropertyComponent> propertyComponentsListeningTo = new LinkedList<>();

    private final IPropertyChangedByUIListener propertyUIChangeListener = new IPropertyChangedByUIListener() {
        @Override
        public void propertyChanged(Property property, Property propertyBeforeChange) {
            UndoablePropertyChangeAction changeAction = new UndoablePropertyChangeAction(
                    property,
                    propertyBeforeChange,
                    getCurrentObjectWithProperties());

            undoManager.addEdit(new AppUndoableEdit(this, changeAction, "change property value"));

            // react to changes of the current process's identifier
            if (property.getPropertiesObjectName().equals(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER)) {

                // TODO find out it the process WAS deployed with the old identifier
                // -> inform user that the deployed process remains with the old identifier
                // inform user if a process with the new identifier value exists
                if (app.currentModelIsDeployed()) {

                    // Get deployment info
                    String identifier = (String) property.getValue();
                    GraphModel model = getGraphView().getGraph().getGraphModel();
                    String server = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);

                    // create and output hint with deployment info
                    String format = AppConstants.FORMATTED_HINT_PROCESS_ALREADY_DEPLOYED;
                    String hint = String.format(format, identifier, server);
                    AppEventService.getInstance().fireAppEvent(hint, AppConstants.INFOTAB_ID_SERVER, AppEvent.PRIORITY.URGENT);
                }

                // update actions
                app.updateDeploymentDependentActions();
            }
        }

    };

    @Override
    public void clearPropertyCache() {
        for (AbstractPropertyComponent component : propertyComponentsListeningTo) {
            component.removePropertyChangedByUIListener(propertyUIChangeListener);
        }
        propertyComponentsListeningTo.clear();

        super.clearPropertyCache();
    }

    /**
     * Sets the object whose properties are to be shown. Removes change listener
     * of currently shown property component.
     *
     * @param object
     */
    @Override
    public void setObjectWithProperties(IObjectWithProperties object) {

        String uomName = QoSTarget.PROPERTY_KEY_QOS_TARGET_UOM;

        // collect QoS target subgroups in order to style them later
        qosTargetSubGroups = new LinkedList<>();
        String qosGroup = ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS;
        for (IObjectWithProperties aProperty : object.getProperties()) {
            String aPropertyName = aProperty.getPropertiesObjectName();

            if (aProperty instanceof PropertyGroup && aPropertyName.equals(qosGroup)) {

                for (IObjectWithProperties property : aProperty.getProperties()) {
                    if (property instanceof PropertyGroup) {
                        qosTargetSubGroups.add(property.getPropertiesObjectName());

                        // create components for QoS Values
                        PropertyGroup<Property> qosSubGroup = (PropertyGroup) property;
                        Property uomProperty = qosSubGroup.getPropertyObject(uomName);

                        for (Property subGroupProperty : qosSubGroup.getProperties()) {
                            String propertyName = subGroupProperty.getPropertiesObjectName();
                            if (!propertyName.equals(uomName)) {
                                AbstractPropertyComponent qosComponent = getComponentFor(subGroupProperty);
                                if (qosComponent instanceof PropertyTextFieldDouble) {
                                    PropertyTextFieldDouble textField = (PropertyTextFieldDouble) qosComponent;
                                    textField.setValueViewFormat("%.2f " + uomProperty.getValue());
                                }
                                this.componentCache.put(subGroupProperty, qosComponent);
                            }
                        }
                    }
                }
            }
        }

        super.setObjectWithProperties(object);
    }

    @Override
    protected AbstractPropertyComponent getComponentFor(Property property) {

        // get property component from cache
        if (componentCache.containsKey(property)) {
            return componentCache.get(property);
        }

        boolean addToCache = true;
        boolean listenToChanges = true;

        AbstractPropertyComponent component;

        // if missing, set literal datatypes for dropdown component
        if (property.getPropertiesObjectName().equals(LiteralInput.PROPERTY_KEY_LITERALDATATYPE)) {

            if (getCurrentObjectWithProperties() instanceof ProcessPort) {
                ProcessPort port = (ProcessPort) getCurrentObjectWithProperties();

                if (port.isGlobal()) {
                    Collection datatypes = property.getPossibleValues();
                    if (null == datatypes || datatypes.isEmpty()) {
                        try {
                            property.setPossibleValues(datatypeProvider.getLiteralDatatypes());
                        } catch (LoadDataTypesException ex) {
                            AppAction action = actionProvider.getAction(AppActionProvider.APP_ACTIONS.SHOW_ERROR_MSG);
                            action.fireActionPerformed(AppConstants.LOAD_DATATYPES_ERROR);
                        }
                    }

                }
            }
        }
        final String componentType = property.getComponentType();

        if (componentType.equals(ComplexDataInput.COMPONENTTYPE_DATATYPEDESCRIPTION)) {
            component = appProperties.createPropertyComplexDataTypeFormat(property, this);

        } else if (componentType.equals(PropertyComponentQosTargets.COMPONENT_TYPE)) {
            component = new PropertyComponentQosTargets(getParentWindow(), property);
            listenToChanges = false;

        } else if (componentType.equals(PropertyComponentQosTarget.COMPONENT_TYPE)) {
            component = new PropertyComponentQosTarget(property);

        } else {

            component = super.getComponentFor(property);
            addToCache = false;
        }

        if (listenToChanges && !propertyComponentsListeningTo.contains(component)) {
            component.addPropertyChangedByUIListener(propertyUIChangeListener);
            propertyComponentsListeningTo.add(component);
        }

        if (addToCache) {
            // add created component to cache
            this.componentCache.put(property, component);
        }

        return component;
    }

    private LinkedList<String> qosTargetSubGroups;

    @Override
    protected void setupPropertyGroupTitledComponent(PropertyGroup<? extends IObjectWithProperties> propertyGroup, TitledComponent groupPanel) {
        super.setupPropertyGroupTitledComponent(propertyGroup, groupPanel);

        // nothing to do here
        String groupName = propertyGroup.getPropertiesObjectName();
        if (null == groupName || groupName.isEmpty()) {
            return;
        }

        boolean hasBrightBg = false;

        // Style monitor metrics group
        boolean isMetricGroup = isMetricGroup(groupName);
        if (groupName.equals(ProcessMetricProvider.PROPERTY_KEY_MONITOR_DATA) || isMetricGroup) {
            groupPanel.setTitleGradientColor2(AppConstants.MONITOR_DATA_BG_COLOR);
            hasBrightBg = true;

            if (isMetricGroup) {
                groupPanel.resetTitleFontStyle();
            }
        }

        // Style the Qos Targets parent group
        if (groupName.equals(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS)) {
            groupPanel.setTitleGradientColor2(AppConstants.QOS_TARGETS_BG_COLOR);
            hasBrightBg = true;
        }

        // Style a Qos Target group
        if (qosTargetSubGroups.contains(groupName)) {
            groupPanel.setTitleGradientColor2(AppConstants.QOS_TARGET_BG_COLOR);
            groupPanel.resetTitleFontStyle();
            hasBrightBg = true;
        }

        // Style port groups
        if (groupName.equals(ProcessEntity.PROPERTIES_KEY_INPUT_PORTS)) {
            groupPanel.setTitleGradientColor2(AppConstants.INPUT_PORT_COLOR);
            hasBrightBg = true;
        } else if (groupName.equals(ProcessEntity.PROPERTIES_KEY_OUTPUT_PORTS)) {
            groupPanel.setTitleGradientColor2(AppConstants.OUTPUT_PORT_COLOR);
            hasBrightBg = true;
        }

        if (hasBrightBg) {
            groupPanel.setTitleGradientColor1(Color.WHITE);
            groupPanel.setTitleFontColor(Color.BLACK);
        }
    }

    private boolean isMetricGroup(String groupName) {
        String[] propertyKeys = ProcessMetricProvider.getPropertyKeys();
        for (String key : propertyKeys) {
            if (key.equals(groupName)) {
                return true;
            }
        }

        return false;
    }

    void resetCurrentComponents() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearPropertyCache();
                setObjectWithProperties(getCurrentObjectWithProperties());
            }
        });
    }

    AppProperties getAppProperties() {
        return this.appProperties;
    }

    void setAppProperties(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

}
