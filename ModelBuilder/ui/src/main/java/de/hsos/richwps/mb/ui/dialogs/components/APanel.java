package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.richWPS.entity.IRequest;

import javax.swing.JPanel;

/**
 * A dialog panel that can be displayed within Execute/Deploy/Test-dialogs.
 *
 * @author dalcacer
 * @version 0.0.2
 * @see de.hsos.richwps.mb.ui.dialogs.ExecuteModelDialog;
 * @see de.hsos.richwps.mb.ui.dialogs.ExecuteDialog;
 * @see de.hsos.richwps.mb.ui.dialogs.UndeployDialog;
 * @see de.hsos.richwps.mb.ui.dialogs.TestModelDialog;
 */
public abstract class APanel extends JPanel {

    /**
     * Update the current request, before getting it.
     *
     * @see ADialogPanel#getRequest().
     */
    public void updateRequest() {
        //noop
    }

    /**
     * Get the current request.
     *
     * @return IRequest a describe/execute-request.
     * @see ADialogPanel#updateRequest().
     */
    public IRequest getRequest() {
        return null;
    }

    /**
     * Validate the panels' contents.
     *
     * @return indicator if panel is valid.
     */
    public boolean isValidInput() {
        return false;
    }

    /**
     * Prepare components for visualisation (besides initcomps).
     */
    public void prepare() {
        //noop
    }

    ;
    /**
     * Visualize components.
     */
    public void visualize() {
        //noop
    }
;
}
