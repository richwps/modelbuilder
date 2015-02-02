package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.control.ProcessEntityTitleComparator;
import de.hsos.richwps.mb.entity.ComplexDataTypeFormat;
import de.hsos.richwps.mb.entity.DataTypeDescriptionComplex;
import de.hsos.richwps.mb.entity.DataTypeDescriptionLiteral;
import de.hsos.richwps.mb.entity.IDataTypeDescription;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.GetProcessesRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Controls the main tree view component and it's interaction with the
 * ModelBuilder.
 *
 * @author dziegenh
 * @author dalcacer
 */
public class MainTreeViewController extends AbstractTreeViewController {

    private DefaultMutableTreeNode processesNode;

    private HashMap<String, MutableTreeNode> remoteNodes = new HashMap<>();

    public MainTreeViewController(AppPreferencesDialog preferencesDialog, SemanticProxyInteractionComponents components) {
        super(components);

        processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);

        // handle changes of the SP url config
        preferencesDialog.addWindowListener(new WindowAdapter() {

            private boolean urlHasChanged() {
                ProcessProvider processProvider = getProcessProvider();
                String spUrl = getSpUrlFromConfig();
                return !processProvider.getUrl().equals(spUrl);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (urlHasChanged()) {
                    fillTree();
                }
            }
        });
    }

    /**
     * Loads and return the configured SP url or the default url.
     *
     * @return
     */
    protected String getSpUrlFromConfig() {
        String key = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();
        String defaultValue = AppConstants.SEMANTICPROXY_DEFAULT_URL;
        return AppConfig.getConfig().get(key, defaultValue);
    }

    @Override
    void fillTree() {
        fillTree(true);
    }

    public void fillTree(boolean clearCache) {

        ProcessProvider processProvider = getProcessProvider();

        // Remove existing child nodes
        DefaultMutableTreeNode root = getRoot();
        root.removeAllChildren();
        processesNode.removeAllChildren();

        // Create and fill Process node
        if (processProvider != null) {
            ProcessEntityTitleComparator processComparator = new ProcessEntityTitleComparator();

            try {
                String spConfigUrl = getSpUrlFromConfig();

                boolean connected = processProvider.isConnected() && processProvider.getUrl().equals(spConfigUrl);
                if (!connected) {
                    connected = processProvider.connect(spConfigUrl);
                }

                int receivedServers = 0;

//                if (connected || processProvider.connect(url)) {
                    for (WpsServer server : processProvider.getAllServerWithProcesses()) {
                        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);

                        // sort the server's processes alphabetically by title
                        List<ProcessEntity> processes = server.getProcesses();
                        Collections.sort(processes, processComparator);
                        for (ProcessEntity process : processes) {
                            if (clearCache) {
                                process.setToolTipText(null);
                            }
                            serverNode.add(new DefaultMutableTreeNode(process));
                        }
                        processesNode.add(serverNode);
                        receivedServers++;
                    }
//                }

                int numRemotes = getProcessProvider().getPersistedRemotes().length;
                sendReceiveResultMessage(receivedServers, connected, numRemotes);

            } catch (Exception ex) {
                // Inform user when SP client can't be created
                AppEvent event = new AppEvent(AppConstants.SEMANTICPROXY_CANNOT_CREATE_CLIENT, this, AppConstants.INFOTAB_ID_SEMANTICPROXY);
                AppEventService.getInstance().fireAppEvent(event);

                // Append exception message if available
                String exMsg = ex.getMessage();
                if (null != exMsg && !exMsg.isEmpty()) {
                    StringBuilder sb = new StringBuilder(200);
                    sb.append(AppConstants.ERROR_MSG_IS);
                    sb.append("\"");
                    sb.append(ex.getMessage());
                    sb.append("\"");
                    event.setMessage(sb.toString());
                    AppEventService.getInstance().fireAppEvent(event);
                }
            }
        }

        // Create node with interface objects like global inputs and outputs
        DefaultMutableTreeNode insAndOuts = new DefaultMutableTreeNode(AppConstants.TREE_INTERFACEOBJECTS_NAME);
        // inputs
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        insAndOuts.add(new DefaultMutableTreeNode(cIn));
        insAndOuts.add(new DefaultMutableTreeNode(lIn));
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        insAndOuts.add(new DefaultMutableTreeNode(cOut));
        insAndOuts.add(new DefaultMutableTreeNode(lOut));

        // add all child nodes to root
        root.add(insAndOuts);
        root.add(processesNode);

