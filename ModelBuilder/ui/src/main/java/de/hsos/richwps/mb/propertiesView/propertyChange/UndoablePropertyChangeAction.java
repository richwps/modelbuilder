package de.hsos.richwps.mb.propertiesView.propertyChange;

import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;

/**
 * Undoable action for property value changes. Undo and redo are done by
 * toggeling between the new and old property value.
 *
 * @author dziegenh
 */
public class UndoablePropertyChangeAction {
    
    protected Property property;
    protected Property propertyWithOldValue;
    
    private boolean isSwitched = false;
    private final IObjectWithProperties parentObject;

    public UndoablePropertyChangeAction(Property property, Property propertyWithOldValue, IObjectWithProperties parentObject) {
        this.property = property;
        this.propertyWithOldValue = propertyWithOldValue;
        this.parentObject = parentObject;
    }

    public IObjectWithProperties getParentObject() {
        return parentObject;
    }
    
    public void undo() {
        if (!isSwitched) {
            this.switchEventPropertyValues();
        }
    }

    public void redo() {
        if (isSwitched) {
            switchEventPropertyValues();
        }
    }

    private void switchEventPropertyValues() {
        // switch values
        Object tmpValue = property.getValue();
        property.setValue(propertyWithOldValue.getValue());
        propertyWithOldValue.setValue(tmpValue);
        
        // toggle switch flag
        isSwitched = !isSwitched;
    }

}
