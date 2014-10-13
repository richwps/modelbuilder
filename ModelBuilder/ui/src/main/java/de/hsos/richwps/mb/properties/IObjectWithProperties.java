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
    public Collection<PropertyGroup> getPropertyGroups();

    /**
     * Returns a specific property value.
     *
     * @param propertyName
     * @return
     */
    public Object getValueOf(String propertyName);
}
