package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import javax.swing.JPanel;

/**
 *
 * @author dalcacer
 */
public abstract class ADialogPanel extends JPanel {

    /**
     *
     */
    public void updateDTO() {
    }

    /**
     *
     * @return
     */
    public ExecuteRequest getDTO() {
        return null;
    }
    
    /**
     *
     * @return
     */
    public boolean isValidInput(){
        return false;
    }
}
