package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.processProvider.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.ows.gettypes.InAndOutputForm;

/**
 *
 * @author dziegenh
 */
public class EntityConverter {

    public static ProcessEntity createProcessEntity(de.hsos.richwps.sp.client.ows.gettypes.Process spProcess, KeyTranslator translator) throws Exception {
        ProcessEntity processEntity = new ProcessEntity(spProcess.getWPS().getEndpoint(), spProcess.getIdentifier());
        processEntity.setOwsAbstract(spProcess.getAbstract());
        processEntity.setOwsTitle(spProcess.getTitle());

        // add qos targets
        PropertyGroup qosGroups = QosConverter.targetsToProperties(spProcess, translator);
        if (null != qosGroups) {
            processEntity.setProperty(qosGroups.getPropertiesObjectName(), qosGroups);
        }

        // add version as property
        String versionKey = ProcessEntity.PROPERTIES_KEY_VERSION;
        String versionType = Property.COMPONENT_TYPE_TEXTFIELD;
        String versionValue = spProcess.getProcessVersion();
        Property versionProperty = new Property(versionKey, versionType, versionValue);
        processEntity.setProperty(versionKey, versionProperty);

        return processEntity;

    }

    public static ProcessPortDatatype getDatatype(InAndOutputForm inputFormChoice) throws UnsupportedWpsDatatypeException {
        switch (inputFormChoice.getDataType()) {
            case InAndOutputForm.LITERAL_TYPE:
                return ProcessPortDatatype.LITERAL;
            case InAndOutputForm.COMPLEX_TYPE:
                return ProcessPortDatatype.COMPLEX;
            case InAndOutputForm.BOUNDING_BOX_TYPE:
                return ProcessPortDatatype.BOUNDING_BOX;
        }

        throw new UnsupportedWpsDatatypeException(inputFormChoice.getDataType());
    }
}
