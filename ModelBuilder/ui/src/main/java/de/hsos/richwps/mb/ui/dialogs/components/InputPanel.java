package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.ui.dialogs.APanel;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputBBoxForm;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputLiteralForm;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.InputComplexForm;
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
import layout.TableLayout;

/**
 * Dialog panel for input parameterisation
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class InputPanel extends APanel {

    /**List of displayable inputs.*/
    private List<TitledComponent> panels;
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
    public InputPanel() {
        initComponents();
    }

    /**
     * Creates new form ExecutePanel
     *
     * @param provider
     * @param request
     */
    public InputPanel(final RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        initComponents();
        this.panels = new ArrayList<>();
        this.expand = false;
        final String selectedserver = this.request.getEndpoint();
        final String selectedprocess = this.request.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);

        if (request instanceof TestRequest) {
            //noop
        } else {
            //update the request-object only if necessary 
            if (!request.isLoaded()) {
                this.provider.wpsDescribeProcess(this.request);
            }
        }
        this.prepare();
        this.visualize();
    }

    /**
     * Creates and adds inputpanels based on inputdescriptions.
     */
    @Override
    public void prepare() {
        //check the request for correctness
        if (this.request.getInputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load inputs from "
                    + "process description.",
                    JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        for (IInputSpecifier specifier : this.request.getInputs()) {
            if (specifier instanceof InputLiteralDataSpecifier) {

                InputLiteralForm pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputLiteralDataArgument arg = (InputLiteralDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputLiteralForm((InputLiteralDataSpecifier) specifier, arg);
                } else {
                    pan = new InputLiteralForm((InputLiteralDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan,
                        TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            } else if (specifier instanceof InputComplexDataSpecifier) {
                InputComplexForm pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputComplexDataArgument arg = (InputComplexDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputComplexForm((InputComplexDataSpecifier) specifier, arg);

                } else {
                    pan = new InputComplexForm((InputComplexDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan,
                        TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);

            } else if (specifier instanceof InputBoundingBoxDataSpecifier) {
                InputBBoxForm pan;
                if (this.request.getInputArguments().containsKey(specifier.getIdentifier())) {
                    InputBoundingBoxDataArgument arg = (InputBoundingBoxDataArgument) this.request.getInputArguments().get(specifier.getIdentifier());
                    pan = new InputBBoxForm((InputBoundingBoxDataSpecifier) specifier, arg);
                } else {
                    pan = new InputBBoxForm((InputBoundingBoxDataSpecifier) specifier);
                }
                TitledComponent tc = new TitledComponent(pan.getTitle(), pan,
                        TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            }
        }

    }

    /**
     * Creates and adds inputpanels based on inputdescriptions.
     */
    @Override
    public void visualize() {
        //check the request for correctness
        if (this.request.getInputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load inputs from "
                    + "process description.",
                    JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        JPanel inputsPanel = new JPanel();

        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL};

        double innersize[] = new double[panels.size()];
        for (int i = 0;
                i < panels.size();
                i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;

        TableLayout layout = new TableLayout(size);

        inputsPanel.setLayout(layout);

        int i = 0;
        for (JPanel panel : this.panels) {
            String c = "0," + i;
            inputsPanel.add(panel, c);
            i++;
        }
        String c = "0," + i + 1;
        inputsPanel.add(new JPanel(), c);

        if (this.panels.size() <= 2) {
            this.expandButton.setText(AppConstants.DIALOG__BTN_COLLAPSE_ALL);
            this.expand = true;
            for (TitledComponent inputpanel : this.panels) {
                inputpanel.setFolded(false);
            }
        }
        this.inputsPanelScrollPane.setViewportView(inputsPanel);
    }

    /**
     * Transcodes the inputs panel-wise into an executerequest actualinputs.
     */
    @Override
    public void updateRequest() {
        HashMap<String, IInputArgument> theinputs = new HashMap<>();

        for (TitledComponent panel : this.panels) {

            if (panel.getComponent() instanceof InputComplexForm) {
                InputComplexForm pan = (InputComplexForm) panel.getComponent();
                InputComplexDataSpecifier specifier = pan.getSpecifier();

                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, AppConstants.DIALOG_VALIDATION_MISSING_INPUT
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
            } else if (panel.getComponent() instanceof InputLiteralForm) {
                InputLiteralForm pan = (InputLiteralForm) panel.getComponent();
                InputLiteralDataSpecifier specifier = pan.getSpecifier();
                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            AppConstants.DIALOG_VALIDATION_MISSING_INPUT
                            + pan.getSpecifier().getIdentifier());
                    break;
                } else if (!pan.isMandatory() & pan.getText().isEmpty()) {
                    //nothing to do
                } else {
                    InputLiteralDataArgument param = new InputLiteralDataArgument(specifier, pan.getText());
                    theinputs.put(param.getIdentifier(), param);
                }
            } else if (panel.getComponent() instanceof InputBBoxForm) {
                InputBBoxForm pan = (InputBBoxForm) panel.getComponent();
                InputBoundingBoxDataSpecifier specifier = pan.getSpecifier();
                if (pan.isMandatory() & pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            AppConstants.DIALOG_VALIDATION_MISSING_INPUT
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
        for (TitledComponent panel : this.panels) {
            if (panel.getComponent() instanceof InputComplexForm) {
                InputComplexForm pan = (InputComplexForm) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            AppConstants.DIALOG_VALIDATION_MISSING_INPUT
                            + pan.getSpecifier().getIdentifier());
                    return false;
                }
            } else if (panel.getComponent() instanceof InputLiteralForm) {
                InputLiteralForm pan = (InputLiteralForm) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            AppConstants.DIALOG_VALIDATION_MISSING_INPUT
                            + pan.getSpecifier().getIdentifier());
                    return false;
                }
            } else if (panel.getComponent() instanceof InputBBoxForm) {
                InputBBoxForm pan = (InputBBoxForm) panel.getComponent();
                //this parameter is optional
                if (pan.getSpecifier().getMinOccur() == 0) {
                    return true;
                }
                if (pan.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            AppConstants.DIALOG_VALIDATION_MISSING_INPUT
                            + pan.getSpecifier().getIdentifier());
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        selectedServer = new javax.swing.JLabel();
        selectedProcess = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        expandButton = new javax.swing.JButton();
        inputsPanelScrollPane = new javax.swing.JScrollPane();
        jSeparator1 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Please provide inputdata for ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedServer, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedProcess, gridBagConstraints);

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
        jPanel1.add(selectedServerLabel, gridBagConstraints);

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
        jPanel1.add(selectedProcessLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(85, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(85, 100));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        expandButton.setText("Expand all");
        expandButton.setMinimumSize(new java.awt.Dimension(70, 32));
        expandButton.setPreferredSize(new java.awt.Dimension(70, 32));
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
        jPanel2.add(expandButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        add(jPanel2, gridBagConstraints);

        inputsPanelScrollPane.setBorder(null);
        inputsPanelScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inputsPanelScrollPane.setViewportBorder(null);
        inputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 550));
        inputsPanelScrollPane.setPreferredSize(new java.awt.Dimension(610, 700));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(inputsPanelScrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jSeparator1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if (this.expand == true) {
            for (TitledComponent tc : this.panels) {
                tc.fold();
            }
            this.expand = false;
            this.expandButton.setText(AppConstants.DIALOG_BTN_EXPAND_ALL);
            return;
        }

        for (TitledComponent tc : this.panels) {
            tc.unfold();
            this.expand = true;
            this.expandButton.setText(AppConstants.DIALOG__BTN_COLLAPSE_ALL);
        }
    }//GEN-LAST:event_expandButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expandButton;
    private javax.swing.JScrollPane inputsPanelScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
