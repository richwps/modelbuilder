/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;

/**
 *
 * @author dziegenh
 */
public class PropertyMultilineLabel extends AbstractPropertyComponent {

    private MultilineLabel component;

    public PropertyMultilineLabel(String propertyName) {
        super(propertyName);
    }

    @Override
    public Object getValue() {
        return component.getText();
    }

    @Override
    public void setValue(Object value) {
        component.setText((String) value);
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
    }

    public void setComponent(MultilineLabel multilineLabel) {
        this.component = multilineLabel;
    }
}
