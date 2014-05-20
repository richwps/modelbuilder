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

        // show single process card
        if (1 == processes.size()) {

            // store panel foldings
            boolean processFolded = singleProcessCard.isProcessPanelFolded();
            boolean inputsFolded = singleProcessCard.isInputsPanelFolded();
            boolean outputsFolded = singleProcessCard.isOutputsPanelFolded();

            // create new card and replace the old one
            Component tmp = singleProcessCard;
            singleProcessCard = new SingleProcessCard(new JPanel());
            singleProcessCard.setProcess(processes.get(0));
            contentPanel.remove(tmp);
            contentPanel.add(singleProcessCard, CARDS.PROCESS_SINGLE_SELECTION.name());
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_SINGLE_SELECTION.name());

            // recall panel foldings
            singleProcessCard.setProcessPanelFolded(processFolded);
            singleProcessCard.setInputsPanelFolded(inputsFolded);
            singleProcessCard.setOutputsPanelFolded(outputsFolded);

            // multiple processes selected => show multi card
        } else if (1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            getCardLayout().show(getContentPanel(), CARDS.PROCESS_MULTI_SELECTION.name());

            // nothing selected => show "void" card
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