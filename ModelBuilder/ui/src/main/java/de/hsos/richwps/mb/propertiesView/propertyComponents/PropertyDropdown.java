/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView.propertyComponents;

import java.awt.Component;
import javax.swing.JComboBox;

/**
 *
 * @author dziegenh
 */
public class PropertyDropdown extends AbstractPropertyComponent {

    private final JComboBox component;

    public PropertyDropdown(String propertyName, Object[] values) {
        super(propertyName);

        component = new JComboBox(values);
    }

    @Override
    public Object getValue() {
        return component.getSelectedItem();
    }

    @Override
    public void setValue(Object value) {
        component.setSelectedItem(value);
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO
    }

}
