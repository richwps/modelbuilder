package de.hsos.richwps.mb.propertiesView.propertyChange;

import de.hsos.richwps.mb.propertiesView.PropertiesView.CARD;

/**
 * Event which is fired when a property value changes.
 *
 * @author dziegenh
 */
public class PropertyChangeEvent {

    protected CARD sourceCard;

    protected String property;

    protected Object sourceObject;
    protected Object newValue;

    public String getProperty() {
        return property;
    }

    public Object getSourceObject() {
        return sourceObject;
    }

    public Object getNewValue() {
        return newValue;
    }

    public CARD getSourceCard() {
        return sourceCard;
    }

    public PropertyChangeEvent(CARD sourceCard, String property, Object sourceObject, Object newValue) {
        this.sourceCard = sourceCard;
        this.property = property;
        this.newValue = newValue;
        this.sourceObject = sourceObject;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("SourceCard: ");
        sb.append(sourceCard.name());
        sb.append(", property: ");
        sb.append(property);
        sb.append(", sourceObject: ");
        sb.append(sourceObject);
        sb.append(", newValue: ");
        sb.append(newValue);
        return sb.toString();
    }

}
