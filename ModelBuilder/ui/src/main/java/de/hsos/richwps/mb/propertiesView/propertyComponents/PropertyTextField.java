package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

/**
 * Simple input for property values.
 *
 * @author dziegenh
 */
public class PropertyTextField extends AbstractPropertyComponent<Component, String> {

    /**
     * Textfield for user input (only for editable properties).
     */
    private final JTextField textField = new JTextField();

    public PropertyTextField(Property<String> property) {
        super(property);
        this.textField.setText(property.getValue());
        this.textField.setEditable(property.isEditable());

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setValue(textField.getText());
            }
        });
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public Component getComponent() {
        if (property.isEditable()) {
            return this.textField;
        } else {
            return new MultilineLabel(property.getValue());
        }

    }

    @Override
    public void setEditable(boolean editable) {
        this.textField.setEditable(editable);
        property.setEditable(editable);
    }

}
