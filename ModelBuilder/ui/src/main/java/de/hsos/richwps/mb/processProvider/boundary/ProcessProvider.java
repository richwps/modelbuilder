package de.hsos.richwps.mb.processProvider.boundary;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.control.EntityConverter;
import de.hsos.richwps.mb.processProvider.control.KeyTranslator;
import de.hsos.richwps.mb.processProvider.control.MonitorDataConverter;
import de.hsos.richwps.mb.processProvider.control.ProcessSearch;
import de.hsos.richwps.mb.processProvider.control.Publisher;
import de.hsos.richwps.mb.processProvider.control.ServerProvider;
import de.hsos.richwps.mb.processProvider.entity.WpsServer;
import de.hsos.richwps.mb.processProvider.exception.SpClientNotAvailableException;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.InAndOutputForm;
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
 * Connects to the SemanticProxy and receives/provides a list of available
 * processes.
 *
 * @author dziegenh
 */
public class ProcessProvider {

    private SPClient spClient;
    private Network net;
    private String spUrl;

    private ProcessSearch processSearch;
    private MonitorDataConverter monitorDataConverter;
    private ServerProvider serverProvider;
    private Publisher publisher;

    private KeyTranslator translator;

    /**
     * Constructor, creates the SP client.
     */
    public ProcessProvider() {
        spClient = SPClient.getInstance();
    }

    public String getUrl() {
        return spUrl;
    }

    public KeyTranslator getTranslator() {
        if (null == translator) {
            translator = new KeyTranslator();
        }

        return translator;
    }

    /**
     * Connects to the SemanticProxy using the url field.
     *
     * @param url SemanticProxy URL
     * @return
     * @throws java.lang.Exception
     */
    public boolean connect(String url) throws Exception {
        this.spUrl = url;

        try {
            //init SP client
            spClient.autoInitClient(new URL(url));
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
        AppEventService.getInstance().fireAppEvent(msg, this, AppEvent.PRIORITY.URGENT);
    }

    public void clear() {
        if (null != spClient) {
            spClient.clearCache();
            this.net = null;
        }
        getServerProvider().clearCache();
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

        // find desired process at the desired endpoint (server)
        try {
            for (WPS wps : getServerProvider().getWPSs()) {

                try {
                    // this is the server we are looking for
                    if (server.equals(wps.getEndpoint())) {

                        for (Process spProcess : wps.getProcesses()) {

                            // this is the process we are looking for
                            if (spProcess.getIdentifier().equals(identifier)) {

                                // if any error occurs, the "fully loaded" flag will not be set
                                boolean loadError = false;

                                // Map process attributes
                                process = EntityConverter.createProcessEntity(spProcess, getTranslator());

                                // Map input ports
                                try {

                                    for (Input spInput : spProcess.getInputs()) {
                                        ProcessPort inPort = EntityConverter.createProcessInput(spInput);
                                        process.addInputPort(inPort);
                                    }

                                } catch (Exception ex) {
                                    loadError = true;
                                    fireSpReceiveExceptionAsAppEvent(ex);
                                }

                                // Map output ports
                                try {

                                    for (Output spOutput : spProcess.getOutputs()) {
                                        ProcessPort outPort = EntityConverter.createProcessOutput(spOutput);
                                        process.addOutputPort(outPort);
                                    }

                                } catch (Exception ex) {
                                    loadError = true;
                                    fireSpReceiveExceptionAsAppEvent(ex);
                                }

                                // Add metric properties
                                getMonitorDataConverter().addProcessMetrics(process);

                                // if any error occured, the process data is propably not complete
                                process.setIsFullyLoaded(!loadError);

                                // process found, return to stop search
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
                            ProcessEntity processEntity = EntityConverter.createProcessEntity(process, getTranslator());
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
            String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, errorMsgType, errorMsg);
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
            processSearch = new ProcessSearch(spClient, getTranslator());
        }

        return processSearch;
    }

    /**
     * Returns a list containing matching SemanticProxy processes. The list is
     * empty if no processes were found. If any error occurs, null is returned
     * and the exception message is shown in the SP InfoTab.
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

    /**
     * Published the given process at the currently connected SemanticProxy. If
     * the process endpoint doesn't exist at the SP, it will also be published.
     *
     * @param process
     * @throws Exception
     */
    public void publishProcess(ProcessEntity process) throws Exception {
        WPS wps = getServerProvider().getSpWpsByEndpoint(process.getServer());

        // endpoint not existing at SP -> publish it and get the new WPS
        if (null == wps) {
            getPublisher().publishWps(process.getServer());
            clear(); // force requesting WPSs
            connect(spUrl);
            wps = getServerProvider().getSpWpsByEndpoint(process.getServer());
        }

        getPublisher().publishProcess(wps, process);
    }

    public void setProcessMetricProvider(ProcessMetricProvider metricProvider) {
        getMonitorDataConverter().setMetricProvider(metricProvider);
    }

}
