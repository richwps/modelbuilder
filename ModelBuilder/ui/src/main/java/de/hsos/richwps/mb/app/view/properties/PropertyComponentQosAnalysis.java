package de.hsos.richwps.mb.app.view.properties;

import de.hsos.richwps.mb.app.view.qos.QosAnalysisPanel;
import de.hsos.richwps.mb.app.view.qos.QosStatusLabel;
import de.hsos.richwps.mb.entity.QoSAnaylsis;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.propertiesView.propertyComponents.AbstractPropertyComponent;
import de.hsos.richwps.mb.ui.ColorBorder;
import java.awt.Color;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 * Property GUI component representing a single QoS target with monitored data.
 *
 * @author dziegenh
 */
public class PropertyComponentQosAnalysis extends AbstractPropertyComponent<JPanel, QoSAnaylsis> {

    private final QosAnalysisPanel component;

    private final JPanel componentWrapper;

    public static String COMPONENT_TYPE = "QoS Analysis";

    public final double analysisPanelHeight = 30d;
    private final QosStatusLabel qosStatus;

    public PropertyComponentQosAnalysis(Property<QoSAnaylsis> property) {
        super(property);

        component = new QosAnalysisPanel(property);
        property.removeChangeListener(changeListener);

        qosStatus = new QosStatusLabel();
        qosStatus.update(property.getValue());

        componentWrapper = new JPanel();
        double f = TableLayout.FILL,
                p = TableLayout.PREFERRED;
        componentWrapper.setLayout(new TableLayout(new double[][]{{240d, f}, {f, analysisPanelHeight, 2d, p, f}}));
        componentWrapper.add(component, "0 1 1 1");
        componentWrapper.add(qosStatus, "0 3");
    }

    @Override
    public QoSAnaylsis getValue() {
        return property.getValue();
    }

    @Override
    public JPanel getComponent() {
        return componentWrapper;
    }

    protected void setEditable(boolean editable) {
        property.setEditable(editable);
    }

    @Override
    public void setProperty(Property<QoSAnaylsis> property) {
        super.setProperty(property);
        component.setQosProperty(property);
        setEditable(property.isEditable());
        qosStatus.update(property.getValue());
    }

    @Override
    protected void propertyValueChanged() {
        component.setQosProperty(property);
        qosStatus.update(property.getValue());
    }

}
