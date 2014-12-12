package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

/**
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class WPSHelper {

    public WPSHelper() {
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param execute 52n execute document.
     * @param description 52n process description.
     * @param responseObject 52n reponse object. Execute-response or exception.
     * @param request ExecuteRequest with possible inputs (IInputSpecifier) and
     * outputs (IOutputSpecifier).
     */
    public void analyseExecuteResponse(ExecuteDocument execute, ProcessDescriptionType description, Object responseObject, ExecuteRequest request) {
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");

        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputArguments();
        //FIXME simplify
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            Logger.log(this.getClass(), "analyseResponse", response.toString());
            try {
                Set<String> keys = theoutputs.keySet();
                for (String key : keys) {
                    Object o = theoutputs.get(key);
                    if (o instanceof OutputLiteralDataArgument) {
                        OutputLiteralDataArgument argument = (OutputLiteralDataArgument) o;
                        OutputDataType[] outputs = response.getExecuteResponse().getProcessOutputs().getOutputArray();
                        String value = "";
                        for (OutputDataType output : outputs) {
                            final String givenIdentifier = output.getIdentifier().getStringValue();
                            final String wantedIdentifer = argument.getIdentifier();
                            if (givenIdentifier.equals(wantedIdentifer)) {
                                value = output.getData().getLiteralData().getStringValue();
                            }
                        }
                        request.addResult(key, value);
                    } else if (o instanceof OutputComplexDataArgument) {
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, description);
                        OutputComplexDataArgument argument = (OutputComplexDataArgument) o;
                        if (argument.isAsReference()) {
                            String httpkvpref = analyser.getComplexReferenceByIndex(0);
                            request.addResult(key, httpkvpref);
                        } else {
                            // FIXME proper analytics for different bindings.
                            // Blocked by broken commons implementation.
                            // The invoked parser relies on a different WPSConfig
                            // an thus raises an exception in context of a client.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);
                            Logger.log(this.getClass(), "analyseResponse", "the size " + binding.getPayload().size());
                        }
                    }
                }
            } catch (WPSClientException e) {
                Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response. " + e.getLocalizedMessage());
            }
        } else {
            ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response." + "Response is Exception: " + exception.toString());
            Logger.log(this.getClass(), "analyseResponse", exception.getExceptionReport());
        }
    }

    /**
     * Add processs inputs to a request.
     *
     * @param request with serverid and processid.
     * @return request with list of input specifiers.
     * @see IInputSpecifier
     */
    void addInputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.DataInputs inputs = process.getDataInputs();
        InputDescriptionType[] _inputs = inputs.getInputArray();
        for (InputDescriptionType description : _inputs) {
            request.addInput(description);
        }
    }

    /**
     * Adds processs outputs to a request.
     *
     * @param request with serverid and processid.
     * @see IOutputSpecifier
     */
    void addOutputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.ProcessOutputs outputs = process.getProcessOutputs();
        OutputDescriptionType[] _outputs = outputs.getOutputArray();
        for (OutputDescriptionType description : _outputs) {
            request.addOutput(description);
        }
    }

    /**
     * Sets given inputs to execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of inputs (InputArguments) that should be set.
     * @see IInputArgument
     */
    void setInputs(ExecuteRequestBuilder executeBuilder, final HashMap theinputs) {
        final Set<String> keys = theinputs.keySet();
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
            } else if (o instanceof InputBoundingBoxDataArgument) {
                InputBoundingBoxDataArgument param;
                param = (InputBoundingBoxDataArgument) o;
                final String crs = param.getCrsType();
                String[] split = param.getValue().split(",");
                BigInteger dimension = BigInteger.valueOf(split.length);
                String[] lower = split[0].split(" ");
                String[] upper = split[1].split(" ");
                executeBuilder.addBoundingBoxData(key, crs, dimension, Arrays.asList(lower), Arrays.asList(upper));
            }
        }
    }

    /**
     * Sets requested outputs to execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of outputs (OutputArgument) that should be set.
     * @see IOutputArgument
     */
    void setOutputs(ExecuteRequestBuilder executeBuilder, final HashMap theoutputs) {
        final Set<String> keys = theoutputs.keySet();
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
            } else if (o instanceof OutputBoundingBoxDataArgument) {
                //FIXME BoundingBox
                executeBuilder.addOutput(key);
            }
        }
    }

    /**
     * Describes a process, via wps:wpsDescribeProcess()-Request.
     *
     *
     * @param request DescribeRequest containing the server- and processid.
     */
    public void wpsDescribeProcess(WPSClientSession wps, DescribeRequest request, RichWPSProvider richWPSProvider) {
        final String wpsurl = request.getEndpoint();
        WPSHelper wpshelper = new WPSHelper();
        try {
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            ProcessDescriptionType processdescriptions = descs[0];
            if (processdescriptions.getProcessVersion() != null) {
                request.setProcessVersion(processdescriptions.getProcessVersion());
            }
            if (processdescriptions.getTitle() != null) {
                request.setTitle(processdescriptions.getTitle().getStringValue());
            }
            if (processdescriptions.getAbstract() != null) {
                request.setAbstract(processdescriptions.getAbstract().getStringValue());
            }
            if (request.getInputs().isEmpty()) {
                wpshelper.addInputs(request, processdescriptions);
            }
            if (request.getOutputs().isEmpty()) {
                wpshelper.addOutputs(request, processdescriptions);
            }
        } catch (WPSClientException ex) {
            Logger.log(richWPSProvider.getClass(), "wpsDescribeProcess()", ex);
        } catch (Exception ex) {
            Logger.log(richWPSProvider.getClass(), "wpsDescribeProcess()", ex);
        }
    }

    /**
     * Lists all available processes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of processes.
     */
    public void wpsGetAvailableProcesses(WPSClientSession wps, GetProcessesRequest request, RichWPSProvider richWPSProvider) {
        List<String> processes = new ArrayList<>();
        try {
            ProcessDescriptionsDocument pdd = wps.describeAllProcesses(request.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessBriefType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessBriefType process : descs) {
                if (process.getIdentifier() != null) {
                    String identifier = process.getIdentifier().getStringValue();
                    processes.add(identifier);
                } else {
                    //de.hsos.richwps.mb.Logger.log("Debug:wpsGetAvailableProcesses()" + process);
                }
            }
        } catch (WPSClientException e) {
            Logger.log(richWPSProvider.getClass(), "wpsGetAvailableProcesses()", e);
        }
        request.setProcesses(processes);
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequest with serverid and processid and in- and
     * outputarguments.
     */
    public void wpsExecuteProcess(WPSClientSession wps, ExecuteRequest request, RichWPSProvider richWPSProvider) {
        final WPSHelper helper = new WPSHelper();
        String severid = request.getEndpoint();
        String processid = request.getIdentifier();
        HashMap theinputs = request.getInputArguments();
        HashMap theoutputs = request.getOutputArguments();
        ProcessDescriptionType description = getProcessDescriptionType(wps, request, richWPSProvider);
        ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(description);
        helper.setInputs(executeBuilder, theinputs);
        helper.setOutputs(executeBuilder, theoutputs);
        ExecuteDocument execute = null;
        Object response = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");
            WPSClientSession wpsClient = WPSClientSession.getInstance();
            response = wpsClient.execute(severid, execute);
            if (response == null) {
                Logger.log(richWPSProvider.getClass(), "wpsExecuteProcess()", "No response");
                return;
            }
        } catch (Exception e) {
            Logger.log(richWPSProvider.getClass(), "wpsExecuteProcess()", processid + ", " + e);
        }
        helper.analyseExecuteResponse(execute, description, response, request);
    }

    /**
     * Retreives and extracts the processdescription type from a given
     * WPS-server.
     *
     * @param request ExecuteRequest with serverid and process id.
     * @return 52n processdescriptiontype.
     */
    public ProcessDescriptionType getProcessDescriptionType(WPSClientSession wps, ExecuteRequest request, RichWPSProvider richWPSProvider) {
        ProcessDescriptionType description = null;
        try {
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = wps.describeProcess(processes, request.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            description = descs[0];
        } catch (WPSClientException e) {
            Logger.log(richWPSProvider.getClass(), "getProcessDescriptionType", request.getIdentifier() + " " + e);
        }
        return description;
    }
}
