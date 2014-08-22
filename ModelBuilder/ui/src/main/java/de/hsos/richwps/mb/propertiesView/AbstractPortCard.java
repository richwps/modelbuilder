/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyComplexDataTypeFormat;
import de.hsos.richwps.mb.semanticProxy.boundary.LoadDataTypesException;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class AbstractPortCard extends AbstractPropertiesCard {

    public AbstractPortCard(Window parentWindow, JPanel contentPanel) {
        super(parentWindow, contentPanel);
    }

    /**
     * Shortcut method.
     *
     * @param port
     * @return
     */
//    protected JPanel createPortPanelContainer(ProcessPort port, boolean editable) {
//        List<ProcessPort> tmpList = new LinkedList<>();
//        tmpList.add(port);
//        return createPortsPanelContainer(tmpList, editable);
//        return createSinglePortPanelContainer
//    }
    protected JPanel createPortsPanelContainer(List<ProcessPort> ports) {
        return createPortsPanelContainer(ports, false);
    }

    protected JPanel createPortPanelContainer(ProcessPort port, boolean editable) {
        double P = TableLayout.PREFERRED;

        double[][] layoutSize = new double[][]{{COLUMN_1_WIDTH, TableLayout.FILL}, {
            P, propertyBorderThickness,
            P, propertyBorderThickness,
            P, propertyBorderThickness,
            P, propertyBorderThickness,
            P, P
        }};
//            int panelRowsPlusBorders = 2 * panelRows - 1; // additional rows for borders
//            layoutSize[0] = new double[]{};
//            layoutSize[1] = new double[panelRowsPlusBorders];
//            for (int r = 0; r < panelRowsPlusBorders; r++) {
//                layoutSize[1][r] = (1 == r % 2) ? propertyBorderThickness : P;
//            }
        JPanel portPanel = new JPanel();
        portPanel.setLayout(new TableLayout(layoutSize));
        portPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

        int r = 0;
        r = createPortPanel(port, portPanel, editable);
//                portPanel.add(createPortBorder(), "0 " + r + " 1 " + r++);
//            }
//        }

        return portPanel;
    }

    protected JPanel createPortsPanelContainer(List<ProcessPort> ports, boolean editable) {

//                 TODO calculate/get the magic number !!
//        int panelRows = 6 * ports.size();
        JPanel portsPanel = new JPanel();
        double P = TableLayout.PREFERRED;

        if (0 < ports.size()) {
            double[][] layoutSize = new double[2][];
            int panelRowsPlusBorders = ports.size(); //2 * panelRows - 1; // additional rows for borders
            layoutSize[0] = new double[]{TableLayout.FILL};
            layoutSize[1] = new double[panelRowsPlusBorders];
            for (int r = 0; r < panelRowsPlusBorders; r++) {
//                layoutSize[1][r] = (1 == r % 2) ? propertyBorderThickness : P;
                layoutSize[1][r] = P;
            }
            portsPanel.setLayout(new TableLayout(layoutSize));
            int y = 0;
            for (ProcessPort port : ports) {
                JPanel aPortPanel = createPortPanelContainer(port, editable);
                portsPanel.add(aPortPanel, "0 " + y++);
            }

        }

        // TODO calculate/get the magic number !!
//        int panelRows = 6 * ports.size();
//
//        JPanel portsPanel = new JPanel();
//        double P = TableLayout.PREFERRED;
//
//        if (0 < panelRows) {
//            double[][] layoutSize = new double[2][];
//            int panelRowsPlusBorders = 2 * panelRows - 1; // additional rows for borders
//            layoutSize[0] = new double[]{COLUMN_1_WIDTH, TableLayout.FILL};
//            layoutSize[1] = new double[panelRowsPlusBorders];
//            for (int r = 0; r < panelRowsPlusBorders; r++) {
//                layoutSize[1][r] = (1 == r % 2) ? propertyBorderThickness : P;
//            }
//
//            portsPanel.setLayout(new TableLayout(layoutSize));
//            portsPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));
//
//            int r = 0;
//            for (ProcessPort port : ports) {
//                r = createPortPanel(port, portsPanel, r, editable);
//                if (r != panelRows) {
//                    portsPanel.add(createPortBorder(), "0 " + r + " 1 " + r++);
//                }
//            }
//        }
//
        return portsPanel;
    }

    /**
     * Returns rowOffset + number of added rows.
     *
     * @param port
     * @param parent
     * @param rowOffset
     * @return
     */
    protected int createPortPanel(ProcessPort port, Container parent, boolean editable) {

        int rowOffset = 0;

        // Row 1
        Component titleLabel;
        if (editable) {
            titleLabel = createBodyComponent(PORT_TITLE, port.getOwsTitle(), true);
        } else {
            MultilineLabel tmpLabel = createBodyLabel(port.getOwsTitle());
            tmpLabel.setFont(tmpLabel.getFont().deriveFont(Font.BOLD));
            titleLabel = tmpLabel;
        }
        parent.add(createHeadLabel("Title"), "0 " + rowOffset);
        parent.add(titleLabel, "1 " + rowOffset++);

        // Row 2
        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        // Row 3
        parent.add(createHeadLabel("Identifier"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_IDENTIFIER, port.getOwsIdentifier(), editable), "1 " + rowOffset++);

        // Row 4
        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        // Row 5
        parent.add(createHeadLabel("Abstract"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_ABSTRACT, port.getOwsAbstract(), editable), "1 " + rowOffset++);

        // Row 6
        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        // Row 7
        parent.add(createHeadLabel("Datatype"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_DATATYPE, port.getDatatype().toString(), false), "1 " + rowOffset++);

        // Row 8
        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        // Row 9
        parent.add(createHeadLabel("Format"), "0 " + rowOffset);
        // TODO move to create method
        ComplexDataTypeFormat cdtvalue = null;
        IDataTypeDescription dataTypeDescription = port.getDataTypeDescription();
        if (null != dataTypeDescription && dataTypeDescription instanceof DataTypeDescriptionComplex) {
            DataTypeDescriptionComplex complexDescription = (DataTypeDescriptionComplex) dataTypeDescription;
            cdtvalue = complexDescription.getFormat();
        }
        try {
            PropertyComplexDataTypeFormat formatProperty = new PropertyComplexDataTypeFormat();
            formatProperty.setEditable(editable);
            formatProperty.setValue(cdtvalue);
            parent.add(formatProperty.getComponent(), "1 " + rowOffset++);
        } catch (LoadDataTypesException ex) {
            // TODO handle or throw exception
        }

        return rowOffset;
    }

    // property constants.
    public static final String PORT_DATATYPE = "datatype";
    public static final String PORT_DATATYPE_FORMAT = "datatype_description";
    public static final String PORT_ABSTRACT = "abstract";
    public static final String PORT_IDENTIFIER = "identifier";
    public static final String PORT_TITLE = "title";

    private Component createBodyComponent(String property, String content, boolean editable) {
        if (editable) {
            return createEditablePropertyField(property, content);

        } else {
            final Component c = createBodyLabel(content);
            getPropertyFields().add(new AbstractPropertyComponent(property) {
                @Override
                public Object getValue() {
                    return ((MultilineLabel) getComponent()).getText();
                }

                @Override
                public void setValue(Object value) {
                    ((MultilineLabel) getComponent()).setText((String) value);
                }

                @Override
                public Component getComponent() {
                    return c;
                }

                @Override
                public void setEditable(boolean editable) {
                    // nothing to edit
                }

            });

            return c;
        }

    }

    protected Component createPortBorder() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(portBorderColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

}
