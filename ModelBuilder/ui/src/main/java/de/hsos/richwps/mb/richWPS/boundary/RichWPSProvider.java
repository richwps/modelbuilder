package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import java.util.HashMap;
import java.util.List;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.ComplexTypesType;
import net.opengis.wps.x100.SupportedTypesResponseDocument;
import net.opengis.wps.x100.TestProcessDocument;
import org.n52.wps.client.richwps.GetSupportedTypesRequestBuilder;
import org.n52.wps.client.richwps.TestProcessRequestBuilder;

/**
 * Interface to RichWPS enabled servers.
 *
 * @version 0.0.5
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
    private RichWPSClientSession richwps;

    /**
     * The deploymentprofile, that should be used.
     */
    public static String deploymentProfile = "rola";

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @throws java.lang.Exception
     */
    @Override
    public void connect(String wpsurl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "connect()", "Unable to connect, " + e);
            throw new Exception("Unable to connect to service " + wpsurl);
        }
    }

    /**
     * Disconnects all connected services.
     *
     * @throws java.lang.Exception
     */
    @Override
    public void disconnect() throws Exception {
        this.wps = WPSClientSession.getInstance();
        this.richwps = RichWPSClientSession.getInstance();

        List<String> wpsurls = this.wps.getLoggedServices();
        for (String serverid : wpsurls) {

            try {
                this.richwps.disconnect(serverid);
                this.wps.disconnect(serverid);
            } catch (Exception e) {
                //nop
            }
        }
    }

    /**
     * Connects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl wpsurl of WebProcessingService (used as unique serverid).
     * @param richwpsurl richwpsurl of RichWPS Service.
     * @throws java.lang.Exception when unable to connect to service.
     */
    @Override
    public void connect(String wpsurl, String richwpsurl) throws Exception {
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "connect()", e.getLocalizedMessage());
            throw new Exception("Unable to connect to service " + wpsurl + ", " + richwpsurl);
        }

        try {
            this.richwps = RichWPSClientSession.getInstance();
            this.richwps.connect(wpsurl, richwpsurl);
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "connect()", e.getLocalizedMessage());
            throw new Exception("Unable to connect to service " + wpsurl + ", " + richwpsurl);
        }
    }

    /**
     * Lists all available processes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of processes.
     */
    @Override
    public List<String> wpsGetAvailableProcesses(String wpsurl) {

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
                    //de.hsos.richwps.mb.Logger.log("Debug:wpsGetAvailableProcesses()" + process);
                }
            }
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "wpsGetAvailableProcesses()", e);
        }
        return processes;
    }

    /**
     * Lists all available inputtypes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of formats..
     */
    @Override
    public List<List<String>> richwpsGetInputTypes(String wpsurl) {

        String richwpsurl = wpsurl;
        richwpsurl = richwpsurl.split(RichWPSProvider.DEFAULT_52N_WPS_ENDPOINT)[0] + DEFAULT_RICHWPS_ENDPOINT;
        List<List<String>> formats = new LinkedList<>();
        try {
            this.richwps = RichWPSClientSession.getInstance();
            this.richwps.connect(wpsurl, richwpsurl);
            GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
            builder.setComplexTypesOnly(true);
            // request supported types
            Object responseObject = richwps.getSupportedTypes(wpsurl, builder.build());
            if (responseObject instanceof SupportedTypesResponseDocument) {
                SupportedTypesResponseDocument response = (SupportedTypesResponseDocument) responseObject;
                ComplexTypesType[] types = response.getSupportedTypesResponse().getSupportedInputTypes().getComplexTypesArray();
                for (ComplexTypesType type : types) {
                    ComplexDataDescriptionType[] schonwiedertsypes = type.getTypeArray();
                    for (ComplexDataDescriptionType atype : schonwiedertsypes) {
                        List<String> aformat = new LinkedList<>();
                        aformat.add(atype.getMimeType());
                        aformat.add(atype.getSchema());
                        aformat.add(atype.getEncoding());
                        formats.add(aformat);
                    }
                }
            }
            if (responseObject instanceof ExceptionReportDocument) {
                ExceptionReportDocument response = (ExceptionReportDocument) responseObject;
                Logger.log(this.getClass(), "richwpsGetInputTypes()", response.toString());
            }
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "richwpsGetInputTypes()", e);
        }
        return formats;
    }

    /**
     * Lists all available inputtypes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of formats..
     */
    @Override
    public List<List<String>> richwpsGetOutputTypes(String wpsurl) {

        String richwpsurl = wpsurl;
        richwpsurl = richwpsurl.split(RichWPSProvider.DEFAULT_52N_WPS_ENDPOINT)[0] + DEFAULT_RICHWPS_ENDPOINT;
        List<List<String>> formats = new LinkedList<>();
        try {
            this.richwps = RichWPSClientSession.getInstance();
            this.richwps.connect(wpsurl, richwpsurl);
            GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
            builder.setComplexTypesOnly(true);
            // request supported types
            Object responseObject = richwps.getSupportedTypes(wpsurl, builder.build());
            if (responseObject instanceof SupportedTypesResponseDocument) {
                SupportedTypesResponseDocument response = (SupportedTypesResponseDocument) responseObject;
                ComplexTypesType[] types = response.getSupportedTypesResponse().getSupportedOutputTypes().getComplexTypesArray();
                for (ComplexTypesType type : types) {
                    ComplexDataDescriptionType[] schonwiedertsypes = type.getTypeArray();
                    for (ComplexDataDescriptionType atype : schonwiedertsypes) {
                        List<String> aformat = new LinkedList<>();
                        aformat.add(atype.getMimeType());
                        aformat.add(atype.getSchema());
                        aformat.add(atype.getEncoding());
                        formats.add(aformat);
                    }
                }
            }
            if (responseObject instanceof ExceptionReportDocument) {
                ExceptionReportDocument response = (ExceptionReportDocument) responseObject;
                Logger.log(this.getClass(), "richwpsGetOutputTypes()", response.toString());
            }
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "richwpsGetOutputTypes()", e);
        }
        return formats;
    }

    /**
     * Describes a process, via wps:wpsDescribeProcess()-Request. Produces
     * DescribeRequest.
     *
     * @param request DescribeRequest with serverid and processid.
     *
     */
    @Override
    public void wpsDescribeProcess(de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest request) {
        ExecuteRequestHelper helper = new ExecuteRequestHelper();
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
                helper.addInputs(request, processdescriptions);
            }

            if (request.getOutputs().isEmpty()) {
                helper.addOutputs(request, processdescriptions);
            }

        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "wpsDescribeProcess()", ex);
        }
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequestDTO with serverid and processid.
     *
     */
    @Override
    public void wpsDescribeProcess(ExecuteRequest request) {
        this.wpsDescribeProcess((de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest) request);
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequestDTO with serverid and processid and in- and
     * outputarguments.
     */
    @Override
    public void wpsExecuteProcess(ExecuteRequest request) {
        final ExecuteRequestHelper helper = new ExecuteRequestHelper();
        String severid = request.getEndpoint();
        String processid = request.getIdentifier();

        HashMap theinputs = request.getInputArguments();
        HashMap theoutputs = request.getOutputArguments();

        ProcessDescriptionType description = this.getProcessDescriptionType(request);
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(description);

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
                Logger.log(this.getClass(), "wpsExecuteProcess()", "No response");
                return;
            }
        } catch (Exception e) {
            Logger.log(this.getClass(), "wpsExecuteProcess()", processid + ", " + e);
        }

        helper.analyseResponse(execute, description, response, request);
    }

    /**
     * Deploys a new process.
     *
     * @param request DeployRequestDTO.
     * @see DeployRequest
     */
    @Override
    public void richwpsTestProcess(TestRequest request) {
        final TestRequestHelper helper = new TestRequestHelper();
        TestProcessRequestBuilder builder = new TestProcessRequestBuilder(request.toProcessDescriptionType());
        builder.setTestExecutionUnit(request.getExecutionUnit());
        builder.setTestDeploymentProfileName(request.getDeploymentprofile());

        HashMap theinputs = request.getInputArguments();
        helper.setInputs(builder, theinputs);

        HashMap theoutputs = request.getOutputArguments();
        helper.setOutputs(builder, theoutputs);

        TestProcessDocument testprocessdocument = null;
        Object response = null;
        try {
            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;

            testprocessdocument = builder.getTestdocument();
            response = this.richwps.test(endp, testprocessdocument);

            if (response == null) {
                Logger.log(this.getClass(), "richwpsTestProcess()", "No response");
                return;
            }
            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.TestProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.TestProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.TestProcessResponseDocumentImpl) response;
                helper.analyseResponse(testprocessdocument, response, request);
            } else {
                Logger.log(this.getClass(), "richwpsTestProcess()",
                        "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsTestProcess()", "Unable to create "
                    + "deploymentdocument. " + ex);
        }
    }

    /**
     * Deploys a new process.
     *
     * @param request DeployRequestDTO.
     * @see DeployRequest
     */
    @Override
    public void richwpsDeployProcess(DeployRequest request) {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();

        builder.setDeployExecutionUnit(request.getExecutionUnit());
        builder.setDeployProcessDescription(request.toProcessDescriptionType());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        builder.setKeepExecutionUnit(request.isKeepExecUnit());

        try {

            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;

            Object response = this.richwps.deploy(endp, builder.getDeploydocument());
            if (response == null) {
                Logger.log(this.getClass(), "richwpsDeployProcess()", "No response");
                return;
            }
            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl) response;
            } else {
                Logger.log(this.getClass(), "richwpsDeployProcess()",
                        "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsDeployProcess()", "Unable to create "
                    + "deploymentdocument. " + ex);
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
    public void richwpsUndeployProcess(UndeployRequest request) {

        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
        builder.setIdentifier(request.getIdentifier());

        try {
            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;
            //this.richwps.connect(request.getEndpoint(), endp);
            Object response = this.richwps.undeploy(endp, builder.getUndeploydocument());

            if (response == null) {
                Logger.log(this.getClass(), "richwpsUndeployProcess()", "No response");
                return;
            }

            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) response;
                Logger.log(this.getClass(), "richwpsUndeployProcess()", deplok);
            } else {
                Logger.log(this.getClass(), "richwpsUndeployProcess()", "Unknown reponse" + response);
                Logger.log(this.getClass(), "richwpsUndeployProcess()", response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsUndeployProcess()", "Unable to create "
                    + "deploymentdocument." + ex);
        }
    }

    /**
     * Performs a wps/richwps-request.
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
        if (request instanceof TestRequest) {   //first to check!
            this.richwpsTestProcess((TestRequest) request);
        } else if (request instanceof ExecuteRequest) {
            this.wpsExecuteProcess((ExecuteRequest) request);
        } else if (request instanceof DeployRequest) {
            this.richwpsDeployProcess((DeployRequest) request);
        } else if (request instanceof UndeployRequest) {
            this.richwpsUndeployProcess((UndeployRequest) request);
        } else if (request instanceof DescribeRequest) {
            this.wpsDescribeProcess((DescribeRequest) request);
        }
    }

    /**
     * Retreives and extracts the processdescription type from a given
     * WPS-server.
     *
     * @param request ExecuteRequestDTO with serverid and process id.
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
            de.hsos.richwps.mb.Logger.log("Debug:getProcessDescriptionType()\n Something went wrong, "
                    + "describing the process " + request.getIdentifier() + ", " + e);
        }
        return description;
    }

    /**
     * Checks whether an URI is an (52n) WPS-endpoint or not.
     *
     * @param uri the given serverid.
     * @return true for wps serverid, false if not.
     */
    public static boolean isWPSEndpoint(String uri) {
        return uri.contains(IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
    }

    /**
     * Checks whether an URI is a RichWPS-endpoint or not.
     *
     * @param uri the given serverid.
     * @return true for richwps serverid, false if not.
     */
    public static boolean isRichWPSEndpoint(String uri) {
        return uri.contains(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
    }

    /**
     * Checks if RichWPS endpoint is reachable.
     *
     * @param uri RichWPS-endpoint.
     * @return <code>true</code> if available, <code>false</code> elsewise.
     */
    public static boolean checkRichWPSEndpoint(String uri) {
        // FIXME How else can we check the wpsurls existence, and readiness?
        // HTTP::HEAD Operation 405 Method Not Allowed, instead of 404?
        // HTTP::GET Operation 405 Method Not Allowed, instead of 404?
        try {
            URL urlobj = new URL(uri);
            URLConnection conn = urlobj.openConnection();
            conn.connect();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int resp = httpConnection.getResponseCode();
            //FIXME move to wps-client-lib
            if ((resp != 405) && (resp != 200)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a process is available.
     *
     * @param auri WPS-enddpoint
     * @param processid processid.
     * @return <code>true</code> process exists, <code>false</code> elsewise.
     */
    public static boolean hasProcess(String auri, String processid) {
        RichWPSProvider provider = new RichWPSProvider();
        try {
            provider.connect(auri);
        } catch (Exception ex) {
            Logger.log(RichWPSProvider.class, "hasProcess", "Unable to connect to " + auri + " " + ex);
        }
        List<String> processes = provider.wpsGetAvailableProcesses(auri);
        return processes.contains(processid);
    }
}
