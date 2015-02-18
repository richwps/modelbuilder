package de.hsos.richwps.mb.propertiesView.propertyComponents;

import de.hsos.richwps.mb.properties.IPropertyChangeListener;
import de.hsos.richwps.mb.properties.Property;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JComboBox;

/**
 * Property GUI component which enables selecting a property value from a
 * dropdown combobox.
 *
 * @author dziegenh
 */
public class PropertyDropdown<E extends Object> extends AbstractPropertyComponent<JComboBox<E>, E> {

    private final transient JComboBox<E> component;

    public PropertyDropdown(final Property property) {
        super(property);

        // initially add all possible values to the combobox
        component = new JComboBox<>((E[]) property.getPossibleValues().toArray());

        // update items when the property's possible values changed
        property.addChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChanged(Object source, IPropertyChangeListener.PropertyChangeType changeType) {
                switch (changeType) {
                    case POSSIBLE_VALUES_CHANGED:
                        component.removeAllItems();
                        for (Object item : property.getPossibleValues()) {
                            component.addItem((E) item);
                        }
                        break;
                }
            }
        });

        // update property value on selection
        component.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                property.setValue(component.getSelectedItem());
                setValue((E) component.getSelectedItem());
            }
        });

        selectPropertyValue();
    }

    private void selectPropertyValue() {
        // property has a value: select item
        if (property.getValue() != null) {
            component.setSelectedItem(property.getValue());
        } else {

            // no property value: set it to the first possible item if available
            Collection<E> possibleValues = property.getPossibleValues();
            if (null != possibleValues && possibleValues.iterator().hasNext()) {
                property.setValue(possibleValues.iterator().next());
            }
        }
    }

    @Override
    public E getValue() {
        return component.getModel().getElementAt(component.getSelectedIndex());
    }

    @Override
    public JComboBox<E> getComponent() {
        return component;
    }

    public void setItems(E[] items) {
        component.removeAllItems();

        for (E anItem : items) {
            component.addItem(anItem);
        }
    }

    @Override
    public void setProperty(Property<E> property) {
        // not allowed
    }

    @Override
    protected void propertyValueChanged() {
        getComponent().setSelectedItem(property.getValue());
    }

}
