package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.IFormatSelectionListener;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyComplexDataTypeFormat;
import de.hsos.richwps.mb.properties.propertyComponents.PropertyTextField;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.ComplexDataTypeFormatLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

/**
 * Component for dynamically showing properties of modelling elements.
 *
 * @author dziegenh
 */
public class PropertiesView extends TitledComponent {

    private final int cardGap = 0;

    private MultiProcessCard multiProcessCard;
    private SingleProcessCard singleProcessCard;
    private JPanel voidCard;

//    private ModelCard modelCard;
    private PropertiesCard modelCard;

    private JPanel contentPanel;

    private Window parentWindow;
    private GlobalPortCard globalPortCard;

    public static enum CARD {

        NO_SELECTION, MODEL, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION, GLOBAL_PORT;

        private static final HashMap<CARD, String> namesForViews = new HashMap<>();

        @Override
        public String toString() {
            if (namesForViews.containsKey(this)) {
                return namesForViews.get(this);
            }

            String name = UiHelper.createStringForViews(name());
            namesForViews.put(this, name);
            return name;
        }
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

        propertyChangeListeners = new LinkedList<>();
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
        getModelCard().setObjectWithProperties(model); //setModel(model);
        setupPropertyFields(CARD.MODEL, model);

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

//    private ModelCard getModelCard() {
    private PropertiesCard getModelCard() {
        if (null == modelCard) {
//            modelCard = new ModelCard(parentWindow, new JPanel());
            modelCard = new PropertiesCard(parentWindow, new JPanel());
        }

        return modelCard;
    }

    private void setupPropertyFields(final CARD card, final IObjectWithProperties property) {
//        for (IObjectWithProperties property : objectWithProperties.getProperties()) {
//            for (final AbstractPropertyComponent property : properties.getPropertyComponents().values()) {

        if (property instanceof PropertyTextField) {
            final AbstractPropertyComponent propertyComponent = (AbstractPropertyComponent) property;

            // listen to changes
            propertyComponent.getComponent().addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    firePropertyChangeEvent(card, property.getPropertiesObjectName(), property, propertyComponent.getValue());
                }
            });
            // setup GUI
            JTextField label = (JTextField) ((AbstractPropertyComponent) property).getComponent();
            CompoundBorder border = new CompoundBorder(new ColorBorder(PropertyCardsConfig.headLabelBgColor, 2, 0, 0, 1), label.getBorder());
            label.setBorder(border);
            label.setFont(label.getFont().deriveFont(PropertyCardsConfig.propertyFieldFontSize));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    parentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            });
        }

        if (null != property.getProperties()) {
            for (IObjectWithProperties aProperty : property.getProperties()) {
                setupPropertyFields(card, aProperty);
            }
        }

        // TODO setup additional components
//                    else if (property instanceof PropertyComplexDataTypeFormat) {
//                    ComplexDataTypeFormatLabel component = (ComplexDataTypeFormatLabel) property.getComponent();
//                    component.addSelectionListener(new IFormatSelectionListener() {
//                        @Override
//                        public void formatSelected(ComplexDataTypeFormat format) {
//                            firePropertyChangeEvent(card, property.getPropertyName(), getGlobalPortCard().getPort(), format);
//                        }
//                    });
//                }
    }
//        }
//}

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

    protected AbstractPropertyComponent getPropertyComponent(CARD card, String property) {
        List<AbstractPropertyComponent> properties = null;

        switch (card) {
            case GLOBAL_PORT:
                properties = getGlobalPortCard().getPropertyFields();
                break;
            case MODEL:
                properties = getModelCard().getPropertyFields();
                break;
        }

        for (AbstractPropertyComponent component : properties) {
            if (component.getPropertiesObjectName().equals(property)) {
                return component;
            }
        }

        return null;
    }

    private GlobalPortCard getGlobalPortCard() {
        if (null == globalPortCard) {
            globalPortCard = new GlobalPortCard(parentWindow, new JPanel());

            // add listeners to fire property changes
            for (final AbstractPropertyComponent property : globalPortCard.getPropertyFields()) {
                if (property instanceof PropertyTextField) {
                    property.getComponent().addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            firePropertyChangeEvent(CARD.GLOBAL_PORT, property.getPropertiesObjectName(), getGlobalPortCard().getPort(), property.getValue());
                        }
                    });

                } else if (property instanceof PropertyComplexDataTypeFormat) {
                    ComplexDataTypeFormatLabel component = (ComplexDataTypeFormatLabel) property.getComponent();
                    component.addSelectionListener(new IFormatSelectionListener() {
                        @Override
                        public void formatSelected(ComplexDataTypeFormat format) {
                            firePropertyChangeEvent(CARD.GLOBAL_PORT, property.getPropertiesObjectName(), getGlobalPortCard().getPort(), format);
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
