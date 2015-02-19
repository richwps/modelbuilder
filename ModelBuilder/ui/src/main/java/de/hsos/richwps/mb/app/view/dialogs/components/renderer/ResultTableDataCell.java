package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import de.hsos.richwps.mb.Logger;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URL;
import javax.swing.JLabel;
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
        this.setBackground(Color.WHITE);
        Object cellvalue = table.getValueAt(row, column);
        JLabel label = new JLabel();
        if (cellvalue instanceof URL) {
            String httpuri = ((URL) cellvalue).toString();
            String abbString = StringUtils.abbreviateMiddle(httpuri, "[...]", 50);
            label.setText(abbString);
            MouseListener l = new URIMouseListener(httpuri);
            label.addMouseListener(l);
        } else if (cellvalue instanceof LiteralDataType) {
            LiteralDataType val = (LiteralDataType) cellvalue;
            label.setText(val.getStringValue());
        }else if(cellvalue instanceof BoundingBoxType){
            BoundingBoxType val = (BoundingBoxType) cellvalue;
            java.util.List upper = val.getUpperCorner();
            java.util.List lower = val.getUpperCorner();
            String bboxstr="";
            bboxstr += val.getCrs()+" : ";
            bboxstr += lower.get(0)+" , "+lower.get(1)+ "  ";
            bboxstr += upper.get(0)+" , "+upper.get(1);
            label.setText(bboxstr);
        }
        this.add(label);
        return this;
    }
    
    class URIMouseListener implements MouseListener {
        
        private String uri;
        
        public URIMouseListener(final String uri) {
            this.uri = uri;
        }
        
        @Override
        public void mouseClicked(MouseEvent me) {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI(uri));
                        return;
                    } catch (Exception exp) {
                        Logger.log(this.getClass(), "mouseClicked()", exp);
                    }
                }
                
            }
        }
        
        @Override
        public void mousePressed(MouseEvent me) {
            //no op
        }
        
        @Override
        public void mouseReleased(MouseEvent me) {
            //no op
        }
        
        @Override
        public void mouseEntered(MouseEvent me) {
            //no op
        }
        
        @Override
        public void mouseExited(MouseEvent me) {
            //no op
        }
        
    }
}
