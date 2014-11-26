package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Component;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.TransferHandler;
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

        // TODO create map serverId->serverNode and sort processes into the nodes:
        // [root]
        //  +--- server 1
        //        +--- process 1
        //        +--- process 2
        //  +--- server 2
        //        +--- process 3
        //        +--- process 4
        for (ProcessEntity aProcess : processes) {
            getRoot().add(new DefaultMutableTreeNode(aProcess));
        }

        updateUI();
    }
    
}
