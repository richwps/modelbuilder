package de.hsos.richwps.mb.processProvider.boundary;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServerSource;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.control.EntityConverter;
import de.hsos.richwps.mb.processProvider.control.KeyTranslator;
import de.hsos.richwps.mb.processProvider.control.ManagedRemoteDiscovery;
import de.hsos.richwps.mb.processProvider.control.MonitorDataConverter;
import de.hsos.richwps.mb.processProvider.control.ProcessCache;
import de.hsos.richwps.mb.processProvider.control.ProcessSearch;
import de.hsos.richwps.mb.processProvider.control.Publisher;
import de.hsos.richwps.mb.processProvider.control.ServerProvider;
import de.hsos.richwps.mb.processProvider.entity.ProcessLoadingStatus;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.processProvider.exception.ProcessMetricProviderNotAvailable;
import de.hsos.richwps.mb.processProvider.exception.SpClientNotAvailableException;
import de.hsos.richwps.sp.client.ows.SPClient;
import de.hsos.richwps.sp.client.ows.gettypes.Input;
import de.hsos.richwps.sp.client.ows.gettypes.Network;
import de.hsos.richwps.sp.client.ows.gettypes.Output;
import de.hsos.richwps.sp.client.ows.gettypes.Process;
import de.hsos.richwps.sp.client.ows.gettypes.WPS;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
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
    private ProcessCache cache;

    private KeyTranslator translator;

    private boolean managedRemotesEnabled = true;

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
     * Gets the process cache.
     *
     * @return
     */
    protected ProcessCache getCache() {
        if (null == this.cache) {
            this.cache = new ProcessCache();
        }

        return this.cache;
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
            AppEventService.getInstance().fireAppEvent(AppConstants.SEMANTICPROXY_NOT_REACHABLE, this, AppEvent.PRIORITY.URGENT);
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
        getCache().resetLoadingStates();
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
        final ProcessEntity cachedProcess = getCache().getCachedProcess(process.getServer(), process.getOwsIdentifier());

        if (null == cachedProcess) {
            getCache().addProcess(process, false);
        }

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

        // already loaded: return cached process.
        if (getCache().isLoaded(server, identifier)) {
            return getCache().getCachedProcess(server, identifier);
        }

        // get full or partially loaded process from cache.
        ProcessEntity process = getCache().getCachedProcess(server, identifier);

        // if any error occurs, the "fully loaded" flag will not be set
        boolean loadError = false;

        boolean spAvailable = false;
        try {
            spAvailable = isConnected() || connect(spUrl);

        } catch (Exception ex) {
            // ignore
        }

        // SP not available and process not cached -> return null.
        if (!spAvailable) {
            if (null == process) {
                return null;
            }

        } else { // SP available: get process from SP 

            // find desired process at the desired endpoint (server)
            try {

                for (WPS wps : getServerProvider().getWPSs()) {

                    try {
                        // this is the server we are looking for
                        if (server.equals(wps.getEndpoint())) {

                            for (Process spProcess : wps.getProcesses()) {

                                // this is the process we are looking for
                                if (spProcess.getIdentifier().equals(identifier)) {

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
        }

        // nothing loaded: return null
        if (null == process) {
            return null;
        }

        // Add metric properties if available
        try {
            getMonitorDataConverter().addProcessMetrics(process);

        } catch (ProcessMetricProviderNotAvailable ex) {
            // ignore
        }

        // add process to cache or update cached process 
        // if any error occured, the process data is propably not complete
        getCache().addProcess(process, !loadError);
        process = getCache().getCachedProcess(server, identifier);

        // trigger toolTipText update
        process.setToolTipText(null);

        // process found, return to stop search
        return process;
    }

    public Collection<WpsServer> getAllServerWithProcesses() {
        HashMap<String, WpsServer> servers = new HashMap<>();

        // indicate error occurences but don't abort loading servers.
        // (errors are handled after the loading is done)
        String errorMsg = null;
        String errorMsgType = null;

        // temporary disable "SP not available" messages to avoid msg flooding
        AppEventService.getInstance().setCommandEnabled(AppConstants.INFOTAB_ID_SEMANTICPROXY, false);

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
                        server.setSource(WpsServerSource.SEMANTIC_PROXY);

                        for (Process process : wps.getProcesses()) {

                            ProcessEntity processEntity;

                            String processServer = wps.getEndpoint();
                            String processId = process.getIdentifier();

                            // fully reload process if it has been reset
                            ProcessLoadingStatus loadingStatus = getCache().getLoadingStatus(processServer, processId);
                            if (null != loadingStatus && loadingStatus.equals(ProcessLoadingStatus.RESET)) {
                                processEntity = getFullyLoadedProcessEntity(processServer, processId);

                            } else {

                                // get cached process
                                processEntity = getCache().getCachedProcess(processServer, processId);
                            }

                            // not cached: add to cache
                            if (null == processEntity) {
                                processEntity = EntityConverter.createProcessEntity(process, getTranslator());
                                getCache().addProcess(processEntity, false);
                            }

                            server.addProcess(processEntity);
                        }

                        servers.put(server.getEndpoint(), server);

                    } catch (Exception ex) {
                        errorMsg = ex.getMessage();
                        errorMsgType = ex.getClass().getSimpleName();
                    }
                }
            }
        }

        // re-enable command (see above)
        AppEventService.getInstance().setCommandEnabled(AppConstants.INFOTAB_ID_SEMANTICPROXY, true);

        // handle occured SP errors
        if (null != errorMsg) {
            String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, errorMsgType, errorMsg);
            AppEventService.getInstance().fireAppEvent(msg, this);
        }

        // add servers and processes from managed remote servers
        String[] persistedRemotes = getServerProvider().getPersistedRemotes();
        for (String aPersistedRemote : persistedRemotes) {
            WpsServer server = new WpsServer(aPersistedRemote);

            WpsServer existingServer = servers.get(aPersistedRemote);

            if (null == existingServer) {
                existingServer = new WpsServer(aPersistedRemote);
                existingServer.setSource(WpsServerSource.MANAGED_REMOTE);

            } else {
                existingServer.setSource(WpsServerSource.MIXED);
            }

            // only discover manged remotes if it is enabled
            if (isManagedRemotesEnabled()) {
                
                server = ManagedRemoteDiscovery.discoverProcesses(server.getEndpoint());
                
                for (ProcessEntity aProcess : server.getProcesses()) {
                    ProcessEntity loadedProcess = this.getFullyLoadedProcessEntity(aProcess);
                    if (!existingServer.getProcesses().contains(loadedProcess)) {
                        existingServer.addProcess(loadedProcess);
                    }
                }
            }

            servers.put(aPersistedRemote, existingServer);

        }

        return servers.values();
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

            List<ProcessEntity> result = new LinkedList<>();
            List<ProcessEntity> processes = getProcessSearch().getProcessesByKeyword(query);

            // get cached process instances of the found processes
            for (ProcessEntity process : processes) {
                String server = process.getServer();
                String identifier = process.getOwsIdentifier();
                ProcessEntity cached = getCache().getCachedProcess(server, identifier);

                // if the process is not cached, cache it.
                if (null == cached) {
                    cached = process;
                    getCache().addProcess(process, false);
                }

                result.add(cached);
            }

            return result;

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

    public void setManagedRemotesEnabled(boolean enabled) {
        this.managedRemotesEnabled = enabled;
    }

    public boolean isManagedRemotesEnabled() {
        return managedRemotesEnabled;
    }

}
