package de.hsos.richwps.mb.processProvider.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.control.EntityConverter;
import de.hsos.richwps.mb.processProvider.control.MonitorDataConverter;
import de.hsos.richwps.mb.processProvider.control.ProcessSearch;
import de.hsos.richwps.mb.processProvider.control.Publisher;
import de.hsos.richwps.mb.processProvider.control.ServerProvider;
import de.hsos.richwps.mb.processProvider.entity.WpsServer;
import de.hsos.richwps.mb.processProvider.exception.ProcessMetricProviderNotAvailable;
import de.hsos.richwps.mb.processProvider.exception.SpClientNotAvailableException;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.Vocabulary;
import de.hsos.richwps.sp.client.ows.gettypes.Input;
import de.hsos.richwps.sp.client.ows.gettypes.Network;
import de.hsos.richwps.sp.client.ows.gettypes.Output;
import de.hsos.richwps.sp.client.ows.gettypes.Process;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Connects to the semantic proxy and receives/provides a list of available
 * processes.
 *
 * @author dziegenh
 */
public class ProcessProvider {

    private SPClient spClient;
    private Network net;
//    private WPS[] wpss;
    private String url;

    private final ProcessMetricProvider processMetricProvider;
    private ProcessSearch processSearch;
    private MonitorDataConverter monitorDataConverter;
    private ServerProvider serverProvider;
    private Publisher publisher;

    /**
     * Constructor, creates the SP client.
     */
    public ProcessProvider(ProcessMetricProvider processMetricProvider) {
        this.processMetricProvider = processMetricProvider;

        spClient = SPClient.getInstance();
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
        spClient.setIdgeneratorURL(url + "/idgenerator");

        try {
            net = spClient.getNetwork();
            getServerProvider().setNet(net);
            spClient.clearCache();
        } catch (Exception ex) {
            net = null;
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_NOT_REACHABLE, this);
            return false;
        }

