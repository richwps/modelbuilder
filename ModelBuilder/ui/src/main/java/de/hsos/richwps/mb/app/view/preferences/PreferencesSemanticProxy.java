package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.PerstistableComboBox;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * SemanticProxy preferences tab.
 *
 * @author dziegenh
 */
public class PreferencesSemanticProxy extends AbstractPreferencesTab {

    private JComboBox<String> urlField;
    public static final String urlKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();
    public static final String urlCountKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_I_URL_COUNT.name();

    private JButton deleteButton;
    private final PerstistableComboBox fieldPanel;

    public PreferencesSemanticProxy() {
        super();

        setLayout(new TableLayout(new double[][]{{f}, {p, p, f}}));

        // create combobox panel
        fieldPanel = new PerstistableComboBox(AppConfig.getConfig(), urlKey);
        fieldPanel.setDefaultValue(AppConstants.SEMANTICPROXY_DEFAULT_URL);

        // Setup delete Button
        deleteButton = fieldPanel.getDeleteButton();
        deleteButton.setText(null);
        deleteButton.setIcon(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));
        deleteButton.setToolTipText("Delete selected URL");

        urlField = fieldPanel.getComboBox();
        urlField.setEditable(true);

        createAndAddComponent(fieldPanel, AppConstants.PREFERENCES_TAB_SP_URL_LABEL, 0);
    }

    @Override
    void save() {
        fieldPanel.saveItemsToPreferences();
    }

    @Override
    void load() {
        fieldPanel.loadItemsFromPreferences();
    }

}
