package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTableTypeCell extends JPanel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.removeAll();
        this.setBackground(Color.WHITE);
        String cellvalue = (String) table.getValueAt(row, column);
        ImageIcon icon = null;
        if (cellvalue.equals("L")) {
            icon = (ImageIcon) (UIManager.get(AppConstants.ICON_PORT_OUT_L_KEY));
        } else if (value.equals("C")) {
            icon = (ImageIcon) (UIManager.get(AppConstants.ICON_PORT_OUT_C_KEY));
        } else if (value.equals("B")) {
            icon = (ImageIcon) (UIManager.get(AppConstants.ICON_PORT_OUT_B_KEY));
        }
        JLabel label = new JLabel();
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        label.setIcon(icon);
        this.add(label);
        this.setSize(48, 48);
        return this;
    }
}
