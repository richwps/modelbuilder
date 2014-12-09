package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Simple input for property values.
 *
 * @author dziegenh
 */
public abstract class PropertyTextField<E extends Object> extends AbstractPropertyComponent<Component, E> {

    /**
     * Textfield for user input (only for editable properties).
     */
    private final transient JTextField textField = new JTextField();

    public PropertyTextField() {
        super();
    }

    public PropertyTextField(Property<E> property) {
        super(property);

        this.textField.setText(valueToString());
        this.textField.setEditable(property.isEditable());

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setValue(parseValue(textField.getText()));
                textField.setText(valueToString());
            }
        });
    }

    private String valueToString() {
        String valueText = " ";
        if (null != property.getValue()) {
            valueText = property.getValue().toString();
        }

        return valueText;
    }

    protected abstract E parseValue(String value);

    @Override
    public E getValue() {
        // Textfield content must be parsed by implementers.
        return parseValue(this.textField.getText());
    }

    @Override
    public Component getComponent() {
        if (property.isEditable()) {
            return this.textField;

        } else {
            MultilineLabel label = new MultilineLabel(getValueForViews());
            label.setBorder(new EmptyBorder(2, 2, 0, 2));
            return label;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof PropertyTextField)) {
            return false;
        }

        PropertyTextField other = (PropertyTextField) obj;
        return UiHelper.equalOrBothNull(other.property, property)
                && UiHelper.equalOrBothNull(other.textField, textField);
    }

    @Override
    protected void propertyValueChanged() {
        this.textField.setText(property.getValue().toString());
    }

    @Override
    public String getValueForViews() {
        return valueToString();
    }

}
