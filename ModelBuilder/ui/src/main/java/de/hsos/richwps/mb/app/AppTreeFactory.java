package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.MbTreeCellRenderer;
import de.hsos.richwps.mb.treeView.TreeView;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Enables graph node creation on double clicking tree nodes.
 *
 * @author dziegenh
 */
class TreeViewMouseAdapter extends MouseAdapter {

    private final GraphView graphView;
    private final TreeView treeView;
    private final ProcessProvider processProvider;

    TreeViewMouseAdapter(GraphView graphView, TreeView treeView, ProcessProvider processProvider) {
        this.graphView = graphView;
        this.treeView = treeView;
        this.processProvider = processProvider;
    }

    private ProcessProvider getProcessProvider() {
        return processProvider;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (2 == e.getClickCount() && graphView.isEnabled()) {
            DefaultMutableTreeNode node = treeView.getSelectedNode();
            if (null != node) {
                Object nodeObject = node.getUserObject();

                Point location = graphView.getEmptyCellLocation(new Point(0, 0));

                if (nodeObject instanceof ProcessEntity) {

                    // try to load missing process data
                    ProcessEntity process = ((ProcessEntity) nodeObject);
                    try {
                        process = getProcessProvider().getFullyLoadedProcessEntity(process.getServer(), process.getOwsIdentifier());
                        node.setUserObject(process);
                    } catch (Exception ex) {
                        Logger.getLogger(TreeViewMouseAdapter.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // create graph cell (node) for this process
                    graphView.createNodeFromProcess(process, location);

                } else if (nodeObject instanceof ProcessPort) {
                    graphView.createNodeFromPort((ProcessPort) nodeObject, location);
                }
            }
        }
    }
}

/**
 * Creates tree view components for the ModelBuilder.
 *
 * @author dziegenh
 */
public class AppTreeFactory {

    public static TreeView createTree(GraphView graphView, final ProcessProvider processProvider) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(AppConstants.TREE_ROOT_NAME);

        // Java 1.7 Bugfix: add dummy node - otherwise the tree doesn't work?!
        root.add(new DefaultMutableTreeNode(""));

        TreeView treeView = new TreeView(root, processProvider);
        treeView.getGui().addMouseListener(new TreeViewMouseAdapter(graphView, treeView, processProvider));
        treeView.getGui().setBorder(new EmptyBorder(2, 2, 2, 2));

        MbTreeCellRenderer cellRenderer = (MbTreeCellRenderer) treeView.getGui().getCellRenderer();
        cellRenderer.setBackgroundSelectionColor(AppConstants.SELECTION_BG_COLOR);
        cellRenderer.setLeafIcon(UIManager.getIcon(AppConstants.ICON_PROCESS_KEY));
        cellRenderer.setPortIconBaseKey(AppConstants.ICON_PORT_BASE_KEY);
        
        return treeView;
    }

}
