/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPortDatatype;
import java.util.LinkedList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dziegenh
 */
public class MainTreeView extends AbstractTreeView {

    public MainTreeView(App app) {
        super(app);
    }

    @Override
    void fillTree() {
        ProcessProvider processProvider = getProcessProvider();

        // Remove existing child-nodes from root
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getTreeView().getGui().getModel().getRoot();
        root.removeAllChildren();

        // Create and fill Process node
        DefaultMutableTreeNode processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);
        if (processProvider != null) {
            try {
                String url = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), AppConstants.SEMANTICPROXY_DEFAULT_URL);
                if (processProvider.isConnected() || processProvider.connect(url)) {
                    for (String server : processProvider.getAllServer()) {
                        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);

                        for (ProcessEntity process : processProvider.getServerProcesses(server)) {
                            serverNode.add(new DefaultMutableTreeNode(process));
                        }
                        processesNode.add(serverNode);
                    }
                }

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

            // add MOCK servers for Development -> to be removed!
            LinkedList<String> mockServers = new LinkedList<String>();
            mockServers.add(processProvider.mockServer1);
            mockServers.add(processProvider.mockServer2);
            for (String server : mockServers) {
                DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);

                for (ProcessEntity process : processProvider.getServerProcesses(server)) {
                    serverNode.add(new DefaultMutableTreeNode(process));
                }
                processesNode.add(serverNode);
            }
        }

        // Create and fill download services node
        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);

        downloadServices.add(new DefaultMutableTreeNode(""));

        // TODO MOCK!! Create and fill local elements node
        DefaultMutableTreeNode local = new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME);
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        local.add(new DefaultMutableTreeNode(cOut));
        local.add(new DefaultMutableTreeNode(lOut));
        // inputs
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        local.add(new DefaultMutableTreeNode(cIn));
        local.add(new DefaultMutableTreeNode(lIn));

        // add all child nodes to root
        root.add(processesNode);
        root.add(downloadServices);
        root.add(local);

        // Update tree GUI
        getTreeView().getGui().updateUI();
        getTreeView().expandAll();
    }

}
