package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.app.view.qos.QosTargetPanel;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;

/**
 * Property GUI component representing a single QoS target.
 *
 * @author dziegenh
 */
public class PropertyComponentQosTarget extends AbstractPropertyComponent<QosTargetPanel, QoSTarget> {

    private final QosTargetPanel component;

    public static String COMPONENT_TYPE = "A QoS Target";

    
    public PropertyComponentQosTarget(Property<QoSTarget> property)  {
        super(property);
        
        component = new QosTargetPanel(property);
    }

    @Override
    public QoSTarget getValue() {
        return property.getValue();
    }

    @Override
    public QosTargetPanel getComponent() {
        return component;
    }

    protected void setEditable(boolean editable) {
//        component.setEditable(editable);
        property.setEditable(editable);
    }

    @Override
    public void setProperty(Property<QoSTarget> property) {
        super.setProperty(property);
        component.setQosProperty(property);
        setEditable(property.isEditable());
    }

    @Override
    protected void propertyValueChanged() {
        component.setQosProperty(property);
    }

}
