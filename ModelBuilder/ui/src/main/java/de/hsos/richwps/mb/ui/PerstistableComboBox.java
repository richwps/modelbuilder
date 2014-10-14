package de.hsos.richwps.mb.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Combines a ComboBox and a button for deleting the selected item. Provides
 * load and save methods for persisting the items as Java Preferences.
 *
 * @author dziegenh
 */
public class PerstistableComboBox extends ComboBoxWithDeletePanel<String> {

    private Preferences preferences;

    private String persistKeyBase;
    private String persistKeyCount;
    private String persistKeyFormat;

    /**
     * Optional default value which is used if no persisted item is available.
     */
    private String defaultValue;

    /**
     *
     * @param preferences The preferences which persist the ComboBox items.
     * @param preferencesKey The preferences key for the selected item. Also
     * used as a base for generating additional keys.
     */
    public PerstistableComboBox(Preferences preferences, String preferencesKey) {
        this.preferences = preferences;
        this.persistKeyBase = preferencesKey;
        this.persistKeyCount = preferencesKey + "_COUNT";
        this.persistKeyFormat = preferencesKey + "_%d";
    }

    /**
     * Sets the optional default value which is used if no persisted item is
     * vailable.
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Saves all ComboBox items as preferences.
     */
    public void saveItemsToPreferences() {
        Object selectedItem = getComboBox().getSelectedItem();
        if (null == selectedItem) {
            selectedItem = "";
        }

        // save selected item
        String selectedItemString = ((String) selectedItem).trim();
        preferences.put(this.persistKeyBase, selectedItemString);

        // save other items
        int numItems = getComboBox().getItemCount();
        for (int i = 0; i < numItems; i++) {
            String key = String.format(this.persistKeyFormat, i);
            preferences.put(key, getComboBox().getItemAt(i));
        }

        // Save the item count for later loading
        preferences.putInt(this.persistKeyCount, numItems);
    }

    /**
     * Loads previously saved items from the preferences into the ComboBox.
     */
    public void loadItemsFromPreferences() {
        String currentItem = preferences.get(this.persistKeyBase, defaultValue);
        int count = preferences.getInt(this.persistKeyCount, 0);

        boolean currentItemAvailable = null != currentItem && !currentItem.isEmpty();
        boolean currentItemAdded = !currentItemAvailable;

        // update delete button state
        boolean deleteButtonDisabled = (!currentItemAvailable);
        deleteButton.setEnabled(!deleteButtonDisabled);

        getComboBox().removeAllItems();

        // load and add persisted items
        List<String> loadedItems = new LinkedList<>();
        for (int c = 0; c < count; c++) {
            String key = String.format(this.persistKeyFormat, c);
            String value = preferences.get(key, "").trim();

            if (null != value) {
                // avoid duplicates
                if (!loadedItems.contains(value)) {
                    getComboBox().addItem(value);
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
            getComboBox().addItem(currentItem);
        }

        // Select the currently used item
        getComboBox().getModel().setSelectedItem(currentItem);
    }

}
