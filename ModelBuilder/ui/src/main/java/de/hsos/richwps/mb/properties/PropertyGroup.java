package de.hsos.richwps.mb.properties;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A group of objects with properties.
 *
 * @author dziegenh
 */
public class PropertyGroup<E extends IObjectWithProperties> implements IObjectWithProperties, Serializable {

    private HashMap<String, E> propertyObjects;
    private String propertiesObjectName;

    public PropertyGroup() {
        this("");
    }

    public PropertyGroup(String name) {
        this.propertiesObjectName = name;
        propertyObjects = new LinkedHashMap<>();
    }

    public void setPropertyComponents(HashMap<String, E> propertyObjects) {
        this.propertyObjects = propertyObjects;
    }

    public HashMap<String, E> getPropertyComponents() {
        return propertyObjects;
    }

    public E getPropertyObject(String propertyName) {
        return propertyObjects.get(propertyName);
    }

    public void addObject(E object) {
        if (null == object) {
            throw new IllegalArgumentException("Property object cannot be null.");
        }

        String propertyName = object.getPropertiesObjectName();
        if (null == propertyName || propertyName.isEmpty()) {
            throw new IllegalArgumentException("Property object has no valid property name.");
        }

        propertyObjects.put(propertyName, object);
    }

    @Override
    public String getPropertiesObjectName() {
        return propertiesObjectName;
    }

    @Override
    public Collection<? extends E> getProperties() {
        return getPropertyComponents().values();
    }

    public void setPropertiesObjectName(String propertiesObjectName) {
        this.propertiesObjectName = propertiesObjectName;
    }

}
