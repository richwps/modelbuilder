/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.util.HashMap;
import javax.swing.JTabbedPane;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class AppPreferencesDialog extends MbDialog {

    private boolean init;

    private double f;
    private double p;
    private JTabbedPane tabsPanel;

    private HashMap<AppConstants.PREFERENCES_TAB, AbstractPreferencesTab> prefTabs;

    public AppPreferencesDialog(Window parent) {
        super(parent, AppConstants.PREFERENCES_DIALOG_TITLE, (BTN_ID_OK | BTN_ID_CANCEL));
    }

    public void init() {
        if (!this.init) {
            f = TableLayout.FILL;
            p = TableLayout.PREFERRED;
            setLayout(new TableLayout(new double[][]{{f}, {f}}));
            setSize(getStartSize());
            setLocation(getStartLocation());

            // add tabs
            prefTabs = new HashMap<>();
            add(createTabPanel(), "0 0");

            init = true;
        }
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        switch (buttonId) {
            case BTN_ID_CANCEL:
                saveDialogAppearance();
                break;

            case BTN_ID_OK:
                saveDialogAppearance();
                savePreferences();
                break;

        }

        super.handleDialogButton(buttonId); //To change body of generated methods, choose Tools | Templates.
    }


    JTabbedPane createTabPanel() {
        tabsPanel = new JTabbedPane();

        addTab(new PreferencesSemanticProxy(), AppConstants.PREFERENCES_TAB.SEMANTICPROXY);
        addTab(new PreferencesHttpProxy(), AppConstants.PREFERENCES_TAB.HTTPPROXY);

        return tabsPanel;
    }

    private String getTabTitle(AppConstants.PREFERENCES_TAB tab) {
        for (String[] tabData : AppConstants.PREFERENCES_TAB_TITLE) {
            if (tabData[0].equals(tab.name())) {
                return tabData[1];
            }
        }
        return null;
    }

    private void addTab(AbstractPreferencesTab component, AppConstants.PREFERENCES_TAB prefTab) {
        this.prefTabs.put(prefTab, component);
        tabsPanel.add(component, getTabTitle(prefTab));
    }

    /**
     * Save frame location and size.
     */
    void saveDialogAppearance() {
        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_WIDTH.name(), getSize().width);
        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_HEIGHT.name(), getSize().height);

        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_POSITIONX.name(), getLocation().x);
        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_POSITIONY.name(), getLocation().y);
    }

    private Dimension getStartSize() {
        int w = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_WIDTH.name(), AppConstants.PREFERENCES_DIALOG_SIZE.width);
        int h = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_HEIGHT.name(), AppConstants.PREFERENCES_DIALOG_SIZE.height);
        return new Dimension(w, h);
    }

    private Point getStartLocation() {
        int x = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_POSITIONX.name(), AppConstants.PREFERENCES_DIALOG_LOCATION.x);
        int y = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.PREFERENCES_I_POSITIONY.name(), AppConstants.PREFERENCES_DIALOG_LOCATION.y);
        return new Point(x, y);
    }

    private void savePreferences() {
        for (AbstractPreferencesTab tab : this.prefTabs.values()) {
            tab.save();
        }
    }

    private void loadPreferences() {
        for (AbstractPreferencesTab tab : this.prefTabs.values()) {
            tab.load();
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            loadPreferences();
        }
        super.setVisible(b);
    }
}
