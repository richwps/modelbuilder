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
public class ResultTable extends JTable {

    public static String colHeadings[] = new String[]{
        "Datatype", "Identifier", "Data", "Action"};
    public static final int TYPCOL = 0;
    public static final int IDCOL = 1;
    public static final int DATACOL = 2;
    public static final int ACTIONCOL = 3;

    public ResultTable(Object[][] rowData) {
        super(new ResultTableModel(rowData));

        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setRowHeight(54);
        String currentfontname = this.getTableHeader().getFont().getFontName();
        int currentfonsize = this.getTableHeader().getFont().getSize();
        this.getTableHeader().setFont(new Font(currentfontname, Font.BOLD, currentfonsize));

        ResultTableTypeCell tcr = new ResultTableTypeCell();
        this.getColumnModel().getColumn(ResultTable.TYPCOL).setCellRenderer(tcr);
        this.getColumnModel().getColumn(ResultTable.TYPCOL).setMinWidth(28);
        this.getColumnModel().getColumn(ResultTable.TYPCOL).setPreferredWidth(50);

        ResultTableIdentifierCell rti = new ResultTableIdentifierCell();
        this.getColumnModel().getColumn(ResultTable.IDCOL).setCellRenderer(rti);
        this.getColumnModel().getColumn(ResultTable.IDCOL).setMinWidth(50);

        ResultTableDataCell dcr = new ResultTableDataCell();
        this.getColumnModel().getColumn(ResultTable.DATACOL).setCellRenderer(dcr);
        this.getColumnModel().getColumn(ResultTable.DATACOL).setMinWidth(325);

        ResultTableActionCell ace = new ResultTableActionCell();
        this.getColumnModel().getColumn(ResultTable.ACTIONCOL).setCellRenderer(ace);
        this.getColumnModel().getColumn(ResultTable.ACTIONCOL).setMinWidth(150);

        MouseAdapter adap = new ResultTableMouseListener(this);
        this.addMouseListener(adap);

         this.setAutoCreateRowSorter(true);
    }
}