        return true;
    }

    ServerProvider getServerProvider() {
        if (null == this.serverProvider) {
            this.serverProvider = new ServerProvider();
        }

        return this.serverProvider;
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

    protected void fireSpReceiveExceptionAsAppEvent(Exception ex) {
        String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        AppEventService.getInstance().fireAppEvent(msg, this);
    }

    public void clear() {
        if (null != spClient) {
            spClient.clearCache();
            getServerProvider().clearCache();
            this.net = null;
        }
    }

    /**
     * Returns a fully loaded process that matches the endpoint and ows
     * identifier. If an error occurs while loading the process, a partially
     * loaded process is returned.
     *
     * @param process
     * @return
     */
    public ProcessEntity getFullyLoadedProcessEntity(ProcessEntity process) {
        process = process.clone();

        ProcessEntity loadedProcess = getFullyLoadedProcessEntity(process.getServer(), process.getOwsIdentifier());
        if (null != loadedProcess) {
            process = loadedProcess;
        }

        return process;
    }

    /**
     * Receives all data belonging to a specific process. The process is
     * identified by its (server-) endpoint and its identifier.
     *
     * @param server
     * @param identifier
     * @return
     */
    public ProcessEntity getFullyLoadedProcessEntity(String server, String identifier) {

        ProcessEntity process = null;

        try {
            // find desired endpoint (server)
            for (WPS wps : getServerProvider().getWPSs()) {
                try {
                    if (server.equals(wps.getEndpoint())) {

                        for (Process spProcess : wps.getProcesses()) {

                            if (spProcess.getIdentifier().equals(identifier)) {

                                // if any error occurs, the "fully loaded" flag will not be set
                                boolean loadError = false;

                                // Map process attributes
                                process = EntityConverter.createProcessEntity(spProcess);

                                // Map input ports
                                try {
                                    for (Input spInput : spProcess.getInputs()) {
                                        ProcessPortDatatype datatype = EntityConverter.getDatatype(spInput.getInputFormChoice());
                                        ProcessPort inPort = new ProcessPort(datatype);
                                        inPort.setOwsIdentifier(spInput.getIdentifier());
                                        inPort.setOwsAbstract(spInput.getAbstract());
                                        inPort.setOwsTitle(spInput.getTitle());
                                        process.addInputPort(inPort);
                                    }
                                } catch (Exception ex) {
                                    loadError = true;
                                    fireSpReceiveExceptionAsAppEvent(ex);
                                }

                                // Map output ports
                                try {
                                    for (Output spOutput : spProcess.getOutputs()) {
                                        ProcessPortDatatype datatype = EntityConverter.getDatatype(spOutput.getOutputFormChoice());
                                        ProcessPort outPort = new ProcessPort(datatype);
                                        outPort.setOwsIdentifier(spOutput.getIdentifier());
                                        outPort.setOwsAbstract(spOutput.getAbstract());
                                        outPort.setOwsTitle(spOutput.getTitle());
                                        process.addOutputPort(outPort);
                                    }
                                } catch (Exception ex) {
                                    loadError = true;
                                    fireSpReceiveExceptionAsAppEvent(ex);
                                }

                                // Add metric properties
                                getMonitorDataConverter().addProcessMetrics(process);

                                // process found, return to stop search
                                process.setIsFullyLoaded(!loadError);

                                return process;
                            }
                        }

                    }
                } catch (Exception ex) {
                    fireSpReceiveExceptionAsAppEvent(ex);
                }
            }

        } catch (Exception ex) {
            fireSpReceiveExceptionAsAppEvent(ex);
        }

        return null;
    }

    public Collection<WpsServer> getAllServerWithProcesses() {
        LinkedList<WpsServer> servers = new LinkedList<>();

        // indicate error occurences but don't abort loading servers.
        // (errors are handled after the loading is done)
        String errorMsg = null;
        String errorMsgType = null;

        if (null != net) {
            WPS[] wpss = null;
            try {
                wpss = getServerProvider().getWPSs();
            } catch (Exception ex) {
                errorMsg = ex.getMessage();
                errorMsgType = ex.getClass().getSimpleName();
            }

            if (null != wpss) {
                for (WPS wps : wpss) {
                    try {
                        WpsServer server = new WpsServer(wps.getEndpoint());
                        for (Process process : wps.getProcesses()) {
                            ProcessEntity processEntity = EntityConverter.createProcessEntity(process);
                            server.addProcess(processEntity);
                        }

                        servers.add(server);

                    } catch (Exception ex) {
                        errorMsg = ex.getMessage();
                        errorMsgType = ex.getClass().getSimpleName();
                    }
                }
            }
        }

        // handle occured errors
        if (null != errorMsg) {
            String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, "Exception", errorMsg);
            AppEventService.getInstance().fireAppEvent(msg, this);
        }

        return servers;
    }

    /**
     * Receives a list of available endpoints from the semantic proxy.
     *
     * @return
     */
    public Collection<String> getAllServersFromSemanticProxy() {
        return getServerProvider().getAllServersFromSemanticProxy();
    }

    public String[] getPersistedRemotes() {
        return getServerProvider().getPersistedRemotes();
    }

    public String[] getAllServers() {
        return getServerProvider().getAllServers();
    }

    protected MonitorDataConverter getMonitorDataConverter() {
        if (null == monitorDataConverter) {
            monitorDataConverter = new MonitorDataConverter();
        }

        return monitorDataConverter;
    }

    protected ProcessSearch getProcessSearch() {
        if (null == processSearch && null != spClient) {
            processSearch = new ProcessSearch(spClient);
        }

        return processSearch;
    }

    /**
     * Returns a list containing matching processes. The list is empty if no
     * processes were found. If any error occurs, null is returned and the
     * exception message is shown in the SP InfoTab.
     *
     * @param query
     * @return
     */
    public List<ProcessEntity> getProcessesByKeyword(String query) {
        try {
            ProcessSearch search = getProcessSearch();

            if (null == search) {
                throw new SpClientNotAvailableException();
            }

            return getProcessSearch().getProcessesByKeyword(query);

        } catch (SpClientNotAvailableException ex) {
            fireSpReceiveExceptionAsAppEvent(ex);
        }

        return null;
    }

    protected Publisher getPublisher() {
        if (null == this.publisher) {
            this.publisher = new Publisher();
        }

        return this.publisher;
    }

    public void publishProcess(ProcessEntity process) {
        WPS[] wpss = null;

        try {
            wpss = getServerProvider().getWPSs();
        } catch (Exception ex) {
            // TODO handle exception
            Logger.log(ex);
        }

        getPublisher().publishProcess(wpss, process);
    }

}
