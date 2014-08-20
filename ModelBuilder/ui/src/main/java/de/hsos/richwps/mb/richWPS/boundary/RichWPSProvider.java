package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import de.hsos.richwps.mb.richWPS.entity.execute.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputLiteralDataArgument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.client.WPSTClientSession;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

/**
 * Interface to RichWPS enabled servers.
 *
 * @author dalcacer
 */
public class RichWPSProvider implements IRichWPSProvider {

    /**
     * WPS client.
     */
    private WPSClientSession wps;
    /**
     * WPS-T client.
     */
    private WPSTClientSession wpst;

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     */
    @Override
    public void connect(String wpsurl) throws Exception {
        try {
            wps = WPSClientSession.getInstance();
            wps.connect(wpsurl);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service.");
        }
    }

    /**
     * Connects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     */
    @Override
    public void connect(String wpsurl, String wpsturl) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Connects the provider to a WPS-server with WPS-T and testing
     * functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @param testurl endpoint of testing interface.
     */
    @Override
    public void connect(String wpsurl, String wpsturl, String testurl) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Connects the provider to a WPS-server with WPS-T, testing and profiling
     * functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @param testurl endpoint of testing interface.
     * @param profileurl endpoint of profiling interface.
     */
    @Override
    public void connect(String wpsurl, String wpsturl, String testurl, String profileurl) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Lists all available processes.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @return list of processes.
     */
    @Override
    public List<String> getAvailableProcesses(String wpsurl) {
        List<String> processes = new ArrayList<>();

        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
            ProcessDescriptionsDocument pdd = this.wps.describeAllProcesses(wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessBriefType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessBriefType process : descs) {
                String identifier = process.getIdentifier().getStringValue();
                processes.add(identifier);
            }
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n " + e.getLocalizedMessage());
        }
        return processes;
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param dto ExecuteRequestDTO with endpoint and processid.
     * @return ExecuteRequestDTO with endpoint and processid an process
     * description.
     */
    @Override
    public ExecuteRequestDTO describeProcess(ExecuteRequestDTO dto) {

        if (dto.getInputSpecifier().isEmpty()) {
            dto = this.execAddInputs(dto);
        }

        if (dto.getOutputSepcifier().isEmpty()) {
            dto = this.execAddOutputs(dto);
        }

        return dto;
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param dto ExecuteRequestDTO with endpoint and processid and in- and
     * outputarguments.
     * @return ExecuteRequestDTO with results or exception.
     */
    @Override
    public ExecuteRequestDTO executeProcess(ExecuteRequestDTO dto) {
        ExecuteRequestDTO resultdto = dto;
        String wpsurl = dto.getEndpoint();
        String processid = dto.getProcessid();

        HashMap theinputs = dto.getInputArguments();
        HashMap theoutputs = dto.getOutputArguments();

        ProcessDescriptionType description = this.getProcessDescriptionType(dto);
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(description);

        this.execSetInputs(executeBuilder, theinputs);
        this.execSetOutputs(executeBuilder, theoutputs);

        ExecuteDocument execute = null;
        Object responseObject = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");

            de.hsos.richwps.mb.Logger.log("Debug:\n Execute String: " + execute.toString());

            WPSClientSession wpsClient = WPSClientSession.getInstance();
            responseObject = wpsClient.execute(wpsurl, execute);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Something went wrong, executing the process " + processid + ", " + e);
        }

        resultdto = this.execAnalyseResponse(execute, description, responseObject, dto);

        return resultdto;
    }

    

