package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.control.ProcessPortFactory;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.processProvider.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.mb.properties.IObjectWithProperties;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.BadRequestException;
import de.hsos.richwps.sp.client.CommunicationException;
import de.hsos.richwps.sp.client.InternalSPException;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.ows.gettypes.ComplexData;
import de.hsos.richwps.sp.client.ows.gettypes.ComplexDataCombination;
import de.hsos.richwps.sp.client.ows.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.ows.gettypes.Input;
import de.hsos.richwps.sp.client.ows.gettypes.LiteralData;
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
public class SpEntityConverter {

    public static ProcessEntity createProcessEntity(de.hsos.richwps.sp.client.ows.gettypes.Process spProcess, KeyTranslator translator) throws Exception {
        ProcessEntity processEntity = new ProcessEntity(spProcess.getWPS().getEndpoint(), spProcess.getIdentifier());
        processEntity.setOwsAbstract(spProcess.getAbstract());
        processEntity.setOwsTitle(spProcess.getTitle());

        // add qos targets
        PropertyGroup qosGroups = QosConverter.spTargetsToProperties(spProcess, translator);
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

            PostInAndOutputForm datatype = SpEntityConverter.createSpInputDatatype(aPort);
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

            PostInAndOutputForm datatype = SpEntityConverter.createSpInputDatatype(aPort);
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
                PostLiteralData postLiteralData = new PostLiteralData();
                
                // set default value
                String defaultValue = (String) inputPort.getPropertyValue(LiteralInput.PROPERTY_KEY_DEFAULTVALUE);
                postLiteralData.setDefaultValue(defaultValue);

                // set datatype
                String literalDatatype = (String) inputPort.getPropertyValue(LiteralInput.PROPERTY_KEY_LITERALDATATYPE);
                postLiteralData.setLiteralDataType(literalDatatype);
                
                spDatatype = postLiteralData;

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
        InAndOutputForm inputFormChoice = spInput.getInputFormChoice();
        ProcessPortDatatype datatype = SpEntityConverter.getDatatype(inputFormChoice);

        ProcessInputPort inPort = ProcessPortFactory.createLocalInputPort(datatype);
        inPort.setOwsIdentifier(spInput.getIdentifier());
        inPort.setOwsAbstract(spInput.getAbstract());
        inPort.setOwsTitle(spInput.getTitle());
        inPort.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS, spInput.getMinOccurs());
        inPort.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS, spInput.getMaxOccurs());

        switch (datatype) {
            case LITERAL:
                LiteralData spInputData = (LiteralData) inputFormChoice;
                convertLiteralPort(inputFormChoice, inPort);
                break;

            case COMPLEX:
                ComplexData spComplexIn = (ComplexData) inputFormChoice;
                inPort.setPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB, spComplexIn.getMaximumMegabytes());
                convertComplexPort(inputFormChoice, inPort);
                break;

            case BOUNDING_BOX:
                break;
        }

        return inPort;
    }

    public static ProcessPort createProcessOutput(Output spOutput) throws Exception {
        InAndOutputForm spDatatype = spOutput.getOutputFormChoice();
        ProcessPortDatatype datatype = SpEntityConverter.getDatatype(spDatatype);

        ProcessPort outPort = ProcessPortFactory.createLocalOutputPort(datatype);
        outPort.setOwsIdentifier(spOutput.getIdentifier());
        outPort.setOwsAbstract(spOutput.getAbstract());
        outPort.setOwsTitle(spOutput.getTitle());

        switch (datatype) {
            case LITERAL:
                convertLiteralPort(spDatatype, outPort);
                break;
            case COMPLEX:
                convertComplexPort(spDatatype, outPort);
                break;

            case BOUNDING_BOX:
                // TODO set description
                break;
        }

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

    private static void convertComplexPort(InAndOutputForm spDatatype, ProcessPort port) throws CommunicationException, RDFException, BadRequestException, InternalSPException {
        ComplexData spComplexData = (ComplexData) spDatatype;
        ComplexDataTypeFormat defaultFormat = createComplexFormat(spComplexData.getDefaultFormat());
        DataTypeDescriptionComplex complexDesc = new DataTypeDescriptionComplex(defaultFormat);

        // convert supported formats
        ComplexDataCombination[] supportedFormats = spComplexData.getSupportedFormats();
        List<ComplexDataTypeFormat> formats = complexDesc.getFormats();

        for (ComplexDataCombination supportedFormat : supportedFormats) {
            formats.add(createComplexFormat(supportedFormat));
        }

        port.setPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION, complexDesc);
    }

    private static void convertLiteralPort(InAndOutputForm spDatatype, ProcessPort port) throws CommunicationException, RDFException, BadRequestException, InternalSPException {
        LiteralData spLiteralData = (LiteralData) spDatatype;

        // set datatype
        String litealDataType = spLiteralData.getLitealDataType();
        port.setPropertyValue(LiteralInput.PROPERTY_KEY_LITERALDATATYPE, litealDataType);

        // set default value
        String defaultValue = spLiteralData.getDefaultValue();
        port.setPropertyValue(LiteralInput.PROPERTY_KEY_DEFAULTVALUE, defaultValue);
    }

    public static ComplexDataTypeFormat createComplexFormat(ComplexDataCombination spFormat) throws RDFException {
        return new ComplexDataTypeFormat(spFormat.getMimeType(), spFormat.getSchema(), spFormat.getEncoding());
    }
}