// download services are currently not available
//        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
//        downloadServices.add(new DefaultMutableTreeNode(""));
//        root.add(downloadServices);
        // adds persisted remote servers
//        if (clearCache) {
//            setRemotes(processProvider.getPersistedRemotes(), true);
//        }
        updateUI();
    }

    /**
     * Adds a node to the maintree.
     *
     * @param uri (WPS-endpoint).
     */
//    public MutableTreeNode addNode(String uri) {
//        // TODO check if it is useful to set the server entity as user object (instead of the endpoint string)
//        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(uri);
//        //Perform discovery.
//        try {
//            IRichWPSProvider provider = new RichWPSProvider();
//            GetProcessesRequest request = new GetProcessesRequest(uri);
//            provider.perform(request);
//            List<String> processes = request.getProcesses();
//
//            for (String processid : processes) {
//
//                DescribeRequest pd = new DescribeRequest();
//                pd.setEndpoint(uri);
//                pd.setIdentifier(processid);
//                provider.perform(pd);
//
//                ProcessEntity pe = new ProcessEntity(uri, pd.getIdentifier());
//                //TRICKY
//                if (pd.getAbstract() != null) {
//                    pe.setOwsAbstract(pd.getAbstract());
//                } else {
//                    pe.setOwsAbstract("");
//                }
//
//                pe.setOwsTitle(pd.getTitle());
//                //FIXME pe.setOwsVersion
//                this.transformInputs(pd, pe);
//                this.transformOutputs(pd, pe);
//
//                // load metric properties
//                pe = getProcessProvider().getFullyLoadedProcessEntity(pe);
//
//                serverNode.add(new DefaultMutableTreeNode(pe));
//            }
//            processesNode.add(serverNode);
//            this.updateUI();
//        } catch (Exception e) {
//            Logger.log("Debug:\n error occured " + e);
//        }
//
//        return serverNode;
//    }
//    public void setRemotes(String[] remotes) {
//        this.setRemotes(remotes, false);
//    }
//    void setRemotes(String[] remotes, boolean addExistingNodes) {
//
//        // clone currently used remote nodes to identify removed nodes
//        LinkedList<String> unusedNodes = new LinkedList<>(remoteNodes.keySet());
//        unusedNodes = (LinkedList<String>) unusedNodes.clone();
//
//        for (String remote : remotes) {
//            if (!remoteNodes.containsKey(remote) || addExistingNodes) {
//                MutableTreeNode node = addNode(remote);
//                remoteNodes.put(remote, node);
//
//                unusedNodes.remove(remote);
//            }
//        }
//
//        for (String unusedNodeKey : unusedNodes) {
//            MutableTreeNode unusedNode = remoteNodes.get(unusedNodeKey);
//
//            if (null != unusedNode && processesNode.isNodeChild(unusedNode)) {
//                processesNode.remove(remoteNodes.get(unusedNodeKey));
//            }
//
//            remoteNodes.remove(unusedNodeKey);
//        }
//
//        updateUI();
//    }
    private void sendReceiveResultMessage(int numReceived, boolean spConnected, int numRemotes) {
        StringBuilder msgSb = new StringBuilder();
        msgSb.append("Received ")
                .append(numReceived)
                .append(" server endpoint");

        if (1 != numReceived) {
            msgSb.append("s");
        }

        if (spConnected) {
            msgSb.append(" from SemanticProxy");
            
            if (0 < numRemotes) {
                msgSb.append(" and");
            }
        }

        if (0 < numRemotes) {
            msgSb.append(" from managed remote servers");
        }

        msgSb.append(".");

        AppEventService.getInstance().fireAppEvent(msgSb.toString(), AppConstants.INFOTAB_ID_SEMANTICPROXY);
    }
}
