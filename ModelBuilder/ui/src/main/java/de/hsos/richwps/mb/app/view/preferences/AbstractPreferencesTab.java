/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.app.view.preferences;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
abstract class AbstractPreferencesTab extends JPanel {

    protected double f = TableLayout.FILL;
    protected double p = TableLayout.PREFERRED;

    public AbstractPreferencesTab() {
        setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    abstract void save();

    protected JTextField createAndAddTextField(String text, String caption, int layoutY) {
        JTextField field = new JTextField(text);
        JLabel fieldLabel = new JLabel(caption);

        add(fieldLabel, "0 "+layoutY);
        layoutY++;
        add(field, "0 "+layoutY);

        return field;
    }

}
