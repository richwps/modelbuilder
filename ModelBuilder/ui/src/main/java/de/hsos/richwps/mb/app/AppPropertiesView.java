package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.view.properties.PropertyComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Color;
import javax.swing.JOptionPane;
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

        // TODO refactor / implement new property change mechanism with the new properties layer !!
        // listen to property changes and add them to the UndoManager
//        addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(final PropertyChangeEvent event) {
//                final Object oldValue = setPropertyValue(event);
//
//                boolean bothNull = (null == event.getNewValue()) && (null == oldValue);
//                boolean valuesEqual = null != event.getNewValue() && event.getNewValue().equals(oldValue);
//                valuesEqual |= null != oldValue && oldValue.equals(event.getNewValue());
//
//                if (!bothNull && !valuesEqual) {
//
//                    // get the component which represents the property in order to update it on undo/redo
//                    AbstractPropertyComponent component = getPropertyComponent(event.getSourceCard(), event.getProperty());
//                    UndoablePropertyChangeAction undoAction = new UndoablePropertyChangeAction(component, event, oldValue);
//
//                    // set property value when action is undone or redone
//                    undoAction.addChangeListener(new PropertyChangeListener() {
//                        @Override
//                        public void propertyChange(PropertyChangeEvent event) {
//                            setPropertyValue(event);
//                            // select source object in graph. this also triggers an update of the properties view.
//                            getGraphView().selectCellByValue(event.getSourceObject());
//                        }
//                    });
//
//                    // Add undo action to undomanager
//                    String propertyName = UiHelper.createStringForViews(event.getProperty());
//                    String cardName = event.getSourceCard().toString();
//                    String undoTitle = String.format(AppConstants.PROPERTIES_PROPERTY_EDIT, propertyName, cardName);
//                    getApp().getUndoManager().addEdit(new AppUndoableEdit(this, undoAction, undoTitle));
//                }
//            }
//        });
    }

    /**
     * @TODO refactor / implement new property change mechanism with the new
     * properties layer !!
     *
     * Sets the (new) property value to the property source object.
     *
     * @param event
     * @return
     */
//    private Object setPropertyValue(PropertyChangeEvent event) {
    //
//        Object oldValue = null;
//
//        switch (event.getSourceCard()) {
//            case NO_SELECTION:
//                break;
//
//            case MODEL:
//                // TODO move String to config or new properties model
//                if (event.getProperty().equals("name")) {
//                    oldValue = getGraphView().getGraphName();
//                    getGraphView().setGraphName((String) event.getNewValue());
//                }
//                break;
//
//            case PROCESS_SINGLE_SELECTION:
//                break;
//            case PROCESS_MULTI_SELECTION:
//                break;
//
//            case GLOBAL_PORT:
//                Object newValue = event.getNewValue();
//                if ((null != event.getSourceObject()
//                        && event.getSourceObject() instanceof ProcessPort)) {
//
//                    ProcessPort port = (ProcessPort) event.getSourceObject();
//
//                    switch (event.getProperty()) {
//                        case AbstractPortCard.PORT_TITLE:
//                            oldValue = port.getOwsTitle();
//                            port.setOwsTitle((String) newValue);
//                            break;
//                        case AbstractPortCard.PORT_ABSTRACT:
//                            oldValue = port.getOwsAbstract();
//                            port.setOwsAbstract((String) newValue);
//                            break;
//                        case AbstractPortCard.PORT_IDENTIFIER:
//                            oldValue = port.getOwsIdentifier();
//                            port.setOwsIdentifier((String) newValue);
//                            break;
//                        case AbstractPortCard.PORT_DATATYPE_FORMAT:
//                            oldValue = port.getDataTypeDescription();
//                            if (null != oldValue) {
//                                oldValue = ((DataTypeDescriptionComplex) oldValue).getFormat();
//                            }
//                            port.setDataTypeDescription(new DataTypeDescriptionComplex((ComplexDataTypeFormat) newValue));
//                    }
//                }
//                break;
//
//            default:
//            // nothing
//        }
//        return oldValue;
//    }
    @Override
    protected AbstractPropertyComponent getComponentFor(Property property) {
        AbstractPropertyComponent component;

        if (property.getComponentType().equals(ProcessPort.COMPONENTTYPE_DATATYPEDESCRIPTION_COMPLEX)) {
            component = createPropertyComplexDataTypeFormat(property);

        } else {

            component = super.getComponentFor(property);
            // TODO add property change listener (undo manager etc.)
        }

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

    private PropertyComplexDataTypeFormat createPropertyComplexDataTypeFormat(Property property) {
        PropertyComplexDataTypeFormat propertyComplexDataTypeFormat = null;
        try {
            propertyComplexDataTypeFormat = new PropertyComplexDataTypeFormat(app.getFrame(), app.getFormatProvider());
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
