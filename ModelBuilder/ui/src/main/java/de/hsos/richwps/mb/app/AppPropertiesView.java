/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.propertiesView.AbstractPortCard;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import de.hsos.richwps.mb.propertiesView.propertyChange.UndoablePropertyChangeAction;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author dziegenh
 */
public class AppPropertiesView extends PropertiesView {

    private App app;

    public AppPropertiesView(App app) {
        super(app.getFrame(), AppConstants.PROPERTIES_PANEL_TITLE);
        this.app = app;

        app.getGraphView().addSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (getGraphView().hasSelection()) {
                    GraphView.ELEMENT_TYPE type = getGraphView().getSelectedElementType();
                    if (null != type && type.equals(GraphView.ELEMENT_TYPE.GLOBAL_PORT)) {
                        setSelectedGlobalPorts(getGraphView().getSelectedGlobalPorts());
                    } else {
                        setSelectedProcesses(getGraphView().getSelectedProcesses());
                    }
                } else {
                    showCard(PropertiesView.CARD.MODEL);
                }
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                final Object oldValue = setPropertyValue(event);

                boolean bothNull = (null == event.getNewValue()) && (null == oldValue);
                boolean valuesEqual = (null != event.getNewValue() && event.getNewValue().equals(oldValue))
                        || (null != oldValue && oldValue.equals(event.getNewValue()));
                if (!bothNull && !valuesEqual) {
                    AbstractPropertyComponent component = getPropertyComponent(event.getSourceCard(), event.getProperty());
                    UndoablePropertyChangeAction undoAction = new UndoablePropertyChangeAction(component, event, oldValue);
                    undoAction.addChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent event) {
                            setPropertyValue(event);
                        }
                    });

                    String undoTitle = String.format(AppConstants.PROPERTIES_PROPERTY_EDIT, event.getProperty());
                    getApp().getUndoManager().addEdit(new AppUndoableEdit(this, undoAction, undoTitle));
                }
            }
        });
    }

    private Object setPropertyValue(PropertyChangeEvent event) {
        Object oldValue = null;

        switch (event.getSourceCard()) {
            case NO_SELECTION:
                break;
            case MODEL:
                // TODO move String to config or new properties model
                if (event.getProperty().equals("name")) {
                    oldValue = getGraphView().getGraphName();
                    getGraphView().setGraphName((String) event.getNewValue());
                }
                break;
            case PROCESS_SINGLE_SELECTION:
                break;
            case PROCESS_MULTI_SELECTION:
                break;
            case GLOBAL_PORT:
                Object newValue = event.getNewValue();
                if ((null != event.getSourceObject()
                        && event.getSourceObject() instanceof ProcessPort)) {

                    ProcessPort port = (ProcessPort) event.getSourceObject();

                    switch (event.getProperty()) {
                        case AbstractPortCard.PORT_TITLE:
                            oldValue = port.getOwsTitle();
                            port.setOwsTitle((String) newValue);
                            break;
                        case AbstractPortCard.PORT_ABSTRACT:
                            oldValue = port.getOwsAbstract();
                            port.setOwsAbstract((String) newValue);
                            break;
                        case AbstractPortCard.PORT_IDENTIFIER:
                            oldValue = port.getOwsIdentifier();
                            port.setOwsIdentifier((String) newValue);
                            break;
                        case AbstractPortCard.PORT_DATATYPE_FORMAT:
                            oldValue = port.getDataTypeDescription();
                            if (null != oldValue) {
                                oldValue = ((DataTypeDescriptionComplex) oldValue).getFormat();
                            }
                            port.setDataTypeDescription(new DataTypeDescriptionComplex((ComplexDataTypeFormat) newValue));
                    }
                }
                break;

            default:
            // nothing
        }

        return oldValue;
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

    private App getApp() {
        return this.app;
    }
}
