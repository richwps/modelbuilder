package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.control.ProcessEntityTitleComparator;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.treeView.MbTreeCellRenderer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author dziegenh
 */
public class SelectProcessTree extends JTree {

    public SelectProcessTree(Collection<WpsServer> processList) {
        super();

        // setup cell renderer
        final MbTreeCellRenderer cellRenderer = new MbTreeCellRenderer();
        cellRenderer.setBackgroundSelectionColor(AppConstants.SELECTION_BG_COLOR);
        cellRenderer.setLeafIcon(UIManager.getIcon(AppConstants.ICON_PROCESS_KEY));
        cellRenderer.setPortIconBaseKey(AppConstants.ICON_PORT_BASE_KEY);
        setCellRenderer(cellRenderer);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        setRootVisible(false);

        // add server nodes
        for (WpsServer aWpsServer : processList) {

            List<ProcessEntity> processes = aWpsServer.getProcesses();
            if (processes.size() > 0) {
                Collections.sort(processes, new ProcessEntityTitleComparator());

                DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(aWpsServer);

                // add server's process nodes
                for (ProcessEntity aProcess : processes) {
                    serverNode.add(new DefaultMutableTreeNode(aProcess));
                }

                root.add(serverNode);
            }
        }

        setModel(new DefaultTreeModel(root));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

}
