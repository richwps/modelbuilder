package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.control.ProcessEntityTitleComparator;
import de.hsos.richwps.mb.control.ProcessPortFactory;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.BoundingBoxOutput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralOutput;
import de.hsos.richwps.mb.processProvider.exception.LoadDataTypesException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

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
    public void fillTree() {

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

                for (WpsServer server : processProvider.getAllServerWithProcesses()) {
                    DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);

                    // sort the server's processes alphabetically by title
                    List<ProcessEntity> processes = server.getProcesses();
                    Collections.sort(processes, processComparator);
                    for (ProcessEntity process : processes) {
                        serverNode.add(new DefaultMutableTreeNode(process));
                    }
                    processesNode.add(serverNode);
                    receivedServers++;
                }

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
        boolean loadDatatypesError = false;
        loadDatatypesError |= !addGlobalInput(insAndOuts, ProcessPortDatatype.COMPLEX);
        loadDatatypesError |= !addGlobalInput(insAndOuts, ProcessPortDatatype.LITERAL);
        loadDatatypesError |= !addGlobalInput(insAndOuts, ProcessPortDatatype.BOUNDING_BOX);

        // Outputs
        insAndOuts.add(new DefaultMutableTreeNode(new ComplexDataOutput(true)));
        insAndOuts.add(new DefaultMutableTreeNode(new LiteralOutput(true)));
        insAndOuts.add(new DefaultMutableTreeNode(new BoundingBoxOutput(true)));

        // add all child nodes to root
        root.add(insAndOuts);
        root.add(processesNode);

// download services are currently not available
//        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
//        downloadServices.add(new DefaultMutableTreeNode(""));
//        root.add(downloadServices);
        updateUI();
        
        // show message if an error occured
        if(loadDatatypesError) {
                JOptionPane.showMessageDialog(
                        getParent(),
                        AppConstants.LOAD_DATATYPES_ERROR,
                        AppConstants.DIALOG_TITLE_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
        }
    }

    private boolean addGlobalInput(DefaultMutableTreeNode parent, ProcessPortDatatype datatype) {
        try {
            ProcessPort literalInput = ProcessPortFactory.createGlobalInputPort(datatype);
            parent.add(new DefaultMutableTreeNode(literalInput));
        } catch (LoadDataTypesException ex) {
            return false;
        }

        return true;
    }

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

    public DefaultMutableTreeNode getProcessesNode() {
        return this.processesNode;
    }
}
