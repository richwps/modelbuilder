package de.hsos.richwps.mb.properties;

import java.util.Collection;

/**
 * Implemented by objects which should take part at the "properties"-mechanism.
 * An object with properties has a list of PropertyGroups. Implementers can be
 * added to a "properties view" component and/or a property-change event system.
 *
 * @author dziegenh
 * @see
 */
public interface IObjectWithProperties {

    /**
     * Returns true if the property (value) is transient should not be persisted.
     *
     * @return
     */
    public boolean isTransient();

    /**
     * Sets the object's property name.
     *
     * @param name
     */
    public void setPropertiesObjectName(String name);

    /**
     * Return the object's name which is used as a key.
     *
     * @return
     */
    public String getPropertiesObjectName();

    /**
     * Returns the property groups holding the property components of this
     * object.
     *
     * @return
     */
    public Collection<? extends IObjectWithProperties> getProperties();

    /**
     * Sets a specific property.
     *
     * @param propertyName identifies the property
     * @param property the property to be set
     */
    public void setProperty(String propertyName, IObjectWithProperties property);

}
