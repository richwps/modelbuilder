package de.hsos.richwps.mb.properties;

/**
 * A group of property fields / components.
 *
 * @author dziegenh
 * @see AbstractPropertyComponent
 */
public class PropertyComponentGroup extends PropertyGroup<AbstractPropertyComponent> {

    public PropertyComponentGroup() {
        this("");
    }

    public PropertyComponentGroup(String name) {
        super(name);
    }
}
