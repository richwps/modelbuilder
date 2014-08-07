/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

    private Window parentWindow;
    private GlobalPortCard globalPortCard;

    public static enum CARD {

        NO_SELECTION, MODEL, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION, GLOBAL_PORT
    }

    private LinkedList<PropertyChangeListener> propertyChangeListeners;

    public PropertiesView(Window parentWindow, String title) {
        super(title, new JPanel());

        this.parentWindow = parentWindow;

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGap, cardGap));
        contentPanel.add(getVoidCard(), CARD.NO_SELECTION.name());
        contentPanel.add(getModelCard(), CARD.MODEL.name());
        contentPanel.add(getSingleProcessCard(), CARD.PROCESS_SINGLE_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARD.PROCESS_MULTI_SELECTION.name());
        contentPanel.add(getGlobalPortCard(), CARD.GLOBAL_PORT.name());

        propertyChangeListeners = new LinkedList<PropertyChangeListener>();
    }

    public boolean addPropertyChangeListener(PropertyChangeListener listener) {
        return propertyChangeListeners.add(listener);
    }

    void firePropertyChangeEvent(CARD sourceCard, String property, Object oldValue, Object newValue) {
        firePropertyChangeEvent(new PropertyChangeEvent(sourceCard, property, oldValue, newValue));
    }

    void firePropertyChangeEvent(PropertyChangeEvent propertyChangeEvent) {
        for (PropertyChangeListener listener : propertyChangeListeners) {
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

    public void setSelectedGlobalPorts(List<ProcessPort> ports) {
        // show single process card
        if (1 == ports.size()) {
            getGlobalPortCard().setPort(ports.get(0));
            showCard(CARD.GLOBAL_PORT);

            // multiple processes selected => show multi card
        } else if (1 < ports.size()) {
            getMultiProcessesCard().setProcesses(null);
            showCard(CARD.PROCESS_MULTI_SELECTION);

            // nothing selected => show "void" card
        } else {
            showCard(CARD.NO_SELECTION);
        }
    }

    public void setSelectedProcesses(List<ProcessEntity> processes) {

        // show single process card
        if (1 == processes.size()) {

            // store panel foldings
            boolean processFolded = singleProcessCard.isProcessPanelFolded();
            boolean inputsFolded = singleProcessCard.isInputsPanelFolded();
            boolean outputsFolded = singleProcessCard.isOutputsPanelFolded();

            // create new card and replace the old one
            Component tmp = singleProcessCard;
            singleProcessCard = new SingleProcessCard(parentWindow, new JPanel());
            singleProcessCard.setProcess(processes.get(0));
            contentPanel.remove(tmp);
            contentPanel.add(singleProcessCard, CARD.PROCESS_SINGLE_SELECTION.name());
            showCard(CARD.PROCESS_SINGLE_SELECTION);

            // recall panel foldings
            singleProcessCard.setProcessPanelFolded(processFolded);
            singleProcessCard.setInputsPanelFolded(inputsFolded);
            singleProcessCard.setOutputsPanelFolded(outputsFolded);

            // multiple processes selected => show multi card
        } else if (1 < processes.size()) {
            getMultiProcessesCard().setProcesses(processes);
            showCard(CARD.PROCESS_MULTI_SELECTION);

            // nothing selected => show "void" card
        } else {
            showCard(CARD.NO_SELECTION);
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
            modelCard = new ModelCard(parentWindow, new JPanel());
            for (final JTextField field : modelCard.getPropertyFields().values()) {
                field.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        JTextField field = (JTextField) e.getSource();
                        firePropertyChangeEvent(CARD.MODEL, field.getName(), getModelCard().getModel(), field.getText());
                    }
                });
            }
        }

        return modelCard;
    }

    private SingleProcessCard getSingleProcessCard() {
        if (null == singleProcessCard) {
            singleProcessCard = new SingleProcessCard(parentWindow, new JPanel());
        }

        return singleProcessCard;
    }

    private MultiProcessCard getMultiProcessesCard() {
        if (null == multiProcessCard) {
            multiProcessCard = new MultiProcessCard();
        }

        return multiProcessCard;
    }

    private GlobalPortCard getGlobalPortCard() {
        if (null == globalPortCard) {
            globalPortCard = new GlobalPortCard(parentWindow, new JPanel());
            Collection<Component> properties = globalPortCard.getPropertyComponents().values();
            for (Component property : properties) {
                if (property instanceof JTextField) {
                    JTextField field = (JTextField) property;
                    field.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            JTextField field = (JTextField) e.getSource();
                            firePropertyChangeEvent(CARD.GLOBAL_PORT, field.getName(), getGlobalPortCard().getPort(), field.getText());
                        }

                    });
                }
            }
        }

        return globalPortCard;
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }

}
