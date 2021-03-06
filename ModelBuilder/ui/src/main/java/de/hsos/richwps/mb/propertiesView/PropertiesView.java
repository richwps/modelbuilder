package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.properties.PropertyKeyTranslator;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyDropdown;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextFieldDouble;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextFieldInteger;
import de.hsos.richwps.mb.propertiesView.propertyComponents.PropertyTextFieldString;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
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

    private final JPanel contentPanel;

    private Window parentWindow;

    private PropertyKeyTranslator translator;

    public static enum CARD {

        NO_SELECTION, PROCESS_MULTI_SELECTION, OBJECT_WITH_PROPERTIES;

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

    /**
     * Caches property components for Objects with Properties.
     */
    protected HashMap<Property, AbstractPropertyComponent> componentCache;

    public PropertiesView(String title) {
        super(title, new JPanel());

        this.propertiesCard = new PropertiesCard(parentWindow, this);
        this.componentCache = new HashMap<>();
        this.translator = new PropertyKeyTranslator();

        contentPanel = (JPanel) getComponent(1);

        contentPanel.setLayout(new CardLayout(cardGap, cardGap));
        contentPanel.add(getVoidCard(), CARD.NO_SELECTION.name());
        contentPanel.add(getMultiProcessesCard(), CARD.PROCESS_MULTI_SELECTION.name());
        contentPanel.add(propertiesCard, CARD.OBJECT_WITH_PROPERTIES.name());
    }

    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    public Window getParentWindow() {
        return parentWindow;
    }

    public void showCard(CARD card) {
        getCardLayout().show(getContentPanel(), card.name());
    }

    public PropertyKeyTranslator getPropertyKeyTranslator() {
        return this.translator;
    }

    /**
     * Clears the cache for property components.
     */
    public void clearPropertyCache() {
        final Collection<AbstractPropertyComponent> components = this.componentCache.values();
        for (AbstractPropertyComponent component : components) {
            component.dispose();
        }

        this.componentCache.clear();
    }

    /**
     * Sets the object whose properties are to be displayed.
     *
     * @param object
     */
    public void setObjectWithProperties(IObjectWithProperties object) {
        propertiesCard.setObjectWithProperties(object);
        setupPropertyFields(CARD.OBJECT_WITH_PROPERTIES, propertiesCard.getObjectWithProperties());
        showCard(CARD.OBJECT_WITH_PROPERTIES);
        invalidate();
        updateUI();
    }

    /**
     * Gets the object whose properties are displayed.
     *
     * @return
     */
    public IObjectWithProperties getCurrentObjectWithProperties() {
        return propertiesCard.objectWithProperties;
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

        if (property instanceof PropertyTextFieldString) {

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

    /**
     * Returns the Swing component for a property . Gets propety components from
     * cache if available. Otherwise, a default component is created.
     *
     * @param property
     * @return
     */
    protected AbstractPropertyComponent getComponentFor(Property property) {

        // get property component from cache
        if (componentCache.containsKey(property)) {
            return componentCache.get(property);
        }

        AbstractPropertyComponent component = null;

        // component not in cache: create it.
        switch (property.getComponentType()) {
            case Property.COMPONENT_TYPE_DROPDOWN:
                component = new PropertyDropdown(property);
                break;

            case Property.COMPONENT_TYPE_INTEGER:
                component = new PropertyTextFieldInteger(property);
                break;

            case Property.COMPONENT_TYPE_DOUBLE:
                component = new PropertyTextFieldDouble(property);
                break;
        }

        // default fallback component: textfield
        if (null == component) {
            component = new PropertyTextFieldString(property);
        }

        // add created component to cache
        this.componentCache.put(property, component);

        return component;
    }

    /**
     * Can be overwritten by sub classes to style titled components of property
     * groups.
     *
     * @param propertyGroup
     * @param groupPanel
     */
    protected void setupPropertyGroupTitledComponent(PropertyGroup<? extends IObjectWithProperties> propertyGroup, TitledComponent groupPanel) {
        groupPanel.setTitleBold();
    }
}
