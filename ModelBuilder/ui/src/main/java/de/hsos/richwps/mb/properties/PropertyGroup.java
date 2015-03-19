package de.hsos.richwps.mb.properties;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A group of objects with properties.
 *
 * @author dziegenh
 * @param <E>
 */
public class PropertyGroup<E extends IObjectWithProperties> implements IObjectWithProperties, Serializable {

    private HashMap<String, E> propertyObjects;
    private String propertiesObjectName;
    private boolean isTransient = false;

    private boolean translatable = true;

    public PropertyGroup() {
        this("");
    }

    public PropertyGroup(String name) {
        this.propertiesObjectName = name;
        propertyObjects = new LinkedHashMap<>();
    }

    /**
     * Returns true if the property's name is a translatable key.
     *
     * @return
     */
    @Override
    public boolean isTranslatable() {
        return translatable;
    }

    /**
     * Sets wether the property's name is a translatable key.
     *
     * @param translatable
     */
    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }

    public void setPropertyComponents(HashMap<String, E> propertyObjects) {
        this.propertyObjects = propertyObjects;
    }

    public E getPropertyObject(String propertyName) {
        return propertyObjects.get(propertyName);
    }

    public boolean hasProperty(String propertyName) {
        return propertyObjects.containsKey(propertyName);
    }

    public void removeProperty(String propertyName) {
        propertyObjects.remove(propertyName);
    }

    public void addObject(E object) {
        if (null == object) {
            throw new IllegalArgumentException("Trying to add a null object.");
        }

        String propertyName = object.getPropertiesObjectName();
        if (null == propertyName || propertyName.isEmpty()) {
            throw new IllegalArgumentException("Trying to add a property object without a valid property name.");
        }

        propertyObjects.put(propertyName, object);
    }

    @Override
    public String getPropertiesObjectName() {
        return propertiesObjectName;
    }

    @Override
    public Collection<? extends E> getProperties() {
        return propertyObjects.values();
    }

    @Override
    public void setPropertiesObjectName(String propertiesObjectName) {
        this.propertiesObjectName = propertiesObjectName;
    }

    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        this.propertyObjects.put(propertyName, (E) property);
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    public void setIsTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public void clear() {
        this.propertyObjects.clear();
    }
    
    public PropertyGroup<E> clone() {
        PropertyGroup<E> clone = new PropertyGroup<>(propertiesObjectName);
        clone.setIsTransient(isTransient);

        for (Entry<String, E> pEntry : this.propertyObjects.entrySet()) {

            E value = pEntry.getValue();
            if (null != value) {
                value = (E) value.clone();
            }

            clone.setProperty(pEntry.getKey(), value);
        }

        return clone;
    }

    public void addAll(Collection<E> properties) {
        for (E iObjectWithProperties : properties) {
            addObject(iObjectWithProperties);
        }
    }

}
