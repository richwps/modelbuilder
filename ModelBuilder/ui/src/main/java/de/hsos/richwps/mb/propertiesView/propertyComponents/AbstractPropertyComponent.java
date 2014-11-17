package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Basic class for GUI components which represent a property. Properties are
 * identified by their name.
 *
 * @author dziegenh
 * @param <C>
 */
public abstract class AbstractPropertyComponent<C extends Component, E> implements Serializable {

    protected Property<E> property;

    protected List<IPropertyChangedByUIListener> propertyUIChangeListeners;

    public AbstractPropertyComponent() {
    }

    /**
     * A name must be given to identify the property which the component
     * represents.
     *
     * @param property
     */
    public AbstractPropertyComponent(Property property) {
        this.property = property;
    }

    public void addPropertyChangedByUIListener(IPropertyChangedByUIListener listner) {
        getPropertyUIChangeListerners().add(listner);
    }

    protected List<IPropertyChangedByUIListener> getPropertyUIChangeListerners() {
        if (null == propertyUIChangeListeners) {
            propertyUIChangeListeners = new LinkedList<>();
        }

        return propertyUIChangeListeners;
    }

    protected void firePropertyUIChange(E oldValue) {
        Property<E> propertyWithOldValue = property.clone();
        propertyWithOldValue.setValue(oldValue);

        // TODO check what information is necessary for listeners
        for (IPropertyChangedByUIListener listener : getPropertyUIChangeListerners()) {
            listener.propertyChanged(property, propertyWithOldValue);
        }
    }

    public Property<E> getProperty() {
        return property;
    }

    /**
     * Gets the property value.
     *
     * @return
     */
    public abstract E getValue();

    /**
     * Sets the property value.
     *
     * @param value
     */
    protected void setValue(E value) {
        E oldValue = property.getValue();

        boolean bothEqual = UiHelper.equalOrBothNull(value, oldValue);

        // only set new value if it's not equal to the current value
        if (!bothEqual) {
            property.setValue(value);
            firePropertyUIChange(oldValue);
        }
    }

    /**
     * Gets the propertie's GUI component.
     *
     * @return
     */
    public abstract C getComponent();

    /**
     * Property components can/must decide by theirselves if they enable
     * editing.
     *
     * @param editable
     */
    public abstract void setEditable(boolean editable);

    public void setProperty(Property<E> property) {
        this.property = property;
        setValue(property.getValue());
    }

}
