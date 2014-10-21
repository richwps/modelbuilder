package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyDropdown;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextField;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
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
    private JPanel voidCard;

    private PropertiesCard propertiesCard;

    private JPanel contentPanel;

    private Window parentWindow;

    public static enum CARD {

        NO_SELECTION, MODEL, PROCESS_SINGLE_SELECTION, PROCESS_MULTI_SELECTION, GLOBAL_PORT, OBJECT_WITH_PROPERTIES;

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

        this.propertiesCard = new PropertiesCard(parentWindow, this);

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGap, cardGap));
        contentPanel.add(getVoidCard(), CARD.NO_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARD.PROCESS_MULTI_SELECTION.name());
        contentPanel.add(propertiesCard, CARD.OBJECT_WITH_PROPERTIES.name());

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
        // TODO remove when only the App is refactored and only uses one properties card.
        if (card.equals(CARD.GLOBAL_PORT) || card.equals(CARD.MODEL) || card.equals(CARD.PROCESS_SINGLE_SELECTION)) {
            card = CARD.OBJECT_WITH_PROPERTIES;
        }

        getCardLayout().show(getContentPanel(), card.name());
    }

    public void setObjectWithProperties(IObjectWithProperties object) {
        propertiesCard.setObjectWithProperties(object);
        setupPropertyFields(CARD.OBJECT_WITH_PROPERTIES, propertiesCard.getObjectWithProperties());
        showCard(CARD.OBJECT_WITH_PROPERTIES);
        invalidate();
        updateUI();
    }

    public void setObjectsWithProperties(List<? extends IObjectWithProperties> objects) {
        if (1 == objects.size()) {
            setObjectWithProperties(objects.get(0));

            // multiple objects selected => show multi card
        } else if (1 < objects.size()) {
            getMultiProcessesCard().setProcesses(null);
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

    private void setupPropertyFields(final CARD card, final IObjectWithProperties property) {

        if (property instanceof PropertyTextField) {
            final AbstractPropertyComponent propertyComponent = (AbstractPropertyComponent) property;

            // listen to changes
//            propertyComponent.getComponent().addFocusListener(new FocusAdapter() {
//                @Override
//                public void focusLost(FocusEvent e) {
//                    firePropertyChangeEvent(card, property.getPropertiesObjectName(), property, propertyComponent.getValue());
//                }
//            });
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

        if (null != property && null != property.getProperties()) {
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

    private MultiProcessCard getMultiProcessesCard() {
        if (null == multiProcessCard) {
            multiProcessCard = new MultiProcessCard();
        }

        return multiProcessCard;
    }

    protected JPanel getContentPanel() {
        return contentPanel;
    }

    protected AbstractPropertyComponent getComponentFor(Property property) {
        if(property.getComponentType().equals(Property.COMPONENT_TYPE_DROPDOWN)) {
            return new PropertyDropdown(property);
        }

        return new PropertyTextField(property);
    }

}
