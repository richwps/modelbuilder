/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyChange;

import de.hsos.richwps.mb.propertiesView.PropertiesView.CARD;

/**
 *
 * @author dziegenh
 */
public class PropertyChangeEvent {

    protected CARD sourceCard;

    protected String property;

    protected Object oldValue;
    protected Object newValue;

    public String getProperty() {
        return property;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public CARD getSourceCard() {
        return sourceCard;
    }


    public PropertyChangeEvent(CARD sourceCard, String property, Object oldValue, Object newValue) {
        this.sourceCard = sourceCard;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        StringBuilder sb =new StringBuilder(200);
        sb.append("SourceCard: ");
        sb.append(sourceCard.name());
        sb.append(", property: ");
        sb.append(property);
        sb.append(", oldValue: ");
        sb.append(oldValue);
        sb.append(", newValue: ");
        sb.append(newValue);
        return sb.toString();
    }

}
