package de.hsos.richwps.mb.properties;

import de.hsos.richwps.mb.properties.IPropertyChangeListener.PropertyChangeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A property is identified by its name and (component) type and has a value.
 *
 * @author dziegenh
 */
public class Property<E> implements IObjectWithProperties, Serializable {

    public final static String COMPONENT_TYPE_TEXTFIELD = "TEXTFIELD";
    public final static String COMPONENT_TYPE_DROPDOWN = "DROPDOWN";

    private String propertiesObjectName;

    private E value;

    private String componentType;

    private boolean editable;
    private Collection<E> possibleValues;

    private Collection<IPropertyChangeListener> changeListeners;
    private boolean isTransient = false;

    public Property() {
        this(null);
    }

    public Property(String propertyName) {
        this(propertyName, COMPONENT_TYPE_TEXTFIELD, null);
    }

    public Property(String propertyName, String componentType) {
        this(propertyName, componentType, null);
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

    public void setPossibleValues(Collection<E> values) {
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

    public void removeChangeListener(IPropertyChangeListener listner) {
        changeListeners.remove(listner);
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
    
    public Property<E> clone() {
        Property<E> clone = new Property<>(propertiesObjectName);
        clone.setValue(value);
        clone.setEditable(editable);
        clone.setIsTransient(isTransient);
        clone.setComponentType(componentType);

        for(E pVal : possibleValues) {
            clone.addPossibleValue(pVal);
        }
        
        return clone;
    }
    
}
