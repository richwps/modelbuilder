package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.JTabbedPane;
import layout.TableLayout;

/**
 * Dialog containing tabs for the configuration of app components (e.g.
 * SemanticProxy).
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

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    parent.setCursor(Cursor.getDefaultCursor());
                }
            });

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
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                saveDialogAppearance();
                savePreferences();
                break;

        }

        super.handleDialogButton(buttonId);
    }

    /**
     * Creates a tabbed pane and adds specific tabs for all
     * AppConstants.PREFERENCES_TAB values.
     *
     * @return
     */
    JTabbedPane createTabPanel() {
        tabsPanel = new JTabbedPane();

        AbstractPreferencesTab tab = null;
        for (AppConstants.PREFERENCES_TAB prefTab : AppConstants.PREFERENCES_TAB.values()) {
            tab = null;

            switch (prefTab) {
                case PROCESSSOURCES:
                    tab = new PreferencesProcessSources();
                    break;
                case HTTPPROXY:
                    tab = new PreferencesHttpProxy();
                    break;
            }

            // only add existing tabs (=no empty tabs)
            if (null != tab) {
                addTab(tab, prefTab);
            }
        }

        return tabsPanel;
    }

    protected String getTabTitle(AppConstants.PREFERENCES_TAB tab) {
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

    /**
     * Sets the dialog visible and shows the given tab. If tab is null, the
     * currently shown tab is not changed.
     *
     * @param tab
     */
    public void showTab(AppConstants.PREFERENCES_TAB tab) {
        if (null != tab) {
            tabsPanel.setSelectedComponent(prefTabs.get(tab));
        }

        setVisible(true);
    }

}
