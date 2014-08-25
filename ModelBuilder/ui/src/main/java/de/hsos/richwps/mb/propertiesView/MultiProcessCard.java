/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author dziegenh
 */
class MultiProcessCard extends JPanel {

    public MultiProcessCard() {
        super();

        add(new JLabel(AppConstants.PROPERTIES_MULTI_ELEMENTS_SELECTION));
    }

    void setProcesses(List<ProcessEntity> processes) {
        // nothing to do at the moment
    }

}
