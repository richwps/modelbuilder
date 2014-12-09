package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.ui.dialogs.components.ADialogPanel;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputBoundingBoxData;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputLiteralData;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputComplexData;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import layout.TableLayout;

/**
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class InputParameterization extends ADialogPanel {

    /**
     * List of panels that provide inputpanels.
     */
    private List<TitledComponent> inputpanels;
    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;
    /**
     * Request that should be executed.
     */
    private ExecuteRequest request;

    private boolean expand = false;

    /**
     * Creates new form ExecutePanel
     */
    public InputParameterization() {
        initComponents();
    }

    /**
     * Creates new form ExecutePanel
     *
     * @param provider
     * @param request
     */
    public InputParameterization(final RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        initComponents();
        this.inputpanels = new ArrayList<>();
        this.expand = false;
        final String selectedserver = this.request.getEndpoint();
        final String selectedprocess = this.request.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);

        if (request instanceof TestRequest) {
            //nop
        } else {
            //update only if necessary 
            if (!request.isLoaded()) {
                this.provider.wpsDescribeProcess(this.request);
            }
        }
        this.createInputPanels();
    }

    /**
     * Creates and adds inputpanels based on inputdescriptions.
     */
    private void createInputPanels() {

        if (this.request.getInputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load inputs from "
                    + "process description.",
                    JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }
        for (IInputSpecifier specifier : this.request.getInputs()) {
            if (specifier instanceof InputLiteralDataSpecifier) {

                InputLiteralData pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputLiteralDataArgument arg = (InputLiteralDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputLiteralData((InputLiteralDataSpecifier) specifier, arg);
                } else {
                    pan = new InputLiteralData((InputLiteralDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.inputpanels.add(tc);
            } else if (specifier instanceof InputComplexDataSpecifier) {
                InputComplexData pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputComplexDataArgument arg = (InputComplexDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputComplexData((InputComplexDataSpecifier) specifier, arg);

                } else {
                    pan = new InputComplexData((InputComplexDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.inputpanels.add(tc);

            } else if (specifier instanceof InputBoundingBoxDataSpecifier) {
                InputBoundingBoxData pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputBoundingBoxDataArgument arg = (InputBoundingBoxDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputBoundingBoxData((InputBoundingBoxDataSpecifier) specifier, arg);
                } else {
                    pan = new InputBoundingBoxData((InputBoundingBoxDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.inputpanels.add(tc);
            }
        }

        JPanel inputsPanel = new JPanel();

        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL
        };

        double innersize[] = new double[inputpanels.size()];
        for (int i = 0;
                i < inputpanels.size();
                i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;

        TableLayout layout = new TableLayout(size);

        inputsPanel.setLayout(layout);

        int i = 0;
        for (JPanel panel : this.inputpanels) {
            String c = "0," + i;
            inputsPanel.add(panel, c);
            i++;
        }
        String c = "0," + i + 1;
        inputsPanel.add(new JPanel(), c);

        this.inputsPanelScrollPane.setViewportView(inputsPanel);
    }

    /**
     * Transcodes the inputs panel-wise into an executerequest actualinputs.
     */
    @Override
    public void updateRequest() {
        HashMap<String, IInputArgument> theinputs = new HashMap<>();

        for (TitledComponent panel : this.inputpanels) {

            if (panel.getComponent() instanceof InputComplexData) {
                InputComplexData pan = (InputComplexData) panel.getComponent();
                InputComplexDataSpecifier specifier = pan.getSpecifier();

                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for "
                            + pan.getSpecifier().getIdentifier());
                    break;
                } else if (!pan.isMandatory() & pan.getText().isEmpty()) {
                    //nothing to do
                } else {
                    List type = pan.getMimeType();
                    String amimetype = (String) type.get(InputComplexDataSpecifier.mimetype_IDX);
                    String aschema = (String) type.get(InputComplexDataSpecifier.schema_IDX);
                    String aencoding = (String) type.get(InputComplexDataSpecifier.encoding_IDX);

                    InputComplexDataArgument param = null;
                    if (pan.isReference()) {
                        param = new InputComplexDataArgument(specifier);
                        String url = pan.getText();
                        param.setURL(url);
                    } else {
                        throw new UnsupportedOperationException("Not implemented, yet.");
                    }

                    param.setMimeType(amimetype);
                    if (aschema != null) {
                        param.setSchema(aschema);
                    }
                    if (aencoding != null) {
                        System.out.println(aencoding);
                        param.setEncoding(aencoding);
                    }
                    theinputs.put(param.getIdentifier(), param);
                }
            } else if (panel.getComponent() instanceof InputLiteralData) {
                InputLiteralData pan = (InputLiteralData) panel.getComponent();
                InputLiteralDataSpecifier specifier = pan.getSpecifier();
                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for "
                            + pan.getSpecifier().getIdentifier());
                    break;
                } else if (!pan.isMandatory() & pan.getText().isEmpty()) {
                    //nothing to do
                } else {
                    InputLiteralDataArgument param = new InputLiteralDataArgument(specifier, pan.getText());
                    theinputs.put(param.getIdentifier(), param);
                }
            } else if (panel.getComponent() instanceof InputBoundingBoxData) {
                InputBoundingBoxData pan = (InputBoundingBoxData) panel.getComponent();
                InputBoundingBoxDataSpecifier specifier = pan.getSpecifier();
                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for "
                            + pan.getSpecifier().getIdentifier());
                    break;
                } else if (!pan.isMandatory() & pan.getText().isEmpty()) {
                    //nothing to do
                } else {
                    InputBoundingBoxDataArgument param;
                    param = new InputBoundingBoxDataArgument(specifier, pan.getText());
                    param.setCrsType(pan.getCRS());
                    theinputs.put(param.getIdentifier(), param);
                }
            }
        }
        this.request.setInputArguments(theinputs);
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
        for (TitledComponent panel : this.inputpanels) {
            if (panel.getComponent() instanceof InputComplexData) {
                InputComplexData pan = (InputComplexData) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for " + pan.getSpecifier().getIdentifier());
                    return false;
                }
            } else if (panel.getComponent() instanceof InputLiteralData) {
                InputLiteralData pan = (InputLiteralData) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for " + pan.getSpecifier().getIdentifier());
                    return false;
                }
            } else if (panel.getComponent() instanceof InputBoundingBoxData) {
                InputBoundingBoxData pan = (InputBoundingBoxData) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for " + pan.getSpecifier().getIdentifier());
                    return false;
                }
            }
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
        inputsPanelScrollPane = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        expandButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.GridBagLayout());

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServer, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
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
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcessLabel, gridBagConstraints);

        inputsPanelScrollPane.setBorder(null);
        inputsPanelScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inputsPanelScrollPane.setViewportBorder(null);
        inputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 550));
        inputsPanelScrollPane.setPreferredSize(new java.awt.Dimension(610, 700));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(inputsPanelScrollPane, gridBagConstraints);

        jLabel1.setText("Please provide inputdata for ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);

        expandButton.setText("Expand all");
        expandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expandButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if (this.expand == true) {
            for (TitledComponent tc : this.inputpanels) {
                tc.fold();
            }
            this.expand = false;
            this.expandButton.setText("Expand all");
            return;
        }

        for (TitledComponent tc : this.inputpanels) {
            tc.unfold();
            this.expand = true;
            this.expandButton.setText("Collapse all");
        }
    }//GEN-LAST:event_expandButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expandButton;
    private javax.swing.JScrollPane inputsPanelScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
