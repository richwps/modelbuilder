package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class VariablesTableModel extends DefaultTableModel {

    public VariablesTableModel(Object[][] rowData, String[] colheadings) {
        super(rowData, colheadings);
    }

    /*@Override
    public boolean isCellEditable(int row, int col) {
        if (col == 2) {
            return true;
        }
        return false;
    }*/

    @Override
    public Class getColumnClass(int column) {
        if (column == VariablesTable.SELCOL) {
            return Boolean.class;
        }
        return String.class;
    }
}
