package de.hsos.richwps.mb.properties.propertyComponents;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;

/**
 * A label for property values which is able to contain multiple lines. Can be
 * editable to enable user input.
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
