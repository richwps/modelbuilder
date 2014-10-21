package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.IPropertyChangeListener;
import de.hsos.richwps.mb.properties.Property;
import javax.swing.JComboBox;

/**
 * Property GUI component which enables selecting a property value from a
 * dropdown combobox.
 *
 * @author dziegenh
 */
public class PropertyDropdown<E extends Object> extends AbstractPropertyComponent<JComboBox<E>, E> {

    private final JComboBox<E> component;

    public PropertyDropdown(final Property property) {
        super(property);

        component = new JComboBox<>((E[]) property.getPossibleValues().toArray());

        property.addChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChanged() {
                component.removeAllItems();
                for (Object item : property.getPossibleValues()) {
                    component.addItem((E) item);
                }
            }
        });
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
