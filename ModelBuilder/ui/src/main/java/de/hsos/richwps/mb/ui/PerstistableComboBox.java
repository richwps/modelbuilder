package de.hsos.richwps.mb.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

public class PerstistableComboBox extends ComboBoxWithDeletePanel<String> {

    private Preferences preferences;

    private String persistKeyBase;
    private String persistKeyCount;
    private String persistKeyFormat;
    private String defaultValue;

    public PerstistableComboBox(Preferences preferences, String persistKeyBase, String defaultValue) {
        this.preferences = preferences;
        this.persistKeyBase = persistKeyBase;
        this.persistKeyCount = persistKeyBase + "_COUNT";
        this.persistKeyFormat = persistKeyBase + "_%d";
        this.defaultValue = defaultValue;
    }

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

    public void loadItemsFromPreferences() {
        String currentItem = preferences.get(this.persistKeyBase, defaultValue);
        int count = preferences.getInt(this.persistKeyCount, 0);

        boolean currentItemAvailable = null != currentItem && !currentItem.isEmpty();
        boolean currentItemAdded = !currentItemAvailable;

        // update delete button state
        boolean deleteButtonDisabled = (!currentItemAvailable);
        deleteButton.setEnabled(!deleteButtonDisabled);

        getComboBox().removeAllItems();

        // load and add url history
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

                // remember if the currently used url has been added
                if (value.equals(currentItem)) {
                    currentItemAdded = true;
                }
            }
        }

        // assure the list contains the current URL after loading
        if (!currentItemAdded) {
            getComboBox().addItem(currentItem);
        }

        // Select the currently used URL
        getComboBox().getModel().setSelectedItem(currentItem);
    }

}
