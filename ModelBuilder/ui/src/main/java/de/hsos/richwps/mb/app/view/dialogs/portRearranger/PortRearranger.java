package de.hsos.richwps.mb.app.view.dialogs.portRearranger;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.AppGraphView;
import de.hsos.richwps.mb.app.view.appFrame.AppFrame;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.graphView.mxGraph.layout.CellXComparator;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class PortRearranger extends MbDialog {

    private final AppGraphView graphView;
    private final mxCell processCell;

    private double[] inputPositions;
    private double[] outputPositions;
    private PortRearrangePanel inputsRearrangePanel;
    private PortRearrangePanel outputsRearrangePanel;

    public PortRearranger(AppFrame frame, AppGraphView graphView) {
        super(frame, AppConstants.PORT_REARRANGER_DIALOG_TITLE, MbDialog.BTN_ID_CANCEL | MbDialog.BTN_ID_OK);

        this.graphView = graphView;
        this.processCell = (mxCell) graphView.getGraph().getSelectionCell();

        setSize(300, 400);

        init();
    }

    private void init() {
        final double p = TableLayout.PREFERRED;
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {p, p, 20d, p, p, TableLayout.FILL}}));

        ProcessEntity process = (ProcessEntity) processCell.getValue();
        List<ProcessPort> inputPorts = process.getInputPorts();
        List<mxCell> inputCells = getPortCells(inputPorts);
        inputPositions = new double[inputCells.size()];
        int i = 0;
        inputPorts = new LinkedList<>();
        for (mxCell cell : inputCells) {
            inputPositions[i++] = cell.getGeometry().getOffset().getX();
            inputPorts.add((ProcessPort) cell.getValue());
        }
        inputsRearrangePanel = new PortRearrangePanel(inputPorts);
        inputsRearrangePanel.setBorder(new ColorBorder(Color.GRAY, 0, 0, 1, 0));

        List<ProcessPort> outputPorts = process.getOutputPorts();
        List<mxCell> outputCells = getPortCells(outputPorts);
        outputPositions = new double[outputCells.size()];
        i = 0;
                outputPorts = new LinkedList<>();

        for (mxCell cell : outputCells) {
            outputPositions[i++] = cell.getGeometry().getOffset().getX();
            outputPorts.add((ProcessPort) cell.getValue());
        }
        outputsRearrangePanel = new PortRearrangePanel(outputPorts);

        // create labels
        JLabel inputsLabel = new JLabel("Input positions order");
        inputsLabel.setFont(inputsLabel.getFont().deriveFont(Font.BOLD));
        inputsLabel.setHorizontalAlignment(JLabel.CENTER);
        JLabel outputsLabel = new JLabel("Output positions  order");
        outputsLabel.setFont(outputsLabel.getFont().deriveFont(Font.BOLD));
        outputsLabel.setHorizontalAlignment(JLabel.CENTER);

        add(inputsLabel, "0 0");
        add(inputsRearrangePanel, "0 1");
        add(outputsLabel, "0 3");
        add(outputsRearrangePanel, "0 4");
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
            // rearrange cells
            mxIGraphModel model = graphView.getGraph().getModel();
            model.beginUpdate();
            
            List<ProcessPort> sortedInputs = this.inputsRearrangePanel.getSortedPorts();
            int i = 0;
            for (ProcessPort aPort : sortedInputs) {
                mxCell aPortCell = graphView.getCellByValue(processCell, aPort);
                double xPos = this.inputPositions[i];
                mxGeometry geometry = aPortCell.getGeometry();
                mxPoint offset = geometry.getOffset();
                offset.setX(xPos);
                geometry.setOffset(offset);
                aPortCell.setGeometry(geometry);
                i++;
            }
            
            List<ProcessPort> sortedOutputs = this.outputsRearrangePanel.getSortedPorts();
            i = 0;
            for (ProcessPort aPort : sortedOutputs) {
                mxCell aPortCell = graphView.getCellByValue(processCell, aPort);
                double xPos = this.outputPositions[i];
                mxGeometry geometry = aPortCell.getGeometry();
                mxPoint offset = geometry.getOffset();
                offset.setX(xPos);
                geometry.setOffset(offset);
                aPortCell.setGeometry(geometry);
                i++;
            }
            
            model.endUpdate();
            graphView.getGraph().refresh();
        }

        super.handleDialogButton(buttonId);
    }

    private List<mxCell> getPortCells(List<ProcessPort> ports) {
        List<mxCell> cells = new LinkedList<>();

        for (ProcessPort aPort : ports) {
            cells.add(this.graphView.getCellByValue(processCell, aPort));
        }

        Collections.sort(cells, new CellXComparator());

        return cells;
    }

}
