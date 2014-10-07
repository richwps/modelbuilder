package de.hsos.richwps.mb.propertiesView.propertyComponents;

import java.awt.Component;
import javax.swing.JComboBox;

/**
 * Property GUI component which enables selecting a property value from a
 * dropdown combobox. Currently no longer used, but may be useful later.
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
