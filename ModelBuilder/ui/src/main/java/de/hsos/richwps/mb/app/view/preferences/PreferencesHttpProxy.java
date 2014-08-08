/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 * Http proxy preferences tab.
 * @author dziegenh
 */
public class PreferencesHttpProxy extends AbstractPreferencesTab {

    private final String hostKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_HOST.name();
    private final String portKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_PORT.name();

    private final JTextField hostField;
    private final JTextField portField;

    public PreferencesHttpProxy() {

        setLayout(new TableLayout(new double[][]{{f}, {p, p, p, p, f}}));

        String host = AppConfig.getConfig().get(hostKey, "");
        hostField = createAndAddTextField(host, AppConstants.PREFERENCES_TAB_HTTP_HOST_LABEL, 0);

        String port = AppConfig.getConfig().get(portKey, "");
        portField = createAndAddTextField(port, AppConstants.PREFERENCES_TAB_HTTP_PORT_LABEL, 2);
    }

    @Override
    void save() {
        saveSystemProperty("http.proxyHost", hostField.getText(), hostKey);
        saveSystemProperty("http.proxyPort", portField.getText(), portKey);
    }

    private void saveSystemProperty(String property, String value, String configKey) {
        if(null != value && value.isEmpty()) {
            value = null;
        }
        
        System.setProperty(property, value);
        AppConfig.getConfig().put(configKey, value);
    }

}
