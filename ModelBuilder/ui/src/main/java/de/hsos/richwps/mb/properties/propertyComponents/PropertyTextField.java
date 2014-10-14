package de.hsos.richwps.mb.properties.propertyComponents;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import java.awt.Component;
import javax.swing.JTextField;

/**
 * Simple input for property values.
 *
 * @author dziegenh
 */
public class PropertyTextField extends AbstractPropertyComponent<String> {

    private final JTextField textField = new JTextField();

    public PropertyTextField(String propertyName, String value) {
        this(propertyName, value, true);
    }

    public PropertyTextField(String propertyName, String value, boolean editable) {
        super(propertyName);
        this.textField.setText(value);
        this.textField.setEditable(editable);
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        this.textField.setText(value);
    }

    @Override
    public Component getComponent() {
        return this.textField;
    }

    @Override
    public void setEditable(boolean editable) {
        this.textField.setEditable(editable);
    }

}
