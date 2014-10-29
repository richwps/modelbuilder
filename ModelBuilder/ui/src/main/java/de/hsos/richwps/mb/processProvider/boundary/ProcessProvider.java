package de.hsos.richwps.mb.processProvider.boundary;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.entity.WpsServer;
import de.hsos.richwps.mb.processProvider.exception.UnsupportedWpsDatatypeException;
import de.hsos.richwps.mb.properties.PropertyGroup;
import de.hsos.richwps.sp.client.RDFException;
import de.hsos.richwps.sp.client.wps.SPClient;
import de.hsos.richwps.sp.client.wps.Vocabulary;
import de.hsos.richwps.sp.client.wps.gettypes.InAndOutputForm;
import de.hsos.richwps.sp.client.wps.gettypes.Input;
import de.hsos.richwps.sp.client.wps.gettypes.Network;
import de.hsos.richwps.sp.client.wps.gettypes.Output;
import de.hsos.richwps.sp.client.wps.gettypes.Process;
import de.hsos.richwps.sp.client.wps.gettypes.WPS;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

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

    private final ProcessMetricProvider processMetricProvider;

    /**
     * Constructor, creates the SP client.
     */
    public ProcessProvider(ProcessMetricProvider processMetricProvider) {
        this.processMetricProvider = processMetricProvider;

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

    private void fireSpReceiveExceptionAsAppEvent(Exception ex) {
        String msg = String.format(AppConstants.SEMANTICPROXY_RECEIVE_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        AppEventService.getInstance().fireAppEvent(msg, this);
    }

    public void clear() {
        if (null != spClient) {
            spClient.clearCache();
        }
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

                            // Add metric properties
                            addProcessMetrics(process);

                            // process found, return to stop search
                            process.setIsFullyLoaded(true);
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

    public Collection<WpsServer> getAllServerWithProcesses() {
        LinkedList<WpsServer> servers = new LinkedList<>();

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
                        WpsServer server = new WpsServer(wps.getEndpoint());
                        for (Process process : wps.getProcesses()) {
                            ProcessEntity processEntity = new ProcessEntity(wps.getEndpoint(), process.getIdentifier());
                            processEntity.setOwsAbstract(process.getAbstract());
                            processEntity.setOwsTitle(process.getTitle());

                            server.addProcess(processEntity);
                        }

                        servers.add(server);

                    } catch (Exception ex) {
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

    /**
     * Receives a list of available endpoints from the semantic proxy.
     *
     * @return
     */
    public Collection<String> getAllServersFromSemanticProxy() {
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

    public String[] getPersistedRemotes() {
        // TODO let app set these values !!!
        Preferences preferences = AppConfig.getConfig();
        String persistKeyBase = AppConfig.CONFIG_KEYS.REMOTES_S_URL.name();
        String persistKeyCount = persistKeyBase + "_COUNT";
        String persistKeyFormat = persistKeyBase + "_%d";

        String currentItem = preferences.get(persistKeyBase, "");
        int count = preferences.getInt(persistKeyCount, 0);

        boolean currentItemAvailable = null != currentItem && !currentItem.isEmpty();
        boolean currentItemAdded = !currentItemAvailable;

        // load and add persisted items
        List<String> loadedItems = new LinkedList<>();
        for (int c = 0; c < count; c++) {
            String key = String.format(persistKeyFormat, c);
            String value = preferences.get(key, "").trim();

            if (null != value) {
                // avoid duplicates
                if (!loadedItems.contains(value)) {
                    loadedItems.add(value);
                }

                // remember if the currently used item has been added
                if (value.equals(currentItem)) {
                    currentItemAdded = true;
                }
            }
        }

        // assure the list contains the current item after loading
        if (!currentItemAdded) {
            loadedItems.add(currentItem);
        }

        return loadedItems.toArray(new String[]{});
    }

    public String[] getAllServers() {
        Collection<String> spServers = getAllServersFromSemanticProxy();
        String[] remotes = getPersistedRemotes();

        String[] servers = new String[spServers.size() + remotes.length];
        int i = 0;

        // add SP servers
        for (String server : spServers) {
            servers[i++] = server;
        }

        // add persisted remotes
        for (String server : remotes) {
            servers[i++] = server;
        }

        return servers;
    }

    public ProcessEntity getFullyLoadedProcessEntity(ProcessEntity process) {
        process = process.clone();

        ProcessEntity loadedProcess = getFullyLoadedProcessEntity(process.getServer(), process.getOwsIdentifier());
        if (null != loadedProcess) {
            process = loadedProcess;
        }

        addProcessMetrics(process);
        process.setIsFullyLoaded(true);
        
        return process;
    }

    private void addProcessMetrics(ProcessEntity process) {
        try {
            String server = process.getServer();
            String identifier = process.getOwsIdentifier();
            
            PropertyGroup processMetric = processMetricProvider.getProcessMetric(server, identifier);
            processMetric.setIsTransient(true);
            
            String metricPropertyName = processMetric.getPropertiesObjectName();
            process.setProperty(metricPropertyName, processMetric);
            
        } catch (Exception ex) {
            // ignore if metrics couldn't ne loaded
        }
    }

}
