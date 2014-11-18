package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.IPropertyChangeListener;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Basic class for GUI components which represent a property.
 *
 * @author dziegenh
 */
public abstract class AbstractPropertyComponent<C extends Component, E> implements Serializable {

    protected Property<E> property;

    protected transient List<IPropertyChangedByUIListener> propertyUIChangeListeners;

    protected transient IPropertyChangeListener changeListener = new IPropertyChangeListener() {

        @Override
        public void propertyChanged(Object source, IPropertyChangeListener.PropertyChangeType changeType) {

            // TODO check if the source is not this component (equals doesn't work)!
//            if (null != source && !source.equals(this)) {
            propertyValueChanged();
//            }
        }
    };

    public AbstractPropertyComponent() {
    }

    public AbstractPropertyComponent(Property property) {
        this.property = property;

        property.addChangeListener(changeListener);
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
            property.setValue(value, this);
            firePropertyUIChange(oldValue);
        }
    }

    /**
     * Called by the property change listener when it received an event.
     */
    protected abstract void propertyValueChanged();

    /**
     * Gets the propertie's GUI component.
     *
     * @return
     */
    public abstract C getComponent();

    public void setProperty(Property<E> property) {
        if (null != this.property) {
            this.property.removeChangeListener(changeListener);
        }

        this.property = property;
        this.property.addChangeListener(changeListener);
    }

}
