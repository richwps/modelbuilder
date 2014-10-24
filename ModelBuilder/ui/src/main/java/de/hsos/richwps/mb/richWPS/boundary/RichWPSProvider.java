package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputBoundingBoxDataArgument;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Interface to RichWPS enabled servers.
 *
 * @version 0.0.3
 * @author dalcacer
 */
public class RichWPSProvider implements IRichWPSProvider {

    /**
     * WPS client.
     */
    private WPSClientSession wps;
    /**
     * RichWPS client.
     */
    private RichWPSClientSession wpst;

    /**
     * The deploymentprofile, that should be used.
     */
    public static String deploymentProfile = "rola";

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @throws java.lang.Exception
     */
    @Override
    public void connect(String wpsurl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug::RichWPSProvider#connect\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service " + wpsurl);
        }
    }

    /**
     * Disconnects all connected sevices.
     *
     * @throws java.lang.Exception
     */
    @Override
    public void disconnect() throws Exception {
        this.wps = WPSClientSession.getInstance();
        List<String> endpoints = this.wps.getLoggedServices();
        for (String endpoint : endpoints) {
            this.wps.disconnect(endpoint);
        }
        //TODO
    }

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @throws java.lang.Exception
     */
    @Override
    public void disconnect(String wpsurl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.disconnect(wpsurl);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug::RichWPSProvider#disconnect\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to disconnect from service " + wpsurl);
        }
    }

    /**
     * Connects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param richwpsurl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    @Override
    public void connect(String wpsurl, String richwpsurl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug::RichWPSProvider#connect\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service " + wpsurl + ", " + richwpsurl);
        }

        try {
            this.wpst = RichWPSClientSession.getInstance();
            this.wpst.connect(wpsurl, richwpsurl);
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug::RichWPSProvider#connect\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service " + wpsurl + ", " + richwpsurl);
        }
    }

    /**
     * Disconnects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    @Override
    public void disconnect(String wpsurl, String wpsturl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.disconnect(wpsurl);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service.");
        }

        try {
            this.wpst = RichWPSClientSession.getInstance();
            this.wpst.disconnect(wpsurl);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Unable to connect, " + e.getLocalizedMessage());
            throw new Exception("Unable to connect to service.");
        }
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
                if (process.getIdentifier() != null) {
                    String identifier = process.getIdentifier().getStringValue();
                    processes.add(identifier);
                } else {
                    //de.hsos.richwps.mb.Logger.log("Debug:getAvailableProcesses()" + process);
                }
            }
        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug:getAvailableProcesses()#WPSClientException:\n " + e);
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Debug:getAvailableProcesses()#Exception:\n " + e);
        }
        return processes;
    }

    /**
     * Describes a process, via wps:describeProcess()-Request. Produces
     * DescribeRequest.
     *
     * @param request DescribeRequest with endpoint and processid.
     *
     */
    @Override
    public void describeProcess(de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest request) {
        try {
            String wpsurl = request.getEndpoint();
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
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
                this.execAddInputs(request, processdescriptions);
            }

            if (request.getOutputs().isEmpty()) {
                this.execAddOutputs(request, processdescriptions);
            }

        } catch (WPSClientException ex) {
            de.hsos.richwps.mb.Logger.log("Debug:\n " + ex.getLocalizedMessage());
        }
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequestDTO with endpoint and processid.
     *
     */
    @Override
    public void describeProcess(ExecuteRequest request) {
        this.describeProcess((de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest) request);
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequestDTO with endpoint and processid and in- and
     * outputarguments.
     */
    @Override
    public void executeProcess(ExecuteRequest request) {

        String wpsurl = request.getEndpoint();
        String processid = request.getIdentifier();

        HashMap theinputs = request.getInputArguments();
        HashMap theoutputs = request.getOutputArguments();

        ProcessDescriptionType description = this.getProcessDescriptionType(request);
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

        this.execAnalyseResponse(execute, description, responseObject, request);
    }

    /**
     * Shows the generated message for a DeyployRequest.
     *
     * @param request DeployRequestDTO.
     * @see DeployRequest
     * @return <code>true</code> for deployment success.
     */
    public String showDeployRequest(DeployRequest request) {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();

        builder.setDeployExecutionUnit(request.getExecutionUnit());
        builder.setDeployProcessDescription(request.toProcessDescriptionType());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        builder.setKeepExecutionUnit(request.isKeepExecUnit());
        String doc = "";
        try {
            doc = builder.getDeploydocument().toString();
        } catch (Exception e) {
            Logger.log("Debug::RichWPSProvider#showDeployRequest()\n unable to create deploy request.");
        }
        return doc;

    }

    /**
     * Deploys a new process.
     *
     * @param request DeployRequestDTO.
     * @see DeployRequest
     */
    @Override
    public void deployProcess(DeployRequest request) {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();

        builder.setDeployExecutionUnit(request.getExecutionUnit());
        builder.setDeployProcessDescription(request.toProcessDescriptionType());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        builder.setKeepExecutionUnit(request.isKeepExecUnit());

        try {
            //de.hsos.richwps.mb.Logger.log("Debug:\n Sending \n" + builder.getDeploydocument());
            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;
            de.hsos.richwps.mb.Logger.log("Debug:\n Deploying at " + endp);
            Object response = this.wpst.deploy(endp, builder.getDeploydocument());

            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl) response;
            } else {
                de.hsos.richwps.mb.Logger.log("Debug:\n Unknown reponse" + response);
                de.hsos.richwps.mb.Logger.log("Debug:\n" + response.getClass());
            }
        } catch (WPSClientException ex) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Unable to create "
                    + "deploymentdocument." + ex.getLocalizedMessage());
        }
    }

    /**
     * Undeploys a given process.
     *
     * @param request DeployRequestDTO.
     * @see UndeployRequest
     * @return <code>true</code> for deployment success.
     */
    @Override
    public void undeployProcess(UndeployRequest request) {

        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();

        builder.setIdentifier(request.getIdentifier());

        try {
            Object response = this.wpst.undeploy(request.getEndpoint(), builder.getUndeploydocument());
            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) response;
                de.hsos.richwps.mb.Logger.log("Debug:\n" + deplok.getStringValue());
            } else {
                de.hsos.richwps.mb.Logger.log("Debug:\n Unknown reponse" + response);
                //de.hsos.richwps.mb.Logger.log("Debug:\n" + response.getClass());
            }
        } catch (WPSClientException ex) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Unable to create "
                    + "deploymentdocument." + ex.getLocalizedMessage());
        }
    }

    /**
     * Performs a request.
     *
     * @param request IRequest.
     * @see IRequest
     * @see ExecuteRequest
     * @see ProcessDescription
     * @see DeployRequest
     * @see UndeployRequest
     * @return <code>true</code> for deployment success.
     */
    @Override
    public void request(IRequest request) {
        if (request instanceof ExecuteRequest) {
            this.executeProcess((ExecuteRequest) request);
        } else if (request instanceof DeployRequest) {
            this.deployProcess((DeployRequest) request);
        } else if (request instanceof UndeployRequest) {
            this.undeployProcess((UndeployRequest) request);
        } else if (request instanceof DescribeRequest) {
            this.describeProcess((DescribeRequest) request);
        }
    }

    /**
     * Retreives and extracts the processdescription type from a given
     * WPS-server.
     *
     * @param request ExecuteRequestDTO with endpoint and process id.
     * @return 52n processdescriptiontype.
     */
    private ProcessDescriptionType getProcessDescriptionType(ExecuteRequest request) {
        ProcessDescriptionType description = null;
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(request.getEndpoint());

            //discover
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, request.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            description = descs[0];

        } catch (WPSClientException e) {
            de.hsos.richwps.mb.Logger.log("Debug:\n Something went wrong, "
                    + "describing the process " + request.getIdentifier() + ", " + e);
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
     * @param request ExecuteRequestDTO with possible inputs (IInputSpecifier)
     * and outputs (IOutputSpecifier).
     * @return ExecuteRequestDTO with results or exception.
     */
    private void execAnalyseResponse(ExecuteDocument execute,
            ProcessDescriptionType description, Object responseObject,
            ExecuteRequest request) {
        java.net.URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");
        de.hsos.richwps.mb.Logger.log("Debug:\n using configuration" + file);
        WPSClientConfig.getInstance(file);

        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputArguments();
        de.hsos.richwps.mb.Logger.log("Debug:\n" + responseObject.getClass());
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            de.hsos.richwps.mb.Logger.log("Debug:\n" + response.toString());

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
                                de.hsos.richwps.mb.Logger.log("Debug;\n #thevalue " + value);
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
                            // Blocked by broken commons.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);
                            de.hsos.richwps.mb.Logger.log("Debug:\n the size " + binding.getPayload().size());
                        }
                    }
                }
            } catch (WPSClientException e) {
                de.hsos.richwps.mb.Logger.log("Debug: \n Unable to analyse response. " + e.getLocalizedMessage());
            }
        } else {
            net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            de.hsos.richwps.mb.Logger.log("Debug: \n Unable to analyse response."
                    + "Response is Exception: " + exception.toString());
            de.hsos.richwps.mb.Logger.log("Debug: \n " + exception.getExceptionReport());
        }
    }

    /**
     * Add processs inputs to a request.
     *
     * @param ExecuteRequestDTO with endpoint and processid.
     * @return ExecuteRequestDTO with list of input specifiers.
     * @see IInputSpecifier
     */
    private void execAddInputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.DataInputs inputs = process.getDataInputs();
        InputDescriptionType[] _inputs = inputs.getInputArray();
        for (InputDescriptionType description : _inputs) {
            request.addInput(description);
        }
    }

    /**
     * Adds processs outputs to a request.
     *
     * @param ExecuteRequestDTO with endpoint and processid.
     * @return ExecuteRequestDTO with list of outputs specifiers.
     * @see IOutputSpecifier
     */
    private void execAddOutputs(
            DescribeRequest request, ProcessDescriptionType process) {
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
            } else if (o instanceof InputBoundingBoxDataArgument) {
                InputBoundingBoxDataArgument param;
                param = (InputBoundingBoxDataArgument) o;
                final String crs = param.getCrsType();
                String[] split = param.getValue().split(",");
                for (String s : split) {
                    System.out.println(s);
                }
                BigInteger dimension = BigInteger.valueOf(split.length);
                String[] lower = split[0].split(" ");
                String[] upper = split[1].split(" ");
                //FIXME Correct values                
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
            } else if (o instanceof OutputBoundingBoxDataArgument) {
                //FIXME BoundingBox
                executeBuilder.addOutput(key);
            }
        }
    }

    /**
     * Checks whether an URI is an (52n) WPS-Endpoint or not.
     *
     * @param uri the given endpoint.
     * @return true for wps endpoint, false if not.
     */
    public static boolean isWPSEndpoint(String uri) {
        if (uri.contains(IRichWPSProvider.DEFAULT_WPS_ENDPOINT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether an URI is an (52n) WPST-Endpoint or not.
     *
     * @param uri the given endpoint.
     * @return true for wpst endpoint, false if not.
     */
    public static boolean isWPSTEndpoint(String uri) {
        if (uri.contains(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if WPSTEndpoint exists.
     * @param uri
     * @return 
     */
    public static boolean checkWPSTEndpoint(String uri) {
        // FIXME How else can we check the endpoints existence, and readiness?
        // HTTP::HEAD Operation 405 Method Not Allowed, instead of 404?
        // HTTP::GET Operation 405 Method Not Allowed, instead of 404?
        try {
            URL urlobj = new URL(uri);
            URLConnection conn = urlobj.openConnection();
            conn.connect();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int resp = httpConnection.getResponseCode();
            //FIXME move to wps-client-lib
            if ((resp != 405) && (resp!=200)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean hasProcess(String auri, String processid){
        RichWPSProvider provider = new RichWPSProvider();
        try{
            provider.connect(auri);
        }catch(Exception ex){
            Logger.log("Debug:\n unable to connect to "+auri);
        }
        List<String> processes = provider.getAvailableProcesses(auri);
        return processes.contains(processid);
    }
}
