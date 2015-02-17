package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.boundary.handler.DeployRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.DescribeRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.ExecuteRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.GetInputTypesRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.GetOutputTypesRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.GetProcessesRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.ProfileRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.TestRequestHandler;
import de.hsos.richwps.mb.richWPS.boundary.handler.UndeployRequestHandler;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.*;
import java.lang.reflect.Constructor;

import java.util.HashMap;
import java.util.List;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.client.RichWPSClientSession;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Interface to RichWPS enabled servers.
 *
 * @version 0.0.7
 * @author dalcacer
 */
public class RichWPSProvider implements IRichWPSProvider {

    private Map<Class, Class> richwpshandler = new HashMap();
    private Map<Class, Class> wpshandler = new HashMap();
    private Class[] richwpshandlerparams = new Class[2];
    private Class[] wpshandlerparams = new Class[2];
    /**
     * WPS client.
     */
    private WPSClientSession wps;
    /**
     * RichWPS client.
     */
    private RichWPSClientSession richwps;

    /**
     * Setup this facade.
     */
    public RichWPSProvider() {
        richwpshandler.put(ProfileRequest.class, ProfileRequestHandler.class);
        richwpshandler.put(TestRequest.class, TestRequestHandler.class);
        richwpshandler.put(GetInputTypesRequest.class, GetInputTypesRequestHandler.class);
        richwpshandler.put(GetOutputTypesRequest.class, GetOutputTypesRequestHandler.class);
        richwpshandler.put(DeployRequest.class, DeployRequestHandler.class);
        richwpshandler.put(UndeployRequest.class, UndeployRequestHandler.class);
        richwpshandlerparams = new Class[2];
        richwpshandlerparams[0] = RichWPSClientSession.class;
        richwpshandlerparams[1] = IRequest.class;
        wpshandler.put(GetProcessesRequest.class, GetProcessesRequestHandler.class);
        wpshandler.put(ExecuteRequest.class, ExecuteRequestHandler.class);
        wpshandler.put(DescribeRequest.class, DescribeRequestHandler.class);
        wpshandlerparams = new Class[2];
        wpshandlerparams[0] = WPSClientSession.class;
        wpshandlerparams[1] = IRequest.class;
    }

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @throws java.lang.Exception
     */
    private void connect(final String wpsurl) throws Exception {
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
    private void connect(final String wpsurl, final String richwpsurl)
            throws Exception {
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
        String[] endpoints = RichWPSProvider.deliverEndpoints(givenendpoint);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

        IRequestHandler handler = null;
        try {
            //test for richwps requests
            for (Class key : richwpshandler.keySet()) {
                if (key == request.getClass()) {
                    Logger.log(this.getClass(), "perform()", "performing " + key);
                    Class handlertype = richwpshandler.get(key);

                    this.connect(wpsendpoint, richwpsendpoint);
                    Constructor ct = handlertype.getDeclaredConstructor(richwpshandlerparams);
                    handler = (IRequestHandler) ct.newInstance(richwps, request);
                }
            }

            //then regular wps requests
            for (Class key : wpshandler.keySet()) {
                if (key == request.getClass()) {
                    Logger.log(this.getClass(), "perform()", "performing " + key);
                    Class handlertype = wpshandler.get(key);
                    //special case
                    if (key == ExecuteRequest.class) {
                        if (!((ExecuteRequest) request).isDescribed()) {
                            handlertype = DescribeRequestHandler.class;
                            //wpshandler.get(DescribeRequest.class);
                        }
                    }
                    this.connect(wpsendpoint);
                    Constructor ct = handlertype.getDeclaredConstructor(wpshandlerparams);
                    handler = (IRequestHandler) ct.newInstance(wps, request);
                }
            }

            handler.handle();
            this.disconnect();
        } catch (Exception e) {
            Logger.log(this.getClass(), "perform()", "Handler for "
                    + "request could not be found." + e);
        }
    }

    /**
     * Performs a preview.
     *
     * @param request the request that should be preview (if possible).
     * @see IRequest
     * @see ExecuteRequest
     * @see DeployRequest
     * @see UndeployRequest
     * @see TestRequest
     * @see ProfileRequest
     * @see GetProcessesRequest
     * @see GetInputTypesRequest
     * @see GetOutputTypesRequest
     * @return preview xml.
     */
    @Override
    public String preview(IRequest request) {

        //endpoint detection.
        final String givenendpoint = request.getEndpoint();
        String[] endpoints = RichWPSProvider.deliverEndpoints(givenendpoint);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];

        IRequestHandler handler = null;
        try {
            //test for richwps requests
            for (Class key : richwpshandler.keySet()) {
                if (key == request.getClass()) {
                    Logger.log(this.getClass(), "preview()", "previewing " + key);
                    Class handlertype = richwpshandler.get(key);

                    this.connect(wpsendpoint, richwpsendpoint);
                    Constructor ct = handlertype.getDeclaredConstructor(richwpshandlerparams);
                    handler = (IRequestHandler) ct.newInstance(richwps, request);
                }
            }

            //then regular wps requests
            for (Class key : wpshandler.keySet()) {
                if (key == request.getClass()) {
                    Logger.log(this.getClass(), "preview()", "previewing " + key);
                    Class handlertype = wpshandler.get(key);

                    this.connect(wpsendpoint);
                    Constructor ct = handlertype.getDeclaredConstructor(wpshandlerparams);
                    handler = (IRequestHandler) ct.newInstance(wps, request);
                }
            }

            String str = handler.preview();
            this.disconnect();
            return str;
        } catch (Exception e) {
            Logger.log(this.getClass(), "perform()", "Handler for "
                    + "request could not be found." + e);
        }
        return "";
    }

