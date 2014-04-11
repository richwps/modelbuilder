/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.ui;

import java.awt.Component;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class TitledComponent extends JPanel {

    private ComponentTitle componentTitle;

    /**
     * Creates a {@link ComponentTitle} and places it above the component.
     * If the title is null, no ComponentTitle will be created.
     */
    public TitledComponent(String title, Component component) {
        if (null != title) {
            componentTitle = new ComponentTitle(title);
            // TODO move magic number to config/constants
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {25, TableLayout.FILL}}));
            add(componentTitle, "0 0");
            add(component, "0 1");

        } else {
            setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
            add(component, "0 0");
        }
    }

    public void setTitle(String title) {
        if(null == componentTitle)
            componentTitle = new ComponentTitle(title);
        else
            componentTitle.setText(title);
    }

    public void setTitleItalic() {
        componentTitle.setItalic();
    }

    public void setTitleBold() {
        componentTitle.setBold();
    }


}
