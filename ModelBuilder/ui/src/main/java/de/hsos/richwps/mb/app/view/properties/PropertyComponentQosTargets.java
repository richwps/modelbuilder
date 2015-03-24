package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.app.view.qos.QosTargetsPanel;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import java.awt.Window;
import java.util.List;

/**
 * Property GUI component representing a QoS targets group.
 *
 * @author dziegenh
 */
public class PropertyComponentQosTargets extends AbstractPropertyComponent<QosTargetsPanel, List<QoSTarget>> {

    private final QosTargetsPanel component;

    public static String PROPERTY_NAME = "Manage QoS Targets";
    public static String COMPONENT_TYPE = "Manage QoS Targets";

    
    public PropertyComponentQosTargets(final Window parent,  Property<List<QoSTarget>> property)  {
        super(property);
        
        component = new QosTargetsPanel(parent, property);
        property.removeChangeListener(changeListener);
    }

    @Override
    public List<QoSTarget> getValue() {
        return property.getValue();
    }

    @Override
    public QosTargetsPanel getComponent() {
        return component;
    }

    protected void setEditable(boolean editable) {
        component.setEditable(editable);
        property.setEditable(editable);
    }

    @Override
    public void setProperty(Property<List<QoSTarget>> property) {
        super.setProperty(property);
        component.setQosPropertyGroup(property);
        setEditable(property.isEditable());
    }

    @Override
    protected void propertyValueChanged() {
        component.setQosPropertyGroup(property);
    }

}
