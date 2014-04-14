/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.propertiesView;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.semanticProxy.entity.IProcessEntity;
import de.hsos.richwps.mb.semanticProxy.entity.ProcessPort;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 *
 * @author dziegenh
 */
public class SingleProcessCard extends JPanel {

    public SingleProcessCard() {
        super();
        setLayout(new GridBagLayout());
    }

    /**
     * Updates UI after a Process has been set
     * @param process
     */
    void setProcess(IProcessEntity process) {
        removeAll();
        addProccessPanel(process);
        addInputPanel(process);
        addOutputPanel(process);
        updateUI();
    }

    /**
     * Adds a Process Overview Panel to the main Panel
     * @param process
     */
    private void addProccessPanel(IProcessEntity process) {
        JPanel processPanel = createProcessPanel(process);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(processPanel, c);
    }

    /**
     * Adds an input port overview to the main panel.
     * @param process
     */
    private void addInputPanel(IProcessEntity process) {
        JPanel inputPanel = createInputPanel(process);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(inputPanel, c);
    }

    /**
     * Adds an output port overview to the main panel.
     * @param process
     */
    private void addOutputPanel(IProcessEntity process) {
        JPanel outputPanel = createOutputPanel(process);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        add(outputPanel, c);
    }

    /**
     * Creates and returns a process overview panel
     * @param process
     * @return
     */
    private JPanel createProcessPanel(IProcessEntity process) {
        JPanel processPanel = new JPanel();
        processPanel.setBorder(new TitledBorder(AppConstants.PROPERTIES_PANEL_TITLE));
        GridLayout grid = new GridLayout(0, 2);
        processPanel.setLayout(grid);
        processPanel.add(createHeadLabel("Identifier"));
        processPanel.add(createBodyLabel(process.getIdentifier()));
        processPanel.add(createHeadLabel("Title"));
        processPanel.add(createBodyLabel(process.getTitle()));
        processPanel.add(createHeadLabel("Abstract"));
        processPanel.add(createBodyLabel(process.getOwsAbstract()));
        return processPanel;
    }

    /**
     * Creates and returns an input overview panel.
     * @param process
     * @return
     */
    private JPanel createInputPanel(IProcessEntity process) {
        JPanel inputPanel = new JPanel();
        String inputTitle = AppConstants.PROPERTIES_INPUTS_TITLE + " (" + process.getNumInputs() + ")";
        inputPanel.setBorder(new TitledBorder(inputTitle));
        GridLayout grid = new GridLayout(0, 1);
        inputPanel.setLayout(grid);
        for (ProcessPort inport : process.getInputPorts()) {
            JPanel singleInputPanel = createPortPanel(inport);
            inputPanel.add(singleInputPanel);
        }
        return inputPanel;
    }

    /**
     * Creates and returns an output overview panel.
     * @param process
     * @return
     */
    private JPanel createOutputPanel(IProcessEntity process) {
        JPanel outputPanel = new JPanel();
        String outputTitle = AppConstants.PROPERTIES_OUTPUTS_TITLE + " (" + process.getNumOutputs() + ")";
        outputPanel.setBorder(new TitledBorder(outputTitle));
        GridLayout grid = new GridLayout(0, 1);
        outputPanel.setLayout(grid);
        for (ProcessPort outport : process.getOutputPorts()) {
            outputPanel.add(createPortPanel(outport));
        }
        return outputPanel;
    }

    /**
     * Creates and returns a single port overview Panel with a GridLayout
     * @param outport
     * @return
     */
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

        return singleInputPanel;
    }

    /**
     * Creates and returns a styled Label for the table head.
     * @param text
     * @return
     */
    private JLabel createHeadLabel(String text){
        JLabel label = createBodyLabel(text);
        label.setBackground(Color.LIGHT_GRAY);
        label.setOpaque(true);
        return label;
    }

    /**
     * Creates and returns a styled label for the table body.
     * @param text
     * @return
     */
    private JLabel createBodyLabel(String text){
        JLabel label = new JLabel(text);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 2, 5);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
        label.setBorder(compoundBorder);
        return label;
    }

}
