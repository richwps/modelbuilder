/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class TitledComponent extends JPanel {

    public TitledComponent(String title, JComponent component) {
        ComponentTitle componentTitle = new ComponentTitle(title);
        // TODO move magic numbers to config/constants
        setLayout(new TableLayout(new double[][] {{TableLayout.FILL},{25, TableLayout.FILL}}));
        add(componentTitle, "0 0");
        add(component, "0 1");
    }


}
