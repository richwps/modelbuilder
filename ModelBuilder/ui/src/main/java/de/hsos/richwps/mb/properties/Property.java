package de.hsos.richwps.mb.properties;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.properties.IPropertyChangeListener.PropertyChangeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.CloneFailedException;

/**
 * A property is identified by its name and (component) type and has a value.
 *
 * @author dziegenh
 */
public class Property<E> implements IObjectWithProperties, Serializable, Cloneable {

    /**
     * Used for properties which should not added to views.
     */
    public final static String COMPONENT_TYPE_NONE = "NONE";

    public final static String COMPONENT_TYPE_TEXTFIELD = "TEXTFIELD";
    public final static String COMPONENT_TYPE_INTEGER = "INTEGER";
    public final static String COMPONENT_TYPE_DOUBLE = "DOUBLE";
    public final static String COMPONENT_TYPE_DROPDOWN = "DROPDOWN";

    /**
     * Used for properties without any component type.
     */
    public final static String DEFAULT_COMPONENT_TYPE = COMPONENT_TYPE_TEXTFIELD;

    private String propertiesObjectName;

    private E value;

    private String componentType;

    private boolean editable;

    private Collection<E> possibleValues;

    private Boolean possibleValuesTransient = false;

    private transient Collection<IPropertyChangeListener> changeListeners;

    private boolean isTransient = false;

    private boolean translatable = true;

    public Property() {
        this(null);
    }

    public Property(String propertyName) {
        this(propertyName, COMPONENT_TYPE_TEXTFIELD, null);
    }

    public Property(String propertyName, String componentType, E value) {
        this(propertyName, componentType, value, false);
    }

    public Property(String propertyName, E value) {
        this(propertyName, COMPONENT_TYPE_TEXTFIELD, value, false);
    }

    public Property(String propertyName, E value, boolean editable) {
        this(propertyName, COMPONENT_TYPE_TEXTFIELD, value, editable);
    }

    public Property(String propertyName, String componentType, E value, boolean editable) {
        this.propertiesObjectName = propertyName;
        this.componentType = componentType;
        this.editable = editable;
        this.value = value;

        this.possibleValues = new LinkedList<>();
        this.changeListeners = new LinkedList<>();
    }

    public void setPropertiesObjectName(String propertiesObjectName) {
        this.propertiesObjectName = propertiesObjectName;
    }

    @Override
    public String getPropertiesObjectName() {
        return propertiesObjectName;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.setValue(value, null);
    }

    public void setValue(E value, Object source) {
        this.value = value;
        firePropertyChanged(source, PropertyChangeType.VALUE_CHANGED);
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    @Override
    public Collection<? extends IObjectWithProperties> getProperties() {
        return null;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    /**
     * Returns true if the property's name is a translatable key.
     *
     * @return
     */
    public boolean isTranslatable() {
        return translatable;
    }

    /**
     * Sets wether the property's name is a translatable key.

     * @param translatable 
     */
    public void setTranslatable(boolean translatable) {
        this.translatable = translatable;
    }
    
    /**
     * Properties are equal if their name and component type are equal.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof Property)) {
            return false;
        }

        Property other = (Property) obj;

        if (!other.getPropertiesObjectName().equals(getPropertiesObjectName())) {
            return false;
        }

        if (!other.getComponentType().equals(getComponentType())) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param values if null, possible values will be an empty list
     */
    public void setPossibleValues(Collection<E> values) {
        if (null == values) {
            values = new LinkedList<>();
        }

        this.possibleValues = values;
        // TODO add source object if necessary
        firePropertyChanged(null, PropertyChangeType.POSSIBLE_VALUES_CHANGED);
    }

    public Collection<E> getPossibleValues() {
        return possibleValues;
    }

    public void addChangeListener(IPropertyChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(IPropertyChangeListener listener) {
        changeListeners.remove(listener);
    }

    public void firePropertyChanged(Object source, PropertyChangeType changeType) {
        for (IPropertyChangeListener listener : changeListeners) {
            listener.propertyChanged(source, changeType);
        }
    }

    @Override
    public void setProperty(String propertyName, IObjectWithProperties property) {
        // a property has no nested properties => ignore setter
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    public void setIsTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public void addPossibleValue(E value) {
        possibleValues.add(value);
    }

    public void setPossibleValuesTransient(Boolean possibleValuesTransient) {
        this.possibleValuesTransient = possibleValuesTransient;
    }

    public Boolean getPossibleValuesTransient() {
        return possibleValuesTransient;
    }

    public Property<E> clone() {
        Property<E> clone = new Property<>(propertiesObjectName);

        // try to clone value
        E cloneValue = null;
        if (null != value) {
            try {
                cloneValue = ObjectUtils.cloneIfPossible(value);
            } catch (CloneFailedException ex) {
                // ignore; don't use clone
                Logger.log("Cloning value of type " + value.getClass().getSimpleName() + " failed! ");
            }
        }

        clone.setValue(cloneValue);
        clone.setEditable(editable);
        clone.setIsTransient(isTransient);
        clone.setComponentType(componentType);
        clone.setPossibleValuesTransient(possibleValuesTransient);

        if (null != possibleValues) {
            for (E pVal : possibleValues) {
                clone.addPossibleValue(pVal);
            }
        }

        return clone;
    }

}
