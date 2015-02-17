package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class DeployRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    DeployRequest request;

    public DeployRequestHandler(RichWPSClientSession wps, DeployRequest request) {
        this.wps = wps;
        this.request = request;
    }

    @Override
    public void handle() {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
        builder.setDeployExecutionUnit(request.getExecutionUnit());
        builder.setDeployProcessDescription(request.toProcessDescriptionType());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        builder.setKeepExecutionUnit(request.isKeepExecUnit());
        try {
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
            Logger.log(this.getClass(), "richwpsDeployProcess", builder.getDeploydocument());
            Object response = wps.deploy(endp, builder.getDeploydocument());
            if (response == null) {
                Logger.log(this.getClass(), "richwpsDeployProcess()", "No response");
                return;
            }
            if (response instanceof ExceptionReportDocumentImpl) {
                ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof DeployProcessResponseDocumentImpl) {
                DeployProcessResponseDocumentImpl deplok = (DeployProcessResponseDocumentImpl) response;
            } else {
                Logger.log(this.getClass(), "richwpsDeployProcess()", "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsDeployProcess()", "Unable to create " + "deploymentdocument. " + ex);
        }
    }

}
