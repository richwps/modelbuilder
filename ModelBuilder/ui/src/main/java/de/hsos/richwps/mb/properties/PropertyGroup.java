package de.hsos.richwps.mb.properties;

import java.util.HashMap;

/**
 * A group of property fields / components.
 *
 * @author dziegenh
 * @see AbstractPropertyComponent
 */
public class PropertyGroup {

    private HashMap<String, AbstractPropertyComponent> propertyComponents;
    private String name;

    public PropertyGroup(String name) {
        this.name = name;
        propertyComponents = new HashMap<>();
    }

    public String getName() {
        return name;
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

        String propertyName = component.getPropertyName();
        if (null == propertyName || propertyName.isEmpty()) {
            throw new IllegalArgumentException("Property component has no valid property name.");
        }

        propertyComponents.put(propertyName, component);
    }

}
