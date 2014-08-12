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
 * SemanticProxy preferences tab.
 * @author dziegenh
 */
public class PreferencesSemanticProxy extends AbstractPreferencesTab {

    private JTextField urlField;
    public static final String urlKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();

    public PreferencesSemanticProxy() {
        super();
        
        setLayout(new TableLayout(new double[][] {{f},{p,p,f}}));

        String defaultUrl = AppConstants.SEMANTICPROXY_DEFAULT_URL;
        String url = AppConfig.getConfig().get(urlKey, defaultUrl);
        urlField = createAndAddTextField(url, AppConstants.PREFERENCES_TAB_SP_URL_LABEL, 0);
    }

    @Override
    void save() {
        AppConfig.getConfig().put(urlKey, urlField.getText());
    }

}
