package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetInputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetOutputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;

/**
 * Interface to RichWPS-enabled servers.
 *
 * @author dalcacer
 * @version 0.0.6
 */
public interface IRichWPSProvider {

    public static final String DEFAULT_52N_WPS_ENDPOINT = "/WebProcessingService";
    public static final String DEFAULT_RICHWPS_ENDPOINT = "/RichWPS";
    public static final String DEFAULT_WPS_ENDPOINT = DEFAULT_52N_WPS_ENDPOINT;
    /**
     * The deploymentprofile, that should be used.
     */
    public static final String DEPLOYMENTPROFILE = "rola";

    /**
     * Performs a perform.
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
    public void perform(IRequest request);

    /**
     * Performs a preview.
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
     * @return xml preview.
     */
    public String preview(IRequest request);

}
