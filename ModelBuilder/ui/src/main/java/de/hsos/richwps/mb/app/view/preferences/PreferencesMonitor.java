package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 * Monitor preferences tab.
 *
 * @author dziegenh
 */
public class PreferencesMonitor extends AbstractPreferencesTab {

    private final String urlKey = AppConfig.CONFIG_KEYS.MONITOR_S_URL.name();

    private final JTextField urlField;

    public PreferencesMonitor() {

        setLayout(new TableLayout(new double[][]{{f}, {p, p, p, p, f}}));

        urlField = createAndAddTextField("", AppConstants.PREFERENCES_TAB_MONITOR_URL_LABEL, 0);
    }

    @Override
    void save() {
        AppConfig.getConfig().put(urlKey, urlField.getText());
    }

    @Override
    void load() {
        String host = AppConfig.getConfig().get(urlKey, AppConstants.MONITOR_DEFAULT_URL);
        urlField.setText(host);
    }

}
