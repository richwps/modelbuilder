/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class GlobalPortCard extends AbstractPortCard {

    private TitledComponent portPanel;

    Map<String, Component> propertyFields;
    private ProcessPort port;

    public GlobalPortCard(final Window parentWindow, final JPanel contentPanel) {
        super(parentWindow, contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM}}));

        Container panelContent = createPortPanelContainer(new ProcessPort(ProcessPortDatatype.COMPLEX), true);
        String processTitle = AppConstants.PROPERTIES_GLOBALPORTDATA_TITLE;
        portPanel = createTitledComponent(processTitle, panelContent);

        propertyFields = getPropertyFields(panelContent);

        contentPanel.add(portPanel, "0 0");
    }

    Map<String, Component> getPropertyComponents() {
        return this.propertyFields;
    }

    ProcessPort getPort() {
        return this.port;
    }

    /**
     * Updates UI after a process has been set.
     *
     * @param process
     */
    void setPort(ProcessPort port) {
        if(!port.isGlobal()) {
            throw new IllegalArgumentException("Port is not global");
        }
        
        this.port = port;

        JTextField titleField = (JTextField) propertyFields.get(AbstractPortCard.PORT_TITLE);
        JTextField abstractField = (JTextField) propertyFields.get(AbstractPortCard.PORT_ABSTRACT);
        JTextField identifierField = (JTextField) propertyFields.get(AbstractPortCard.PORT_IDENTIFIER);
        MultilineLabel typeField = (MultilineLabel) propertyFields.get(AbstractPortCard.PORT_DATATYPE);

        titleField.setText(port.getOwsTitle());
        abstractField.setText(port.getOwsAbstract());
        identifierField.setText(port.getOwsIdentifier());
        typeField.setText(port.getDatatype().toString());
    }

}
