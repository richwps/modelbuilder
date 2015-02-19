package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.control.ProcessPortFactory;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.processProvider.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.ows.gettypes.ComplexData;
import de.hsos.richwps.sp.client.ows.gettypes.ComplexDataCombination;
import de.hsos.richwps.sp.client.ows.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.ows.gettypes.Input;
import de.hsos.richwps.sp.client.ows.gettypes.Output;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            postPort.setMinOcc((int) aPort.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS));
            postPort.setMaxOcc((int) aPort.getPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS));

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

                Object descValue = inputPort.getPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION);
                DataTypeDescriptionComplex datatype = (DataTypeDescriptionComplex) descValue;
                for (ComplexDataTypeFormat aFormat : datatype.getFormats()) {
                    spSupportedFormats.add(createSpDatatypeComplexFormat(aFormat));
                }

                spComplexDatatype.setSupportedFormats(spSupportedFormats);
                spComplexDatatype.setDefaultFormat(createSpDatatypeComplexFormat(datatype.getDefaultFormat()));

                Integer maxMb = (Integer) inputPort.getPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB);
                if (null != maxMb) {
                    BigInteger bigMaxMb = new BigInteger(maxMb.toString());
                    spComplexDatatype.setMaximumMegaBytes(bigMaxMb);
                }

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

    public static ProcessPort createProcessInput(Input spInput) throws Exception {
        ProcessPortDatatype datatype = EntityConverter.getDatatype(spInput.getInputFormChoice());

        ProcessInputPort inPort = ProcessPortFactory.createLocalInputPort(datatype);
        inPort.setOwsIdentifier(spInput.getIdentifier());
        inPort.setOwsAbstract(spInput.getAbstract());
        inPort.setOwsTitle(spInput.getTitle());
        inPort.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS, spInput.getMinOccurs());
        inPort.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS, spInput.getMaxOccurs());

        switch (datatype) {
            case LITERAL:
                break;
            case COMPLEX:
                ComplexData spInputData = (ComplexData) spInput.getInputFormChoice();
                inPort.setPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB, spInputData.getMaximumMegabytes());
                break;
            case BOUNDING_BOX:
                break;
        }

//        Collection<? extends IObjectWithProperties> properties = inPort.getProperties();
//        inPort.get
//        for (IObjectWithProperties aPropertyObject : properties) {
//
//            if (aPropertyObject instanceof Property) {
//
//                Property aProperty = (Property) aPropertyObject;
//                String aKey = aProperty.getPropertiesObjectName();
//
//                switch (aKey) {
//                    case ProcessInputPort.PROPERTY_KEY_MINOCCURS:
//                        aProperty.setValue(spInput.getMinOccurs());
//                        break;
//
//                    case ProcessInputPort.PROPERTY_KEY_MAXOCCURS:
//                        aProperty.setValue(spInput.getMaxOccurs());
//                        break;
//
//                    case ComplexDataInput.PROPERTY_KEY_MAXMB:
//                        // TODO max MB ??
//                        break;
//                }
//            }
//
//        }
        return inPort;
    }

    public static ProcessPort createProcessOutput(Output spOutput) throws Exception {
        InAndOutputForm spDatatype = spOutput.getOutputFormChoice();
        ProcessPortDatatype datatype = EntityConverter.getDatatype(spDatatype);

        ProcessPort outPort = ProcessPortFactory.createLocalOutputPort(datatype);
        outPort.setOwsIdentifier(spOutput.getIdentifier());
        outPort.setOwsAbstract(spOutput.getAbstract());
        outPort.setOwsTitle(spOutput.getTitle());

//        IDataTypeDescription datatypeDescription = null;
        switch (datatype) {
            case LITERAL:
                // TODO set description
                
                break;
            case COMPLEX:
                ComplexData spComplexData = (ComplexData) spDatatype;
                ComplexDataTypeFormat defaultFormat = createComplexFormat(spComplexData.getDefaultFormat());
                DataTypeDescriptionComplex complexDesc = new DataTypeDescriptionComplex(defaultFormat);

                // convert supported formats
                ComplexDataCombination[] supportedFormats = spComplexData.getSupportedFormats();
                List<ComplexDataTypeFormat> formats = complexDesc.getFormats();
                for (ComplexDataCombination aSpFormat : supportedFormats) {
                    formats.add(createComplexFormat(aSpFormat));
                }

                outPort.setPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION, complexDesc);

                break;

            case BOUNDING_BOX:
                // TODO set description
                break;
        }
//        outPort.setDataTypeDescription(datatypeDescription);

        Collection<? extends IObjectWithProperties> properties = outPort.getProperties();
        for (IObjectWithProperties aPropertyObject : properties) {

            if (aPropertyObject instanceof Property) {

                Property aProperty = (Property) aPropertyObject;
                String aKey = aProperty.getPropertiesObjectName();

                switch (aKey) {
                    // currently no additional properties.
                }
            }

        }

        return outPort;
    }

    public static ComplexDataTypeFormat createComplexFormat(ComplexDataCombination spFormat) throws RDFException {
        return new ComplexDataTypeFormat(spFormat.getMimeType(), spFormat.getSchema(), spFormat.getEncoding());
    }
}
