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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class SingleProcessCard extends JScrollPane {

    private JPanel contentPanel;

    // TODO move magic number to config/create setter
    private double propertyBorderThickness = 1; // property row: height of the bottom border
    private final int COLUMN_1_WIDTH = 56;      // fixed width of first column ("header")
    private int titleHeight = 20;               // height of titles ("process", "inputs" etc.)

    protected Insets labelInsets = new Insets(2, 5, 2, 5);

    protected Color headLabelBgColor = new Color(0, 0, 0, 0);
    protected Color bodyLabelBgColor = Color.WHITE;

    protected Color propertyTitleFgColor = Color.WHITE;
    protected Color propertyTitleBgColor1 = Color.LIGHT_GRAY;
    protected Color propertyTitleBgColor2 = Color.LIGHT_GRAY.darker();
    protected Color portBorderColor = Color.LIGHT_GRAY.darker();

    public SingleProcessCard(JPanel contentPanel) {
        super(contentPanel);

        this.contentPanel = contentPanel;
//        add(contentPanel);
//        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
//        setViewportView(contentPanel);
    }

    /**
     * Updates UI after a Process has been set
     *
     * @param process
     */
    void setProcess(IProcessEntity process) {
        contentPanel.removeAll();
        contentPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM}}));
        addProccessPanel(process);
        addInputPanel(process);
        addOutputPanel(process);
        updateUI();
    }

    /**
     * Adds a Process Overview Panel to the main Panel
     *
     * @param process
     */
    private void addProccessPanel(IProcessEntity process) {
        contentPanel.add(createProcessPanel(process), "0 0");
    }

    /**
     * Adds an input port overview to the main panel.
     *
     * @param process
     */
    private void addInputPanel(IProcessEntity process) {
        contentPanel.add(createInputPanel(process), "0 1");
    }

    /**
     * Adds an output port overview to the main panel.
     *
     * @param process
     */
    private void addOutputPanel(IProcessEntity process) {
        contentPanel.add(createOutputPanel(process), "0 2");
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

        int y = 0;
        processPanel.add(createHeadLabel(AppConstants.PROCESS_IDENTIFIER_LABEL), "0 " + y);
        processPanel.add(createBodyLabel(process.getIdentifier()), "1 " + y);
        y++;
        processPanel.add(createColumn1Border(), "0 " + y);
        processPanel.add(createColumn2Border(), "1 " + y);
        y++;
        processPanel.add(createHeadLabel("Title"), "0 " + y);
        processPanel.add(createBodyLabel(process.getTitle()), "1 " + y);
        y++;
        processPanel.add(createColumn1Border(), "0 " + y);
        processPanel.add(createColumn2Border(), "1 " + y);
        y++;
        processPanel.add(createHeadLabel("Abstract"), "0 " + y);
        processPanel.add(createBodyLabel(process.getOwsAbstract()), "1 " + y);

        String processTitle = AppConstants.PROPERTIES_PROCESS_TITLE;

        return createTitledComponent(processTitle, processPanel);
    }

    protected Component createTitledComponent(String title, Component component) {
        TitledComponent titledComponent = new TitledComponent(title, component, titleHeight, true);
        titledComponent.setTitleFontColor(propertyTitleFgColor);
        titledComponent.setTitleGradientColor1(propertyTitleBgColor1);
        titledComponent.setTitleGradientColor2(propertyTitleBgColor2);
        titledComponent.setTitleInsets(labelInsets);
        return titledComponent;
    }

    protected Component createColumn1Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(bodyLabelBgColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    protected Component createColumn2Border() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(headLabelBgColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    protected Component createPortBorder() {
        JLabel border = new JLabel("");
        border.setBorder(new ColorBorder(portBorderColor, 0, 0, (int) propertyBorderThickness, 0));
        return border;
    }

    /**
     * Creates and returns an input overview panel.
     *
     * @param process
     * @return
     */
    private Component createInputPanel(IProcessEntity process) {
        return createPortsPanel(AppConstants.PROPERTIES_INPUTS_TITLE, process.getInputPorts());
    }

    private Component createOutputPanel(IProcessEntity process) {
        return createPortsPanel(AppConstants.PROPERTIES_OUTPUTS_TITLE, process.getOutputPorts());
    }

    private Component createPortsPanel(String title, List<ProcessPort> ports) {
        // TODO calculate/get the magic number !!
        int panelRows = 4 * ports.size();

        JPanel portsPanel = new JPanel();
        double P = TableLayout.PREFERRED;
        title += " (" + ports.size() + ")";

        if (0 < panelRows) {
            double[][] layoutSize = new double[2][];//{{CULUMN_1_WIDTH, TableLayout.FILL}, {P, propertyBorderThickness, P, propertyBorderThickness, P}};
            int panelRowsPlusBorders = 2 * panelRows - 1;
            layoutSize[0] = new double[]{COLUMN_1_WIDTH, TableLayout.FILL};
            layoutSize[1] = new double[panelRowsPlusBorders];// additional rows for borders // {COLUMN_1_WIDTH, TableLayout.FILL};
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

        return createTitledComponent(title, portsPanel);
    }

    /**
     * Creates and returns an output overview panel.
     *
     * @param process
     * @return
     */
//    private JPanel createOutputPanel(IProcessEntity process) {
//        JPanel outputPanel = new JPanel();
//        String outputTitle = AppConstants.PROPERTIES_OUTPUTS_TITLE + " (" + process.getNumOutputs() + ")";
//        outputPanel.setBorder(new TitledBorder(outputTitle));
//        GridLayout grid = new GridLayout(0, 1);
//        outputPanel.setLayout(grid);
//        for (ProcessPort outport : process.getOutputPorts()) {
//            outputPanel.add(createPortPanel(outport));
//        }
//        return outputPanel;
//    }
    /**
     * Creates and returns a single port overview Panel with a GridLayout
     *
     * @param outport
     * @return
     */
    @Deprecated
    private JPanel createPortPanel(ProcessPort outport) {
        JPanel singleInputPanel = new JPanel();
        singleInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        GridLayout grid = new GridLayout(0, 2);
        singleInputPanel.setLayout(grid);

        singleInputPanel.add(createHeadLabel("Identifier"));
        singleInputPanel.add(createBodyLabel(outport.getOwsIdentifier()));

        singleInputPanel.add(createHeadLabel("Title"));
        singleInputPanel.add(createBodyLabel(outport.getOwsTitle()));

        singleInputPanel.add(createHeadLabel("Abstract"));
        singleInputPanel.add(createBodyLabel(outport.getOwsAbstract()));

        // TODO add datatype
        return singleInputPanel;
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
//        JPanel singleInputPanel = new JPanel();
//        singleInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
//        GridLayout grid = new GridLayout(0, 2);
//        singleInputPanel.setLayout(grid);

        parent.add(createHeadLabel("Identifier"), "0 " + rowOffset);
        parent.add(createBodyLabel(port.getOwsIdentifier()), "1 " + rowOffset++);

        parent.add(createColumn1Border(), "0 " + rowOffset);
        parent.add(createColumn2Border(), "1 " + rowOffset++);

        parent.add(createHeadLabel("Title"), "0 " + rowOffset);
        parent.add(createBodyLabel(port.getOwsTitle()), "1 " + rowOffset++);

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

    /**
     * Creates and returns a styled Label for the table head.
     *
     * @param text
     * @return
     */
    private Component createHeadLabel(String text) {
        return createMultilineLabel(text, headLabelBgColor);
    }

    /**
     * Creates and returns a styled label for the table body.
     *
     * @param text
     * @return
     */
    private Component createBodyLabel(String text) {
        return createMultilineLabel(text, bodyLabelBgColor);
    }

    private Component createMultilineLabel(String text, Color background) {
        MultilineLabel label = new MultilineLabel(text);
        label.setBackground(background);
        Border emptyBorder = new EmptyBorder(labelInsets);
        label.setBorder(emptyBorder);
        return label;
    }

}
