package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.ui.dialogs.components.ADialogPanel;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputBoundingBoxData;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputComplexData;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputLiteralData;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import layout.TableLayout;

/**
 * Dialog for ouput parameterisation.
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class OutputParameterization extends ADialogPanel {

    private List<TitledComponent> outputs;
    private RichWPSProvider provider;
    private ExecuteRequest request;
    private boolean allSelected = false;
    private boolean expand = false;

    /**
     * Creates new form OutputParameterization.
     */
    public OutputParameterization() {
        initComponents();
    }

    /**
     * Creates new form OutputParameterization
     *
     * @param provider
     * @param request
     */
    public OutputParameterization(final RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        this.allSelected = false;
        this.expand = false;
        initComponents();

        final String selectedserver = this.request.getEndpoint();
        final String selectedprocess = this.request.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);

        this.outputs = new ArrayList<>();

        if (request instanceof TestRequest) {
            //nop
        } else {
            //update only if necessary 
            if (!request.isLoaded()) {
                this.provider.wpsDescribeProcess(this.request);
            }
        }
        this.createOutputPanels();
    }

    private void createOutputPanels() {

        if (this.request.getOutputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load outputs from"
                    + "process description.", JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        for (IOutputSpecifier specifier : this.request.getOutputs()) {
            if (specifier instanceof OutputLiteralDataSpecifier) {
                OutputLiteralData pan = new OutputLiteralData((OutputLiteralDataSpecifier) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.outputs.add(tc);
            } else if (specifier instanceof OutputComplexDataSpecifier) {
                OutputComplexData pan = new OutputComplexData((OutputComplexDataSpecifier) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.outputs.add(tc);
            } else if (specifier instanceof OutputBoundingBoxDataSpecifier) {
                OutputBoundingBoxData pan = new OutputBoundingBoxData((OutputBoundingBoxDataSpecifier) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.outputs.add(tc);
            }
        }

        JPanel outputsPanel = new JPanel();
        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL};

        double innersize[] = new double[outputs.size()];
        for (int i = 0; i < outputs.size(); i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;

        TableLayout layout = new TableLayout(size);
        outputsPanel.setLayout(layout);

        int i = 0;
        for (JPanel panel : this.outputs) {
            String c = "0," + i;
            outputsPanel.add(panel, c);
            i++;
        }

        String c = "0," + i + 1;
        outputsPanel.add(new JPanel(), c);

        this.outputsPanelScrollPane.setViewportView(outputsPanel);
    }

    /**
     * Transcodes the outputs panel-wise into executerequest actualoutputs.
     */
    @Override
    public void updateRequest() {
        HashMap<String, IOutputArgument> theoutputs = new HashMap<>();

        for (TitledComponent panel : this.outputs) {

            if (panel.getComponent() instanceof OutputComplexData) {

                OutputComplexData pan = (OutputComplexData) panel.getComponent();
                if (pan.isSelected()) {
                    OutputComplexDataSpecifier specifier = pan.getSpecifier();
                    OutputComplexDataArgument argument = new OutputComplexDataArgument(specifier);

                    Boolean asRef = pan.asReference();
                    argument.setAsReference(asRef);
                    List type = pan.getType();
                    String amimetype = (String) type.get(OutputComplexDataSpecifier.mimetype_IDX);
                    String aschema = (String) type.get(OutputComplexDataSpecifier.schema_IDX);
                    String aencoding = (String) type.get(OutputComplexDataSpecifier.encoding_IDX);

                    argument.setMimetype(amimetype);
                    argument.setSchema(aschema);
                    argument.setEncoding(aencoding);

                    theoutputs.put(argument.getIdentifier(), argument);
                }
            } else if (panel.getComponent() instanceof OutputLiteralData) {
                OutputLiteralData pan = (OutputLiteralData) panel.getComponent();

                if (pan.isSelected()) {
                    OutputLiteralDataSpecifier specifier = pan.getSpecifier();
                    OutputLiteralDataArgument argument = new OutputLiteralDataArgument(specifier);
                    theoutputs.put(argument.getIdentifier(), argument);
                }

            } else if (panel.getComponent() instanceof OutputBoundingBoxData) {
                OutputBoundingBoxData pan = (OutputBoundingBoxData) panel.getComponent();

                if (pan.isSelected()) {
                    OutputBoundingBoxDataSpecifier specifier = pan.getSpecifier();
                    OutputBoundingBoxDataArgument argument = new OutputBoundingBoxDataArgument(specifier);
                    theoutputs.put(argument.getIdentifier(), argument);
                }
            }
        }
        this.request.setOutputArguments(theoutputs);
    }

    /**
     *
     * @return
     */
    @Override
    public ExecuteRequest getRequest() {
        return this.request;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isValidInput() {
        boolean someThingSelected = false;
        for (TitledComponent panel : this.outputs) {
            if (panel.getComponent() instanceof OutputComplexData) {
                OutputComplexData pan = (OutputComplexData) panel.getComponent();
                if (pan.isSelected()) {
                    someThingSelected = true;
                }

            } else if (panel.getComponent() instanceof OutputLiteralData) {
                OutputLiteralData pan = (OutputLiteralData) panel.getComponent();
                if (pan.isSelected()) {
                    someThingSelected = true;
                }
            } else if (panel.getComponent() instanceof OutputBoundingBoxData) {
                OutputBoundingBoxData pan = (OutputBoundingBoxData) panel.getComponent();
                if (pan.isSelected()) {
                    someThingSelected = true;
                }
            }
        }

        if (!someThingSelected) {
            JOptionPane.showMessageDialog(this, "Please select at least one output.");
            return false;
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectedServer = new javax.swing.JLabel();
        selectedProcess = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        outputsPanelScrollPane = new javax.swing.JScrollPane();
        selectAllButton = new javax.swing.JButton();
        expandButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.GridBagLayout());

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServer, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcess, gridBagConstraints);

        selectedServerLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedServerLabel.setLabelFor(selectedServer);
        selectedServerLabel.setText("Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServerLabel, gridBagConstraints);

        selectedProcessLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedProcessLabel.setLabelFor(selectedProcess);
        selectedProcessLabel.setText("Process:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcessLabel, gridBagConstraints);

        outputsPanelScrollPane.setBorder(null);
        outputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputsPanelScrollPane.setViewportBorder(null);
        outputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 600));
        outputsPanelScrollPane.setPreferredSize(new java.awt.Dimension(610, 550));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(outputsPanelScrollPane, gridBagConstraints);

        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectAllButton, gridBagConstraints);

        expandButton.setText("Expand all");
        expandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expandButton, gridBagConstraints);

        jLabel1.setText("Plase select required outputs.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (allSelected == false) {
            for (TitledComponent panel : this.outputs) {
                if (panel.getComponent() instanceof OutputComplexData) {
                    OutputComplexData pan = (OutputComplexData) panel.getComponent();
                    pan.setSelected();

                } else if (panel.getComponent() instanceof OutputLiteralData) {
                    OutputLiteralData pan = (OutputLiteralData) panel.getComponent();
                    pan.setSelected();
                } else if (panel.getComponent() instanceof OutputBoundingBoxData) {
                    OutputBoundingBoxData pan = (OutputBoundingBoxData) panel.getComponent();
                    pan.setSelected();
                }
            }
            allSelected = true;
            this.selectAllButton.setText("Deselect all");
            return;
        }

        for (TitledComponent panel : this.outputs) {
            if (panel.getComponent() instanceof OutputComplexData) {
                OutputComplexData pan = (OutputComplexData) panel.getComponent();
                pan.setUnselected();

            } else if (panel.getComponent() instanceof OutputLiteralData) {
                OutputLiteralData pan = (OutputLiteralData) panel.getComponent();
                pan.setUnselected();
            } else if (panel.getComponent() instanceof OutputBoundingBoxData) {
                OutputBoundingBoxData pan = (OutputBoundingBoxData) panel.getComponent();
                pan.setSelected();
                pan.setUnselected();
            }
        }
        allSelected = false;
        this.selectAllButton.setText("Select all");
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if (this.expand == true) {
            for (TitledComponent tc : this.outputs) {
                tc.fold();
            }
            this.expand = false;
            this.expandButton.setText("Expand all");
            return;
        }

        for (TitledComponent tc : this.outputs) {
            tc.unfold();
            this.expand = true;
            this.expandButton.setText("Collapse all");
        }
    }//GEN-LAST:event_expandButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expandButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane outputsPanelScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
