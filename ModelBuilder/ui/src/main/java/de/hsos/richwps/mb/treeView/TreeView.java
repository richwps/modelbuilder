package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.ui.UiHelper;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
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
                                ProcessEntity process = ((ProcessEntity) userObject);
                                if (!process.isIsFullyLoaded()) {
                                    process = processProvider.getProcessEntity(process.getServer(), process.getOwsIdentifier());
                                    treenode.setUserObject(process);
                                }

                                return process.getToolTipText();
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
                        return UiHelper.limitString(((ProcessEntity) userObject).toString(), AppConstants.TREEVIEW_PROCESS_TITLE_MAX_VIEW_LENGTH);
                    }
//                    else if(userObject instanceof String) {
//                        return UiHelper.limitString((String) userObject, AppConstants.PROCESS_TITLE_MAX_VIEW_LENGTH);
//                    }

                }

                return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus); //To change body of generated methods, choose Tools | Templates.
            }

        };
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
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
