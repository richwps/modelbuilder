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
    public void updateRequest() {
    }

    /**
     *
     * @return
     */
    public ExecuteRequest getRequest() {
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
