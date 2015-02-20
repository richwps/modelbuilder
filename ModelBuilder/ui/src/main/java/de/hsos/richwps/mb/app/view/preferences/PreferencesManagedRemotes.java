package de.hsos.richwps.mb.app.view.preferences;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.ui.ListWithButtons;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import layout.TableLayout;

/**
 * Preferences tab for adding, selecting and removing WPS remotes..
 *
 * @author dziegenh
 */
public class PreferencesManagedRemotes extends AbstractPreferencesTab {

    private ListWithButtons<String> remotesPanel;

    private final String persistKeyBase;
    private final String persistKeyCount;
    private final String persistKeyFormat;

    private final Preferences preferences;

    private final String inputMessage = "";

    public PreferencesManagedRemotes(Window parent) {
        super();

        createContent(parent);

        this.preferences = AppConfig.getConfig();
        this.persistKeyBase = AppConfig.CONFIG_KEYS.REMOTES_S_URL.name();
        this.persistKeyCount = this.persistKeyBase + "_COUNT";
        this.persistKeyFormat = this.persistKeyBase + "_%d";
    }

    private void createContent(final Window parent) {
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED}}));

        remotesPanel = new ListWithButtons<>(parent);

        remotesPanel.getAddItemButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String remoteInput = (String) JOptionPane.showInputDialog(parent,
                        inputMessage
                );

                if (null != remoteInput && !remoteInput.isEmpty()) {
                    remotesPanel.addItemToList(remoteInput);
                }
            }
        });

        remotesPanel.getEditItemButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedIndex = remotesPanel.getSelectedIndex();

                String remoteInput = (String) JOptionPane.showInputDialog(parent,
                        inputMessage,
                        remotesPanel.getSelectedItem()
                );

                if (null != remoteInput && !remoteInput.isEmpty()) {
                    remotesPanel.setItemAt(selectedIndex, remoteInput);
                }
            }
        }
        );

        add(remotesPanel, "0 0");
    }

    @Override
    void save() {
        saveItemsToPreferences();
    }

    @Override
    void load() {
        loadItemsFromPreferences();
    }

    /**
     * Saves all ComboBox items as preferences.
     */
    public void saveItemsToPreferences() {
        String selectedItem = remotesPanel.getSelectedItem();
        if (null == selectedItem) {
            selectedItem = "";
        }

        // save selected item
        String selectedItemString = ((String) selectedItem).trim();
        AppConfig.getConfig().put(this.persistKeyBase, selectedItemString);

        // save other items
        List<String> items = remotesPanel.getAllItems();
        int numItems = items.size();
        for (int i = 0; i < numItems; i++) {
            String key = String.format(this.persistKeyFormat, i);
            AppConfig.getConfig().put(key, items.get(i));
        }

        // Save the item count for later loading
        AppConfig.getConfig().putInt(this.persistKeyCount, numItems);
    }

    /**
     * Loads previously saved items from the preferences into the ComboBox.
     */
    public void loadItemsFromPreferences() {
        String currentItem = preferences.get(this.persistKeyBase, "");
        int count = preferences.getInt(this.persistKeyCount, 0);

        boolean currentItemAvailable = null != currentItem && !currentItem.isEmpty();
        boolean currentItemAdded = !currentItemAvailable;

        remotesPanel.clear();

        // load and add persisted items
        List<String> loadedItems = new LinkedList<>();
        for (int c = 0; c < count; c++) {
            String key = String.format(this.persistKeyFormat, c);
            String value = preferences.get(key, "").trim();

            if (null != value) {
                // avoid duplicates
                if (!loadedItems.contains(value)) {
                    loadedItems.add(value);
                }

                // remember if the currently used item has been added
                if (value.equals(currentItem)) {
                    currentItemAdded = true;
                }
            }
        }

        // assure the list contains the current item after loading
        if (!currentItemAdded) {
            loadedItems.add(currentItem);
        }

        // set items
        remotesPanel.init(loadedItems);

        // Select the currently used item
        remotesPanel.selectItem(currentItem);
    }
}
