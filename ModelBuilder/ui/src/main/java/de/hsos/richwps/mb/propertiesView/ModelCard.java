/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JPanel;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class ModelCard extends AbstractPropertiesCard {

    private TitledComponent modeldataPanel;

//    private MultilineLabel nameLabel;
    private JTextField nameLabel;

    public ModelCard(final Window parentWindow, final JPanel contentPanel) {
        super(parentWindow, contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM}}));

        Component panelContent = createModeldataPanel(null);
        String processTitle = AppConstants.PROPERTIES_MODELDATA_TITLE;
        modeldataPanel = createTitledComponent(processTitle, panelContent);
        contentPanel.add(modeldataPanel, "0 0");
    }

    /**
     * Updates UI after a process has been set.
     *
     * @param process
     */
    void setModel(GraphModel model) {
        setModelPanelData(model);
    }

    /**
     * Adds a Process Overview Panel to the main Panel
     *
     * @param process
     */
    private void setModelPanelData(GraphModel model) {
        nameLabel.setText(model.getName());
    }

    private Component createModeldataPanel(GraphModel model) {
        JPanel modeldataPanel = new JPanel();

        double P = TableLayout.PREFERRED;
        modeldataPanel.setLayout(new TableLayout(new double[][]{{COLUMN_1_WIDTH, TableLayout.FILL}, {P, propertyBorderThickness, P, propertyBorderThickness, P}}));
        modeldataPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

        String name = (null != model) ? model.getName() : "";
        nameLabel = createEditablePropertyField("name", name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
//        nameLabel.setEditable(true);

        int y = 0;
        modeldataPanel.add(createHeadLabel(AppConstants.PROPERTIES_MODELDATA_NAME), "0 " + y);
        modeldataPanel.add(nameLabel, "1 " + y);
        y++;

        return modeldataPanel;
    }

}
