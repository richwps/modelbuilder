package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import java.awt.Font;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
        // one row for each input and output plus the header label row
        final int numRows = targetInputs.size() + targetOutputs.size() + 1;
        double[] layoutRows = new double[numRows];
        Arrays.fill(layoutRows, TableLayout.PREFERRED);
        setLayout(new TableLayout(new double[][]{{.5, .5}, layoutRows}));
        int rowY = 0;

        // add header row
        JLabel leftHeader = createHeaderLabel("New Port:");
        add(leftHeader, "0 " + rowY);
        JLabel rightHeader = createHeaderLabel("Old Port:");
        add(rightHeader, "1 " + rowY++);

        // add input port rows
        for (ProcessPort port : targetInputs) {
            final MapPortPanel mapPortPanel = new MapPortPanel(port, sourceInputs);
            portPanels.add(mapPortPanel);
            add(mapPortPanel, "0 " + rowY + " 1 " + rowY++);
        }

        // add output port rows
        for (ProcessPort port : targetOutputs) {
            final MapPortPanel mapPortPanel = new MapPortPanel(port, sourceOutputs);
            portPanels.add(mapPortPanel);
            add(mapPortPanel, "0 " + rowY + " 1 " + rowY++);
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

    /**
     * Creates a styled label for the header row.
     *
     * @param text
     * @return
     */
    private JLabel createHeaderLabel(String text) {
        Border headerBorder = new EmptyBorder(2, 2, 2, 2);
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setBorder(headerBorder);
        label.setHorizontalAlignment(JLabel.CENTER);
//        label.setAlignmentX(CENTER_ALIGNMENT);
//        label.setFont(label.getFont().deriveFont(Font.));

        return label;
    }
    
}
