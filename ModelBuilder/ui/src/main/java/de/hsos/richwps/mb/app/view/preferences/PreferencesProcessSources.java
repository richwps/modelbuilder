package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.PerstistableComboBox;
import java.awt.Dimension;
import java.awt.Window;
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

    private JComboBox<String> urlField;

    private JButton deleteButton;

    private final PerstistableComboBox fieldPanel;

    // Managed remotes
    private static final String REMOTES_DISCOVER_KEY = AppConfig.CONFIG_KEYS.REMOTES_B_DISCOVER_ON_START.name();
    private JCheckBox loadOnStartupBox;
    private final ManagedRemotesPanel remotesPanel;

    public PreferencesProcessSources(Window parent) {
        super();

        setLayout(new TableLayout(new double[][]{{f}, {p, p, 10d, p, p, p, p, f}}));

        // create combobox panel
        fieldPanel = new PerstistableComboBox(AppConfig.getConfig(), urlKey);
        fieldPanel.setDefaultValue(AppConstants.SEMANTICPROXY_DEFAULT_URL);

        // Setup delete Button
        deleteButton = fieldPanel.getDeleteButton();
        deleteButton.setText(null);
        deleteButton.setIcon(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));
        deleteButton.setToolTipText("Delete selected URL");
        deleteButton.setPreferredSize(new Dimension(20, 20));

        urlField = fieldPanel.getComboBox();
        urlField.setEditable(true);

        createAndAddComponent(fieldPanel, AppConstants.PREFERENCES_TAB_SP_URL_LABEL, 0);

        remotesPanel = new ManagedRemotesPanel(parent);
        createAndAddComponent(remotesPanel, "Remote Servers", 3);

        loadOnStartupBox = new JCheckBox(AppConstants.PREFERENCES_TAB_SP_LOADONSTART_LABEL);
        add(loadOnStartupBox, "0 5");
//        createAndAddComponent(loadOnStartupBox, "Remote Servers", 4);
    }

    @Override
    void save() {
        fieldPanel.saveItemsToPreferences();
        remotesPanel.saveItemsToPreferences();

        boolean discover = this.loadOnStartupBox.isSelected();
        AppConfig.getConfig().putBoolean(REMOTES_DISCOVER_KEY, discover);
    }

    @Override
    void load() {
        fieldPanel.loadItemsFromPreferences();
        remotesPanel.loadItemsFromPreferences();

        boolean defaultValue = AppConstants.PREFERENCES_DISCOVER_REMOTES_ON_STARTUP_DEFAULT;
        boolean discover = AppConfig.getConfig().getBoolean(REMOTES_DISCOVER_KEY, defaultValue);
        this.loadOnStartupBox.setSelected(discover);
    }

}