    /**
     * Retreives and extracts the processdescription type from a given
     * WPS-server.
     *
     * @param dto ExecuteRequestDTO with endpoint and process id.
     * @return 52n processdescriptiontype.
     */
    private ProcessDescriptionType getProcessDescriptionType(ExecuteRequestDTO dto) {
        ProcessDescriptionType description = null;
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(dto.getEndpoint());

            //discover
            String[] processes = new String[1];
            processes[0] = dto.getProcessid();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, dto.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            description = descs[0];

        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Something went wrong, describing the process " + dto.getProcessid() + ", " + e);
        }
        return description;
    }
    
    /**
     * Analyses a given response and add specific results or exception to dto.
     *
     * @param execute 52n execute document.
     * @param description 52n process description.
     * @param responseObject 52n reponse object. Execute-response or exception.
     * @param dto ExecuteRequestDTO with possible inputs (IInputSpecifier) and
     * outputs (IOutputSpecifier).
     * @return ExecuteRequestDTO with results or exception.
     */
    private ExecuteRequestDTO execAnalyseResponse(ExecuteDocument execute, ProcessDescriptionType description, Object responseObject, ExecuteRequestDTO dto) {
        java.net.URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");
        System.out.println(file);
        WPSClientConfig.getInstance(file);

        ExecuteRequestDTO resultdto = dto;
        HashMap theoutputs = dto.getOutputArguments();
        de.hsos.richwps.mb.Logger.log("Debug: " + responseObject.getClass());
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            de.hsos.richwps.mb.Logger.log("Debug: \n" + response.toString());

            try {
                java.util.Set<String> keys = theoutputs.keySet();

                for (String key : keys) {
                    Object o = theoutputs.get(key);
                    if (o instanceof OutputLiteralDataArgument) {
                        OutputLiteralDataArgument argument = (OutputLiteralDataArgument) o;
                        OutputDataType[] outputs = response.getExecuteResponse().getProcessOutputs().getOutputArray();
                        String value = "";
                        for (OutputDataType output : outputs) {
                            String givenIdentifier = output.getIdentifier().getStringValue();
                            String wantedIdentifer = argument.getIdentifier();
                            if (givenIdentifier.equals(wantedIdentifer)) {
                                value = output.getData().getLiteralData().getStringValue();
                                de.hsos.richwps.mb.Logger.log("#thevalue " + value);
                            }
                        }
                        dto.addResult(key, value);

                    } else if (o instanceof OutputComplexDataArgument) {
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, description);

                        OutputComplexDataArgument argument = (OutputComplexDataArgument) o;
                        if (argument.isAsReference()) {
                            String httpkvpref = analyser.getComplexReferenceByIndex(0);
                            dto.addResult(key, httpkvpref);
                        } else {
                            // FIXME proper analytics for different bindings.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);//FIXME
                            System.out.println(binding.getPayload().size());
                        }
                    }
                }
            } catch (WPSClientException e) {
                de.hsos.richwps.mb.Logger.log("Debug: \n Unable to analyse response. " + e.getLocalizedMessage());
            }
        } else {
            net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) responseObject;
            resultdto.addException(exception.getExceptionReport().toString());
            de.hsos.richwps.mb.Logger.log("Debug: \n Unable to analyse response. Response is Exception: " + exception.toString());
            de.hsos.richwps.mb.Logger.log("Debug: \n " + exception.getExceptionReport());
        }
        return resultdto;
    }

    

    /**
     * Add processs inputs to a dto.
     *
     * @param ExecuteRequestDTO with endpoint and processid.
     * @return ExecuteRequestDTO with list of input specifiers.
     * @see IInputSpecifier
     */
    private ExecuteRequestDTO execAddInputs(ExecuteRequestDTO dto) {
        try {
            String wpsurl = dto.getEndpoint();
            String[] processes = new String[1];
            processes[0] = dto.getProcessid();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessDescriptionType process : descs) {
                ProcessDescriptionType.DataInputs inputs = process.getDataInputs();
                InputDescriptionType[] _inputs = inputs.getInputArray();
                for (InputDescriptionType description : _inputs) {
                    dto.addInput(description);
                }
            }
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n " + e.getLocalizedMessage());
        }
        return dto;
    }

    /**
     * Adds processs outputs to a dto.
     *
     * @param ExecuteRequestDTO with endpoint and processid.
     * @return ExecuteRequestDTO with list of outputs specifiers.
     * @see IOutputSpecifier
     */
    private ExecuteRequestDTO execAddOutputs(ExecuteRequestDTO dto) {

        try {
            String wpsurl = dto.getEndpoint();
            String[] processes = new String[1];
            processes[0] = dto.getProcessid();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessDescriptionType process : descs) {
                ProcessDescriptionType.ProcessOutputs outputs = process.getProcessOutputs();
                OutputDescriptionType[] _outputs = outputs.getOutputArray();
                for (OutputDescriptionType description : _outputs) {
                    dto.addOutput(description);
                }
            }
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n " + e.getLocalizedMessage());
        }
        return dto;
    }
    
    /**
     * Sets given inputs to  execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of inputs (InputArguments) that should be set.
     * @see IInputArgument
     */
    private void execSetInputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theinputs) {

        java.util.Set<String> keys = theinputs.keySet();

        for (String key : keys) {
            Object o = theinputs.get(key);
            if (o instanceof InputLiteralDataArgument) {
                String value = ((InputLiteralDataArgument) o).getValue();
                executeBuilder.addLiteralData(key, value);
            } else if (o instanceof InputComplexDataArgument) {
                InputComplexDataArgument param = (InputComplexDataArgument) o;

                String url = param.getURL();
                String mimetype = param.getMimeType();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                executeBuilder.addComplexDataReference(key, url, schema, encoding, mimetype);
            }
            //FIXME add BoundingBox
        }
    }

    /**
     * Sets requested outputs to  execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of outputs (OutputArgument) that should be set.
     * @see IOutputArgument
     */
    private void execSetOutputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theoutputs) {
        java.util.Set<String> keys = theoutputs.keySet();

        for (String key : keys) {
            Object o = theoutputs.get(key);

            if (o instanceof OutputLiteralDataArgument) {
                executeBuilder.addOutput(key);
            } else if (o instanceof OutputComplexDataArgument) {
                OutputComplexDataArgument param = (OutputComplexDataArgument) o;
                executeBuilder.addOutput(key);
                boolean asReference = param.isAsReference();
                String mimetype = param.getMimetype();
                String encoding = param.getEncoding();
                String schema = param.getSchema();

                if (asReference) {
                    executeBuilder.setAsReference(key, true);
                }

                executeBuilder.setMimeTypeForOutput(mimetype, key);
                executeBuilder.setEncodingForOutput(encoding, key);
                executeBuilder.setSchemaForOutput(schema, key);
            }
            //FIXME add BoundingBox
        }
    }
}
