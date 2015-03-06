package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import de.hsos.richwps.mb.ui.MultilineLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.wps.x100.LiteralDataType;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTableDataCell extends JPanel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.removeAll();
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.LEFT);
        flow.setAlignOnBaseline(true);
        this.setLayout(flow);
        this.setBackground(Color.WHITE);
        Object cellvalue = table.getValueAt(row, column);
        MultilineLabel label = new MultilineLabel();
        label.setPreferredSize(new Dimension(300,48));
        
        if (cellvalue instanceof URL) {
            String httpuri = ((URL) cellvalue).toString();
            String abbString = StringUtils.abbreviateMiddle(httpuri, "[...]", 50);
            label.setText(abbString);
        } else if (cellvalue instanceof LiteralDataType) {
            LiteralDataType val = (LiteralDataType) cellvalue;
            label.setText(val.getStringValue());
        } else if (cellvalue instanceof BoundingBoxType) {
            BoundingBoxType val = (BoundingBoxType) cellvalue;
            java.util.List upper = val.getUpperCorner();
            java.util.List lower = val.getLowerCorner();
            String bboxstr = "";
            bboxstr += val.getCrs() + " : ";
            bboxstr += lower.get(0) + " , " + lower.get(1) + "  ";
            bboxstr += upper.get(0) + " , " + upper.get(1);
            label.setText(bboxstr);
        }

        this.add(label);
        return this;
    }
}
