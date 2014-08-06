/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.ui.TitledComponent;
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

//    private MultilineLabel nameLabel;
//    private JTextField nameLabel;

    Map<String, JTextField> propertyFields;

    public GlobalPortCard(final Window parentWindow, final JPanel contentPanel) {
        super(parentWindow, contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM}}));

        Container panelContent = createPortPanelContainer(new ProcessPort(ProcessPortDatatype.COMPLEX), true);
        String processTitle = AppConstants.PROPERTIES_GLOBALPORTDATA_TITLE;
        portPanel = createTitledComponent(processTitle, panelContent);

        propertyFields = getPropertyFields(panelContent);

        contentPanel.add(portPanel, "0 0");
    }

    /**
     * Updates UI after a process has been set.
     *
     * @param process
     */
    void setPort(ProcessPort port) {
//        nameLabel.setText(model.getName());
    }

//    private Component createModeldataPanel(GraphModel model) {
//        JPanel modeldataPanel = new JPanel();
//
//        double P = TableLayout.PREFERRED;
//        modeldataPanel.setLayout(new TableLayout(new double[][]{{COLUMN_1_WIDTH, TableLayout.FILL}, {P, propertyBorderThickness, P, propertyBorderThickness, P}}));
//        modeldataPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));
//
//        String name = (null != model) ? model.getName() : "";
//        nameLabel = createEditablePropertyField("name", name);
//        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
////        nameLabel.setEditable(true);
//
//        int y = 0;
//        modeldataPanel.add(createHeadLabel(AppConstants.PROPERTIES_MODELDATA_NAME), "0 " + y);
//        modeldataPanel.add(nameLabel, "1 " + y);
//        y++;
//
//        return modeldataPanel;
//    }

}
