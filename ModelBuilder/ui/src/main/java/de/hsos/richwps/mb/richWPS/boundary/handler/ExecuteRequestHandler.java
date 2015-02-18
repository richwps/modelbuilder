package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputLiteralDataValue;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import de.hsos.richwps.mb.richWPS.entity.IRequest;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ExecuteRequestHandler implements IRequestHandler {

    WPSClientSession wps;
    ExecuteRequest request;

    public ExecuteRequestHandler(WPSClientSession wps, IRequest request) {
        this.wps = wps;
        this.request = (ExecuteRequest) request;
    }

    public void handle() {
        String severid = request.getEndpoint();
        String processid = request.getIdentifier();
        HashMap theinputs = request.getInputValues();
        HashMap theoutputs = request.getOutputValues();
        ProcessDescriptionType description = getProcessDescriptionType();
        ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(description);
        setInputs(executeBuilder, theinputs);
        setOutputs(executeBuilder, theoutputs);
        ExecuteDocument execute = null;
        Object response = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");
            WPSClientSession wpsClient = WPSClientSession.getInstance();
            response = wpsClient.execute(severid, execute);
            if (response == null) {
                Logger.log(this.getClass(), "handle()", "No response");
                return;
            }
        } catch (Exception e) {
            Logger.log(this.getClass(), "handle()", processid + ", " + e);
        }
        analyseResponse(execute, description, response);
    }

    /**
     * Sets given inputs to execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of inputs (InputValuess) that should be set.
     * @see IInputValue
     */
    private void setInputs(ExecuteRequestBuilder executeBuilder, final HashMap theinputs) {
        final Set<String> keys = theinputs.keySet();
        for (String key : keys) {
            Object o = theinputs.get(key);
            if (o instanceof InputLiteralDataValue) {
                String value = ((InputLiteralDataValue) o).getValue();
                executeBuilder.addLiteralData(key, value);
            } else if (o instanceof InputComplexDataValue) {
                InputComplexDataValue param = (InputComplexDataValue) o;
                String url = param.getURL();
                String mimetype = param.getMimeType();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                executeBuilder.addComplexDataReference(key, url, schema, encoding, mimetype);
            } else if (o instanceof InputBoundingBoxDataValue) {
                InputBoundingBoxDataValue param;
                param = (InputBoundingBoxDataValue) o;
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
     * @param theinputs list of outputs (OutputValue) that should be set.
     * @see IOutputDescription
     */
    private void setOutputs(ExecuteRequestBuilder executeBuilder, final HashMap theoutputs) {
        final Set<String> keys = theoutputs.keySet();
        for (String key : keys) {
            Object o = theoutputs.get(key);
            if (o instanceof OutputLiteralDataValue) {
                executeBuilder.addOutput(key);
            } else if (o instanceof OutputComplexDataValue) {
                OutputComplexDataValue param = (OutputComplexDataValue) o;
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
            } else if (o instanceof OutputBoundingBoxDataValue) {
                //FIXME BoundingBox
                executeBuilder.addOutput(key);
            }
        }
    }

    /**
     * Retreives and extracts the processdescription type from a given
     * WPS-server.
     *
     * @param wps WPSClientSession actual wps client.
     * @param request ExecuteRequest.
     *
     * @return 52n processdescriptiontype.
     */
    private ProcessDescriptionType getProcessDescriptionType() {
        ProcessDescriptionType description = null;
        try {
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = wps.describeProcess(processes, request.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            description = descs[0];
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "getProcessDescriptionType", request.getIdentifier() + " " + e);
        }
        return description;
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param execute 52n execute document.
     * @param description 52n process description.
     * @param responseObject 52n reponse object. Execute-response or exception.
     * @param request ExecuteRequest with possible inputs (IInputDescription)
     * and outputs (IOutputValue).
     */
    private void analyseResponse(ExecuteDocument execute, ProcessDescriptionType description, Object responseObject) {
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");

        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputValues();
        //FIXME simplify
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            Logger.log(this.getClass(), "analyseResponse", response.toString());
            try {
                Set<String> keys = theoutputs.keySet();
                for (String key : keys) {
                    Object o = theoutputs.get(key);
                    if (o instanceof OutputLiteralDataValue) {
                        OutputLiteralDataValue outputvalue = (OutputLiteralDataValue) o;
                        OutputDataType[] outputs = response.getExecuteResponse().getProcessOutputs().getOutputArray();
                        String value = "";
                        for (OutputDataType output : outputs) {
                            final String givenIdentifier = output.getIdentifier().getStringValue();
                            final String wantedIdentifer = outputvalue.getIdentifier();
                            if (givenIdentifier.equals(wantedIdentifer)) {
                                value = output.getData().getLiteralData().getStringValue();
                            }
                        }
                        request.addResult(key, value);
                    } else if (o instanceof OutputComplexDataValue) {
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, description);
                        OutputComplexDataValue outputvalue = (OutputComplexDataValue) o;
                        
                        if (outputvalue.isAsReference()) {
                            String httpkvpref = analyser.getComplexReferenceByIndex(0);
                            URL httpKVPref = new URL(httpkvpref);
                            request.addResult(key, httpKVPref);
                                
                            /*if (httpKVPref.toString().equalsIgnoreCase(httpkvpref)) {
                            
                            } else {
                                // TODO: error
                            }*/

                        } else {
                            // FIXME proper analytics for different bindings.
                            // Blocked by broken commons implementation.
                            // The invoked parser relies on a different WPSConfig
                            // an thus raises an exception in context of a client.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);
                            Logger.log(this.getClass(), "analyseResponse", "the size " + binding.getPayload().size());
                        }
                    } else if (o instanceof OutputBoundingBoxDataValue) {

                        ExecuteResponseDocument.ExecuteResponse exResp;
                        exResp = response.getExecuteResponse();
                        if ("Process successful".equals(exResp.getStatus().getProcessSucceeded())) {
                            OutputDataType[] outputArray;
                            outputArray = exResp.getProcessOutputs().getOutputArray();
                            request.addResult(key, outputArray);
                        }
                    }
                }
            } catch (WPSClientException e) {
                Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response. " + e.getLocalizedMessage());
            } catch (MalformedURLException ex) {
                Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response. " + ex.getLocalizedMessage());
            }
        } else {
            ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response." + "Response is Exception: " + exception.toString());
            Logger.log(this.getClass(), "analyseResponse", exception.getExceptionReport());
        }
    }

    public String preview() {
        final String processid = request.getIdentifier();

        HashMap theinputs = request.getInputValues();
        HashMap theoutputs = request.getOutputValues();

        /*GetProcessesRequest request = new GetProcessesRequest(serverid);
         new GetProcessesRequestHandler(this.wps, (GetProcessesRequest) request).handle();*/
        ProcessDescriptionType description = getProcessDescriptionType();
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(description);

        setInputs(executeBuilder, theinputs);
        setOutputs(executeBuilder, theoutputs);

        ExecuteDocument execute = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");

            return execute.toString();
        } catch (Exception e) {
            Logger.log(this.getClass(), "preview()", processid + ", " + e);
        }
        return "";
    }
}
