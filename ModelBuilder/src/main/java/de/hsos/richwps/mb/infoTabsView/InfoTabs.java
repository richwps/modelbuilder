/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.infoTabsView;

import java.util.LinkedList;
import javax.swing.JTabbedPane;

/**
 *
 * @author dziegenh
 */
public class InfoTabs extends JTabbedPane {

//    public static enum TABS {SEMANTIC_PROXY, GRAPH}

    private LinkedList<String> tabIds;

    private String newline = System.getProperty("line.separator");


    public InfoTabs() {
        super();

        tabIds = new LinkedList<String>();
    }

    public void addTab(String tabId, String title) {
        tabIds.add(tabId);
        add(title, new InfoTabPanel());
    }

    /**
     * Append a value to a tab component.
     * @param tab
     * @param value
     */
    public void output(String tabId, Object value) {
        getInfoTabPanel(tabId).appendOutput(value.toString());
        getInfoTabPanel(tabId).appendOutput(newline);
    }


    /**
     * Get current text of a specific tab.
     * @param tab
     * @return
     */
    public String getOutput(String tabId) {
        return getInfoTabPanel(tabId).getOutput();
    }

    InfoTabPanel getInfoTabPanel(String tabId) {
        return (InfoTabPanel) getComponentAt(getIndex(tabId));
    }


    /**
     * Returns index of tab or -1 if there's no such tab.
     * @param tabId
     * @return
     */
    protected int getIndex(String tabId) {
        return tabIds.indexOf(tabId);
    }

}
