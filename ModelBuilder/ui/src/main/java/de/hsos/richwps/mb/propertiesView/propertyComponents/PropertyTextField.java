/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView.propertyComponents;

import java.awt.Component;
import javax.swing.JTextField;

/**
 *
 * @author dziegenh
 */
public class PropertyTextField extends AbstractPropertyComponent {

    private final JTextField textField = new JTextField();

    public PropertyTextField(String proptertyName, String value) {
        super(proptertyName);
        this.textField.setText(value);
    }

    @Override
    public Object getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(Object value) {
        this.textField.setText((String) value);
    }

    @Override
    public Component getComponent() {
        return this.textField;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO
    }

}
