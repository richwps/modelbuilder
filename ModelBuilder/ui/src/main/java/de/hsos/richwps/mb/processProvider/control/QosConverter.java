package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.ows.EUOM;
import de.hsos.richwps.sp.client.ows.gettypes.QoSTarget;

/**
 *
 * @author dziegenh
 */
public class QosConverter {

    /**
     * Creates property groups for the processes QoS targets.
     *
     * @param spProcess
     * @return
     */
    public static PropertyGroup targetsToProperties(de.hsos.richwps.sp.client.ows.gettypes.Process spProcess) {

        PropertyGroup<PropertyGroup> qosGroup = null;

        try {
            QoSTarget[] qoSTargets = spProcess.getQoSTargets();
            if (null != qoSTargets && qoSTargets.length > 0) {
                qosGroup = new PropertyGroup<>(ProcessProviderConfig.QOS_TARGETS_GROUP);

                for (QoSTarget target : qoSTargets) {
                    PropertyGroup<Property> targetGroup = new PropertyGroup<>(target.getTitle());

                    Property<String> abstractProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_ABSTRACT);
                    abstractProperty.setComponentType(Property.COMPONENT_TYPE_TEXTFIELD);
                    abstractProperty.setValue(target.getAbstract());

                    Property<Double> minProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_MIN);
                    minProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
                    minProperty.setValue(target.getMin());

                    Property<Double> maxProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_MAX);
                    maxProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
                    maxProperty.setValue(target.getMax());

                    Property<Double> idealProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_IDEAL);
                    idealProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
                    idealProperty.setValue(target.getIdeal());

                    Property<Double> varProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_VAR);
                    varProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
                    varProperty.setValue(target.getVariance());

//                    EUOM uom = qoSTargets[0].getUOM();
//                    String uomString = " (" + uom.toString() + ")";
                    
                    targetGroup.addObject(abstractProperty);
                    targetGroup.addObject(minProperty);
                    targetGroup.addObject(maxProperty);
                    targetGroup.addObject(idealProperty);
                    targetGroup.addObject(varProperty);

                    qosGroup.addObject(targetGroup);
                }
            }

        } catch (Exception ex) {
            Logger.log("Error gettin qos targets: " + ex);
        }

        return qosGroup;
    }

}
