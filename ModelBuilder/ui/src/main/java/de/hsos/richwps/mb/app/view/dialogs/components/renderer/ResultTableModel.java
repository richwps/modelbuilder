package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class ResultTableModel extends AbstractTableModel {

    String colHeadings[] = new String[]{
        "Typ", "ID", "Data", "Action"};

    Object[][] data;

    public ResultTableModel(Object[][] rowdata) {
        this.data = rowdata;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return colHeadings.length;
    }

    @Override
    public String getColumnName(int col) {
        return colHeadings[col];
    }

    @Override
    public Object getValueAt(int i, int i1) {
        return data[i][i1];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
