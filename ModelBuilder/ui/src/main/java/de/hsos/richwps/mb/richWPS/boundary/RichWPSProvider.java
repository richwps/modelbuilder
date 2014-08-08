package de.hsos.richwps.mb.richWPS.boundary;

import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import de.hsos.richwps.mb.richWPS.entity.execute.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputLiteralDataArgument;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.geotools.gml3.bindings.GML3ParsingUtils;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientConfig;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

/**
 * Interface to RichWPS enabled servers.
 *
 * @author dalcacer
 */
public class RichWPSProvider implements IRichWPSProvider {

    private WPSClientSession wps;

    @Override
    public void connect(String wpsurl) {
        try {
            wps.connect(wpsurl);
        } catch (Exception e) {
            System.err.println("Unable to connect, " + e.getLocalizedMessage());
        }
    }

    @Override
    public void connect(String wpsurl, String wpsturl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void connect(String wpsurl, String wpsturl, String testurl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void connect(String wpsurl, String wpsturl, String testurl, String profileurl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getAvailableProcesses(String wpsurl) {
        List<String> processes = new ArrayList<>();

        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);
            ProcessDescriptionsDocument pdd = this.wps.describeAllProcesses(wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessBriefType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessBriefType process : descs) {
                String identifier = process.getIdentifier().getStringValue();
                processes.add(identifier);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return processes;
    }

    @Override
    public ExecuteRequestDTO describeProcess(ExecuteRequestDTO dto) {
        ExecuteRequestDTO resultdto = dto;
        if (dto.getInputSpecifier().isEmpty()) {
            resultdto = this.describeInputs(dto);
        }

        if (dto.getOutputSepcifier().isEmpty()) {
            resultdto = this.describeOutputs(dto);
        }
        return resultdto;
    }

    private ExecuteRequestDTO describeInputs(ExecuteRequestDTO dto) {
        try {
            String wpsurl = dto.getEndpoint();
            String[] processes = new String[1];
            processes[0] = dto.getProcessid();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessDescriptionType process : descs) {
                ProcessDescriptionType.DataInputs inputs = process.getDataInputs();
                InputDescriptionType[] _inputs = inputs.getInputArray();
                for (InputDescriptionType description : _inputs) {
                    dto.addInput(description);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return dto;
    }

    private ExecuteRequestDTO describeOutputs(ExecuteRequestDTO dto) {

        try {
            String wpsurl = dto.getEndpoint();
            String[] processes = new String[1];
            processes[0] = dto.getProcessid();
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            for (ProcessDescriptionType process : descs) {
                ProcessDescriptionType.ProcessOutputs outputs = process.getProcessOutputs();
                OutputDescriptionType[] _outputs = outputs.getOutputArray();
                for (OutputDescriptionType description : _outputs) {
                    dto.addOutput(description);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return dto;
    }

    //FIXME splitup
    public ExecuteRequestDTO executeProcess(ExecuteRequestDTO dto) {
        ExecuteRequestDTO resultdto = dto;
        String wpsurl = dto.getEndpoint();
        String processid = dto.getProcessid();

        HashMap theinputs = dto.getInputArguments();
        HashMap theoutputs = dto.getOutputArguments();

        ProcessDescriptionType description = this._describeProcess(wpsurl, processid);
        org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(description);

        this.prepareInputs(executeBuilder, theinputs);
        this.prepareOutputs(executeBuilder, theoutputs);

        ExecuteDocument execute = null;
        Object responseObject = null;
        try {
            execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");

            System.err.println("Execute String: " + execute.toString());

            WPSClientSession wpsClient = WPSClientSession.getInstance();
            responseObject = wpsClient.execute(wpsurl, execute);
        } catch (Exception e) {
            System.err.println("Something went wrong, executing the process " + processid + ", " + e);
        }

        resultdto = this.analyseResponse(execute, description, responseObject, dto);

        return resultdto;
    }

    private void prepareInputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theinputs) {

        java.util.Set<String> keys = theinputs.keySet();
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
            }
            //FIXME add BoundingBox
        }
    }

    private void prepareOutputs(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap theoutputs) {
        java.util.Set<String> keys = theoutputs.keySet();

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
            }
            //FIXME add BoundingBox
        }
    }

    private ExecuteRequestDTO analyseResponse(ExecuteDocument execute, ProcessDescriptionType description, Object responseObject, ExecuteRequestDTO dto) {
        java.net.URL res = this.getClass().getResource("/xml/wps_config.xml");
        String file = res.toExternalForm().replace("file:", "");
        System.out.println(file);
        WPSClientConfig.getInstance(file);
        
        ExecuteRequestDTO resultdto = dto;
        HashMap theoutputs = dto.getOutputArguments();
        System.err.println("Debug: " + responseObject.getClass());
        if (responseObject instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
            System.out.println("Debug: \n" + response.toString());

            try {
                java.util.Set<String> keys = theoutputs.keySet();

                for (String key : keys) {
                    Object o = theoutputs.get(key);
                    if (o instanceof OutputLiteralDataArgument) {
                        OutputLiteralDataArgument argument = (OutputLiteralDataArgument) o;

                        //String subtype = argument.getSepcifier().getSubtype();
                        //FIXME
                        //dto.addResult(key, o);
                    } else if (o instanceof OutputComplexDataArgument) {
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, description);
                        
                        OutputComplexDataArgument argument = (OutputComplexDataArgument) o;
                        if (argument.isAsReference()) {
                            String httpkvpref = analyser.getComplexReferenceByIndex(0);
                            System.out.println(key + ", " + httpkvpref);
                            // FIXME Typedetection.
                        } else {
                            // FIXME proper analytics for different bindings.
                            GTVectorDataBinding binding = (GTVectorDataBinding) analyser.getComplexData(key, GTVectorDataBinding.class);//FIXME
                            System.out.println(binding.getPayload().size());
                        }
                    }
                }

            } catch (Exception e) {
                //throw new Exception("Unable to analyse response.");
                e.printStackTrace();
            }
        } else if (responseObject instanceof net.opengis.ows.x11.ExceptionDocument) {
            net.opengis.ows.x11.ExceptionDocument exception = (net.opengis.ows.x11.ExceptionDocument) responseObject;
            System.err.println("Unable to analyse response. Response is Exception: " + exception.toString());
        } else {
            //throw new Exception("Unable to analyse response.");
            System.err.println("Unable to analyse response. Response is not an valid ExecuteResponse.");
        }
        return resultdto;
    }

    private ProcessDescriptionType _describeProcess(String wpsurl, String processid) {
        ProcessDescriptionType description = null;
        try {
            this.wps = WPSClientSession.getInstance();
            this.wps.connect(wpsurl);

            //discover
            String[] processes = new String[1];
            processes[0] = processid;
            ProcessDescriptionsDocument pdd = this.wps.describeProcess(processes, wpsurl);
            ProcessDescriptionsDocument.ProcessDescriptions descriptions = pdd.getProcessDescriptions();
            ProcessDescriptionType[] descs = descriptions.getProcessDescriptionArray();
            description = descs[0];

        } catch (Exception e) {
            System.err.println("Something went wrong, describing the process " + processid + ", " + e);
        }
        return description;
    }

}
