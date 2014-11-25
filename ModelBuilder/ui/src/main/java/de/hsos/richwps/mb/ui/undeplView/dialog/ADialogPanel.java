package de.hsos.richwps.mb.ui.undeplView.dialog;

import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import javax.swing.JPanel;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public abstract class ADialogPanel extends JPanel {

    /**
     * Update request, before getting it.
     */
    public void updateRequest() {
    }

    /**
     * Gets request.
     *
     * @return request.
     */
    public DescribeRequest getRequest() {
        return null;
    }

    /**
     * Validate the panels contents.
     *
     * @return indicator if panel is valid.
     */
    public boolean isValidInput() {
        return false;
    }
}
