package de.hsos.richwps.mb.app.view;

import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.MbDialog;
import de.hsos.richwps.mb.ui.PerstistableComboBox;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * Dialog for adding, selecting and removing WPS remotes.
 *
 * @author dziegenh
 */
public class ManageRemotesDialog extends MbDialog {

    private PerstistableComboBox remotesComboPanel;

    public ManageRemotesDialog(Window parent) {
        super(parent, AppConstants.MANAGEREMOTES_DIALOG_TITLE, BTN_ID_CANCEL | BTN_ID_OK);
        createContent();
    }

    private void createContent() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.FILL}}));
        remotesComboPanel = new PerstistableComboBox(AppConfig.getConfig(), AppConfig.CONFIG_KEYS.REMOTES_S_URL.name());

        JButton deleteButton = remotesComboPanel.getDeleteButton();
        deleteButton.setText(null);
        deleteButton.setIcon(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));

        getContentPane().add(remotesComboPanel, "0 0");
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        // save items if OK button was clicked
        switch (buttonId) {
            case BTN_ID_OK:
                remotesComboPanel.saveItemsToPreferences();
                break;
        }

        super.handleDialogButton(buttonId);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            remotesComboPanel.loadItemsFromPreferences();
        }

        super.setVisible(visible);
    }

    public String[] getRemotes() {
        JComboBox<String> remotesCombo = remotesComboPanel.getComboBox();

        int numItems = remotesCombo.getItemCount();
        String[] remotes = new String[numItems];

        for (int i = 0; i < numItems; i++) {
            remotes[i] = remotesCombo.getItemAt(i);
        }

        return remotes;
    }

}
