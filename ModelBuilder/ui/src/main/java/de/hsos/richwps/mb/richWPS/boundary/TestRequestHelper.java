package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.TestProcessDocument;
import net.opengis.wps.x100.TestProcessResponseDocument;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.richwps.TestProcessRequestBuilder;

/**
 *
 * @author dalcacer
 */
public class TestRequestHelper {

    /**
     * Sets given inputs to a test-request.
     *
     * @param builder 52n executebuilder.
     * @param theinputs list of inputs (InputArguments) that should be set.
     * @see IInputArgument
     */
    void setInputs(TestProcessRequestBuilder builder, final HashMap theinputs) {
        final Set<String> keys = theinputs.keySet();
        for (String key : keys) {
            Object o = theinputs.get(key);
            if (o instanceof InputLiteralDataArgument) {
                String value = ((InputLiteralDataArgument) o).getValue();
                builder.addLiteralData(key, value);
            } else if (o instanceof InputComplexDataArgument) {
                InputComplexDataArgument param = (InputComplexDataArgument) o;
                String url = param.getURL();
                String mimetype = param.getMimeType();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                builder.addComplexDataReference(key, url, schema, encoding, mimetype);
            } else if (o instanceof InputBoundingBoxDataArgument) {
                InputBoundingBoxDataArgument param;
                param = (InputBoundingBoxDataArgument) o;
                final String crs = param.getCrsType();
                String[] split = param.getValue().split(",");
                for (String s : split) {
                    System.out.println(s);
                }
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
     * @param builder 52n executebuilder.
     * @param theinputs list of outputs (OutputArgument) that should be set.
     * @see IOutputArgument
     */
    void setOutputs(TestProcessRequestBuilder builder, final HashMap theoutputs) {
        final Set<String> keys = theoutputs.keySet();
        for (String key : keys) {
            Object o = theoutputs.get(key);
            if (o instanceof OutputLiteralDataArgument) {
                builder.addOutput(key);
            } else if (o instanceof OutputComplexDataArgument) {
                OutputComplexDataArgument param = (OutputComplexDataArgument) o;
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
            } else if (o instanceof OutputBoundingBoxDataArgument) {
                builder.addOutput(key);
            }
        }
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param testdocument 52n execute document.
     * @param responseObject 52n reponse object. Execute-response or exception.
     * @param request TestRequest with possible inputs (IInputSpecifier)
     * and outputs (IOutputSpecifier).
     * @return ExecuteRequestDTO with results or exception.
     */
    public void analyseResponse(TestProcessDocument testdocument, Object responseObject, TestRequest request) {
        final ProcessDescriptionType description = request.toProcessDescriptionType();
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");
        Logger.log(this.getClass(), "analyseResponse", "using configuration" + file);
        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputArguments();
        Logger.log(this.getClass(), "analyseResponse", responseObject.getClass());
        if (responseObject instanceof TestProcessResponseDocument) {
            TestProcessResponseDocument response = (TestProcessResponseDocument) responseObject;
            Logger.log(this.getClass(), "analyseResponse", response.toString());
        } else {
            ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response." + "Response is Exception: " + exception.toString());
            Logger.log(this.getClass(), "analyseResponse", exception.getExceptionReport());
        }
    }

}
