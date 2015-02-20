package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import java.awt.Font;
import javax.swing.JTable;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class VariablesTable extends JTable {

    public static String colHeadings[] = new String[]{
        "Identifier", "Description", "Selected"};

    public static int IDCOL = 0;
    public static int DESCCOL = 1;
    public static int SELCOL = 2;

    public VariablesTable(Object[][] rowData) {
        super();
        this.setModel(new VariablesTableModel(rowData, colHeadings));

        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setRowHeight(54);
        String currentfontname = this.getTableHeader().getFont().getFontName();
        int currentfonsize = this.getTableHeader().getFont().getSize();
        this.getTableHeader().setFont(new Font(currentfontname, Font.BOLD, currentfonsize));

        this.setAutoCreateRowSorter(true);
    }
}
