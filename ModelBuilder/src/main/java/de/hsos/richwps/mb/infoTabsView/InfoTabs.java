/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.infoTabsView;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JTabbedPane;

/**
 *
 * @author dziegenh
 */
public class InfoTabs extends JTabbedPane {

    private LinkedList<String> tabIds;

    private String newline = System.getProperty("line.separator");

    private Color textColor;
    private float fontSize = 11f;

    public InfoTabs() {
        super();

        tabIds = new LinkedList<String>();
    }

    public void setFontSize(float size) {
        this.fontSize = size;
        for (Component c : getComponents()) {
            if (c instanceof InfoTabPanel) {
                ((InfoTabPanel) c).setFontSize(size);
            }
        }
    }

    /**
     * Sets the text color of new tabs.
     *
     * @param color
     */
    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void addTab(String tabId, String title) {
        tabIds.add(tabId);
        InfoTabPanel infoTabPanel = new InfoTabPanel();
        infoTabPanel.setTextColor(textColor);
        infoTabPanel.setFontSize(fontSize);
        add(title, infoTabPanel);
    }

    /**
     * Append a value to a tab component.
     *
     * @param tab
     * @param value
     */
    public void output(String tabId, Object value) {
        getInfoTabPanel(tabId).appendOutput(value.toString());
        getInfoTabPanel(tabId).appendOutput(newline);
    }

    /**
     * Get current text of a specific tab.
     *
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
     *
     * @param tabId
     * @return
     */
    protected int getIndex(String tabId) {
        return tabIds.indexOf(tabId);
    }

}
