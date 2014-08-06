package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import javax.swing.JPanel;

/**
 *
 * @author dalcacer
 */
public abstract class ADialogPanel extends JPanel {

    public void updateDTO() {
    }

    public ExecuteRequestDTO getDTO() {
        return null;
    }
}
