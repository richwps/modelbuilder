package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
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
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

/**
 *
 * @author dalcacer
 */
public class ExecuteRequestHelper {

    public ExecuteRequestHelper() {
    }

    /**
     * Analyses a given response and add specific results or exception to
     * request.
     *
     * @param execute 52n execute document.
     * @param description 52n process description.
     * @param responseObject 52n reponse object. Execute-response or exception.
     * @param request ExecuteRequest with possible inputs (IInputSpecifier)
     * and outputs (IOutputSpecifier).
     */
    public void analyseResponse(ExecuteDocument execute, ProcessDescriptionType description, Object responseObject, ExecuteRequest request) {
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");
        
        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputArguments();
        //FIXME simplify
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            Logger.log(this.getClass(), "analyseResponse", response.toString());
            try {
                Set<String> keys = theoutputs.keySet();
                for (String key : keys) {
                    Object o = theoutputs.get(key);
                    if (o instanceof OutputLiteralDataArgument) {
                        OutputLiteralDataArgument argument = (OutputLiteralDataArgument) o;
                        OutputDataType[] outputs = response.getExecuteResponse().getProcessOutputs().getOutputArray();
                        String value = "";
                        for (OutputDataType output : outputs) {
                            final String givenIdentifier = output.getIdentifier().getStringValue();
                            final String wantedIdentifer = argument.getIdentifier();
                            if (givenIdentifier.equals(wantedIdentifer)) {
                                value = output.getData().getLiteralData().getStringValue();
                            }
                        }
                        request.addResult(key, value);
                    } else if (o instanceof OutputComplexDataArgument) {
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, description);
                        OutputComplexDataArgument argument = (OutputComplexDataArgument) o;
                        if (argument.isAsReference()) {
                            String httpkvpref = analyser.getComplexReferenceByIndex(0);
                            request.addResult(key, httpkvpref);
                        } else {
                            // FIXME proper analytics for different bindings.
                            // Blocked by broken commons.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);
                            Logger.log(this.getClass(), "analyseResponse", "the size " + binding.getPayload().size());
                        }
                    }
                }
            } catch (WPSClientException e) {
                Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response. " + e.getLocalizedMessage());
            }
        } else {
            ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) responseObject;
            resultrequest.addException(exception.getExceptionReport().toString());
            Logger.log(this.getClass(), "analyseResponse", "Unable to analyse response." + "Response is Exception: " + exception.toString());
            Logger.log(this.getClass(), "analyseResponse", exception.getExceptionReport());
        }
    }

    /**
     * Add processs inputs to a request.
     *
     * @param request with serverid and processid.
     * @return request with list of input specifiers.
     * @see IInputSpecifier
     */
    void addInputs(DescribeRequest request, ProcessDescriptionType process) {
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
     * @see IOutputSpecifier
     */
    void addOutputs(DescribeRequest request, ProcessDescriptionType process) {
        ProcessDescriptionType.ProcessOutputs outputs = process.getProcessOutputs();
        OutputDescriptionType[] _outputs = outputs.getOutputArray();
        for (OutputDescriptionType description : _outputs) {
            request.addOutput(description);
        }
    }

    /**
     * Sets given inputs to execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of inputs (InputArguments) that should be set.
     * @see IInputArgument
     */
    void setInputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theinputs) {
        Set<String> keys = theinputs.keySet();
        for (String key : keys) {
            Object o = theinputs.get(key);
            if (o instanceof InputLiteralDataArgument) {
                String value = ((InputLiteralDataArgument) o).getValue();
                executeBuilder.addLiteralData(key, value);
            } else if (o instanceof InputComplexDataArgument) {
                InputComplexDataArgument param = (InputComplexDataArgument) o;
                String url = param.getURL();
                String mimetype = param.getMimeType();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                executeBuilder.addComplexDataReference(key, url, schema, encoding, mimetype);
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
                executeBuilder.addBoundingBoxData(key, crs, dimension, Arrays.asList(lower), Arrays.asList(upper));
            }
        }
    }

    /**
     * Sets requested outputs to execute-request.
     *
     * @param executeBuilder 52n executebuilder.
     * @param theinputs list of outputs (OutputArgument) that should be set.
     * @see IOutputArgument
     */
    void setOutputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theoutputs) {
        Set<String> keys = theoutputs.keySet();
        for (String key : keys) {
            Object o = theoutputs.get(key);
            if (o instanceof OutputLiteralDataArgument) {
                executeBuilder.addOutput(key);
            } else if (o instanceof OutputComplexDataArgument) {
                OutputComplexDataArgument param = (OutputComplexDataArgument) o;
                executeBuilder.addOutput(key);
                boolean asReference = param.isAsReference();
                String mimetype = param.getMimetype();
                String encoding = param.getEncoding();
                String schema = param.getSchema();
                if (asReference) {
                    executeBuilder.setAsReference(key, true);
                }
                executeBuilder.setMimeTypeForOutput(mimetype, key);
                executeBuilder.setEncodingForOutput(encoding, key);
                executeBuilder.setSchemaForOutput(schema, key);
            } else if (o instanceof OutputBoundingBoxDataArgument) {
                //FIXME BoundingBox
                executeBuilder.addOutput(key);
            }
        }
    }
}
