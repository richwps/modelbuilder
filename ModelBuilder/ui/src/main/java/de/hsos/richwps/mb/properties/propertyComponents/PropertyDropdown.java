package de.hsos.richwps.mb.properties.propertyComponents;

import de.hsos.richwps.mb.properties.AbstractPropertyComponent;
import javax.swing.JComboBox;

/**
 * Property GUI component which enables selecting a property value from a
 * dropdown combobox. Currently no longer used, but may be useful later.
 *
 * @author dziegenh
 */
public class PropertyDropdown<E> extends AbstractPropertyComponent<JComboBox<E>, E> {

    private final JComboBox<E> component;

    public PropertyDropdown(String propertyName, Object[] values) {
        super(propertyName);

        component = new JComboBox(values);
    }

    @Override
    public E getValue() {
        return component.getModel().getElementAt(component.getSelectedIndex());
    }

    @Override
    public void setValue(E value) {
        component.setSelectedItem(value);
    }

    @Override
    public JComboBox<E> getComponent() {
        return component;
    }

    @Override
    public void setEditable(boolean editable) {
        component.setEditable(editable);
    }

    public void setItems(E[] items) {
        component.removeAllItems();

        for (E anItem : items) {
            component.addItem(anItem);
        }
    }

}
