package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTableActionCell implements TableCellRenderer {

    public Component getTableCellRendererComponent(final JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton) value;
        return button;
    }

}
