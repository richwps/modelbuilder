package de.hsos.richwps.mb.properties;

import java.awt.Component;
import java.io.Serializable;
import java.util.Collection;

/**
 * Basic class for GUI components which represent a property. Properties are
 * identified by their name.
 *
 * @author dziegenh
 * @param <C>
 */
public abstract class AbstractPropertyComponent<C extends Component, E> implements IObjectWithProperties, Serializable {

    private String propertiesObjectName;

    public AbstractPropertyComponent() {
    }

    /**
     * A name must be given to identify the property which the component
     * represents.
     *
     * @param name
     */
    public AbstractPropertyComponent(String name) {
        this.propertiesObjectName = name;
    }

    /**
     * Gets the name (=identifier) of the property.
     *
     * @return
     */
    @Override
    public String getPropertiesObjectName() {
        return this.propertiesObjectName;
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

    /**
     * A component doesn't have nested properties.
     *
     * @return
     */
    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return null;
    }

    public void setPropertiesObjectName(String propertiesObjectName) {
        this.propertiesObjectName = propertiesObjectName;
    }

}
