package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.sp.client.InternalSPException;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import de.hsos.richwps.sp.client.ows.posttypes.PostProcess;
import de.hsos.richwps.sp.client.ows.posttypes.PostWPS;
import java.net.URL;

/**
 * Provides methods for publishing at the SemanticProxy.
 *
 * @author dziegenh
 */
public class Publisher {

    /**
     * Tries to post the given process at the SemanticProxy via the SPClient.
     *
     * @param wps the SP's WPS to which the process should be added.
     * @param process the ModelBuilder's process which should be published.
     * @throws Exception
     */
    public void publishProcess(WPS wps, ProcessEntity process) throws Exception {

        PostWPS processPostWps = new PostWPS(wps.getRDFID());
        PostProcess postProcess = EntityConverter.createSpProcess(processPostWps, process);

        try {
            SPClient.getInstance().postProcess(postProcess);

            // FIXME create a unique exception for posting already published processes 
            //  - OR - 
            // integrate hasProcess(WPS, id) at SPClient in order to distinguish postProcess and updateProcess
        } catch (InternalSPException ispex) {
            if (ispex.getMessage().contains("A process with this WPS and identifier is already registered")) {
                // FIXME won't work (see SP ticket #1)
                SPClient.getInstance().updateProcess(postProcess);
            } else {
                throw ispex;
            }
        }
    }

    /**
     * Tries to post a WPS for the given endpoint at the SemanticProxy via the
     * SPClient.
     *
     * @param server the endpoint for the WPS.
     * @throws Exception
     */
    public void publishWps(String server) throws Exception {
        PostWPS postWPS = new PostWPS();
        postWPS.setEndpoint(new URL(server));
        String richWps = server.replace(RichWPSProvider.DEFAULT_WPS_ENDPOINT, RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
        postWPS.setRichWPSEndpoint(new URL(richWps));
        SPClient.getInstance().postWPS(postWPS);
    }

}
