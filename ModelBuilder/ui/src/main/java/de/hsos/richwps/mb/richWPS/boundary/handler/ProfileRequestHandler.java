package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputValue;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputLiteralDataValue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.ProfileProcessDocument;
import net.opengis.wps.x100.ProfileType;
import net.opengis.wps.x100.RuntimeInfoType;
import net.opengis.wps.x100.impl.ProfileProcessResponseDocumentImpl;
import org.n52.wps.client.ProfileProcessResponseAnalyser;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.ProfileProcessRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ProfileRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    ProfileRequest request;

    public ProfileRequestHandler(RichWPSClientSession wps, IRequest request) {
        this.wps = wps;
        this.request = (ProfileRequest) request;
    }

    @Override
    public void handle() {
        ProfileProcessRequestBuilder builder = new ProfileProcessRequestBuilder(request.toProcessDescriptionType());
        builder.setExecutionUnit(request.getExecutionUnit());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        HashMap theinputs = request.getInputValues();
        setInputs(builder, theinputs);
        HashMap theoutputs = request.getOutputValues();
        setOutputs(builder, theoutputs);

        ProfileProcessDocument profileprocessdocument = null;
        Object response = null;
        try {
            String endp = request.getEndpoint();
            endp = endp.split(IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
            profileprocessdocument = builder.getProfiledocument();
            response = this.wps.profile(endp, profileprocessdocument);
            if (response == null) {
                Logger.log(this.getClass().getClass(), "handle()", "No response.");
                return;
            }
            if (response instanceof ExceptionReportDocumentImpl) {
                ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof ProfileProcessResponseDocumentImpl) {
                ProfileProcessResponseDocumentImpl deplok = (ProfileProcessResponseDocumentImpl) response;
                this.analyseResponse(profileprocessdocument, deplok);
                Logger.log(this.getClass(), "handle()", deplok);
            } else {
                Logger.log(this.getClass(), "handle()", "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "handle()", "Unable to create " + "deploymentdocument. " + ex);
        }
    }

    /**
     * Sets given inputs to a test-request.
     *
     * @param builder TestProcessRequestBuilder.
     * @param theinputs list of inputs (Inputvalues) that should be set.
     * @see IInputValue
     */
    private void setInputs(ProfileProcessRequestBuilder builder, final HashMap theinputs) {
        final Set<String> keys = theinputs.keySet();
        for (String key : keys) {
            Object o = theinputs.get(key);
            if (o instanceof InputLiteralDataValue) {
                String value = ((InputLiteralDataValue) o).getValue();
                builder.addLiteralData(key, value);
            } else if (o instanceof InputComplexDataValue) {
                InputComplexDataValue param = (InputComplexDataValue) o;
                String url = param.getURL();
                String mimetype = param.getMimeType();
                String encoding = param.getEncoding();
                String schema = param.getSchema();

                builder.addComplexDataReference(key, url, schema, encoding, mimetype);
            } else if (o instanceof InputBoundingBoxDataValue) {
                InputBoundingBoxDataValue param;
                param = (InputBoundingBoxDataValue) o;
                final String crs = param.getCrsType();
                String[] split = param.getValue().split(",");
                BigInteger dimension = BigInteger.valueOf(split.length);
                String[] lower = split[0].split(" ");
                String[] upper = split[1].split(" ");
                builder.addBoundingBoxData(key, crs, dimension, Arrays.asList(lower), Arrays.asList(upper));
            }
        }
    }

    /**
     * Sets requested outputs to execute-request.
     *
     * @param builder TestProcessRequestBuilder.
     * @param theinputs list of outputs (OutputValues) that should be set.
     * @see IOutputDescription
     */
    private void setOutputs(ProfileProcessRequestBuilder builder, final HashMap theoutputs) {
        final Set<String> keys = theoutputs.keySet();
        for (String key : keys) {
            Object o = theoutputs.get(key);
            if (o instanceof OutputLiteralDataValue) {
                builder.addOutput(key);
            } else if (o instanceof OutputComplexDataValue) {
                OutputComplexDataValue param = (OutputComplexDataValue) o;
                builder.addOutput(key);
                boolean asReference = param.isAsReference();
                String mimetype = param.getMimetype();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                if (asReference) {
                    builder.setAsReference(key, true);
                }
                builder.setMimeTypeForOutput(mimetype, key);
                builder.setEncodingForOutput(encoding, key);
                builder.setSchemaForOutput(schema, key);
            } else if (o instanceof OutputBoundingBoxDataValue) {
                builder.addOutput(key);
            }
        }
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param profiledocument testprocess document.
     * @param responseObject reponse object. TestProcess-response or exception.
     * @param request TestRequest with possible inputs (IInputDescription) and
     * outputs (IOutputDescription).
     */
    private void analyseResponse(ProfileProcessDocument profiledocument, ProfileProcessResponseDocumentImpl responseObject) {

        HashMap<String, Object> results = new HashMap<>();
        try {
            ProfileProcessResponseAnalyser analyser = new ProfileProcessResponseAnalyser(profiledocument, responseObject);
            ProfileType[] pt = analyser.getProfiles();
            for (ProfileType profile : pt) {
                List<String> aresult = new ArrayList<>();
                String aidentifier = profile.getIdentifier().getStringValue();
                String adescription = null;
                if (profile.getTitle() == null) {
                    adescription = "None";
                } else {
                    adescription = profile.getTitle().getStringValue();
                }
                RuntimeInfoType theruntime = profile.getRuntimeInfo();
                String astartime = theruntime.getStarttime().toString();
                String aruntime = theruntime.getRuntime().toString();
                aresult.add(aidentifier);
                aresult.add(adescription);
                aresult.add(astartime);
                aresult.add(aruntime);
                results.put(aidentifier, aresult);
            }
            request.setResults(results);
        } catch (Exception e) {
            Logger.log(this.getClass(), "analyseResponse()", "Analysing the response "
                    + "failed due to " + e);
            e.printStackTrace();
        }
    }

    @Override
    public String preview() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
