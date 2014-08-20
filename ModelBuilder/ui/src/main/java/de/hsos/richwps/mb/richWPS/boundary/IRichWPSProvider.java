package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import java.util.List;

/**
 * Interface to RichWPS-enabled servers.
 * @author dalcacer
 */
public interface IRichWPSProvider {

    public void connect(final String wpsurl) throws Exception;

    public void connect(final String wpsurl, final String wpsturl) throws Exception;

    public void connect(final String wpsurl, final String wpsturl, final String testurl) throws Exception;

    public void connect(final String wpsurl, final String wpsturl, final String testurl, final String profileurl) throws Exception;

    public List<String> getAvailableProcesses(final String wpsurl);
    
    public ExecuteRequestDTO describeProcess(ExecuteRequestDTO dto);
    
    public ExecuteRequestDTO executeProcess(final ExecuteRequestDTO dto);
}
