package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.GetOutputTypesRequest;
import java.util.LinkedList;
import java.util.List;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.ComplexTypesType;
import net.opengis.wps.x100.SupportedTypesResponseDocument;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.GetSupportedTypesRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class GetOutputTypesRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    GetOutputTypesRequest request;

    public GetOutputTypesRequestHandler(RichWPSClientSession wps, GetOutputTypesRequest request) {
        this.wps = wps;
        this.request = request;
    }

    @Override
    public void handle() {
        List<List<String>> formats = new LinkedList<>();
        GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
        builder.setComplexTypesOnly(true);
        Object responseObject = null;
        try {
            responseObject = wps.getSupportedTypes(request.getServerId(), builder.build());
        } catch (WPSClientException e) {
            Logger.log(this.getClass(), "richwpsGetOutputTypes()", e);
        }
        if (responseObject instanceof SupportedTypesResponseDocument) {
            SupportedTypesResponseDocument response = (SupportedTypesResponseDocument) responseObject;
            ComplexTypesType[] types = response.getSupportedTypesResponse().getSupportedOutputTypes().getComplexTypesArray();
            for (ComplexTypesType type : types) {
                ComplexDataDescriptionType[] cddtype = type.getTypeArray();
                for (ComplexDataDescriptionType atype : cddtype) {
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
            Logger.log(this.getClass(), "richwpsGetOutputTypes()", response.toString());
        }
    }
}
