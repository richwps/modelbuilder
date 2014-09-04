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
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
        super(parent, AppConstants.PREFERENCES_DIALOG_TITLE);
    }

    public void init() {
        if (!this.init) {
            f = TableLayout.FILL;
            p = TableLayout.PREFERRED;
            setLayout(new TableLayout(new double[][]{{f}, {f, p}}));
            setSize(getStartSize());
            setLocation(getStartLocation());

            // save config on close
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    saveDialogAppearance();
                }
            });

            // add tabs
            prefTabs = new HashMap<>();
            add(createTabPanel(),"0 0");
            add(createBottomButtons(), "0 1");

            init = true;
        }
    }

    JTabbedPane createTabPanel() {
        tabsPanel = new JTabbedPane();

        addTab(new PreferencesSemanticProxy(), AppConstants.PREFERENCES_TAB.SEMANTICPROXY);
        addTab(new PreferencesHttpProxy(), AppConstants.PREFERENCES_TAB.HTTPPROXY);

        return tabsPanel;
    }

    private String getTabTitle(AppConstants.PREFERENCES_TAB tab) {
        for(String[] tabData : AppConstants.PREFERENCES_TAB_TITLE) {
            if(tabData[0].equals(tab.name()))
                return tabData[1];
        }
        return null;
    }

    private void addTab(AbstractPreferencesTab component, AppConstants.PREFERENCES_TAB prefTab) {
        this.prefTabs.put(prefTab, component);
        tabsPanel.add(component, getTabTitle(prefTab));
    }

    JPanel createBottomButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[][]{{f, p, p}, {p}}));

        JButton btnCancel = new JButton(AppConstants.PREFERENCES_DIALOG_BTN_CANCEL);
        btnCancel.setPreferredSize(AppConstants.DIALOG_BTN_SIZE);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton btnOk = new JButton(AppConstants.PREFERENCES_DIALOG_BTN_OK);
        btnOk.setFont(btnOk.getFont().deriveFont(Font.BOLD));
        btnOk.setPreferredSize(AppConstants.DIALOG_BTN_SIZE);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreferences();
                dispose();
            }
        });

        panel.add(new JLabel(""), "0 0");
        panel.add(btnCancel, "1 0");
        panel.add(btnOk, "2 0");

        return panel;
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
        for(AbstractPreferencesTab tab : this.prefTabs.values()) {
            tab.save();
        }
    }
}
