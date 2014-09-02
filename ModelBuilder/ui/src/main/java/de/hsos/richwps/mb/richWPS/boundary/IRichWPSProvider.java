package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import java.util.List;

/**
 * Interface to RichWPS-enabled servers.
 *
 * @author dalcacer
 */
public interface IRichWPSProvider {

    /**
     *
     * @param wpsurl
     * @throws Exception
     */
    public void connect(final String wpsurl) throws Exception;

    /**
     *
     * @param wpsurl
     * @param wpsturl
     * @throws Exception
     */
    public void connect(final String wpsurl, final String wpsturl) throws Exception;

    /**
     *
     * @param wpsurl
     * @param wpsturl
     * @param testurl
     * @throws Exception
     */
    public void connect(final String wpsurl, final String wpsturl, final String testurl) throws Exception;

    /**
     *
     * @param wpsurl
     * @param wpsturl
     * @param testurl
     * @param profileurl
     * @throws Exception
     */
    public void connect(final String wpsurl, final String wpsturl, final String testurl, final String profileurl) throws Exception;

    /**
     *
     * @param wpsurl
     * @return
     */
    public List<String> getAvailableProcesses(final String wpsurl);

    /**
     *
     * @param dto
     */
    public void describeProcess(ExecuteRequest dto);

    /**
     *
     * @param dto
     */
    public void executeProcess(ExecuteRequest dto);

    /**
     *
     * @param dto
     * @return
     */
    public DeployRequest deploy(final DeployRequest dto);
}
