package de.hsos.richwps.mb.semanticProxy.boundary;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.semanticProxy.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.wps.SPClient;
import de.hsos.richwps.sp.client.wps.Vocabulary;
import de.hsos.richwps.sp.client.wps.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.wps.gettypes.Input;
import de.hsos.richwps.sp.client.wps.gettypes.Network;
import de.hsos.richwps.sp.client.wps.gettypes.Output;
import de.hsos.richwps.sp.client.wps.gettypes.WPS;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Connects to the semantic proxy and receives/provides a list of available
 * processes.
 *
 * @author dziegenh
 */
public class ProcessProvider {

    private SPClient spClient;
    private Network net;
    private WPS[] wpss;
    private String url;

    /**
     * Constructor, creates the SP client.
     */
    public ProcessProvider() {
        spClient = SPClient.getInstance();
        this.wpss = new WPS[]{};
    }

    public String getUrl() {
        return url;
    }

    /**
     * Connects to the SemanticProxy using the url field.
     *
     * @param url SemanticProxy URL
     * @return
     * @throws java.lang.Exception
     */
    public boolean connect(String url) throws Exception {
        this.url = url;

        // init SP Client
        Vocabulary.init(new URL(url + "/resources/vocab"));
        spClient.setRootURL(url + "/resources");
        spClient.setSearchURL(url + "/search");
        spClient.setWpsListURL(url + "/resources/wpss");
        spClient.setProcessListURL(url + "/resources/processes");

        try {
            net = spClient.getNetwork();
            spClient.clearCache();
        } catch (Exception ex) {
            net = null;
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_NOT_REACHABLE, this);
            return false;
        }

        return true;
    }

    /**
     * Returns true if an connection to the SP has been established previously.
     * Caution: this method doesn't check if the connection is still alive!!
     *
     * @return
     */
    public boolean isConnected() {
        return null != net;
    }

    /**
     * Receives all data belonging to a specific process. The process is
     * identified by its (server) endpoint and its identifier.
     */
    public ProcessEntity getProcessEntity(String server, String identifier) {
        return getProcessWithPorts(server, identifier);
    }

    private void fireSpReceiveExceptionAsAppEvent(Exception ex) {
        String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        AppEventService.getInstance().fireAppEvent(msg, this);
    }

    private ProcessEntity getProcessWithPorts(String server, String identifier) {

        ProcessEntity process = null;

        // find desired endpoint (server)
        for (WPS wps : wpss) {
            try {
                if (server.equals(wps.getEndpoint())) {

                    for (de.hsos.richwps.sp.client.wps.gettypes.Process spProcess : wps.getProcesses()) {

                        if (spProcess.getIdentifier().equals(identifier)) {
                            // Map process properties
                            process = new ProcessEntity(server, spProcess.getIdentifier());
                            process.setOwsAbstract(spProcess.getAbstract());
                            process.setOwsTitle(spProcess.getTitle());

                            // Map input ports
                            try {
                                for (Input spInput : spProcess.getInputs()) {
                                    ProcessPort inPort = new ProcessPort(getDatatype(spInput.getInputFormChoice()));
                                    inPort.setOwsIdentifier(spInput.getIdentifier());
                                    inPort.setOwsAbstract(spInput.getAbstract());
                                    inPort.setOwsTitle(spInput.getTitle());
                                    process.addInputPort(inPort);
                                }
                            } catch (Exception ex) {
                                fireSpReceiveExceptionAsAppEvent(ex);
                            }

                            // Map output ports
                            try {
                                for (Output spOutput : spProcess.getOutputs()) {
                                    ProcessPort outPort = new ProcessPort(getDatatype(spOutput.getOutputFormChoice()));
                                    outPort.setOwsIdentifier(spOutput.getIdentifier());
                                    outPort.setOwsAbstract(spOutput.getAbstract());
                                    outPort.setOwsTitle(spOutput.getTitle());
                                    process.addOutputPort(outPort);
                                }
                            } catch (Exception ex) {
                                fireSpReceiveExceptionAsAppEvent(ex);
                            }

                            // process found, return to stop search
                            return process;
                        }
                    }

                }
            } catch (Exception ex) {
                fireSpReceiveExceptionAsAppEvent(ex);
            }
        }
        return null;
    }

    /**
     * Receives all processes for the given endpoint from the SP.
     *
     * @param server
     * @return
     */
    public Collection<ProcessEntity> getServerProcesses(String server) {

        // find desired endpoint (server)
        LinkedList<ProcessEntity> serverProcesses = new LinkedList<>();
        for (WPS wps : wpss) {
            try {
                if (server.equals(wps.getEndpoint())) {
                    ProcessEntity process = null;

                    for (de.hsos.richwps.sp.client.wps.gettypes.Process spProcess : wps.getProcesses()) {

                        // Map process properties
                        process = new ProcessEntity(server, spProcess.getIdentifier());
                        process.setOwsAbstract(spProcess.getAbstract());
                        process.setOwsTitle(spProcess.getTitle());

                        // add process to current server's list
                        serverProcesses.add(process);
                    }

                }
            } catch (Exception ex) {
                fireSpReceiveExceptionAsAppEvent(ex);
            }
        }

        return serverProcesses;
    }

    /**
     * Receives a list of available endpoints from the semantic proxy.
     *
     * @return
     */
    public Collection<String> getAllServer() {
        LinkedList<String> servers = new LinkedList<>();

        // indicate error occurences but don't abort loading servers.
        // (errors are handled after the loading is done)
        boolean rdfError = false;

        if (null != net) {
            try {
                wpss = net.getWPSs();
            } catch (Exception ex) {
                rdfError = true;
            }

            if (!rdfError) {
                for (WPS wps : wpss) {
                    try {
                        servers.add(wps.getEndpoint());
                    } catch (RDFException ex) {
                        rdfError = true;
                    }
                }
            }
        }

        // handle occured errors
        if (rdfError) {
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, this);
        }

        // TODO replace String with formatable AppConstant
        AppEventService.getInstance().fireAppEvent("Received " + servers.size() + " servers from '" + url + "'.", this);

        return servers;
    }

    private ProcessPortDatatype getDatatype(InAndOutputForm inputFormChoice) throws UnsupportedWpsDatatypeException {
        switch (inputFormChoice.getDataType()) {
            case InAndOutputForm.LITERAL_TYPE:
                return ProcessPortDatatype.LITERAL;
            case InAndOutputForm.COMPLEX_TYPE:
                return ProcessPortDatatype.COMPLEX;
            case InAndOutputForm.BOUNDING_BOX_TYPE:
                return ProcessPortDatatype.BOUNDING_BOX;
        }

        throw new UnsupportedWpsDatatypeException(inputFormChoice.getDataType());
    }

}
