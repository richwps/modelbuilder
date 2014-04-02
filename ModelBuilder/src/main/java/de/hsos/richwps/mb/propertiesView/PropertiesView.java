/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author dziegenh
 */
public class PropertiesView extends JPanel {

    private final int cardGHap = 10;
    private MultiProcessCard multiProcessCard;
    private SingleProcessCard singleProcessCard;
    private JPanel voidCard;

    protected static enum CARDS {

        NO_SELECTION, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION
    }

    public PropertiesView() {
        super();

        setLayout(new CardLayout(cardGHap, cardGHap));
        add(getVoidCard(), CARDS.NO_SELECTION.name());
        add(getSingleProcessCard(), CARDS.PROCESS_SINGLE_SELECTION.name());
        add(getMultiProcessesCard(), CARDS.PROCESS_MULTI_SELECTION.name());
//        add(new JLabel(""));
    }

    public void setSelectedProcesses(List<IProcessEntity> processes) {

        if(1 == processes.size()) {
            getSingleProcessCard().setProcess(processes.get(0));
            getCardLayout().show(this, CARDS.PROCESS_SINGLE_SELECTION.name());
        }

        else if(1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            getCardLayout().show(this, CARDS.PROCESS_MULTI_SELECTION.name());
        }
        
        else {
            getCardLayout().show(this, CARDS.NO_SELECTION.name());
        }

    }

    protected CardLayout getCardLayout() {
        return (CardLayout) getLayout();
    }


    private Component getVoidCard() {
        if(null == voidCard) {
            voidCard = new JPanel();
        }
        return voidCard;
    }


    private SingleProcessCard getSingleProcessCard() {
        if (null == singleProcessCard) {
            singleProcessCard = new SingleProcessCard();
        }

        return singleProcessCard;
    }

    private MultiProcessCard getMultiProcessesCard() {
        if (null == multiProcessCard) {
            multiProcessCard = new MultiProcessCard();
        }

        return multiProcessCard;
    }
}