    /**
     * Checks whether an URI is an (52n) WPS-endpoint or not.
     *
     * @param uri the given endpooint.
     * @return true for wps endpooint, false if not.
     */
    public static boolean isWPSEndpoint(final String uri) {
        return uri.contains(IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
    }

    /**
     * Checks whether an URI is a RichWPS-endpoint or not.
     *
     * @param uri the given endpooint.
     * @return true for richwps endpooint, false if not.
     */
    public static boolean isRichWPSEndpoint(final String uri) {
        return uri.contains(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
    }

    /**
     * Checks if RichWPS endpoint is reachable.
     *
     * @param uri RichWPS-endpoint.
     * @return <code>true</code> if available, <code>false</code> elsewise.
     */
    public static boolean checkRichWPSEndpoint(final String uri) {
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
     * @param wpsendpoint WPS-endpoint
     * @param processid processid.
     * @return <code>true</code> process exists, <code>false</code> elsewise.
     */
    public static boolean hasProcess(final String wpsendpoint, final String processid) {
        RichWPSProvider provider = new RichWPSProvider();
        try {
            provider.connect(wpsendpoint);

        } catch (Exception ex) {
            Logger.log(RichWPSProvider.class, "hasProcess", "Unable to connect to "
                    + wpsendpoint + " " + ex);
        }
        GetProcessesRequest request = new GetProcessesRequest(wpsendpoint);
        provider.perform(request);
        List<String> processes = request.getProcesses();
        return processes.contains(processid);
    }

    /**
     * Takes one endpoint (richwps or wps) and delivers the tupel of endpoints.
     *
     * @param auri
     * @return <code>[0]=wpsendpoint</code>, <code>[1]=richwpsendpoints</code>.
     */
    public static String[] deliverEndpoints(final String auri) {
        String wpsendpoint = "";
        String richwpsendpoint = "";
        if (RichWPSProvider.isWPSEndpoint(auri)) {
            wpsendpoint = auri;
            richwpsendpoint = wpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT,
                    RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        } else if (RichWPSProvider.isRichWPSEndpoint(auri)) {
            richwpsendpoint = auri;
            wpsendpoint = richwpsendpoint.replace(
                    IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT,
                    IRichWPSProvider.DEFAULT_WPS_ENDPOINT);
        }
        String[] ret = new String[2];
        ret[0] = wpsendpoint;
        ret[1] = richwpsendpoint;
        return ret;
    }
}
