package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTableIdentifierCell extends JPanel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.removeAll();
        FlowLayout flow = new FlowLayout();
        flow.setAlignOnBaseline(true);
        flow.setAlignment(FlowLayout.LEFT);
        flow.setAlignOnBaseline(true);
        this.setLayout(flow);
        this.setBackground(Color.WHITE);
        String cellvalue = (String) table.getValueAt(row, column);
        JLabel label = new JLabel();
        label.setText(cellvalue);
        
        this.add(label);
        return this;
    }
}
