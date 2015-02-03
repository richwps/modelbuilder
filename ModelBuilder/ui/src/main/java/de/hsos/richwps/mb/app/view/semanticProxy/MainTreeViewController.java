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
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        ProcessPort bIn = new ProcessPort(ProcessPortDatatype.BOUNDING_BOX, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        bIn.setGlobalOutput(false);
        insAndOuts.add(new DefaultMutableTreeNode(cIn));
        insAndOuts.add(new DefaultMutableTreeNode(lIn));
        insAndOuts.add(new DefaultMutableTreeNode(bIn));
        
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        ProcessPort bOut = new ProcessPort(ProcessPortDatatype.BOUNDING_BOX, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        bOut.setGlobalOutput(true);
        insAndOuts.add(new DefaultMutableTreeNode(cOut));
        insAndOuts.add(new DefaultMutableTreeNode(lOut));
        insAndOuts.add(new DefaultMutableTreeNode(bOut));

        // add all child nodes to root
        root.add(insAndOuts);
        root.add(processesNode);

// download services are currently not available
//        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);
//        downloadServices.add(new DefaultMutableTreeNode(""));
//        root.add(downloadServices);
     
        updateUI();
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
}
