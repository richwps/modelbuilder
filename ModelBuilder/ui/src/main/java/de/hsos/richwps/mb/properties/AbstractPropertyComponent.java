package de.hsos.richwps.mb.properties;

import java.awt.Component;

/**
 * Basic class for GUI components which represent a property value. Properties
 * are identified by their name.
 *
 * @author dziegenh
 */
public abstract class AbstractPropertyComponent {

    private String name;

    /**
     * A name must be given to identify the property which the component
     * represents.
     *
     * @param name
     */
    public AbstractPropertyComponent(String name) {
        this.name = name;
    }

    /**
     * Gets the name (=identifier) of the property.
     *
     * @return
     */
    public String getPropertyName() {
        return this.name;
    }

    /**
     * Gets the property value.
     *
     * @return
     */
    public abstract Object getValue();

    /**
     * Sets the property value.
     *
     * @param value
     */
    public abstract void setValue(Object value);

    /**
     * Gets the propertie's GUI component.
     *
     * @return
     */
    public abstract Component getComponent();

    /**
     * Property components can/must decide by theirselves if they enable
     * editing.
     *
     * @param editable
     */
    public abstract void setEditable(boolean editable);

}
