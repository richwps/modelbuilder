/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author dziegenh
 */
public class PropertiesView extends TitledComponent {

    // TODO move magic number to config/constants. Create setter !
    private final int cardGap = 0;
    private MultiProcessCard multiProcessCard;
    private SingleProcessCard singleProcessCard;
    private JPanel voidCard;
    private JPanel contentPanel;

    protected static enum CARDS {

        NO_SELECTION, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION
    }

    public PropertiesView(String title) {
        super(title, new JPanel());

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGap, cardGap));
        contentPanel.add(getVoidCard(), CARDS.NO_SELECTION.name());
        contentPanel.add(getSingleProcessCard(), CARDS.PROCESS_SINGLE_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARDS.PROCESS_MULTI_SELECTION.name());
    }

    public void setSelectedProcesses(List<IProcessEntity> processes) {

        if (1 == processes.size()) {
            getSingleProcessCard().setProcess(processes.get(0));
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_SINGLE_SELECTION.name());
        } else if (1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_MULTI_SELECTION.name());
        } else {
            getCardLayout().show(getContentPanel(), CARDS.NO_SELECTION.name());
        }

    }

    protected CardLayout getCardLayout() {
        return (CardLayout) getContentPanel().getLayout();
    }

    private Component getVoidCard() {
        if (null == voidCard) {
            voidCard = new JPanel();
            return voidCard;
            
//            voidCard = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, 20, TableLayout.PREFERRED, 20, TableLayout.PREFERRED}}));
//
//            class ComponentTitleSetup {
//
//                final Color col1 = new Color(0x505050);
//                final Color col2 = new Color(0x808090);
//                final Color fontColor = Color.WHITE;
//
//                void setupComponentTitle(TitledComponent titledCompoment) {
//                    titledCompoment.setTitleGradientColor1(col1);
//                    titledCompoment.setTitleGradientColor2(col2);
//                    titledCompoment.setTitleFontColor(fontColor);
//                }
//            }
//            final int headerHeight = 20;
//            ComponentTitleSetup titleSetup = new ComponentTitleSetup();
//            TableModel mockTM = new DefaultTableModel(2, 2);
//            mockTM.setValueAt("Name", 0, 0);
//            mockTM.setValueAt("MSRLD5selection", 0, 1);
//            mockTM.setValueAt("Abstract", 1, 0);
//            mockTM.setValueAt("Selection of MSRLD data.", 1, 1);
//            JTable jTable = new JTable(mockTM);
//            TitledComponent procPanel = new TitledComponent("Process", jTable, headerHeight);
//            titleSetup.setupComponentTitle(procPanel);
//
//            voidCard.add(procPanel, "0 0");
//
//            mockTM = new DefaultTableModel(2, 2);
//            mockTM.setValueAt("MSRL-D5 Daten", 0, 0);
//            mockTM.setValueAt("Complex", 0, 1);
//            mockTM.setValueAt("Bewertungsjahr", 1, 0);
//            mockTM.setValueAt("Literal", 1, 1);
//            jTable = new JTable(mockTM);
//            procPanel = new TitledComponent("Inputs", jTable, headerHeight);
//            titleSetup.setupComponentTitle(procPanel);
//            voidCard.add(procPanel, "0 2");
//
//            mockTM = new DefaultTableModel(3, 2);
//            mockTM.setValueAt("relevant Algea", 0, 0);
//            mockTM.setValueAt("Complex", 0, 1);
//            mockTM.setValueAt("relevant Seagras", 1, 0);
//            mockTM.setValueAt("Complex", 1, 1);
//            mockTM.setValueAt("relevant Zears", 2, 0);
//            mockTM.setValueAt("Complex", 2, 1);
//            jTable = new JTable(mockTM);
//
//            procPanel = new TitledComponent("Outputs", jTable, headerHeight);
//            titleSetup.setupComponentTitle(procPanel);
//            voidCard.add(procPanel, "0 4");
        }
        return voidCard;
    }

    private SingleProcessCard getSingleProcessCard() {
        if (null == singleProcessCard) {
            singleProcessCard = new SingleProcessCard(new JPanel());
        }

        return singleProcessCard;
    }

    private MultiProcessCard getMultiProcessesCard() {
        if (null == multiProcessCard) {
            multiProcessCard = new MultiProcessCard();
        }

        return multiProcessCard;
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }
}
