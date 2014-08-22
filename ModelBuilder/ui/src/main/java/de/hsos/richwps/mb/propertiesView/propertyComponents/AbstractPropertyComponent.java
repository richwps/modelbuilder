/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyComponents;

import java.awt.Component;

/**
 *
 * @author dziegenh
 */
public abstract class AbstractPropertyComponent {

    private String name;

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
}
