package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.RDFException;
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

                    de.hsos.richwps.mb.entity.QoSTarget mbTarget = spQosTargetToMbEntity(target, translator);
                    PropertyGroup<Property> targetGroup = createTargetProperties(mbTarget);

                    qosGroup.addObject(targetGroup);
                }
            }

        } catch (Exception ex) {
            Logger.log("Error getting qos targets: " + ex);
        }

        return qosGroup;
    }

    public static de.hsos.richwps.mb.entity.QoSTarget spQosTargetToMbEntity(QoSTarget target, KeyTranslator translator) throws RDFException {
        de.hsos.richwps.mb.entity.QoSTarget qoSTarget = new de.hsos.richwps.mb.entity.QoSTarget();

        qoSTarget.setTargetTitle(target.getTitle());
        qoSTarget.setTargetAbstract(target.getAbstract());
        qoSTarget.setIdeal(target.getIdeal());
        qoSTarget.setMax(target.getMax());
        qoSTarget.setMin(target.getMin());
        qoSTarget.setVariance(target.getVariance());

        EUOM uom = target.getUOM();
        qoSTarget.setUomTranslated(translator.getTranslation(uom.name()));

        return qoSTarget;
    }

    public static PropertyGroup<Property> createTargetProperties(de.hsos.richwps.mb.entity.QoSTarget qosTarget) {
        PropertyGroup<Property> targetGroup = new PropertyGroup<>(qosTarget.getTargetTitle());

        Property<String> abstractProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_ABSTRACT);
        abstractProperty.setComponentType(Property.COMPONENT_TYPE_TEXTFIELD);
        abstractProperty.setValue(qosTarget.getTargetAbstract());

        Property<Double> minProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_MIN);
        minProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        minProperty.setValue(qosTarget.getMin());

        Property<Double> maxProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_MAX);
        maxProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        maxProperty.setValue(qosTarget.getMax());

        Property<Double> idealProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_IDEAL);
        idealProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        idealProperty.setValue(qosTarget.getIdeal());

        Property<Double> varProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_VAR);
        varProperty.setComponentType(Property.COMPONENT_TYPE_DOUBLE);
        varProperty.setValue(qosTarget.getVariance());

        Property<String> uomProperty = new Property<>(de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_UOM);
        uomProperty.setComponentType(Property.COMPONENT_TYPE_NONE);
        uomProperty.setValue(qosTarget.getUomTranslated());

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
        Object aAbstract = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_ABSTRACT);
        Object min = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_MIN);
        Object max = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_MAX);
        Object ideal = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_IDEAL);
        Object variance = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_VAR);
        Object uom = getPropertyValue(group, de.hsos.richwps.mb.entity.QoSTarget.PROPERTY_KEY_QOS_TARGET_UOM);
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
