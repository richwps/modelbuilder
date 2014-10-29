package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.PerstistableComboBox;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * SemanticProxy preferences tab.
 *
 * @author dziegenh
 */
public class PreferencesProcessSources extends AbstractPreferencesTab {

    // Semantic Proxy
    private static final String urlKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name();
    private static final String urlCountKey = AppConfig.CONFIG_KEYS.SEMANTICPROXY_I_URL_COUNT.name();
    private JComboBox<String> urlField;
    private JButton deleteButton;
    private final PerstistableComboBox fieldPanel;

    // Managed remotes
    private static final String REMOTES_DISCOVER_KEY = AppConfig.CONFIG_KEYS.REMOTES_B_DISCOVER_ON_START.name();
    private JCheckBox loadOnStartupBox;

    public PreferencesProcessSources() {
        super();

        setLayout(new TableLayout(new double[][]{{f}, {p, p, 10d, p, p, f}}));

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

        loadOnStartupBox = new JCheckBox(AppConstants.PREFERENCES_TAB_SP_LOADONSTART_LABEL);
        createAndAddComponent(loadOnStartupBox, "Remote Servers", 3);
    }

    @Override
    void save() {
        fieldPanel.saveItemsToPreferences();

        boolean discover = this.loadOnStartupBox.isSelected();
        AppConfig.getConfig().putBoolean(REMOTES_DISCOVER_KEY, discover);
    }

    @Override
    void load() {
        fieldPanel.loadItemsFromPreferences();

        boolean defaultValue = AppConstants.PREFERENCES_DISCOVER_REMOTES_ON_STARTUP_DEFAULT;
        boolean discover = AppConfig.getConfig().getBoolean(REMOTES_DISCOVER_KEY, defaultValue);
        this.loadOnStartupBox.setSelected(discover);
    }

}
