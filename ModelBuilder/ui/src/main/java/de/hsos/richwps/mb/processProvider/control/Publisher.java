package de.hsos.richwps.mb.processProvider.control;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProviderConfig;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.sp.client.ows.EUOM;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import de.hsos.richwps.sp.client.ows.posttypes.PostProcess;
import de.hsos.richwps.sp.client.ows.posttypes.PostQoSTarget;
import de.hsos.richwps.sp.client.ows.posttypes.PostWPS;
import de.hsos.richwps.sp.client.rdf.RDFID;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for publishing at the SemanticProxy.
 *
 * @author dziegenh
 */
public class Publisher {

    private KeyTranslator translator;

    public Publisher(KeyTranslator translator) {
        this.translator = translator;
    }
    
    /**
     * Tries to post the given process at the SemanticProxy via the SPClient.
     *
     * @param wps the SP's WPS to which the process should be added.
     * @param process the ModelBuilder's process which should be published.
     * @throws Exception
     */
    public void publishProcess(WPS wps, ProcessEntity process) throws Exception {

        PostWPS processPostWps = new PostWPS(wps.getRDFID());
        PostProcess postProcess = SpEntityConverter.createSpProcess(processPostWps, process);

        Object targetGroup = process.getPropertyValue(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS);
        if(null == targetGroup) {
            PropertyGroup<Property> mockTarget = QosConverter.createTargetProperties("aTitle", "aAbstr", 0d, 2d, 1d, .5d, "sec");
            PropertyGroup<PropertyGroup> mockTargets = new PropertyGroup<>(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS);
            mockTargets.addObject(mockTarget);
            process.setProperty(ProcessProviderConfig.PROPERTY_KEY_QOS_TARGETS, mockTargets);
        }
        
        List<PostQoSTarget> targets = QosConverter.groupsToSpTargets(process, this.translator);
        postProcess.setQosTargets(new ArrayList<>(targets));
        
        final SPClient spClient = SPClient.getInstance();

        URL endpoint = new URL(process.getServer());
        RDFID lookupProcess = spClient.lookupProcess(endpoint, process.getOwsIdentifier());

        // post new process
        if (null == lookupProcess) {
            spClient.postProcess(postProcess);

        } else {
            // update process
            postProcess.setRDFID(lookupProcess);
            spClient.updateProcess(postProcess);
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
