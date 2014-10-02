/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import layout.TableLayout;

/**
 * SemanticProxy preferences tab.
 *
 * @author dziegenh
 */
public class PreferencesSemanticProxy extends AbstractPreferencesTab {

    private JComboBox<String> urlField;
    private ComboBoxModel<String> urlFieldModel = new DefaultComboBoxModel<String>();
    public static final String urlKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();
    public static final String urlCountKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_I_URL_COUNT.name();

    private String url_keys_format = urlKey + "_%d";

    public PreferencesSemanticProxy() {
        super();

        setLayout(new TableLayout(new double[][]{{f}, {p, p, f}}));
        urlField = new JComboBox<>(urlFieldModel);
        urlField.setEditable(true);

        createAndAddComponent(urlField, AppConstants.PREFERENCES_TAB_SP_URL_LABEL, 0);
    }

    @Override
    void save() {
        String url = ((String) urlField.getSelectedItem()).trim();
        AppConfig.getConfig().put(urlKey, url);

        // save url history
        int numItems = urlField.getItemCount();
        for (int i = 0; i < numItems; i++) {
            String key = String.format(url_keys_format, i);
            AppConfig.getConfig().put(key, urlField.getItemAt(i));
        }

        // Save the size of the url history for later loading
        AppConfig.getConfig().putInt(urlCountKey, numItems);
    }

    @Override
    void load() {
        String defaultUrl = AppConstants.SEMANTICPROXY_DEFAULT_URL;
        String currentUrl = AppConfig.getConfig().get(urlKey, defaultUrl);
        int count = AppConfig.getConfig().getInt(urlCountKey, 0);
        
        boolean currentUrlAvailable = null != currentUrl && !currentUrl.isEmpty();
        boolean currentUrlAdded = !currentUrlAvailable;

        urlField.removeAllItems();

        // load and add url history
        List<String> loadedUrls = new LinkedList<>();
        for (int c = 0; c < count; c++) {
            String key = String.format(url_keys_format, c);
            String value = AppConfig.getConfig().get(key, "").trim();

            // avoid duplicates
            if (!loadedUrls.contains(value)) {
                urlField.addItem(value);
                loadedUrls.add(value);
            }

            // remember if the currently used url has been added
            if (value.equals(currentUrl)) {
                currentUrlAdded = true;
            }
        }

        // assure the list contains the current URL after loading
        if (!currentUrlAdded) {
            urlField.addItem(currentUrl);
        }

        // Select the currently used URL
        urlField.getModel().setSelectedItem(currentUrl);
    }

}
