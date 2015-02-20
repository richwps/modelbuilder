package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.PerstistableComboBox;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * Monitor preferences tab.
 *
 * @author dziegenh
 */
public class PreferencesMonitor extends AbstractPreferencesTab {

    private final String urlKey = AppConfig.CONFIG_KEYS.MONITOR_S_URL.name();
    
    private JComboBox<String> urlField;
    
    private JButton deleteButton;
    
    private final PerstistableComboBox fieldPanel;

    public PreferencesMonitor() {
        super();

        setLayout(new TableLayout(new double[][]{{f}, {p, p, 10d, p, p, f}}));

        // create combobox panel
        fieldPanel = new PerstistableComboBox(AppConfig.getConfig(), urlKey);
        fieldPanel.setDefaultValue(AppConstants.MONITOR_DEFAULT_URL);

        // Setup delete Button
        deleteButton = fieldPanel.getDeleteButton();
        deleteButton.setText(null);
        deleteButton.setIcon(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));
        deleteButton.setToolTipText("Delete selected URL");
        deleteButton.setPreferredSize(new Dimension(20, 20));

        urlField = fieldPanel.getComboBox();
        urlField.setEditable(true);

        createAndAddComponent(fieldPanel, AppConstants.PREFERENCES_TAB_MONITOR_URL_LABEL, 0);
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
