package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.processProvider.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.ows.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import de.hsos.richwps.sp.client.ows.posttypes.PostBoundingBoxData;
import de.hsos.richwps.sp.client.ows.posttypes.PostComplexData;
import de.hsos.richwps.sp.client.ows.posttypes.PostComplexDataCombination;
import de.hsos.richwps.sp.client.ows.posttypes.PostInAndOutputForm;
import de.hsos.richwps.sp.client.ows.posttypes.PostInput;
import de.hsos.richwps.sp.client.ows.posttypes.PostLiteralData;
import de.hsos.richwps.sp.client.ows.posttypes.PostOutput;
import de.hsos.richwps.sp.client.ows.posttypes.PostProcess;
import de.hsos.richwps.sp.client.ows.posttypes.PostQoSTarget;
import de.hsos.richwps.sp.client.ows.posttypes.PostWPS;
import java.net.MalformedURLException;
import java.util.ArrayList;

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

    public static PostProcess createSpProcess(PostWPS spWps, ProcessEntity process) throws Exception {
        PostProcess postProcess = new PostProcess();
        postProcess.setWps(spWps);

        postProcess.setIdentifier(process.getOwsIdentifier());
        postProcess.setBstract(process.getOwsAbstract());
        postProcess.setTitle(process.getOwsTitle());

        Object value;
        value = process.getPropertyValue(ProcessEntity.PROPERTIES_KEY_VERSION);
        postProcess.setProcessVersion((String) value);

        ArrayList<PostInput> postInputs = new ArrayList<>();

        for (ProcessPort aPort : process.getInputPorts()) {
            PostInput postPort = new PostInput();
            postPort.setBstract(aPort.getOwsAbstract());
            postPort.setIdentifier(aPort.getOwsIdentifier());
            postPort.setTitle(aPort.getOwsTitle());

            value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXOCCURS);
            postPort.setMaxOcc((int) value);

            value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MINOCCURS);
            postPort.setMinOcc((int) value);

            // TODO what about max mb ?
            // value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXMB);
            // ...
            PostInAndOutputForm datatype = EntityConverter.createSpInputDatatype(aPort);
            postPort.setPostInputFormChoice(datatype);

            postInputs.add(postPort);
        }

        postProcess.setInputs(postInputs);

        ArrayList<PostOutput> postOutputs = new ArrayList<>();

        for (ProcessPort aPort : process.getOutputPorts()) {
            PostOutput postPort = new PostOutput();
            postPort.setBstract(aPort.getOwsAbstract());
            postPort.setIdentifier(aPort.getOwsIdentifier());
            postPort.setTitle(aPort.getOwsTitle());

            // TODO what about max mb ?
            // value = aPort.getPropertyValue(ProcessPort.PROPERTY_KEY_MAXMB);
            // ...
            PostInAndOutputForm datatype = EntityConverter.createSpInputDatatype(aPort);
            postPort.setPostOutputFormChoice(datatype);

            postOutputs.add(postPort);
        }
        postProcess.setOutputs(postOutputs);

        // add empty array to prevent null pointer exception...
        // the target values are to be set via the MB's QoS tool
        postProcess.setQosTargets(new ArrayList<PostQoSTarget>());
        
        return postProcess;
    }

    public static PostInAndOutputForm createSpInputDatatype(ProcessPort inputPort) throws Exception {
        PostInAndOutputForm spDatatype = null;

        switch (inputPort.getDatatype()) {
            case LITERAL:
                spDatatype = new PostLiteralData();
                // TODO set default value !
                break;

            case COMPLEX:
                PostComplexData spComplexDatatype = new PostComplexData();
                ArrayList<PostComplexDataCombination> spSupportedFormats = new ArrayList<>();

                DataTypeDescriptionComplex datatype = (DataTypeDescriptionComplex) inputPort.getDataTypeDescription();
                for (ComplexDataTypeFormat aFormat : datatype.getFormats()) {
                    spSupportedFormats.add(createSpDatatypeComplexFormat(aFormat));
                }

                spComplexDatatype.setSupportedFormats(spSupportedFormats);
                spComplexDatatype.setDefaultFormat(createSpDatatypeComplexFormat(datatype.getDefaultFormat()));
                
                spDatatype = spComplexDatatype;
                
                break;

            case BOUNDING_BOX:
                spDatatype = new PostBoundingBoxData();
                break;
        }

        return spDatatype;
    }

    /**
     * Converts the ModelBuilder's complex datatype format to the corresponding
     * SemanticProxy object. This means mapping the MimeType, Encoding and
     * Schema.
     *
     * @param format
     * @return
     */
    public static PostComplexDataCombination createSpDatatypeComplexFormat(ComplexDataTypeFormat format) throws Exception {
        PostComplexDataCombination spDatatype = new PostComplexDataCombination();

        spDatatype.setMimeType(format.getMimeType());
        spDatatype.setEncoding(format.getEncoding());
        spDatatype.setSchema(format.getSchema());

        return spDatatype;
    }

}
