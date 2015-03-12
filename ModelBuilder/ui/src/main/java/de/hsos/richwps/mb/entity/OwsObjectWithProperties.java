package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.io.Serializable;
import java.util.Collection;

/**
 * Base class for OWS entities (Processes, Ports). Necessary (or at least
 * helpful) for encoding/decoding using the specific mxCodecs.
 *
 * @author dziegenh
 */
public abstract class OwsObjectWithProperties implements IObjectWithProperties, IOwsObject, Serializable {

    public final static String PROPERTIES_KEY_IDENTIFIER = "Identifier";
    public final static String PROPERTIES_KEY_TITLE = "Title";
    public final static String PROPERTIES_KEY_ABSTRACT = "Abstract";
    public static final String PROPERTY_KEY_OWS_GROUP = "OWS Data";

    protected PropertyGroup<Property> owsGroup;

    protected String toolTipText;

    public OwsObjectWithProperties(String owsIdentifier) {
        createProperties(owsIdentifier);
    }

    public OwsObjectWithProperties() {
        this("");
    }

    /**
     * Sets the title and resets the toolTipText.
     *
     * @param owsTitle
     */
    @Override
    public void setOwsTitle(String owsTitle) {
        owsGroup.getPropertyObject(PROPERTIES_KEY_TITLE).setValue(owsTitle);
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     *
     * @param owsIdentifier
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        owsGroup.getPropertyObject(PROPERTIES_KEY_IDENTIFIER).setValue(owsIdentifier);
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     *
     * @param owsAbstract
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        owsGroup.getPropertyObject(PROPERTIES_KEY_ABSTRACT).setValue(owsAbstract);
        toolTipText = null;
    }

    @Override
    public String getOwsIdentifier() {
        return (String) owsGroup.getPropertyObject(PROPERTIES_KEY_IDENTIFIER).getValue();
    }

    @Override
    public String getOwsTitle() {
        return (String) owsGroup.getPropertyObject(PROPERTIES_KEY_TITLE).getValue();
    }

    @Override
    public String getOwsAbstract() {
        return (String) owsGroup.getPropertyObject(PROPERTIES_KEY_ABSTRACT).getValue();
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    protected OwsObjectWithProperties cloneInto(OwsObjectWithProperties clone) {
        for (Property property : owsGroup.getProperties()) {
            IObjectWithProperties propertyClone = (IObjectWithProperties) property.clone();
            clone.setProperty(property.getPropertiesObjectName(), propertyClone);
        }

        // indicate lazy init
        clone.setToolTipText(null);

        return clone;
    }

    @Override
    public String getPropertiesObjectName() {
        return getOwsIdentifier();
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return owsGroup.getProperties();
    }

    protected void createProperties(String owsIdentifier) {
        if (null == owsGroup) {
            owsGroup = new PropertyGroup(PROPERTY_KEY_OWS_GROUP);
        }

        if (!owsGroup.hasProperty(PROPERTIES_KEY_IDENTIFIER)) {
            owsGroup.addObject(new Property<>(PROPERTIES_KEY_IDENTIFIER, Property.COMPONENT_TYPE_TEXTFIELD, owsIdentifier));
        }
        if (!owsGroup.hasProperty(PROPERTIES_KEY_TITLE)) {
            owsGroup.addObject(new Property<>(PROPERTIES_KEY_TITLE, Property.COMPONENT_TYPE_TEXTFIELD, ""));
        }
        if (!owsGroup.hasProperty(PROPERTIES_KEY_ABSTRACT)) {
            owsGroup.addObject(new Property<>(PROPERTIES_KEY_ABSTRACT, Property.COMPONENT_TYPE_TEXTFIELD, ""));
        }
    }

    /**
     *
     * @param propertyName
     * @param property if null, the property will be removed from this object.
     */
    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {

        // property is null => remove it
        if (null == property) {
            this.owsGroup.removeProperty(propertyName);

            // property is the main OWS group -> replace the current group
        } else if (property instanceof PropertyGroup && propertyName.equals(PROPERTY_KEY_OWS_GROUP)) {
            this.owsGroup = (PropertyGroup<Property>) property;
        } // add property to main ows group
        else {
            this.owsGroup.setProperty(propertyName, property);
        }
    }

    @Override
    public void setPropertiesObjectName(String name) {
        setOwsIdentifier(name);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    public Object getPropertyValue(String key) {
        Property property = owsGroup.getPropertyObject(key);

        if (null == property) {
            return null;
        }

        return property.getValue();
    }

    public abstract IObjectWithProperties clone();

    public static String getValueForViews(String value) {
        return (null == value || value.isEmpty()) ? "-" : value.trim();
    }

    /**
     * Replaces all values by cloning the values of the given object.
     *
     * @param other
     */
    public void copyValuesFrom(OwsObjectWithProperties other) {
        if (System.identityHashCode(this) == System.identityHashCode(other)) {
            return;
        }

        this.toolTipText = null;
        this.owsGroup = new PropertyGroup<>(PROPERTY_KEY_OWS_GROUP);

        for (IObjectWithProperties property : other.owsGroup.getProperties()) {
            IObjectWithProperties propertyClone = property.clone();
            this.setProperty(property.getPropertiesObjectName(), propertyClone);
        }
    }

}
