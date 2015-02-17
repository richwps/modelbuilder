package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import java.util.ArrayList;
import java.util.List;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import de.hsos.richwps.mb.richWPS.entity.IRequest;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class GetProcessesRequestHandler implements IRequestHandler{
    
    WPSClientSession wps;
    GetProcessesRequest request;
    
    public GetProcessesRequestHandler(WPSClientSession wps,  IRequest request) {
        this.wps = wps;
        this.request = (GetProcessesRequest) request;
    }

    @Override
    public void handle() {
         List<String> processes = new ArrayList<>();
        try {
            ProcessDescriptionsDocument pdd = wps.describeAllProcesses(request.getEndpoint());
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessBriefType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessBriefType process : descs) {
                if (process.getIdentifier() != null) {
                    String identifier = process.getIdentifier().getStringValue();
                    processes.add(identifier);
                } else {
                    //de.hsos.richwps.mb.Logger.log("Debug:getAvailableProcesses()" + process);
                }
            }
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "handle()", e);
        }
        request.setProcesses(processes);
    }

    @Override
    public String preview() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
