package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentComplexDataType;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentLiteralDataType;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.UndoablePropertyChangeAction;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.IPropertyChangedByUIListener;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Controlls the properties view and it's interaction with other app components.
 *
 * @author dziegenh
 */
public class AppPropertiesView extends PropertiesView {

    private App app;

    public AppPropertiesView(App app) {
        super(app.getFrame(), AppConstants.PROPERTIES_PANEL_TITLE);
        this.app = app;

        // update properties view depending on the graph selection
        app.getGraphView().addSelectionListener(new ListSelectionListener() {
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
        });
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

            app.getUndoManager().addEdit(new AppUndoableEdit(this, changeAction, "change property value"));

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

        // collect QoS target subgroups in order to style them later
        qosTargetSubGroups = new LinkedList<>();
        String qosGroup = ProcessProviderConfig.QOS_TARGETS_GROUP;
        for (IObjectWithProperties aProperty : object.getProperties()) {
            String aPropertyName = aProperty.getPropertiesObjectName();

            if (aProperty instanceof PropertyGroup && aPropertyName.equals(qosGroup)) {

                for (IObjectWithProperties property : aProperty.getProperties()) {
                    if (property instanceof PropertyGroup) {
                        qosTargetSubGroups.add(property.getPropertiesObjectName());
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

        AbstractPropertyComponent component;

        if (property.getComponentType().equals(ProcessPort.COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX)) {
            component = createPropertyComplexDataTypeFormat(property);

        } else if (property.getComponentType().equals(ProcessPort.COMPONENTTYPE_DATATYPEDESCRIPTION_LITERAL)) {
            component = new PropertyComponentLiteralDataType(property);

        } else {

            component = super.getComponentFor(property);
            addToCache = false;
        }

        if (!propertyComponentsListeningTo.contains(component)) {
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
        if (groupName.equals(AppConstants.MONITOR_DATA) || isMetricGroup) {
            groupPanel.setTitleGradientColor2(AppConstants.MONITOR_DATA_BG_COLOR);
            hasBrightBg = true;

            if (isMetricGroup) {
                groupPanel.resetTitleFontStyle();
            }
        }

        if (groupName.equals(ProcessProviderConfig.QOS_TARGETS_GROUP)) {
            groupPanel.setTitleGradientColor2(AppConstants.QOS_TARGETS_BG_COLOR);
            hasBrightBg = true;
        }

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
        for (String[] keyTranslation : AppConstants.MONITOR_KEY_TRANSLATIONS) {
            if (keyTranslation[1].equals(groupName)) {
                return true;
            }
        }

        return false;
    }

    private PropertyComponentComplexDataType createPropertyComplexDataTypeFormat(Property<DataTypeDescriptionComplex> property) {
        PropertyComponentComplexDataType propertyComplexDataTypeFormat = null;
        try {
            propertyComplexDataTypeFormat = new PropertyComponentComplexDataType(app.getFrame(), app.getFormatProvider());
            propertyComplexDataTypeFormat.setProperty(property);

        } catch (LoadDataTypesException ex) {
            app.showErrorMessage(AppConstants.FORMATS_CSV_FILE_LOAD_ERROR);
        }

        return propertyComplexDataTypeFormat;
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

}
