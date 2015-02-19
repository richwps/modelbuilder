package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.BoundingBoxOutput;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.LiteralOutput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.boundary.RequestFactory;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import java.util.List;

/**
 *
 * @author dziegenh
 * @author dalcacer
 * @version 0.0.1
 */
public class ManagedRemoteDiscovery {

    public static WpsServer discoverProcesses(String uri) {
        // TODO check if it is useful to set the server entity as user object (instead of the endpoint string)
        WpsServer server = new WpsServer(uri);

        //Perform discovery.
        try {
            IRichWPSProvider provider = new RichWPSProvider();
            GetProcessesRequest request = new GetProcessesRequest(uri);
            provider.perform(request);
            List<String> processes = request.getProcesses();

            for (String processid : processes) {

                de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest pd
                        = (de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest) RequestFactory.createDescribeRequest(uri, processid);

                provider.perform(pd);

                ProcessEntity pe = new ProcessEntity(uri, pd.getIdentifier());
                //TRICKY
                if (pd.getAbstract() != null) {
                    pe.setOwsAbstract(pd.getAbstract());
                } else {
                    pe.setOwsAbstract("");
                }

                pe.setOwsTitle(pd.getTitle());
                //FIXME pe.setOwsVersion
                ManagedRemoteDiscovery.transformInputs(pd, pe);
                ManagedRemoteDiscovery.transformOutputs(pd, pe);

                // load metric properties
//                pe = getProcessProvider().getFullyLoadedProcessEntity(pe);
                server.addProcess(pe);
            }

        } catch (Exception e) {
            Logger.log("Debug:\n error occured " + e);
        }

        return server;
    }

    /**
     * Transforms DescribeRequest Inputs to ProcessEntity ProcessPorts.
     *
     * @param pd ProcessDescription with IInputDescription.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private static void transformInputs(
            de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest pd,
            ProcessEntity pe) {

        for (IInputDescription description : pd.getInputs()) {
            ProcessPort pp = null;

            if (description instanceof InputComplexDataDescription) {
                InputComplexDataDescription complex = (InputComplexDataDescription) description;
                pp = new ComplexDataInput();
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());

                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(InputComplexDataDescription.encoding_IDX);
                String mimetype = defaulttype.get(InputComplexDataDescription.mimetype_IDX);
                String schema = defaulttype.get(InputComplexDataDescription.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                pp.setPropertyValue(ComplexDataInput.PROPERTY_KEY_DATATYPEDESCRIPTION, new DataTypeDescriptionComplex(format));

                pp.setPropertyValue(ComplexDataInput.PROPERTY_KEY_MAXMB, complex.getMaximumMegabytes());

                pe.addInputPort(pp);

            } else if (description instanceof InputLiteralDataDescription) {
                InputLiteralDataDescription literal = (InputLiteralDataDescription) description;
                pp = new LiteralInput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());

                pp.setPropertyValue(LiteralInput.PROPERTY_KEY_DEFAULTVALUE, literal.getDefaultvalue());
                pp.setPropertyValue(LiteralInput.PROPERTY_KEY_LITERALDATATYPE, literal.getType());

                pe.addInputPort(pp);

            } else if (description instanceof InputBoundingBoxDataDescription) {
                InputBoundingBoxDataDescription literal = (InputBoundingBoxDataDescription) description;
                pp = new BoundingBoxInput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                // TODO set datatype description
//                pp.setPropertyValue(LiteralOutput.PROPERTY_KEY_LITERALDATATYPE, literal.getType());

                pe.addInputPort(pp);
            }

            if (null != pp) {
                pp.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MAXOCCURS, description.getMaxOccur());
                pp.setPropertyValue(ProcessInputPort.PROPERTY_KEY_MINOCCURS, description.getMinOccur());
            }
        }
    }

    /**
     * Transforms DescribeRequest Outputs to ProcessEntity ProcessPorts.
     *
     * @param pd DescribeRequest with IOutputValue.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private static void transformOutputs(
            de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest pd,
            ProcessEntity pe) {

        for (IOutputValue description : pd.getOutputs()) {
            if (description instanceof OutputComplexDataDescription) {
                OutputComplexDataDescription complex = (OutputComplexDataDescription) description;
                ProcessPort pp = new ComplexDataOutput();
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());
                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(OutputComplexDataDescription.encoding_IDX);
                String mimetype = defaulttype.get(OutputComplexDataDescription.mimetype_IDX);
                String schema = defaulttype.get(OutputComplexDataDescription.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                DataTypeDescriptionComplex typedesc = new DataTypeDescriptionComplex(format);
                pp.setPropertyValue(ComplexDataOutput.PROPERTY_KEY_DATATYPEDESCRIPTION, typedesc);
                pe.addOutputPort(pp);

            } else if (description instanceof OutputLiteralDataDescription) {
                OutputLiteralDataDescription literal = (OutputLiteralDataDescription) description;
                ProcessPort pp = new LiteralOutput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                pp.setPropertyValue(LiteralOutput.PROPERTY_KEY_LITERALDATATYPE, literal.getType());
                pe.addInputPort(pp);

            } else if (description instanceof OutputBoundingBoxDataDescription) {

                OutputBoundingBoxDataDescription bbox = (OutputBoundingBoxDataDescription) description;
                ProcessPort pp = new BoundingBoxOutput();
                pp.setOwsIdentifier(bbox.getIdentifier());
                pp.setOwsTitle(bbox.getTitle());
                pp.setOwsAbstract(bbox.getAbstract());

                // TODO set datatype description
                pe.addInputPort(pp);
            }
        }
    }
}
