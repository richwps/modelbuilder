package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author dziegenh
 */
public class ProcessesTreeViewController extends AbstractTreeViewController {

    private List<ProcessEntity> processes;

    public ProcessesTreeViewController(SemanticProxyInteractionComponents components) {
        super(components);
        getTreeView().getGui().setBorder(null);
    }

    public void setProcesses(List<ProcessEntity> processes) {
        this.processes = processes;
    }

    @Override
    void fillTree() {

        if (null == processes) {
            return;
        }

        getRoot().removeAllChildren();

        HashMap<String, DefaultMutableTreeNode> serverNodes = new HashMap<>();
        DefaultMutableTreeNode serverNode = null;

        for (ProcessEntity aProcess : processes) {
            String aProcessServer = aProcess.getServer();

            serverNode = serverNodes.get(aProcessServer);
            if (null == serverNode) {
                serverNode = new DefaultMutableTreeNode(aProcessServer);
                serverNodes.put(aProcessServer, serverNode);
                getRoot().add(serverNode);
            }
            serverNode.add(new DefaultMutableTreeNode(aProcess));
        }

        updateUI();
    }

}
