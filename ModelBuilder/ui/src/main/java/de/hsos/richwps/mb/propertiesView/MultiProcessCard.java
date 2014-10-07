package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessEntity;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Property card showing properties of multiple processes (currently empty).
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
