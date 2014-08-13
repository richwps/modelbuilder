/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.propertiesView.AbstractPortCard;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
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
            public void propertyChange(PropertyChangeEvent event) {
                switch (event.getSourceCard()) {
                    case NO_SELECTION:
                        break;
                    case MODEL:
                        // TODO move String to config or new properties model
                        if (event.getProperty().equals("name")) {
                            getGraphView().setGraphName((String) event.getNewValue());
                        }
                        break;
                    case PROCESS_SINGLE_SELECTION:
                        break;
                    case PROCESS_MULTI_SELECTION:
                        break;
                    case GLOBAL_PORT:
                        Object newValue = event.getNewValue();
                        if (null != newValue
                                && newValue instanceof String
                                && (null != event.getSourceObject()
                                && event.getSourceObject() instanceof ProcessPort)) {

                            ProcessPort port = (ProcessPort) event.getSourceObject();
                            String value = (String) newValue;
                            switch (event.getProperty()) {
                                case AbstractPortCard.PORT_TITLE:
                                    port.setOwsTitle(value);
                                    break;
                                case AbstractPortCard.PORT_ABSTRACT:
                                    port.setOwsAbstract(value);
                                    break;
                                case AbstractPortCard.PORT_IDENTIFIER:
                                    port.setOwsIdentifier(value);
                                    break;
                            }
                            de.hsos.richwps.mb.Logger.log(newValue);
                        }
                        break;

                    default:
                    // nothing
                }
            }
        });
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

}
