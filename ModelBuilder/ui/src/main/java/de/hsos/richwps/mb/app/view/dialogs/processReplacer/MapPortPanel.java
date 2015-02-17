package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.ui.ColorBorder;
import java.awt.Color;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

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

        final EmptyBorder innerBorder = new EmptyBorder(2, 2, 1, 2);
        final ColorBorder outerBorder = new ColorBorder(Color.GRAY, 0, 0, 1, 0);
        setBorder(new CompoundBorder(outerBorder, innerBorder));
        setLayout(new TableLayout(new double[][]{{.5, .5}, {TableLayout.PREFERRED}}));

        JLabel label = createLabel();
        add(label, "0 0");

        dropdown = createDropdown(sources);
        if (dropdown.getItemCount() > 1) {
            add(dropdown, "1 0");
        } else {
            add(new JLabel("(no ports with matching datatype)"), "1 0");
        }
    }

    private JComboBox createDropdown(List<ProcessPort> sources) {
        JComboBox dropdown = new JComboBox<>();
        dropdown.setRenderer(new ProcessPortListCellRenderer());
        dropdown.addItem(null);

        // temp index
        int idx = 1;

        // index of selected item
        int selectIdx = -1;

        for (ProcessPort sourcePort : sources) {

            // only add ports of the same datatype
            if (sourcePort.getDatatype().equals(this.target.getDatatype())) {
                dropdown.addItem(sourcePort);

                if (sourcePort.getOwsIdentifier().equals(this.target.getOwsIdentifier())) {
                    selectIdx = idx;
                }

                idx++;
            }
        }

        if (selectIdx > 0) {
            dropdown.setSelectedIndex(selectIdx);
        }

        return dropdown;
    }

    public ProcessPort getTarget() {
        return target;
    }

    public ProcessPort getSelectedSourcePort() {
        return (ProcessPort) dropdown.getSelectedItem();
    }

    private JLabel createLabel() {
        JLabel label = new JLabel(target.getOwsIdentifier());
        label.setToolTipText(target.getToolTipText());
        return label;
    }

}
