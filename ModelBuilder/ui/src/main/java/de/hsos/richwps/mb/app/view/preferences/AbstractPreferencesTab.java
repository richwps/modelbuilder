/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.app.view.preferences;

import java.awt.Component;
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

    abstract void load();


    protected JTextField createAndAddTextField(String text, String caption, int layoutY) {
        JTextField field = new JTextField(text);
        createAndAddComponent(field, caption, layoutY);

        return field;
    }

    /**
     * Creates a label for the caption and adds it to the tab together with the component.
     * Returns the created label.
     * 
     * @param component the component for which the label is
     * @param caption text for the caption label
     * @param layoutY start row (default: 0)
     * @return
     */
    protected JLabel createAndAddComponent(Component component, String caption, int layoutY) {
        JLabel fieldLabel = new JLabel(caption);

        add(fieldLabel, "0 "+layoutY);
        layoutY++;
        add(component, "0 "+layoutY);

        return fieldLabel;
    }

}
