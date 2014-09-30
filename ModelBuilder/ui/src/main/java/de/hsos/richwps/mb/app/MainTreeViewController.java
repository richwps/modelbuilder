/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ProcessPortDatatype;
import de.hsos.richwps.mb.semanticProxy.boundary.IProcessProvider;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dziegenh
 */
public class MainTreeViewController extends AbstractTreeViewController {

    public MainTreeViewController(App app) {
        super(app);
    }

    @Override
    void fillTree() {
        IProcessProvider processProvider = getProcessProvider();

        // Remove existing child-nodes from root
        DefaultMutableTreeNode root = getRoot();
        root.removeAllChildren();

        // Create and fill Process node
        DefaultMutableTreeNode processesNode = new DefaultMutableTreeNode(AppConstants.TREE_PROCESSES_NAME);
        if (processProvider != null) {
            try {
                String url = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), AppConstants.SEMANTICPROXY_DEFAULT_URL);

                boolean connected = processProvider.isConnected() && processProvider.getUrl().equals(url);

                if (connected || processProvider.connect(url)) {
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
        }

        // Create and fill download services node
        DefaultMutableTreeNode downloadServices = new DefaultMutableTreeNode(AppConstants.TREE_DOWNLOADSERVICES_NAME);

        downloadServices.add(new DefaultMutableTreeNode(""));

        // TODO MOCK!! Create and fill local elements node
        DefaultMutableTreeNode local = new DefaultMutableTreeNode(AppConstants.TREE_LOCALS_NAME);
        // inputs
        ProcessPort cIn = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lIn = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cIn.setGlobalOutput(false);
        lIn.setGlobalOutput(false);
        local.add(new DefaultMutableTreeNode(cIn));
        local.add(new DefaultMutableTreeNode(lIn));
        // Outputs
        ProcessPort cOut = new ProcessPort(ProcessPortDatatype.COMPLEX, true);
        ProcessPort lOut = new ProcessPort(ProcessPortDatatype.LITERAL, true);
        cOut.setGlobalOutput(true);
        lOut.setGlobalOutput(true);
        local.add(new DefaultMutableTreeNode(cOut));
        local.add(new DefaultMutableTreeNode(lOut));

        // add all child nodes to root
        root.add(processesNode);
        root.add(downloadServices);
        root.add(local);

        updateUI();
    }

}
