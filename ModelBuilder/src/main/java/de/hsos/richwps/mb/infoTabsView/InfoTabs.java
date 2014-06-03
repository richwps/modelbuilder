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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author dziegenh
 */
public class InfoTabs extends JTabbedPane {

    private LinkedList<String> tabIds;

    private String newline = System.getProperty("line.separator");

    private Color contentTextColor;
    private float fontSize = 11f;

    private Color DEFAULT_FG_COLOR = Color.GRAY;
    private Color SELECTED_FG_COLOR = Color.BLACK;

    public InfoTabs() {
        super();

        tabIds = new LinkedList<String>();

        // Reset tab appearance when it gains the focus.
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setForegroundAt(getSelectedIndex(), DEFAULT_FG_COLOR);
            }
        });
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
        this.contentTextColor = color;
    }

    public void addTab(String tabId, String title) {
        tabIds.add(tabId);
        InfoTabPanel infoTabPanel = new InfoTabPanel();
        infoTabPanel.setTextColor(contentTextColor);
        infoTabPanel.setFontSize(fontSize);
        add(title, infoTabPanel);
        setForegroundAt(getIndex(tabId), DEFAULT_FG_COLOR);
    }

    /**
     * Append a value to a tab component.
     *
     * @param tab
     * @param value
     */
    public void output(String tabId, Object value) {
        InfoTabPanel infoTabPanel = getInfoTabPanel(tabId);

        infoTabPanel.appendOutput(value.toString());
        infoTabPanel.appendOutput(newline);

        if (!getSelectedComponent().equals(infoTabPanel)) {
            setForegroundAt(getIndex(tabId), SELECTED_FG_COLOR);
        }
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
