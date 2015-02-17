package de.hsos.richwps.mb.richWPS.boundary.handler;


import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import static de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class UndeployRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    UndeployRequest request;

    public UndeployRequestHandler(RichWPSClientSession wps, IRequest request) {
        this.wps = wps;
        this.request = (UndeployRequest) request;
    }

    @Override
    public void handle() {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
        builder.setIdentifier(request.getIdentifier());

        try {
            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;
            //this.richwps.connect(perform.getEndpoint(), endp);
            Object response = wps.undeploy(endp, builder.getUndeploydocument());

            if (response == null) {
                Logger.log(this.getClass(), "handle()", "No response");
                return;
            }

            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) response;
                Logger.log(this.getClass(), "handle()", deplok);
            } else {
                Logger.log(this.getClass(), "handle()", "Unknown reponse" + response);
                Logger.log(this.getClass(), "handle()", response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "handle()", "Unable to create "
                    + "deploymentdocument." + ex);
        }
    }

    public String preview() {
        try {
            TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
            builder.setIdentifier(request.getIdentifier());
            return builder.getUndeploydocument().toString();
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "preview", ex);
        }
        return "";
    }

}
