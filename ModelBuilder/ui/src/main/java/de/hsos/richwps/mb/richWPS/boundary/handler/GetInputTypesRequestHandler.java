package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.GetInputTypesRequest;
import java.util.LinkedList;
import java.util.List;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.ComplexTypesType;
import net.opengis.wps.x100.SupportedTypesResponseDocument;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.richwps.GetSupportedTypesRequestBuilder;
import de.hsos.richwps.mb.richWPS.entity.IRequest;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class GetInputTypesRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    GetInputTypesRequest request;

    public GetInputTypesRequestHandler(RichWPSClientSession wps, IRequest request) {
        this.wps = wps;
        this.request = (GetInputTypesRequest) request;
    }

    @Override
    public void handle() {
        List<List<String>> formats = new LinkedList<>();
        GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
        builder.setComplexTypesOnly(true);
        Object responseObject = null;
        try {
            responseObject = wps.getSupportedTypes(request.getServerId(), builder.build());
        } catch (Exception e) {
            Logger.log(this.getClass(), "handle()", e);
        }
        if (responseObject instanceof SupportedTypesResponseDocument) {
            SupportedTypesResponseDocument response = (SupportedTypesResponseDocument) responseObject;
            ComplexTypesType[] types = response.getSupportedTypesResponse().getSupportedInputTypes().getComplexTypesArray();
            for (ComplexTypesType type : types) {
                ComplexDataDescriptionType[] schonwiedertsypes = type.getTypeArray();
                for (ComplexDataDescriptionType atype : schonwiedertsypes) {
                    List<String> aformat = new LinkedList<>();
                    aformat.add(atype.getMimeType());
                    aformat.add(atype.getSchema());
                    aformat.add(atype.getEncoding());
                    formats.add(aformat);
                }
            }
        }
        request.setFormats(formats);
        if (responseObject instanceof ExceptionReportDocument) {
            ExceptionReportDocument response = (ExceptionReportDocument) responseObject;
            Logger.log(this.getClass(), "handle()", response.toString());
        }
    }

    @Override
    public String preview() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
