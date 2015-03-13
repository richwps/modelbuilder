package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.ows.EUOM;
import de.hsos.richwps.sp.client.ows.gettypes.QoSTarget;
import de.hsos.richwps.sp.client.ows.posttypes.PostQoSTarget;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    public static PropertyGroup spTargetsToProperties(de.hsos.richwps.sp.client.ows.gettypes.Process spProcess, KeyTranslator translator) {

        PropertyGroup<PropertyGroup> qosGroup = null;

        try {
            QoSTarget[] qoSTargets = spProcess.getQoSTargets();
            if (null != qoSTargets && qoSTargets.length > 0) {
                qosGroup = new PropertyGroup<>(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS);

                for (QoSTarget target : qoSTargets) {

                    final String aAbstract = target.getAbstract();
                    final double min = target.getMin();
                    final double max = target.getMax();
                    final double ideal = target.getIdeal();
                    final double variance = target.getVariance();
                    EUOM uom = target.getUOM();
                    final String uomTranslated = translator.getTranslation(uom.name());
                    final String targetTitle = target.getTitle();

                    PropertyGroup<Property> targetGroup = createTargetProperties(targetTitle, aAbstract, min, max, ideal, variance, uomTranslated);

                    qosGroup.addObject(targetGroup);
                }
            }

        } catch (Exception ex) {
            Logger.log("Error getting qos targets: " + ex);
        }

        return qosGroup;
    }

    public static PropertyGroup<Property> createTargetProperties(final String targetTitle, final String aAbstract, final double min, final double max, final double ideal, final double variance, final String uomTranslated) {
        PropertyGroup<Property> targetGroup = new PropertyGroup<>(targetTitle);

        Property<String> abstractProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_ABSTRACT);
        abstractProperty.setComponentType(Property.COMPONENT_TYPE_TEXTFIELD);
        abstractProperty.setValue(aAbstract);

        Property<Double> minProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_MIN);
        minProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        minProperty.setValue(min);

        Property<Double> maxProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_MAX);
        maxProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        maxProperty.setValue(max);

        Property<Double> idealProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_IDEAL);
        idealProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        idealProperty.setValue(ideal);

        Property<Double> varProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_VAR);
        varProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        varProperty.setValue(variance);

        Property<String> uomProperty = new Property<>(ProcessProviderConfig.QOS_TARGET_UOM);
        uomProperty.setComponentType(Property.COMPONENT_TYPE_NONE);
        uomProperty.setValue(uomTranslated);

        targetGroup.addObject(abstractProperty);
        targetGroup.addObject(minProperty);
        targetGroup.addObject(maxProperty);
        targetGroup.addObject(idealProperty);
        targetGroup.addObject(varProperty);
        targetGroup.addObject(uomProperty);

        return targetGroup;
    }

    public static List<PostQoSTarget> groupsToSpTargets(ProcessEntity process, KeyTranslator translator) throws Exception {
        PropertyGroup mainGroup = process.getPropertyGroup(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS);

        if (null == mainGroup) {
            return null;
        }

        List<PostQoSTarget> targets = new LinkedList<>();

        Collection properties = mainGroup.getProperties();
        for (Object property : properties) {
            PostQoSTarget target = propertiesToSpTargets((PropertyGroup) property, translator);
            targets.add(target);
        }

        return targets;
    }

    /**
     * Converts a processes' qos target properties to a postable SP object.
     *
     * @param group
     * @param translator
     * @return
     */
    public static PostQoSTarget propertiesToSpTargets(PropertyGroup group, KeyTranslator translator) throws Exception {
        PostQoSTarget postQoSTarget = new PostQoSTarget();

        Object title = group.getPropertiesObjectName();
        Object aAbstract = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_ABSTRACT);
        Object min = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_MIN);
        Object max = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_MAX);
        Object ideal = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_IDEAL);
        Object variance = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_VAR);
        Object uom = getPropertyValue(group, ProcessProviderConfig.QOS_TARGET_UOM);
        EUOM spUom = toSpUom((String) uom, translator);

        postQoSTarget.setBstract((String) aAbstract);
        postQoSTarget.setMin((double) min);
        postQoSTarget.setMax((double) max);
        postQoSTarget.setIdeal((double) ideal);
        postQoSTarget.setVariance((double) variance);
        postQoSTarget.setTitle((String) title);
        postQoSTarget.setUOM(spUom);

        return postQoSTarget;
    }

    private static Object getPropertyValue(PropertyGroup group, String property) {
        IObjectWithProperties propertyObject = group.getPropertyObject(property);
        if (null != propertyObject && (propertyObject instanceof Property)) {
            return ((Property) propertyObject).getValue();
        }

        return null;
    }

    private static EUOM toSpUom(String uom, KeyTranslator translator) {
        String key = translator.getKey(uom);

        // find EUOM for the translation key
        if (null != key) {
            for (EUOM euom : EUOM.values()) {
                if (euom.name().equals(key)) {
                    return euom;
                }
            }
        }

        // nothing found -> undefinded
        return EUOM.UNDEFINED;
    }

}
