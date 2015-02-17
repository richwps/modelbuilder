package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.IInputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import de.hsos.richwps.mb.richWPS.entity.IRequest;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class DescribeRequestHandler implements IRequestHandler {

    WPSClientSession wps;
    DescribeRequest request;

    public DescribeRequestHandler(WPSClientSession wps, IRequest  request) {
        this.wps = wps;
        this.request = (DescribeRequest) request;
    }

    @Override
    public void handle() {

        final String wpsurl = request.getEndpoint();
        try {
            String[] processes = new String[1];
            processes[0] = request.getIdentifier();
            ProcessDescriptionsDocument pdd = wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            ProcessDescriptionType processdescriptions = descs[0];
            if (processdescriptions.getProcessVersion() != null) {
                request.setProcessVersion(processdescriptions.getProcessVersion());
            }
            if (processdescriptions.getTitle() != null) {
                request.setTitle(processdescriptions.getTitle().getStringValue());
            }
            if (processdescriptions.getAbstract() != null) {
                request.setAbstract(processdescriptions.getAbstract().getStringValue());
            }
            if (request.getInputs().isEmpty()) {
                addInputs(request, processdescriptions);
            }
            if (request.getOutputs().isEmpty()) {
                addOutputs(request, processdescriptions);
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "handle()", ex);
        } catch (Exception ex) {
            Logger.log(this.getClass(), "handle()", ex);
        }
    }

    /**
     * Add processs inputs to a request.
     *
     * @param request with serverid and processid.
     * @see IInputDescription
     */
    public void addInputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.DataInputs inputs = process.getDataInputs();
        InputDescriptionType[] _inputs = inputs.getInputArray();
        for (InputDescriptionType description : _inputs) {
            request.addInput(description);
        }
    }

    /**
     * Adds processs outputs to a request.
     *
     * @param request with serverid and processid.
     * @see IOutputValue
     */
    public void addOutputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.ProcessOutputs outputs = process.getProcessOutputs();
        OutputDescriptionType[] _outputs = outputs.getOutputArray();
        for (OutputDescriptionType description : _outputs) {
            request.addOutput(description);
        }
    }

    @Override
    public String preview() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
