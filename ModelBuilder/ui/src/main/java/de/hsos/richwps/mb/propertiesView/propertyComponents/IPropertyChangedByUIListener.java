package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;

/**
 * Fired when the UI component triggered a property change.
 *
 * @author dziegenh
 */
public interface IPropertyChangedByUIListener {

    public void propertyChanged(Property property, Property propertyBeforeChange);

}
