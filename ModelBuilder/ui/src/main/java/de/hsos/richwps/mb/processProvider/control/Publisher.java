package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import de.hsos.richwps.sp.client.ows.posttypes.PostProcess;
import de.hsos.richwps.sp.client.ows.posttypes.PostWPS;

/**
 *
 * @author dziegenh
 */
public class Publisher {

    public void publishProcess(WPS[] spWpss, ProcessEntity process) throws Exception {

        String server = process.getServer();
        WPS wps = ServerProvider.getSpWpsByEndpoint(spWpss, server);
        PostWPS postWps = new PostWPS();

        // TODO handle non-existing WPS ! (->postWps())
        if (null == wps) {
//                PostWPS postWPS = new PostWPS();
//                postWPS.setEndpoint(new URL(server));
//                postWPS.setRichWPSEndpoint(new URL(wps.getRichWPSEndpoint()));

        } else {
            postWps.setRdfId(wps.getRDFID());
        }

        PostProcess postProcess = EntityConverter.createSpProcess(postWps, process);

        SPClient.getInstance().postProcess(postProcess);
    }

}
