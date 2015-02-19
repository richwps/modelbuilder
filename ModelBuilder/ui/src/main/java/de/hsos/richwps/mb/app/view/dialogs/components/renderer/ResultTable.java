/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import de.hsos.richwps.mb.app.view.dialogs.components.listener.ResultTableMouseListener;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import javax.swing.JTable;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTable extends JTable{
       String colHeadings[] = new String[]{
            "Typ", "ID", "Data", "Action"};

    public ResultTable(Object [][] rowData){
             super(new ResultTableModel(rowData));

            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            this.setRowHeight(54);
            String currentfontname = this.getTableHeader().getFont().getFontName();
            int currentfonsize = this.getTableHeader().getFont().getSize();
            this.getTableHeader().setFont(new Font(currentfontname, Font.BOLD, currentfonsize));
            ResultTableTypeCell tcr = new ResultTableTypeCell();
            this.getColumnModel().getColumn(0).setCellRenderer(tcr);
            this.getColumnModel().getColumn(0).setMinWidth(28);
            this.getColumnModel().getColumn(0).setPreferredWidth(50);

            this.getColumnModel().getColumn(1).setMinWidth(50);
            ResultTableDataCell dcr = new ResultTableDataCell();

            this.getColumnModel().getColumn(2).setCellRenderer(dcr);
            this.getColumnModel().getColumn(2).setMinWidth(350);

            ResultTableActionCell ace = new ResultTableActionCell();
            this.getColumnModel().getColumn(3).setCellRenderer(ace);
            this.getColumnModel().getColumn(3).setMinWidth(120);

            MouseAdapter adap = new ResultTableMouseListener(this);
            this.addMouseListener(adap);
    }
}
