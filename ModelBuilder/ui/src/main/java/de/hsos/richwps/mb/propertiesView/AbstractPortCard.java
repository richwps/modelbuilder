/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.util.LinkedList;
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
    protected JPanel createPortPanelContainer(ProcessPort port, boolean editable) {
        List<ProcessPort> tmpList = new LinkedList<>();
        tmpList.add(port);
        return createPortsPanelContainer(tmpList, editable);
    }

    protected JPanel createPortsPanelContainer(List<ProcessPort> ports) {
        return createPortsPanelContainer(ports, false);
    }

    protected JPanel createPortsPanelContainer(List<ProcessPort> ports, boolean editable) {
        // TODO calculate/get the magic number !!
        int panelRows = 4 * ports.size();

        JPanel portsPanel = new JPanel();
        double P = TableLayout.PREFERRED;

        if (0 < panelRows) {
            double[][] layoutSize = new double[2][];
            int panelRowsPlusBorders = 2 * panelRows - 1;
            layoutSize[0] = new double[]{COLUMN_1_WIDTH, TableLayout.FILL};
            layoutSize[1] = new double[panelRowsPlusBorders];// additional rows for borders
            for (int r = 0; r < panelRowsPlusBorders; r++) {
                layoutSize[1][r] = (1 == r % 2) ? propertyBorderThickness : P;
            }

            portsPanel.setLayout(new TableLayout(layoutSize));
            portsPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

            int r = 0;
            for (ProcessPort inport : ports) {
                r = createPortPanel(inport, portsPanel, r, editable);
                if (r != panelRows) {
                    portsPanel.add(createPortBorder(), "0 " + r + " 1 " + r++);
                }
            }
        }

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
    protected int createPortPanel(ProcessPort port, Container parent, int rowOffset, boolean editable) {

        Component titleLabel;
        if(editable) {
            titleLabel = createBodyComponent(PORT_TITLE, port.getOwsTitle(), true);
        } else {
            MultilineLabel tmpLabel = createBodyLabel(port.getOwsTitle());
            tmpLabel.setFont(tmpLabel.getFont().deriveFont(Font.BOLD));
            titleLabel = tmpLabel;
        }
        parent.add(createHeadLabel("Title"), "0 " + rowOffset);
        parent.add(titleLabel, "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Identifier"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_IDENTIFIER, port.getOwsIdentifier(), editable), "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Abstract"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_ABSTRACT, port.getOwsAbstract(), editable), "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Datatype"), "0 " + rowOffset);
        parent.add(createBodyComponent(PORT_DATATYPE, port.getDatatype().toString(), false), "1 " + rowOffset++);

        return rowOffset;
    }

    // property constants.
    public static final String PORT_DATATYPE = "datatype";
    public static final String PORT_ABSTRACT = "abstract";
    public static final String PORT_IDENTIFIER = "identifier";
    public static final String PORT_TITLE = "title";

    private Component createBodyComponent(String property, String content, boolean editable) {
        if (editable) {
            return createEditablePropertyField(property, content);
        } else {
            Component c = createBodyLabel(content);
            c.setName(property);
            return c;
        }

    }

    protected Component createPortBorder() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(portBorderColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

}
