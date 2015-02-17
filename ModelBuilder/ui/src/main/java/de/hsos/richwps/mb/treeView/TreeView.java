package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
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

        tree = new Tree(processProvider, root);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // context menu trigger
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

        // set customn tree cell renderer
        tree.setCellRenderer(new MbTreeCellRenderer());
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
