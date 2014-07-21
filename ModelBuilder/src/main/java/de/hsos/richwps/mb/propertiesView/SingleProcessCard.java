/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.MultilineLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class SingleProcessCard extends AbstractPropertiesCard {

    private TitledComponent processPanel;
    private TitledComponent inputsPanel;
    private TitledComponent outputsPanel;

    private MultilineLabel processIdentifierLabel;
    private MultilineLabel processTitleLabel;
    private MultilineLabel processAbstractLabel;

    public SingleProcessCard(final JPanel contentPanel) {
        super(contentPanel);

        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM}}));
    }

    /**
     * Updates UI after a process has been set.
     *
     * @param process
     */
    void setProcess(IProcessEntity process) {
        setProccessPanelData(process);
        setInputPanelData(process);
        setOutputPanelData(process);
    }

    /**
     * Adds a Process Overview Panel to the main Panel
     *
     * @param process
     */
    private void setProccessPanelData(IProcessEntity process) {
        if (null == processPanel) {
            Component panelContent = createProcessPanel(process);
            String processTitle = AppConstants.PROPERTIES_PROCESS_TITLE;
            processPanel = createTitledComponent(processTitle, panelContent);
            contentPanel.add(processPanel, "0 0");
        } else {
            processTitleLabel.setText(process.getTitle());
            processAbstractLabel.setText(process.getOwsAbstract());
            processIdentifierLabel.setText(process.getIdentifier());
        }
    }

    /**
     * Adds an input port overview to the main panel.
     *
     * @param process
     */
    private void setInputPanelData(IProcessEntity process) {
        List<ProcessPort> ports = process.getInputPorts();
        String title = AppConstants.PROPERTIES_INPUTS_TITLE + " (" + ports.size() + ")";

        JPanel content = createPortsPanel(ports);

        if (null == inputsPanel) {
            inputsPanel = createTitledComponent(title, content);
            contentPanel.add(inputsPanel, "0 1");

        } else {
            inputsPanel.setComponent(content);
            inputsPanel.setTitle(title);
        }

    }

    /**
     * Adds an output port overview to the main panel.
     *
     * @param process
     */
    private void setOutputPanelData(IProcessEntity process) {
        List<ProcessPort> ports = process.getOutputPorts();
        String title = AppConstants.PROPERTIES_OUTPUTS_TITLE + " (" + ports.size() + ")";

        JPanel content = createPortsPanel(ports);

        if (null == outputsPanel) {
            outputsPanel = createTitledComponent(title, content);
            contentPanel.add(outputsPanel, "0 2");

        } else {
            outputsPanel.setComponent(content);
            outputsPanel.setTitle(title);
        }
    }

    /**
     * Creates and returns a process overview panel
     *
     * @param process
     * @return
     */
    private Component createProcessPanel(IProcessEntity process) {
        JPanel processPanel = new JPanel();

        double P = TableLayout.PREFERRED;
        processPanel.setLayout(new TableLayout(new double[][]{{COLUMN_1_WIDTH, TableLayout.FILL}, {P, propertyBorderThickness, P, propertyBorderThickness, P}}));
        processPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

        processIdentifierLabel = createBodyLabel(process.getIdentifier());
        processTitleLabel = createBodyLabel(process.getTitle());
        processTitleLabel.setFont(processTitleLabel.getFont().deriveFont(Font.BOLD));
        processAbstractLabel = createBodyLabel(process.getOwsAbstract());

        int y = 0;
        processPanel.add(createHeadLabel("Title"), "0 " + y);
        processPanel.add(processTitleLabel, "1 " + y);
        y++;
        processPanel.add(createColumn1Border(), "0 " + y);
        processPanel.add(createColumn2Border(), "1 " + y);
        y++;
        processPanel.add(createHeadLabel(AppConstants.PROCESS_IDENTIFIER_LABEL), "0 " + y);
        processPanel.add(processIdentifierLabel, "1 " + y);
        y++;
        processPanel.add(createColumn1Border(), "0 " + y);
        processPanel.add(createColumn2Border(), "1 " + y);
        y++;
        processPanel.add(createHeadLabel("Abstract"), "0 " + y);
        processPanel.add(processAbstractLabel, "1 " + y);

        return processPanel;
    }

    protected Component createPortBorder() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(portBorderColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    private JPanel createPortsPanel(List<ProcessPort> ports) {
        // TODO calculate/get the magic number !!
        int panelRows = 4 * ports.size();

        JPanel portsPanel = new JPanel();
        double P = TableLayout.PREFERRED;

        if (0 < panelRows) {
            double[][] layoutSize = new double[2][];
            int panelRowsPlusBorders = 2 * panelRows - 1;
            layoutSize[0] = new double[]{COLUMN_1_WIDTH, TableLayout.FILL};
            layoutSize[1] = new double[panelRowsPlusBorders];// additional rows for borders
            for (int r = 0; r < panelRowsPlusBorders; r++) {
                layoutSize[1][r] = (1 == r % 2) ? propertyBorderThickness : P;
            }

            portsPanel.setLayout(new TableLayout(layoutSize));
            portsPanel.setBorder(new ColorBorder(propertyTitleBgColor2, 0, 0, 1, 0));

            int r = 0;
            for (ProcessPort inport : ports) {
                r = createPortPanel(inport, portsPanel, r);
                if (r != panelRows) {
                    portsPanel.add(createPortBorder(), "0 " + r + " 1 " + r++);
                }
            }
        }

        return portsPanel;
    }

    /**
     * Returns rowOffset + number of added rows.
     *
     * @param port
     * @param parent
     * @param rowOffset
     * @return
     */
    private int createPortPanel(ProcessPort port, Container parent, int rowOffset) {

        MultilineLabel titleLabel = createBodyLabel(port.getOwsTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        parent.add(createHeadLabel("Title"), "0 " + rowOffset);
        parent.add(titleLabel, "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Identifier"), "0 " + rowOffset);
        parent.add(createBodyLabel(port.getOwsIdentifier()), "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Abstract"), "0 " + rowOffset);
        parent.add(createBodyLabel(port.getOwsAbstract()), "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Datatype"), "0 " + rowOffset);
        parent.add(createBodyLabel(port.getDatatype()), "1 " + rowOffset++);

        return rowOffset;
    }

    void setProcessPanelFolded(boolean processFolded) {
        if (null != processPanel) {
            processPanel.setFolded(processFolded);
        }
    }

    void setInputsPanelFolded(boolean inputsFolded) {
        inputsPanel.setFolded(inputsFolded);
    }

    void setOutputsPanelFolded(boolean outputsFolded) {
        outputsPanel.setFolded(outputsFolded);
    }

    boolean isProcessPanelFolded() {
        if (null == processPanel) {
            return false;
        }
        return processPanel.isFolded();
    }

    boolean isInputsPanelFolded() {
        if (null == inputsPanel) {
            return false;
        }
        return inputsPanel.isFolded();
    }

    boolean isOutputsPanelFolded() {
        if (null == outputsPanel) {
            return false;
        }
        return outputsPanel.isFolded();
    }

}
