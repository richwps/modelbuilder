/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.graphView.GraphModel;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.LinkedList;
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
    private ModelCard modelCard;

    private JPanel voidCard;
    private JPanel contentPanel;


    public static enum CARD {
        NO_SELECTION, MODEL, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION
    }

    private LinkedList<PropertyChangeListener> propertyChangeListeners;
    
    public PropertiesView(String title) {
        super(title, new JPanel());

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGap, cardGap));
        contentPanel.add(getVoidCard(), CARD.NO_SELECTION.name());
        contentPanel.add(getModelCard(), CARD.MODEL.name());
        contentPanel.add(getSingleProcessCard(), CARD.PROCESS_SINGLE_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARD.PROCESS_MULTI_SELECTION.name());

        propertyChangeListeners = new LinkedList<PropertyChangeListener>();
    }

    public boolean addPropertyChangeListener(PropertyChangeListener listener) {
        return propertyChangeListeners.add(listener);
    }

    void firePropertyChangeEvent(CARD sourceCard, String property, Object oldValue, Object newValue) {
        firePropertyChangeEvent(new PropertyChangeEvent(sourceCard, property, oldValue, newValue));
    }

    void firePropertyChangeEvent(PropertyChangeEvent propertyChangeEvent) {
        for(PropertyChangeListener listener : propertyChangeListeners) {
            listener.propertyChange(propertyChangeEvent);
        }
    }

    public void showCard(CARD card) {
        getCardLayout().show(getContentPanel(), card.name());
    }

    // TODO create graph model properties as a common layer which GraphView and PropertiesView can use!
    public void setModel(GraphModel model) {
        getModelCard().setModel(model);
        getCardLayout().show(getContentPanel(), CARD.MODEL.name());
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
            contentPanel.add(singleProcessCard, CARD.PROCESS_SINGLE_SELECTION.name());
            getCardLayout().show(getContentPanel(), CARD.PROCESS_SINGLE_SELECTION.name());

            // recall panel foldings
            singleProcessCard.setProcessPanelFolded(processFolded);
            singleProcessCard.setInputsPanelFolded(inputsFolded);
            singleProcessCard.setOutputsPanelFolded(outputsFolded);

            // multiple processes selected => show multi card
        } else if (1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            getCardLayout().show(getContentPanel(), CARD.PROCESS_MULTI_SELECTION.name());

            // nothing selected => show "void" card
        } else {
            getCardLayout().show(getContentPanel(), CARD.NO_SELECTION.name());
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

    private ModelCard getModelCard() {
        if (null == modelCard) {
            modelCard = new ModelCard(new JPanel());
        }

        return modelCard;
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
