/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author dziegenh
 */
public class MultiProcessCard extends JPanel {

    public MultiProcessCard() {
        super();

        add(new JLabel(AppConstants.CARD_MULTI_PROCESS_SELECTION));
    }

    void setProcesses(List<IProcessEntity> processes) {
        // nothing to do at the moment
    }

}
