package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentComplexDataType;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
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

    private List<Property> propertiesListeningTo = new LinkedList<>();

    private final IPropertyChangedByUIListener propertyUIChangeListener = new IPropertyChangedByUIListener() {
        @Override
        public void propertyChanged(Property property, Property propertyBeforeChange) {
            UndoablePropertyChangeAction changeAction = new UndoablePropertyChangeAction(
                    property,
                    propertyBeforeChange,
                    getCurrentObjectWithProperties());

            app.getUndoManager().addEdit(new AppUndoableEdit(this, changeAction, "change property value"));
        }

    };

    @Override
    protected AbstractPropertyComponent getComponentFor(Property property) {
        AbstractPropertyComponent component;

        if (property.getComponentType().equals(ProcessPort.COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX)) {
            component = createPropertyComplexDataTypeFormat(property);

        } else {

            component = super.getComponentFor(property);

        }

        // TODO check if listeners have to be removed after changing the components/card
        component.addPropertyChangedByUIListener(propertyUIChangeListener);

        return component;
    }

    @Override
    protected void setupPropertyGroupTitledComponent(PropertyGroup propertyGroup, TitledComponent groupPanel) {
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

    private App getApp() {
        return this.app;
    }
}
