package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.ListWithButtons;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import layout.TableLayout;

/**
 * Dialog for adding, selecting and removing WPS remotes.
 *
 * @author dziegenh
 */
public class ManageRemotesDialog extends MbDialog {

//    private PerstistableComboBox remotesComboPanel;
    private ListWithButtons<String> remotesPanel;

    private String persistKeyBase;
    private String persistKeyCount;
    private String persistKeyFormat;

    private String[] exportRemotes;

    private Preferences preferences;

    private final String inputMessage = "";

    public ManageRemotesDialog(final Window parent) {
        super(parent, AppConstants.MANAGEREMOTES_DIALOG_TITLE, BTN_ID_CANCEL | BTN_ID_OK);
        createContent();
        setSize(400, 160);

        this.preferences = AppConfig.getConfig();
        this.persistKeyBase = AppConfig.CONFIG_KEYS.REMOTES_S_URL.name();
        this.persistKeyCount = this.persistKeyBase + "_COUNT";
        this.persistKeyFormat = this.persistKeyBase + "_%d";
    }

    private void createContent() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED}}));

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

        getContentPane().add(remotesPanel, "0 0");
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        // save items if OK button was clicked
        switch (buttonId) {
            case BTN_ID_OK:
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                saveItemsToPreferences();
                exportRemotes();
                break;
        }

        super.handleDialogButton(buttonId);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            exportRemotes = null;
            loadItemsFromPreferences();
        }

        super.setVisible(visible);
    }

    private void exportRemotes() {
        exportRemotes = remotesPanel.getAllItems().toArray(new String[]{});
    }

    public String[] getRemotes() {
        return exportRemotes;
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
