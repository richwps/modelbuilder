package de.hsos.richwps.mb.app.view.dialogs.components.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;

/**
 *
 * @author dalcacer
 * @verision 0.0.1
 */
public class ResultTableMouseListener  extends MouseAdapter {

        private final JTable table;

        public ResultTableMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
            int row = e.getY() / table.getRowHeight(); //get the row of the button

            /*Checking the row or column is valid or not*/
            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {

                    ((JButton) value).getModel().setPressed(true);
                    ((JButton) value).getModel().setArmed(true);
                    ((JButton) value).doClick();                //perform action
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                    ((JButton) value).getModel().setPressed(false);
                    ((JButton) value).getModel().setArmed(false);

                }
            }
        }
    }
