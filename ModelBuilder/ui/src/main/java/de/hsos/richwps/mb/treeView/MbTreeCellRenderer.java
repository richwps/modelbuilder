package de.hsos.richwps.mb.treeView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.WpsServer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author dziegenh
 */
public class MbTreeCellRenderer extends DefaultTreeCellRenderer {

    private String portIconBaseKey;

    public MbTreeCellRenderer() {
        super();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (null == value || !(component instanceof JLabel)) {
            return component;
        }

        JLabel label = (JLabel) component;

        if (value instanceof DefaultMutableTreeNode) {
            Object nodeValue = ((DefaultMutableTreeNode) value).getUserObject();

            if (null != nodeValue) {

                if (nodeValue instanceof WpsServer) {

                    WpsServer server = (WpsServer) nodeValue;

                    if (0 == server.getProcesses().size()) {
                        label.setIcon(closedIcon);
                    }
                }

                if(nodeValue instanceof String) {
                    String nodeText = (String) nodeValue;
                    if(nodeText.equals(AppConstants.TREE_INTERFACEOBJECTS_NAME)) {
                        label.setIcon(UIManager.getIcon(portIconBaseKey + "s"));
                    }
                    else if(nodeText.equals(AppConstants.TREE_PROCESSES_NAME)) {
                        label.setIcon(UIManager.getIcon(AppConstants.ICON_PROCESSES_KEY));
                    }
                }
                
                if (nodeValue instanceof ProcessPort) {
                    ProcessPort port = (ProcessPort) nodeValue;

                    String type;
                    if (port.isGlobalInput()) {
                        type = "_in_";
                    } else {
                        type = "_out_";
                    }

                    String datatype = port.toString().toLowerCase();

                    String iconKey = portIconBaseKey + type + datatype;
                    label.setIcon(UIManager.getIcon(iconKey));
                }
            }
        }

        return label;
    }

    public void setPortIconBaseKey(String portIconBaseKey) {
        this.portIconBaseKey = portIconBaseKey;
    }

    public String getPortIconBaseKey() {
        return portIconBaseKey;
    }

}
