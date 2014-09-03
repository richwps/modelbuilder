package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import java.util.List;

/**
 * Interface to RichWPS-enabled servers.
 *
 * @author dalcacer
 */
public interface IRichWPSProvider {

    /**
     * Connects the provider to a WPS-server.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl) throws Exception;

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
     * @param wpsturl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl, final String wpsturl) throws Exception;

    /**
     * Disconnects the provider to a WPS-server with WPS-T functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @throws java.lang.Exception
     */
    public void disconnect(final String wpsurl, final String wpsturl) throws Exception;

    /**
     * Connects the provider to a WPS-server with WPS-T and testing
     * functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @param testurl endpoint of testing interface.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl, final String wpsturl, final String testurl) throws Exception;

    /**
     * Connects the provider to a WPS-server with WPS-T, testing and profiling
     * functionality.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @param wpsturl endpoint of transactional interface.
     * @param testurl endpoint of testing interface.
     * @param profileurl endpoint of profiling interface.
     * @throws java.lang.Exception
     */
    public void connect(final String wpsurl, final String wpsturl, final String testurl, final String profileurl) throws Exception;

    /**
     * Lists all available processes.
     *
     * @param wpsurl endpoint of WebProcessingService.
     * @return list of processes.
     */
    public List<String> getAvailableProcesses(final String wpsurl);

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
