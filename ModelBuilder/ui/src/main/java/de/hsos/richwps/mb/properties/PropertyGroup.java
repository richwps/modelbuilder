package de.hsos.richwps.mb.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A group of property fields / components.
 *
 * @author dziegenh
 * @see AbstractPropertyComponent
 */
public class PropertyGroup implements IObjectWithProperties {

    private HashMap<String, AbstractPropertyComponent> propertyComponents;
    private String name;

    public PropertyGroup(String name) {
        this.name = name;
        propertyComponents = new LinkedHashMap<>();
    }

    public HashMap<String, AbstractPropertyComponent> getPropertyComponents() {
        return propertyComponents;
    }

    public AbstractPropertyComponent getPropertyComponent(String propertyName) {
        return propertyComponents.get(propertyName);
    }

    public void addComponent(AbstractPropertyComponent component) {
        if (null == component) {
            throw new IllegalArgumentException("Property component cannot be null.");
        }

        String propertyName = component.getPropertiesObjectName();
        if (null == propertyName || propertyName.isEmpty()) {
            throw new IllegalArgumentException("Property component has no valid property name.");
        }

        propertyComponents.put(propertyName, component);
    }

    @Override
    public String getPropertiesObjectName() {
        return name;
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return getPropertyComponents().values();
    }

}
