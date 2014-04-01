/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.infoTabsView;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/**
 *
 * @author dziegenh
 */
public class InfoTabs extends JTabbedPane {

    public static enum TABS {SEMANTIC_PROXY, GRAPH}

    public InfoTabs() {
        super();
    }

    public void addTab(String title) {
        // TODO mocked; create and add content component (e.g label or a disabled textArea)
        add(title, new JLabel(title + " tab content"));
    }

    /**
     * Append a value to a tab component.
     * @param tab
     * @param value
     */
    public void output(TABS tab, Object value) {
        // TODO just mocked!
        Component c = getComponentAt(getIndex(tab));
        if(c instanceof JLabel) {
            JLabel l = (JLabel) c;
            StringBuilder sb = new StringBuilder(l.getText());
            sb.append(value.toString());
            l.setText(sb.toString());
        }
    }


    /**
     * Get current text of a specific tab.
     * @param tab
     * @return
     */
    public String getOutput(TABS tab) {
        // TODO just mocked!
        JLabel l = (JLabel) getComponentAt(getIndex(tab));
        return l.getText();
    }



    /**
     * Returns index of tab or -1 if there's no such tab.
     * @param tab
     * @return
     */
    protected int getIndex(TABS tab) {
        int idx = 0;
        for(InfoTabs.TABS tabName : InfoTabs.TABS.values()) {
            if(tabName.equals(tab))
                return idx;
            idx++;
        }

        return -1;
    }

}
