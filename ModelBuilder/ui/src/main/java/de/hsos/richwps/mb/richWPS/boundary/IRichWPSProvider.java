package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;

/**
 * Interface to RichWPS-enabled servers.
 *
 * @author dalcacer
 * @version 0.0.5
 */
public interface IRichWPSProvider {

    public static final String DEFAULT_52N_WPS_ENDPOINT = "/WebProcessingService";
    public static final String DEFAULT_RICHWPS_ENDPOINT = "/RichWPS";
    public static final String DEFAULT_WPS_ENDPOINT = DEFAULT_52N_WPS_ENDPOINT;

    /**
     * Describes a process, via wps:DescribeRequest()-Request.
     *
     * @param request ExecuteRequest with endpoint and processid.
     *
     
    public void wpsDescribeProcess(ExecuteRequest request);*/

    public String wpsPreviewExecuteProcess(ExecuteRequest request);

    public String richwpsPreviewUndeployProcess(UndeployRequest request);

    public String richwpsPreviewTestProcess(TestRequest request);

    /**
     * Performs a request.
     *
     * @param request IRequest.
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
    public void request(IRequest request);

}
