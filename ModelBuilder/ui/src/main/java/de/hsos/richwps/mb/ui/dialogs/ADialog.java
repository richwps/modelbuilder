package de.hsos.richwps.mb.ui.dialogs;

import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.ui.MbDialog;
import de.hsos.richwps.mb.ui.dialogs.components.APanel;
import java.awt.Window;
import java.util.List;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
abstract class ADialog extends MbDialog {

    protected APanel currentPanel;
    /**
     * List of viable endpoints.
     */
    protected List<String> serverids;

    
    /**
     * Interface to WPS/RichWPS-server.
     */
    protected RichWPSProvider provider;
    
    
    public ADialog(Window parent, String title) {
        super(parent, title, MbDialog.BTN_ID_NONE);
    }

    public void showServersPanel() {
    }

    public void showProcessesPanel(final boolean isBackAction) {//noop
    }

    public void showInputsPanel(final boolean isBackAction) {//noop

    }

    public void showOutputsPanel(final boolean isBackAction) {//noop

    }

    public void showResultsPanel() {//noop
    }
}
