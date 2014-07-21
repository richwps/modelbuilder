/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.graphView.GraphModel;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class ModelCard extends AbstractPropertiesCard {

    private TitledComponent modeldataPanel;

    private MultilineLabel nameLabel;

    public ModelCard(final JPanel contentPanel) {
        super(contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM}}));
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
        if (null == modeldataPanel) {
            Component panelContent = createModeldataPanel(model);
            String processTitle = AppConstants.PROPERTIES_MODELDATA_TITLE;
            modeldataPanel = createTitledComponent(processTitle, panelContent);
            contentPanel.add(modeldataPanel, "0 0");
        } else {
            nameLabel.setText(model.getName());
//            processAbstractLabel.setText(process.getOwsAbstract());
//            processIdentifierLabel.setText(process.getIdentifier());
        }
    }

    private Component createModeldataPanel(GraphModel model) {
        JPanel modeldataPanel = new JPanel();

        double P = TableLayout.PREFERRED;
        modeldataPanel.setLayout(new TableLayout(new double[][]{{COLUMN_1_WIDTH, TableLayout.FILL}, {P, propertyBorderThickness, P, propertyBorderThickness, P}}));
        modeldataPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

//        processIdentifierLabel = createBodyLabel(process.getIdentifier());
        nameLabel = createBodyLabel(model.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        nameLabel.setEditable(true);
//        processAbstractLabel = createBodyLabel(process.getOwsAbstract());

        int y = 0;
        modeldataPanel.add(createHeadLabel(AppConstants.PROPERTIES_MODELDATA_NAME), "0 " + y);
        modeldataPanel.add(nameLabel, "1 " + y);
        y++;
//        processPanel.add(createColumn1Border(), "0 " + y);
//        processPanel.add(createColumn2Border(), "1 " + y);
//        y++;
//        processPanel.add(createHeadLabel(AppConstants.PROCESS_IDENTIFIER_LABEL), "0 " + y);
//        processPanel.add(processIdentifierLabel, "1 " + y);
//        y++;
//        processPanel.add(createColumn1Border(), "0 " + y);
//        processPanel.add(createColumn2Border(), "1 " + y);
//        y++;
//        processPanel.add(createHeadLabel("Abstract"), "0 " + y);
//        processPanel.add(processAbstractLabel, "1 " + y);

        return modeldataPanel;
    }

}
