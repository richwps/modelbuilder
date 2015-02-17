package de.hsos.richwps.mb.richWPS.boundary.handler;

import de.hsos.richwps.mb.richWPS.boundary.IRequestHandler;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputLiteralDataValue;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.TestProcessDocument;
import net.opengis.wps.x100.TestProcessResponseDocument;
import net.opengis.wps.x100.impl.TestProcessResponseDocumentImpl;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.TestProcessRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class TestRequestHandler implements IRequestHandler {

    RichWPSClientSession wps;
    TestRequest request;

    public TestRequestHandler(RichWPSClientSession wps, TestRequest request) {
        this.wps = wps;
        this.request = request;
    }

    @Override
    public void handle() {
        TestProcessRequestBuilder builder = new TestProcessRequestBuilder(request.toProcessDescriptionType());
        builder.setExecutionUnit(request.getExecutionUnit());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        HashMap theinputs = request.getInputValues();
        setInputs(builder, theinputs);
        HashMap theoutputs = request.getOutputValues();
        setOutputs(builder, theoutputs);
        setVariables(builder, request.getVariables());
        TestProcessDocument testprocessdocument = null;
        Object response = null;
        try {
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
            testprocessdocument = builder.getTestdocument();
            response = wps.test(endp, testprocessdocument);
            if (response == null) {
                Logger.log(this.getClass(), "richwpsTestProcess()", "No response");
                return;
            }
            if (response instanceof ExceptionReportDocumentImpl) {
                ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof TestProcessResponseDocumentImpl) {
                TestProcessResponseDocumentImpl deplok = (TestProcessResponseDocumentImpl) response;
                analyseResponse(testprocessdocument, response, request);
            } else {
                Logger.log(this.getClass(), "richwpsTestProcess()", "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsTestProcess()", "Unable to create " + "deploymentdocument. " + ex);
        }
    }

    /**
     * Sets given inputs to a test-request.
     *
     * @param builder TestProcessRequestBuilder.
     * @param theinputs list of inputs (Inputvalues) that should be set.
     * @see IInputValue
     */
    private void setInputs(TestProcessRequestBuilder builder, final HashMap theinputs) {
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
    private void setOutputs(TestProcessRequestBuilder builder, final HashMap theoutputs) {
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
     * Sets requested variables to execute-request.
     *
     */
    private void setVariables(TestProcessRequestBuilder builder, final List<String> variables) {
        for (String key : variables) {
            builder.addOutput(key);
            builder.setAsReference(key, true);
        }
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param testdocument testprocess document.
     * @param responseObject reponse object. TestProcess-response or exception.
     * @param request TestRequest with possible inputs (IInputDescription) and
     * outputs (IOutputDescription).
     */
    private void analyseResponse(TestProcessDocument testdocument, Object responseObject, TestRequest request) {
        final ProcessDescriptionType description = request.toProcessDescriptionType();
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");

        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputValues();

        if (responseObject instanceof TestProcessResponseDocument) {
            TestProcessResponseDocument response = (TestProcessResponseDocument) responseObject;
            Logger.log(this.getClass(), "analyseResponse", response.toString());

            OutputDataType[] overalloutputs = response.getTestProcessResponse().getProcessOutputs().getOutputArray();
            Logger.log(this.getClass(), "analyseResponse", overalloutputs);

            //FIXME make use of executeresponseanalyzer
            for (OutputDataType o : overalloutputs) {
                if (o.getData() != null) {
                    //we might have a literaldata
                    if (o.getData().getLiteralData() != null) {
                        String key = o.getIdentifier().getStringValue();
                        String value = o.getData().getLiteralData().getStringValue();
                        request.addResult(key, value);
                    }
                }

                //we might have a complexdata with reference
                if (o.getReference() != null) {
                    String key = o.getIdentifier().getStringValue();
                    String value = o.getReference().getHref();
                    URL url;
                    try {
                        url = new URL(value);
                        request.addResult(key, url);
                    } catch (MalformedURLException ex) {
                        java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //TODO: BoundingBox ?
            }
        } else {
            ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response." + "Response is Exception: " + exception.toString());
            Logger.log(this.getClass(), "analyseResponse", exception.getExceptionReport());
        }
    }

    public String preview() {
        try {
            TestProcessRequestBuilder builder = new TestProcessRequestBuilder(request.toProcessDescriptionType());
            builder.setExecutionUnit(request.getExecutionUnit());
            builder.setDeploymentProfileName(request.getDeploymentprofile());

            HashMap theinputs = request.getInputValues();
            setInputs(builder, theinputs);

            HashMap theoutputs = request.getOutputValues();
            setOutputs(builder, theoutputs);

            setVariables(builder, request.getVariables());

            TestProcessDocument testprocessdocument = null;
            testprocessdocument = builder.getTestdocument();
            return testprocessdocument.toString();
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsPreviewTestProcess", ex);
        }

        return "";
    }
}
