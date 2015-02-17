package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.entity.WpsServerSource;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author dziegenh
 */
public class Tree extends JTree {

    private final ProcessProvider processProvider;

    public Tree(ProcessProvider processProvider, TreeNode root) {
        super(root);

        this.processProvider = processProvider;
    }

    public String getToolTipText(MouseEvent event) {
        if (event != null) {
            Point p = event.getPoint();
            int selRow = getRowForLocation(p.x, p.y);
            TreeCellRenderer r = getCellRenderer();

            if (selRow != -1 && r != null) {
                TreePath path = getPathForRow(selRow);
                Object lastPath = path.getLastPathComponent();

                if (null != lastPath && lastPath instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) lastPath;
                    Object userObject = treenode.getUserObject();

                    if (userObject instanceof ProcessEntity) {
                        // trigger loading update
                        ProcessEntity process = ((ProcessEntity) userObject);
                        process = processProvider.getFullyLoadedProcessEntity(process.getServer(), process.getOwsIdentifier());

                        return process.getToolTipText();

                    } else if (userObject instanceof WpsServer) {
                        WpsServer server = (WpsServer) userObject;
                        WpsServerSource source = server.getSource();
                        if (null == source) {
                            source = WpsServerSource.UNKNOWN;
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append("Processes source: ");
                        sb.append(source.toString());

                        return sb.toString();
                    }
                }
            }
        }

        return super.getToolTipText(event);
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof ProcessPort) {
                ProcessPort port = (ProcessPort) userObject;
                if (port.isGlobal()) {
                    return port.getDatatype().toString() + (port.isGlobalInput() ? " Input" : " Output");
                }
            } else if (userObject instanceof ProcessEntity) {
                return ((ProcessEntity) userObject).toString();
            }

        }

        return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }

}
