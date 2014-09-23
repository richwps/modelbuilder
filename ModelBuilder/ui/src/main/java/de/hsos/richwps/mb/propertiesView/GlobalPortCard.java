/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.ComplexDataTypeFormatLabel;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class GlobalPortCard extends AbstractPortCard {

    private TitledComponent portPanel;

//    Map<String, Component> propertyFields;
    private ProcessPort port;

    protected JTextField titleField;
    protected JTextField abstractField;
    protected JTextField identifierField;
    protected MultilineLabel typeField;

    protected ComplexDataTypeFormatLabel datatypeDescription;

    public GlobalPortCard(final Window parentWindow, final JPanel contentPanel) {
        super(parentWindow, contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM}}));

        Container panelContent = createPortPanelContainer(new ProcessPort(ProcessPortDatatype.COMPLEX), true);
        String processTitle = AppConstants.PROPERTIES_GLOBALPORTDATA_TITLE;
        portPanel = createTitledComponent(processTitle, panelContent);

        for (AbstractPropertyComponent field : propertyFields) {
            switch (field.getPropertyName()) {
                case PORT_TITLE:
                    titleField = (JTextField) field.getComponent();
                    break;
                case PORT_ABSTRACT:
                    abstractField = (JTextField) field.getComponent();
                    break;
                case PORT_IDENTIFIER:
                    identifierField = (JTextField) field.getComponent();
                    break;
                case PORT_DATATYPE:
                    typeField = (MultilineLabel) field.getComponent();
                    break;
                case PORT_DATATYPE_FORMAT:
                    datatypeDescription = (ComplexDataTypeFormatLabel) field.getComponent();
                    break;
            }
        }

        contentPanel.add(portPanel, "0 0");
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
        if (!port.isGlobal()) {
            throw new IllegalArgumentException("Port is not global");
        }

        this.port = port;

        titleField.setText(port.getOwsTitle());
        abstractField.setText(port.getOwsAbstract());
        identifierField.setText(port.getOwsIdentifier());
        typeField.setText(port.getDatatype().toString());

        boolean hasDescription = (null != port.getDatatype()) 
                && (port.getDatatype().equals(ProcessPortDatatype.COMPLEX))
                && port.isGlobal();
        datatypeDescription.setEditable(hasDescription);
        ComplexDataTypeFormat format = null;
        if (hasDescription && null != port.getDataTypeDescription()) {
            format = ((DataTypeDescriptionComplex) port.getDataTypeDescription()).getFormat();
        }

        datatypeDescription.setComplexDataTypeFormat(format);
    }

}
