package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.richWPS.entity.impl.RequestExecute;
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
    public RequestExecute getDTO() {
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
