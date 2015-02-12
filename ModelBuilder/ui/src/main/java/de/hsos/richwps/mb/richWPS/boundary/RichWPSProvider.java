package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.*;

import java.util.HashMap;
import java.util.List;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import net.opengis.wps.x100.TestProcessDocument;
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
    private void connect(String wpsurl) throws Exception {
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
    private void disconnect() throws Exception {
        this.wps = WPSClientSession.getInstance();
        this.richwps = RichWPSClientSession.getInstance();

        List<String> wpsurls = this.wps.getLoggedServices();
        for (String serverid : wpsurls) {

            try {
                this.richwps.disconnect(serverid);
                this.wps.disconnect(serverid);
            } catch (Exception e) {
                Logger.log(this.getClass(), "disconnect()", e.getLocalizedMessage());
                throw new Exception("Unable to disconnect from service " + serverid);

            }
        }
    }

    /**
     * Connects the provider to a WPS-server with RichWPS functionality.
     *
     * @param wpsurl wpsurl of WebProcessingService (used as unique serverid).
     * @param richwpsurl richwpsurl of RichWPS Service.
     * @throws java.lang.Exception when unable to connect to service.
     */
    private void connect(String wpsurl, String richwpsurl) throws Exception {
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
     * Performs a request.
     *
     * @param request the request that should be performed.
     * @see IRequest
     * @see ExecuteRequest
     * @see DeployRequest
     * @see UndeployRequest
     * @see TestRequest
     * @see ProfileRequest
     * @see GetProcessesRequest
     * @see GetInputTypesRequest
     * @see GetOutputTypesRequest
     */
    @Override
    public void perform(IRequest request) {

        //endpoint detection.
        final String givenendpoint = request.getEndpoint();

        String richwpsurl = "";
        String wpsurl = "";
        if (RichWPSProvider.isWPSEndpoint(givenendpoint)) {
            wpsurl = givenendpoint;
            richwpsurl = givenendpoint.replace(RichWPSProvider.DEFAULT_WPS_ENDPOINT, RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isRichWPSEndpoint(givenendpoint)) {
            richwpsurl = givenendpoint;
            wpsurl = givenendpoint.replace(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT, RichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }

        final RichWPSHelper richwpshelper = new RichWPSHelper();
        final WPSHelper wpshelper = new WPSHelper();
        try {
            if (request instanceof TestRequest) {
                //check TestRequest before Execute and Desscribe, it is a differentiation.
                Logger.log(this.getClass(), "request()", "performing " + TestRequest.class.getSimpleName());
                this.connect(wpsurl, richwpsurl);
                richwpshelper.richwpsTestProcess(this.richwps, (TestRequest) request, this);
            } else if (request instanceof GetInputTypesRequest) {
                Logger.log(this.getClass(), "request()", "performing " + GetInputTypesRequest.class.getSimpleName());
                this.connect(wpsurl, richwpsurl);
                richwpshelper.richwpsGetInputTypes(this.richwps, (GetInputTypesRequest) request, this);
            } else if (request instanceof GetOutputTypesRequest) {
                Logger.log(this.getClass(), "request()", "performing " + GetOutputTypesRequest.class.getSimpleName());
                this.connect(wpsurl, richwpsurl);
                richwpshelper.richwpsGetOutputTypes(this.richwps, (GetOutputTypesRequest) request, this);
            } else if (request instanceof DeployRequest) {
                Logger.log(this.getClass(), "request()", "performing " + DeployRequest.class.getSimpleName());
                this.connect(wpsurl, richwpsurl);
                richwpshelper.richwpsDeployProcess(this.richwps, (DeployRequest) request, this);
            } else if (request instanceof UndeployRequest) {
                Logger.log(this.getClass(), "request()", "performing " + UndeployRequest.class.getSimpleName());
                this.connect(wpsurl, richwpsurl);
                richwpshelper.richwpsUndeployProcess(this.richwps, (UndeployRequest) request);
            } else if (request instanceof GetProcessesRequest) {
                Logger.log(this.getClass(), "request()", "performing " + GetProcessesRequest.class.getSimpleName());
                this.connect(wpsurl);
                wpshelper.wpsGetAvailableProcesses(this.wps, (GetProcessesRequest) request, this);
            } else if (request instanceof ExecuteRequest) {
                //check Execute before Describe, it is a differentiation.
                this.connect(wpsurl);
                //executes can be used for process discovery/description, too!
                if (((ExecuteRequest) request).isDescribed()) {
                    Logger.log(this.getClass(), "request()", "performing " + ExecuteRequest.class.getSimpleName());
                    wpshelper.wpsExecuteProcess(this.wps, (ExecuteRequest) request, this);
                } else {
                    Logger.log(this.getClass(), "request()", "performing " + DescribeRequest.class.getSimpleName());
                    wpshelper.wpsDescribeProcess(this.wps, (DescribeRequest) request, this);
                }
            } else if (request instanceof DescribeRequest) {
                Logger.log(this.getClass(), "request()", "performing " + DescribeRequest.class.getSimpleName());
                this.connect(wpsurl);
                wpshelper.wpsDescribeProcess(this.wps, (DescribeRequest) request, this);
            }
            this.disconnect();
        } catch (Exception e) {
            Logger.log(this.getClass(), "request()", e);
        }
    }

    /**
     * Describes process and its' in and outputs.
     *
     * @param request ExecuteRequestDTO with serverid and processid and in- and
     * outputvalues.
     * @return ExecuteRequest as xml or emptystring.
     */
    @Override
    public String wpsPreviewExecuteProcess(ExecuteRequest request) {
        final WPSHelper helper = new WPSHelper();
        String serverid = request.getEndpoint();
        String processid = request.getIdentifier();
        try {
            this.connect(serverid);
        } catch (Exception e) {
        }

        HashMap theinputs = request.getInputValues();
        HashMap theoutputs = request.getOutputValues();

        ProcessDescriptionType description = helper.getProcessDescriptionType(wps, request, this);
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(description);

        helper.setInputs(executeBuilder, theinputs);
        helper.setOutputs(executeBuilder, theoutputs);

        ExecuteDocument execute = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");
            this.disconnect();
            return execute.toString();
        } catch (Exception e) {
            Logger.log(this.getClass(), "wpsPreviewExecuteProcess()", processid + ", " + e);
        }
        return "";
    }

    /**
     * Deploys a new process.
     *
     * @param request TestRequest.
     * @return TestRequest as xml or emptystring.
     * @see TestRequest
     */
    @Override
    public String richwpsPreviewTestProcess(TestRequest request) {
        try {
            final RichWPSHelper richwpsHelper = new RichWPSHelper();
            TestProcessRequestBuilder builder = new TestProcessRequestBuilder(request.toProcessDescriptionType());
            builder.setExecutionUnit(request.getExecutionUnit());
            builder.setDeploymentProfileName(request.getDeploymentprofile());

            HashMap theinputs = request.getInputValues();
            richwpsHelper.setTestProcessInputs(builder, theinputs);

            HashMap theoutputs = request.getOutputValues();
            richwpsHelper.setTestProcessOutputs(builder, theoutputs);

            richwpsHelper.setTestProcessVariables(builder, request.getVariables());

            TestProcessDocument testprocessdocument = null;
            testprocessdocument = builder.getTestdocument();
            return testprocessdocument.toString();
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsPreviewTestProcess", ex);
        }
        return "";
    }

    /**
     * Undeploys a given process.
     *
     * @param request UndeployRequest.
     * @return UndeployRequest as xml or emptystring.
     * @see UndeployRequest
     */
    @Override
    public String richwpsPreviewUndeployProcess(UndeployRequest request) {

        try {
            TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
            builder.setIdentifier(request.getIdentifier());
            return builder.getUndeploydocument().toString();
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsPreviewUndeployProcess", ex);
        }
        return "";
    }

    /**
     * Checks whether an URI is an (52n) WPS-endpoint or not.
     *
     * @param uri the given endpooint.
     * @return true for wps endpooint, false if not.
     */
    public static boolean isWPSEndpoint(String uri) {
        return uri.contains(IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
    }

    /**
     * Checks whether an URI is a RichWPS-endpoint or not.
     *
     * @param uri the given endpooint.
     * @return true for richwps endpooint, false if not.
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
        // FIXME move logic to commons::wps-client-lib
        try {
            URL urlobj = new URL(uri);
            URLConnection conn = urlobj.openConnection();
            conn.connect();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int resp = httpConnection.getResponseCode();

            if ((resp != 405) && (resp != 200)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a process is available on WPS-server.
     *
     * @param auri WPS-endpoint
     * @param processid processid.
     * @return <code>true</code> process exists, <code>false</code> elsewise.
     */
    public static boolean hasProcess(String auri, String processid) {
        RichWPSProvider provider = new RichWPSProvider();
        try {
            provider.connect(auri);
        } catch (Exception ex) {
            Logger.log(RichWPSProvider.class, "hasProcess", "Unable to connect to "
                    + auri + " " + ex);
        }
        GetProcessesRequest request = new GetProcessesRequest(auri);
        provider.perform(request);
        List<String> processes = request.getProcesses();
        return processes.contains(processid);
    }
}
