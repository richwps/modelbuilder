package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.execView.dialog.components.OutputBoundingBoxData;
import de.hsos.richwps.mb.execView.dialog.components.OutputComplexData;
import de.hsos.richwps.mb.execView.dialog.components.OutputLiteralData;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputLiteralDataSpecifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 *
 * @author dalcacer
 */
public class OutputParameterization extends ADialogPanel {

    private List<JPanel> outputs;
    private RichWPSProvider provider;
    private ExecuteRequest request;

    /**
     * Creates new form ExecutePanel
     */
    public OutputParameterization() {
        initComponents();
    }

    /**
     * Creates new form ExecutePanel
     *
     * @param provider
     * @param dto
     */
    public OutputParameterization(final RichWPSProvider provider, ExecuteRequest dto) {
        this.provider = provider;
        this.request = dto;
        initComponents();

        String selectedserver = this.request.getEndpoint();
        String selectedprocess = this.request.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);

        this.outputs = new ArrayList<>();
        this.provider.describeProcess(this.request);
        this.showOutputs();
    }

    private void showOutputs() {

        if (this.request.getOutputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load outputs.", JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        for (IOutputSpecifier specifier : this.request.getOutputs()) {
            if (specifier instanceof OutputLiteralDataSpecifier) {
                this.outputs.add(new OutputLiteralData((OutputLiteralDataSpecifier) specifier));
            } else if (specifier instanceof OutputComplexDataSpecifier) {
                this.outputs.add(new OutputComplexData((OutputComplexDataSpecifier) specifier));
            } //TODO test BoundingBox
            else if (specifier instanceof OutputBoundingBoxDataSpecifier) {
                this.outputs.add(new OutputBoundingBoxData((OutputBoundingBoxDataSpecifier) specifier));
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

        this.outputsPanelScrollPane.setViewportView(outputsPanel);
    }

    /**
     *
     */
    @Override
    public void updateRequest() {
        HashMap<String, IOutputArgument> theoutputs = new HashMap<>();

        for (JPanel panel : this.outputs) {
            if (panel instanceof OutputComplexData) {

                OutputComplexData pan = (OutputComplexData) panel;

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
            } else if (panel instanceof OutputLiteralData) {
                OutputLiteralData pan = (OutputLiteralData) panel;

                if (pan.isSelected()) {
                    OutputLiteralDataSpecifier specifier = pan.getSpecifier();
                    OutputLiteralDataArgument argument = new OutputLiteralDataArgument(specifier);
                    theoutputs.put(argument.getIdentifier(), argument);
                }

            } else if (panel instanceof OutputBoundingBoxData) {
                OutputBoundingBoxData pan = (OutputBoundingBoxData) panel;

                if (pan.isSelected()) {
                    OutputBoundingBoxDataSpecifier specifier = pan.getSpecifier();
                    OutputBoundingBoxDataArgument argument = new OutputBoundingBoxDataArgument(specifier);
                    theoutputs.put(argument.getIdentifier(), argument);
                }

            }
            //FIXME BoundingBox
            //FIXME BoundingBox
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
    public boolean isValidInput() {
        boolean someThingSelected = false;
        for (JPanel panel : this.outputs) {
            if (panel instanceof OutputComplexData) {
                OutputComplexData pan = (OutputComplexData) panel;
                if (pan.isSelected()) {
                    someThingSelected = true;
                }

            } else if (panel instanceof OutputLiteralData) {
                OutputLiteralData pan = (OutputLiteralData) panel;
                if (pan.isSelected()) {
                    someThingSelected = true;
                }
            } else if (panel instanceof OutputBoundingBoxData) {
                OutputBoundingBoxData pan = (OutputBoundingBoxData) panel;
                if (pan.isSelected()) {
                    someThingSelected = true;
                }
            }
        }

        if (!someThingSelected) {
            JOptionPane.showMessageDialog(this, "Please select an output.");
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

        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.GridBagLayout());

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServer, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 0;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcessLabel, gridBagConstraints);

        outputsPanelScrollPane.setBorder(null);
        outputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 600));
        outputsPanelScrollPane.setPreferredSize(new java.awt.Dimension(610, 600));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(outputsPanelScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane outputsPanelScrollPane;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
