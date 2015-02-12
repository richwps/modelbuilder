package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.Logger;
import static de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.DeployRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetInputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetOutputTypesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.impl.ExceptionReportDocumentImpl;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.ComplexTypesType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.SupportedTypesResponseDocument;
import net.opengis.wps.x100.TestProcessDocument;
import net.opengis.wps.x100.TestProcessResponseDocument;
import net.opengis.wps.x100.impl.DeployProcessResponseDocumentImpl;
import net.opengis.wps.x100.impl.TestProcessResponseDocumentImpl;
import org.n52.wps.client.RichWPSClientSession;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.richwps.GetSupportedTypesRequestBuilder;
import org.n52.wps.client.richwps.TestProcessRequestBuilder;
import org.n52.wps.client.richwps.TransactionalRequestBuilder;

/**
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class RichWPSHelper {

    /**
     * Sets given inputs to a test-request.
     *
     * @param builder TestProcessRequestBuilder.
     * @param theinputs list of inputs (InputArguments) that should be set.
     * @see IInputArgument
     */
    void setTestProcessInputs(TestProcessRequestBuilder builder, final HashMap theinputs) {
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
     * @param theinputs list of outputs (OutputArgument) that should be set.
     * @see IOutputArgument
     */
    void setTestProcessOutputs(TestProcessRequestBuilder builder, final HashMap theoutputs) {
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
    void setTestProcessVariables(TestProcessRequestBuilder builder, final List<String> variables) {
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
     * @param request TestRequest with possible inputs (IInputSpecifier) and
     * outputs (IOutputSpecifier).
     */
    public void analyseTestResponse(TestProcessDocument testdocument, Object responseObject, TestRequest request) {
        final ProcessDescriptionType description = request.toProcessDescriptionType();
        final URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");

        WPSClientConfig.getInstance(file);
        ExecuteRequest resultrequest = request;
        HashMap theoutputs = request.getOutputArguments();

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
                        java.util.logging.Logger.getLogger(RichWPSHelper.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Lists all available inputtypes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of formats..
     */
    void richwpsGetInputTypes(RichWPSClientSession richwps, GetInputTypesRequest request, RichWPSProvider richWPSProvider) {
        List<List<String>> formats = new LinkedList<>();
        GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
        builder.setComplexTypesOnly(true);
        Object responseObject = null;
        try {
            responseObject = richwps.getSupportedTypes(request.getServerId(), builder.build());
        } catch (Exception e) {
            Logger.log(richWPSProvider.getClass(), "richwpsGetInputTypes()", e);
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
            Logger.log(richWPSProvider.getClass(), "richwpsGetInputTypes()", response.toString());
        }
    }

    /**
     * Lists all available inputtypes.
     *
     * @param wpsurl serverid of WebProcessingService.
     * @return list of formats..
     */
    void richwpsGetOutputTypes(RichWPSClientSession richwps, GetOutputTypesRequest request, RichWPSProvider richWPSProvider) {
        List<List<String>> formats = new LinkedList<>();
        GetSupportedTypesRequestBuilder builder = new GetSupportedTypesRequestBuilder();
        builder.setComplexTypesOnly(true);
        Object responseObject = null;
        try {
            responseObject = richwps.getSupportedTypes(request.getServerId(), builder.build());
        } catch (WPSClientException e) {
            Logger.log(richWPSProvider.getClass(), "richwpsGetOutputTypes()", e);
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
            Logger.log(richWPSProvider.getClass(), "richwpsGetOutputTypes()", response.toString());
        }
    }

    /**
     * Deploys a new process.
     *
     * @param request DeployRequestDTO.
     * @see DeployRequest
     */
    void richwpsTestProcess(RichWPSClientSession richwps, TestRequest request, RichWPSProvider richWPSProvider) {
        final RichWPSHelper richwpshelper = new RichWPSHelper();
        TestProcessRequestBuilder builder = new TestProcessRequestBuilder(request.toProcessDescriptionType());
        builder.setExecutionUnit(request.getExecutionUnit());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        HashMap theinputs = request.getInputArguments();
        richwpshelper.setTestProcessInputs(builder, theinputs);
        HashMap theoutputs = request.getOutputArguments();
        richwpshelper.setTestProcessOutputs(builder, theoutputs);
        richwpshelper.setTestProcessVariables(builder, request.getVariables());
        TestProcessDocument testprocessdocument = null;
        Object response = null;
        try {
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
            testprocessdocument = builder.getTestdocument();
            response = richwps.test(endp, testprocessdocument);
            if (response == null) {
                Logger.log(richWPSProvider.getClass(), "richwpsTestProcess()", "No response");
                return;
            }
            if (response instanceof ExceptionReportDocumentImpl) {
                ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof TestProcessResponseDocumentImpl) {
                TestProcessResponseDocumentImpl deplok = (TestProcessResponseDocumentImpl) response;
                richwpshelper.analyseTestResponse(testprocessdocument, response, request);
            } else {
                Logger.log(richWPSProvider.getClass(), "richwpsTestProcess()", "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(richWPSProvider.getClass(), "richwpsTestProcess()", "Unable to create " + "deploymentdocument. " + ex);
        }
    }

    /**
     * Deploys a new process.
     *
     * @param request DeployRequest.
     * @see DeployRequest
     */
    void richwpsDeployProcess(RichWPSClientSession richwps, DeployRequest request, RichWPSProvider richWPSProvider) {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
        builder.setDeployExecutionUnit(request.getExecutionUnit());
        builder.setDeployProcessDescription(request.toProcessDescriptionType());
        builder.setDeploymentProfileName(request.getDeploymentprofile());
        builder.setKeepExecutionUnit(request.isKeepExecUnit());
        try {
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + IRichWPSProvider.DEFAULT_52N_WPS_ENDPOINT;
            Logger.log(richWPSProvider.getClass(), "richwpsDeployProcess", builder.getDeploydocument());
            Object response = richwps.deploy(endp, builder.getDeploydocument());
            if (response == null) {
                Logger.log(richWPSProvider.getClass(), "richwpsDeployProcess()", "No response");
                return;
            }
            if (response instanceof ExceptionReportDocumentImpl) {
                ExceptionReportDocumentImpl exception = (ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof DeployProcessResponseDocumentImpl) {
                DeployProcessResponseDocumentImpl deplok = (DeployProcessResponseDocumentImpl) response;
            } else {
                Logger.log(richWPSProvider.getClass(), "richwpsDeployProcess()", "Unknown reponse" + response + ", " + response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(richWPSProvider.getClass(), "richwpsDeployProcess()", "Unable to create " + "deploymentdocument. " + ex);
        }
    }

    /**
     * Undeploys a given process.
     *
     * @param request UndeployRequest.
     * @see UndeployRequest
     */
    public void richwpsUndeployProcess(RichWPSClientSession richwps, UndeployRequest request) {
        TransactionalRequestBuilder builder = new TransactionalRequestBuilder();
        builder.setIdentifier(request.getIdentifier());

        try {
            //FIXME
            String endp = request.getEndpoint();
            endp = endp.split(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT)[0] + DEFAULT_52N_WPS_ENDPOINT;
            //this.richwps.connect(perform.getEndpoint(), endp);
            Object response = richwps.undeploy(endp, builder.getUndeploydocument());

            if (response == null) {
                Logger.log(this.getClass(), "richwpsUndeployProcess()", "No response");
                return;
            }

            if (response instanceof net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) {
                net.opengis.ows.x11.impl.ExceptionReportDocumentImpl exception = (net.opengis.ows.x11.impl.ExceptionReportDocumentImpl) response;
                request.addException(exception.getExceptionReport().toString());
            } else if (response instanceof net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) {
                net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl deplok = (net.opengis.wps.x100.impl.UndeployProcessResponseDocumentImpl) response;
                Logger.log(this.getClass(), "richwpsUndeployProcess()", deplok);
            } else {
                Logger.log(this.getClass(), "richwpsUndeployProcess()", "Unknown reponse" + response);
                Logger.log(this.getClass(), "richwpsUndeployProcess()", response.getClass());
            }
        } catch (WPSClientException ex) {
            Logger.log(this.getClass(), "richwpsUndeployProcess()", "Unable to create "
                    + "deploymentdocument." + ex);
        }
    }

}
