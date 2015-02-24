package de.hsos.richwps.mb.app.view.dialogs.portRearranger;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class PortRearrangePanel extends JPanel {

    private JTextField input;
    private HashMap<JTextField, ProcessPort> inputs;

    public PortRearrangePanel(List<ProcessPort> ports) {

        inputs = new HashMap<>();

        final int numRows = ports.size();
        double[] layoutRows = new double[numRows];
        Arrays.fill(layoutRows, TableLayout.PREFERRED);
        setLayout(new TableLayout(new double[][]{{.8, .2}, layoutRows}));
        int rowY = 0;

        for (ProcessPort aPort : ports) {
            Color labelBg = AppConstants.INPUT_PORT_COLOR;
            if (aPort.isFlowOutput()) {
                labelBg = AppConstants.OUTPUT_PORT_COLOR;
            }
            JLabel label = new JLabelWithBackground(labelBg);
            String labelText = "[" + aPort.toString() + "] " + aPort.getOwsIdentifier();
            label.setText(labelText);
            JTextField input = new JTextField("" + (rowY + 1));
            this.inputs.put(input, aPort);

            add(label, "0 " + rowY);
            add(input, "1 " + rowY++);
        }
    }

    List<ProcessPort> getSortedPorts() {
        List<ProcessPort> sortedPorts = new LinkedList<>();
        HashMap<JTextField, ProcessPort> clonedInputs = (HashMap<JTextField, ProcessPort>) this.inputs.clone();

        int numPorts = clonedInputs.size();
        JTextField currentInput;
        double currentMin = 0;

        for (int i = 0; i < numPorts; i++) {

            currentInput = null;
            for (Map.Entry<JTextField, ProcessPort> entry : clonedInputs.entrySet()) {

                final JTextField aField = entry.getKey();
                double value = Double.parseDouble(aField.getText());
                if (null == currentInput || value < currentMin) {
                    currentMin = value;
                    currentInput = aField;
                }
            }

            ProcessPort currentPort = clonedInputs.remove(currentInput);
            sortedPorts.add(currentPort);
        }

        return sortedPorts;
    }
}
