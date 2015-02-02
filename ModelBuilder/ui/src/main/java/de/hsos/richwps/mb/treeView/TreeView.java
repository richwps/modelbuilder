package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServer;
import de.hsos.richwps.mb.entity.WpsServerSource;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Swing tree component containing modelling elements (processes, data etc).
 *
 * @author dziegenh
 */
public class TreeView {

    private JTree tree;

    public TreeView(TreeNode root, final ProcessProvider processProvider) {

        tree = new JTree(root) {

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

        };
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (null == path) {
                    return;
                }
                tree.setSelectionPath(path);

                Object last = path.getLastPathComponent();
                if (!(last instanceof DefaultMutableTreeNode)) {
                    return;
                }

                JPopupMenu contextmenu = createContextMenu((DefaultMutableTreeNode) last, e);
                contextmenu.show(tree, e.getX(), e.getY());
            }

        });

    }

    /**
     * Creates the right-click context menu depending on the underlying tree
     * node.
     *
     * @param node
     * @param e
     * @return
     */
    protected JPopupMenu createContextMenu(DefaultMutableTreeNode node, MouseEvent e) {
        JPopupMenu contextmenu = new JPopupMenu();
        Object userObject = node.getUserObject();
        final String copyText;
        if (null == userObject) {
            copyText = null;
        } else {
            copyText = userObject.toString();
        }

        JMenuItem copyItem = new JMenuItem(AppConstants.COPY_TEXT_TO_CLIPBOARD);
        if (null == copyText) {
            copyItem.setEnabled(false);
        } else {
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipBoard.setContents(new StringSelection(copyText), null);
                }
            });
        }
        contextmenu.add(copyItem);

        return contextmenu;
    }

    /**
     * Returns the Swing tree component.
     *
     * @return
     */
    public JTree getGui() {
        return tree;
    }

    /**
     * Expands all tree rows.
     */
    public void expandAll() {
        tree.cancelEditing();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Return the selected tree node.
     *
     * @return
     */
    public DefaultMutableTreeNode getSelectedNode() {
        Object path = tree.getSelectionPath().getLastPathComponent();
        if (path instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) path;
        }

        return null;
    }

    /**
     * Returns true if to tree root has no children.
     *
     * @return
     */
    public boolean isEmpty() {
        TreeModel model = getGui().getModel();
        return model.getChildCount(model.getRoot()) <= 0;
    }

}
