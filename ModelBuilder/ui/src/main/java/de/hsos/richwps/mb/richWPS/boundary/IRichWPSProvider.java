package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import java.util.List;

/**
 * Interface to RichWPS-enabled servers.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public interface IRichWPSProvider {
    public static final String DEFAULT_52N_WPS_ENDPOINT = "/WebProcessingService";
    public static final String DEFAULT_RICHWPS_ENDPOINT = "/RichWPS";
    public static final String DEFAULT_WPS_ENDPOINT = DEFAULT_52N_WPS_ENDPOINT;

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl) throws Exception;

    /**
     * Disconnects all connected services.
     * @throws Exception 
     */
    public void disconnect() throws Exception;
    /**
     * Disconnects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @throws java.lang.Exception
     */
    public void disconnect(final String wpsurl) throws Exception;

    /**
     * Connects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param richwpsurl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl, final String richwpsurl) throws Exception;

    /**
     * Disconnects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param richwpsurl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    public void disconnect(final String wpsurl, final String richwpsurl) throws Exception;


    /**
     * Lists all available processes.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @return list of processes.
     */
    public List<String> getAvailableProcesses(final String wpsurl);

     /**
     * Describes a process, via wps:describeProcess()-Request.
     * Produces DescribeRequest.
     *
     * @param request DescribeRequest with endpoint and processid.
     *
     */
    public void describeProcess(DescribeRequest request);
    
    /**
     * Describes a process, via wps:describeProcess()-Request.
     *
     * @param request ExecuteRequest with endpoint and processid.
     *
     */
    public void describeProcess(ExecuteRequest request);

    /**
     * Executes a process, via wps:execute()-Request.
     *
     * @param request ExecuteRequest with endpoint and processid and in- and
     * outputarguments.
     */
    public void executeProcess(ExecuteRequest request);

    /**
     * Deploys a process, via wpst:deploy()-Request.
     *
     * @param request DeployRequest.
     * @see DeployRequest
     */
    public void deployProcess(DeployRequest request);

    /**
     * Undeploys a given process, via wpst:undeploy()-Request.
     *
     * @param request DeployRequest.
     * @see UneployRequest
     */
    public void undeployProcess(UndeployRequest request);

    /**
     * Performs a request.
     *
     * @param request IRequest.
     * @see IRequest
     * @see ExecuteRequest
     * @see DeployRequest
     * @see UndeployRequest
     * @return <code>true</code> for deployment success.
     */
    public void request(IRequest request);
    
}
