package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;
import java.awt.Component;
import java.io.Serializable;

/**
 * Basic class for GUI components which represent a property. Properties are
 * identified by their name.
 *
 * @author dziegenh
 * @param <C>
 */
public abstract class AbstractPropertyComponent<C extends Component, E> implements Serializable {

    protected Property<E> property;

    public AbstractPropertyComponent() {
    }

    /**
     * A name must be given to identify the property which the component
     * represents.
     *
     * @param name
     */
    public AbstractPropertyComponent(Property property) {
        this.property = property;
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
    public abstract void setValue(E value);

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
