/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeEvent;
import de.hsos.richwps.mb.propertiesView.propertyChange.PropertyChangeListener;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public abstract class AbstractPropertyComponent {

    private String name;

    private List<PropertyChangeListener> changeListeners;

    public AbstractPropertyComponent(String name) {
        this.name = name;
    }

    public String getPropertyName() {
        return this.name;
    }

    public abstract Object getValue();

    public abstract void setValue(Object value);

    public abstract Component getComponent();

    public abstract void setEditable(boolean editable);

    private List<PropertyChangeListener> getChangeListeners() {
        if(null == changeListeners) {
            changeListeners = new LinkedList<>();
        }

        return changeListeners;
    }

    public void addChangeListener(PropertyChangeListener listener) {
        getChangeListeners().add(listener);
    }

    public void firePropertyChange() {
        for(PropertyChangeListener listener : getChangeListeners()) {
            listener.propertyChange(new PropertyChangeEvent(null, name, this, getValue()));
        }
    }
}
