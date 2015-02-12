package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class MapPortsPanel extends JPanel {

    private ProcessEntity source;
    private ProcessEntity target;

    private final List<MapPortPanel> portPanels = new LinkedList<>();

    public MapPortsPanel(ProcessEntity source, ProcessEntity target) {
        this.source = source;
        this.target = target;

        List<ProcessPort> sourceInputs = source.getInputPorts();
        List<ProcessPort> sourceOutputs = source.getOutputPorts();
        List<ProcessPort> targetInputs = target.getInputPorts();
        List<ProcessPort> targetOutputs = target.getOutputPorts();

        // set(up) layout
        double[] layoutRows = new double[targetInputs.size() + targetOutputs.size()];
        Arrays.fill(layoutRows, TableLayout.FILL);
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, layoutRows}));
        int rowY = 0;

        for (ProcessPort port : targetInputs) {
            final MapPortPanel mapPortPanel = new MapPortPanel(port, sourceInputs);
            portPanels.add(mapPortPanel);
            add(mapPortPanel, "0 " + rowY++);
        }

        for (ProcessPort port : targetOutputs) {
            final MapPortPanel mapPortPanel = new MapPortPanel(port, sourceOutputs);
            portPanels.add(mapPortPanel);
            add(mapPortPanel, "0 " + rowY++);
        }

    }

    public List<MapPortPanel> getPortPanelsForSourcePort(ProcessPort sourcePort) {
        List<MapPortPanel> panels = new LinkedList<>();

        for (MapPortPanel mapPortPanel : this.portPanels) {
            ProcessPort selectedSourcePort = mapPortPanel.getSelectedSourcePort();
            if (null != selectedSourcePort && selectedSourcePort.equals(sourcePort)) {
                panels.add(mapPortPanel);
            }
        }

        return panels;
    }

}
