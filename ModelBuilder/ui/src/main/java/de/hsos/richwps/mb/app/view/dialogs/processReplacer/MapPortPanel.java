package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

/**
 *
 * @author dziegenh
 */
public class MapPortPanel extends JPanel {

    private final ProcessPort target;
    private final JComboBox<ProcessPort> dropdown;

    public MapPortPanel(ProcessPort target, List<ProcessPort> sources) {
        super();

        this.target = target;

        if (target.isFlowOutput()) {
            setBackground(AppConstants.OUTPUT_PORT_COLOR);
        } else {
            setBackground(AppConstants.INPUT_PORT_COLOR);
        }

        TitledBorder border = new TitledBorder(target.getOwsIdentifier());
        setBorder(border);

        dropdown = new JComboBox<>();
        dropdown.setRenderer(new ProcessPortListCellRenderer());

        int idx = 1;
        dropdown.addItem(null);

        int selectIdx = -1;
        for (ProcessPort sourcePort : sources) {
            dropdown.addItem(sourcePort);
            if (sourcePort.getOwsIdentifier().equals(target.getOwsIdentifier())) {
                selectIdx = idx;
            }
            idx++;
        }

        if (selectIdx > 0) {
            dropdown.setSelectedIndex(selectIdx);
        }

        add(new JLabel("Select old port: "));
        add(dropdown);
    }

    public ProcessPort getTarget() {
        return target;
    }

    public ProcessPort getSelectedSourcePort() {
        return (ProcessPort) dropdown.getSelectedItem();
    }

}
