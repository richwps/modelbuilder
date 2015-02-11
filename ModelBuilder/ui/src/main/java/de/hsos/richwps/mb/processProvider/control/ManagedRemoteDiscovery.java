package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.datatypes.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.BoundingBoxOutput;
import de.hsos.richwps.mb.entity.ports.ComplexDataInput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.LiteralOutput;
import de.hsos.richwps.mb.exception.IllegalDatatypeDescriptionException;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import java.util.List;

/**
 *
 * @author dziegenh
 * @author dalcacer
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

                DescribeRequest pd = new DescribeRequest();
                pd.setEndpoint(uri);
                pd.setIdentifier(processid);
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
     * @param pd ProcessDescription with IInputSpecifier.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private static void transformInputs(DescribeRequest pd, ProcessEntity pe) throws IllegalDatatypeDescriptionException {

        for (IInputSpecifier specifier : pd.getInputs()) {
            if (specifier instanceof InputComplexDataSpecifier) {
                InputComplexDataSpecifier complex = (InputComplexDataSpecifier) specifier;
                ProcessPort pp = new ComplexDataInput();
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());
                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(InputComplexDataSpecifier.encoding_IDX);
                String mimetype = defaulttype.get(InputComplexDataSpecifier.mimetype_IDX);
                String schema = defaulttype.get(InputComplexDataSpecifier.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                IDataTypeDescription typedesc = new DataTypeDescriptionComplex(format);
                pp.setDataTypeDescription(typedesc);
                pe.addInputPort(pp);

            } else if (specifier instanceof InputLiteralDataSpecifier) {
                InputLiteralDataSpecifier literal = (InputLiteralDataSpecifier) specifier;
                ProcessPort pp = new LiteralInput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                pp.setDataTypeDescription(new DataTypeDescriptionLiteral(literal.getDefaultvalue()));
                pe.addInputPort(pp);

            } else if (specifier instanceof InputBoundingBoxDataSpecifier) {
                InputBoundingBoxDataSpecifier literal = (InputBoundingBoxDataSpecifier) specifier;
                ProcessPort pp = new BoundingBoxInput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                // TODO set datatype description
//                pp.setDataTypeDescription(new DataTypeDescriptionLiteral(literal.getDefaultvalue()));
                pe.addInputPort(pp);
            }
        }
    }

    /**
     * Transforms DescribeRequest Outputs to ProcessEntity ProcessPorts.
     *
     * @param pd DescribeRequest with IOutputSpecifier.
     * @param pe ProcessEntity with ProcessPorts.
     */
    private static void transformOutputs(DescribeRequest pd, ProcessEntity pe) throws IllegalDatatypeDescriptionException {

        for (IOutputSpecifier specifier : pd.getOutputs()) {
            if (specifier instanceof OutputComplexDataSpecifier) {
                OutputComplexDataSpecifier complex = (OutputComplexDataSpecifier) specifier;
                ProcessPort pp = new ComplexDataOutput();
                pp.setOwsIdentifier(complex.getIdentifier());
                pp.setOwsTitle(complex.getTitle());
                pp.setOwsAbstract(complex.getAbstract());
                List<String> defaulttype = complex.getDefaultType();
                String encoding = defaulttype.get(OutputComplexDataSpecifier.encoding_IDX);
                String mimetype = defaulttype.get(OutputComplexDataSpecifier.mimetype_IDX);
                String schema = defaulttype.get(OutputComplexDataSpecifier.schema_IDX);
                ComplexDataTypeFormat format = new ComplexDataTypeFormat(mimetype, schema, encoding);
                IDataTypeDescription typedesc = new DataTypeDescriptionComplex(format);
                pp.setDataTypeDescription(typedesc);
                pe.addOutputPort(pp);

            } else if (specifier instanceof OutputLiteralDataSpecifier) {
                OutputLiteralDataSpecifier literal = (OutputLiteralDataSpecifier) specifier;
                ProcessPort pp = new LiteralOutput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());
                pe.addInputPort(pp);

            } else if (specifier instanceof OutputBoundingBoxDataSpecifier) {

                OutputBoundingBoxDataSpecifier literal = (OutputBoundingBoxDataSpecifier) specifier;
                ProcessPort pp = new BoundingBoxOutput();
                pp.setOwsIdentifier(literal.getIdentifier());
                pp.setOwsTitle(literal.getTitle());
                pp.setOwsAbstract(literal.getAbstract());

                // TODO set datatype description
                pe.addInputPort(pp);
            }
        }
    }
}
