/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.entity;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author dziegenh
 */
public class OwsObjectWithProperties implements IObjectWithProperties, IOwsObject, Serializable {

    public static String KEY_IDENTIFIER = "Identifier";
    public static String KEY_TITLE = "Title";
    public static String KEY_ABSTRACT = "Abstract";
    public static String KEY_VERSION = "Version";

    protected final String OWS_PROPERTY_GROUP_NAME = "OWS Data";

    protected PropertyGroup<Property> owsGroup;

    protected String toolTipText;

//    protected HashMap<String, Property> properties = new HashMap<>();
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
        owsGroup.getPropertyObject(KEY_TITLE).setValue(owsTitle);
        toolTipText = null;
    }

    /**
     * Sets the identifier and resets the toolTipText.
     *
     * @param owsIdentifier
     */
    @Override
    public void setOwsIdentifier(String owsIdentifier) {
        owsGroup.getPropertyObject(KEY_IDENTIFIER).setValue(owsIdentifier);
        toolTipText = null;
    }

    /**
     * Sets the abstract and resets the toolTipText.
     *
     * @param owsAbstract
     */
    @Override
    public void setOwsAbstract(String owsAbstract) {
        owsGroup.getPropertyObject(KEY_ABSTRACT).setValue(owsAbstract);
        toolTipText = null;
    }

    @Override
    public String getOwsIdentifier() {
        return (String) owsGroup.getPropertyObject(KEY_IDENTIFIER).getValue();
    }

    @Override
    public String getOwsTitle() {
        return (String) owsGroup.getPropertyObject(KEY_TITLE).getValue();
    }

    @Override
    public String getOwsAbstract() {
        return (String) owsGroup.getPropertyObject(KEY_ABSTRACT).getValue();
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    protected OwsObjectWithProperties cloneInto(OwsObjectWithProperties clone) {
        for (Property property : owsGroup.getProperties()) {
            clone.setProperty(property.getPropertiesObjectName(), (IObjectWithProperties) property.clone());
        }

        clone.setToolTipText(toolTipText);

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
        owsGroup = new PropertyGroup(OWS_PROPERTY_GROUP_NAME);
        owsGroup.addObject(new Property<>(KEY_IDENTIFIER, Property.COMPONENT_TYPE_TEXTFIELD, owsIdentifier));
        owsGroup.addObject(new Property<>(KEY_TITLE, Property.COMPONENT_TYPE_TEXTFIELD, ""));
        owsGroup.addObject(new Property<>(KEY_ABSTRACT, Property.COMPONENT_TYPE_TEXTFIELD, ""));
        owsGroup.addObject(new Property<>(KEY_VERSION, Property.COMPONENT_TYPE_TEXTFIELD, ""));
    }


    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        if (property instanceof PropertyGroup) {
            if (propertyName.equals(OWS_PROPERTY_GROUP_NAME)) {
                this.owsGroup = (PropertyGroup<Property>) property;
            }

        } else {

            // set property
            this.owsGroup.setProperty(propertyName, (Property) property);
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

}
