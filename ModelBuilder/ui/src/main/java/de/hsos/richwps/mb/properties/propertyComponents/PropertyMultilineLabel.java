package de.hsos.richwps.mb.properties.propertyComponents;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Component;
import java.io.Serializable;

/**
 * A label for property values which is able to contain multiple lines. Can be
 * editable to enable user input.
 *
 * @author dziegenh
 */
public class PropertyMultilineLabel extends AbstractPropertyComponent<String> implements Serializable {

    private MultilineLabel component;

    public PropertyMultilineLabel() {
        this("", "");
    }

    public PropertyMultilineLabel(MultilineLabel component) {
        super("");
        this.component = component;
    }

    public PropertyMultilineLabel(String propertyName, String value) {
        this(propertyName, value, false);
    }
    
    public PropertyMultilineLabel(String propertyName, String value, boolean editable) {
        super(propertyName);
        component = new MultilineLabel(value, editable);
    }

    @Override
    public String getValue() {
        return component.getText();
    }

    @Override
    public void setValue(String value) {
        component.setText(value);
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
        component.setFocusable(editable);
    }

    public void setComponent(MultilineLabel multilineLabel) {
        this.component = multilineLabel;
    }
}
