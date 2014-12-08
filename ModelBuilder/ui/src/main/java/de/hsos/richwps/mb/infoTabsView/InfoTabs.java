package de.hsos.richwps.mb.infoTabsView;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Swing GUI component and boundary class for the info tabs component.
 *
 * @author dziegenh
 */
public class InfoTabs extends JTabbedPane {

//    private LinkedList<String> tabIds;
    private HashMap<String, InfoTabData> tabs;

    private String newline = System.getProperty("line.separator");

    private Color contentTextColor;
    private float fontSize = 11f;

    private Color DEFAULT_FG_COLOR = Color.GRAY;
    private Color HIGHLIGHT_FG_COLOR = Color.BLACK;

    private String iconKeyWarning = "OptionPane.warningIcon";
    private String iconKeyHighLight = "OptionPane.informationIcon";

    public static enum TAB_STATUS {

        NONE, HIGHLIGHT, WARNING
    }

    protected TAB_STATUS defaultTabStatus = TAB_STATUS.NONE;

    public InfoTabs() {
        super();

        tabs = new HashMap<>();

        // Reset tab appearance when it gains the focus.
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setTabStatus(getSelectedIndex(), TAB_STATUS.NONE);
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

    public void addTab(String tabId, String tabTitle) {
        String title = tabTitle.trim();
        String id = tabId.trim();

        // don't replace an existing tab with the same title (id)
        if (!this.tabs.containsKey(id)) {

            // gets the index which will be used for add()
            int index = getTabCount();  
            
            InfoTabData tabData = new InfoTabData(id, getDefaultTabStatus(), index);
            tabs.put(id, tabData);

            // create tab
            InfoTabPanel infoTabPanel = new InfoTabPanel();
            infoTabPanel.setTextColor(contentTextColor);
            infoTabPanel.setFontSize(fontSize);
            add(title, infoTabPanel);

            setTabStatus(getIndex(id), getDefaultTabStatus());
        }
    }

    /**
     * Append a value to a tab component.
     *
     * @param tabId
     * @param value
     */
    public void output(String tabId, Object value) {
        this.output(tabId, value, TAB_STATUS.HIGHLIGHT);
    }

    /**
     * Append a value to a tab component set tab's status.
     *
     * @param tabId
     * @param status
     * @param value
     */
    public void output(String tabId, Object value, TAB_STATUS status) {

        InfoTabPanel infoTabPanel = getInfoTabPanel(tabId);

        infoTabPanel.appendOutput(value.toString());
        infoTabPanel.appendOutput(newline);
        infoTabPanel.scrollToBottom();

        if (!getSelectedComponent().equals(infoTabPanel)) {
            setTabStatus(getIndex(tabId), status);
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
        return this.tabs.get(tabId).getIndex();
    }

    protected void setTabStatus(int index, TAB_STATUS status) {
        // TODO let icons or icon keys be set by app
        Icon icon = null;
        Color color = DEFAULT_FG_COLOR;

        switch (status) {
            case HIGHLIGHT:
                icon = UIManager.getIcon(iconKeyHighLight);
                color = HIGHLIGHT_FG_COLOR;
                break;
            case WARNING:
                icon = UIManager.getIcon(iconKeyWarning);
                color = HIGHLIGHT_FG_COLOR;
                break;
        }

        setForegroundAt(index, color);
        setIconAt(index, icon);
    }

    public void setDefaultTabStatus(TAB_STATUS defaultTabStatus) {
        this.defaultTabStatus = defaultTabStatus;
    }

    public TAB_STATUS getDefaultTabStatus() {
        return defaultTabStatus;
    }

    public void removeTab(String tabId) {
        // when implementing this method: 
        // removing a tab may influence the other tab's indices!
        // thus, the tab indices may have to be updated in the InfoTabData 
        // objects (=values of HashMap this.tabs)

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setIconKeyHighLight(String iconKeyHighLight) {
        this.iconKeyHighLight = iconKeyHighLight;
    }

    public void setIconKeyWarning(String iconKeyWarning) {
        this.iconKeyWarning = iconKeyWarning;
    }

}
