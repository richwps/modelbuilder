package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputBBoxForm;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputComplexForm;
import de.hsos.richwps.mb.ui.dialogs.components.inputforms.OutputLiteralForm;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IOutputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.values.OutputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputLiteralDataDescription;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 * Dialog for ouput parameterisation.
 *
 * @author dalcacer
 * @version 0.0.2
 * @see OutputBBoxForm
 * @see OutputLiteralForm
 * @see OutputComplexForm
 */
public class OutputPanel extends APanel {

    /**
     * List of displayable output forms.
     */
    private List<TitledComponent> panels;
    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;
    private ExecuteRequest request;
    private boolean allSelected = false;
    private boolean expand = false;

    /**
     * Creates new form OutputParameterization.
     */
    public OutputPanel() {
        initComponents();
    }

    /**
     * Creates new form OutputParameterization
     *
     * @param provider
     * @param request
     */
    public OutputPanel(final RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        this.allSelected = false;
        this.expand = false;
        initComponents();

        final String selectedserver = this.request.getEndpoint();
        final String selectedprocess = this.request.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);

        this.panels = new ArrayList<>();

        if (request instanceof TestRequest) {
            //noop
        } else if (request instanceof ProfileRequest) {
            //noop
        } else {
            //update only if necessary 
            if (!request.isLoaded()) {
                this.provider.perform((DescribeRequest) this.request);
            }
        }
        this.prepare();
        this.visualize();
    }

    /**
     *
     */
    @Override
    public void prepare() {

        if (this.request.getOutputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load outputs from"
                    + "process description.", JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        for (IOutputSpecifier specifier : this.request.getOutputs()) {
            if (specifier instanceof OutputLiteralDataDescription) {
                OutputLiteralForm pan = new OutputLiteralForm((OutputLiteralDataDescription) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            } else if (specifier instanceof OutputComplexDataDescription) {
                OutputComplexForm pan = new OutputComplexForm((OutputComplexDataDescription) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            } else if (specifier instanceof OutputBoundingBoxDataDescription) {
                OutputBBoxForm pan = new OutputBBoxForm((OutputBoundingBoxDataDescription) specifier);
                TitledComponent tc = new TitledComponent(specifier.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            }
        }

    }

    /**
     *
     */
    @Override
    public void visualize() {

        if (this.request.getOutputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load outputs from"
                    + "process description.", JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }

        JPanel outputsPanel = new JPanel();
        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL};

        double innersize[] = new double[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;

        TableLayout layout = new TableLayout(size);
        outputsPanel.setLayout(layout);

        int i = 0;
        for (JPanel panel : this.panels) {
            String c = "0," + i;
            outputsPanel.add(panel, c);
            i++;
        }

        String c = "0," + i + 1;
        outputsPanel.add(new JPanel(), c);

        if (this.panels.size() <= 2) {
            this.expandButton.setText(AppConstants.DIALOG__BTN_COLLAPSE_ALL);
            this.expand = true;
            for (TitledComponent outputpanel : this.panels) {
                outputpanel.setFolded(false);
            }
        }
        this.outputsPanelScrollPane.setViewportView(outputsPanel);
    }

    /**
     * Transcodes the outputs panel-wise into executerequest actualoutputs.
     */
    @Override
    public void updateRequest() {
        HashMap<String, IOutputArgument> theoutputs = new HashMap<>();

        for (TitledComponent panel : this.panels) {

            if (panel.getComponent() instanceof OutputComplexForm) {

                OutputComplexForm pan = (OutputComplexForm) panel.getComponent();
                if (pan.isSelected()) {
                    OutputComplexDataDescription specifier = pan.getSpecifier();
                    OutputComplexDataValue argument = new OutputComplexDataValue(specifier);

                    Boolean asRef = pan.asReference();
                    argument.setAsReference(asRef);
                    List type = pan.getType();
                    String amimetype = (String) type.get(OutputComplexDataDescription.mimetype_IDX);
                    String aschema = (String) type.get(OutputComplexDataDescription.schema_IDX);
                    String aencoding = (String) type.get(OutputComplexDataDescription.encoding_IDX);

                    argument.setMimetype(amimetype);
                    argument.setSchema(aschema);
                    argument.setEncoding(aencoding);

                    theoutputs.put(argument.getIdentifier(), argument);
                }
            } else if (panel.getComponent() instanceof OutputLiteralForm) {
                OutputLiteralForm pan = (OutputLiteralForm) panel.getComponent();

                if (pan.isSelected()) {
                    OutputLiteralDataDescription specifier = pan.getSpecifier();
                    OutputLiteralDataValue argument = new OutputLiteralDataValue(specifier);
                    theoutputs.put(argument.getIdentifier(), argument);
                }

            } else if (panel.getComponent() instanceof OutputBBoxForm) {
                OutputBBoxForm pan = (OutputBBoxForm) panel.getComponent();

                if (pan.isSelected()) {
                    OutputBoundingBoxDataDescription specifier = pan.getSpecifier();
                    OutputBoundingBoxDataValue argument = new OutputBoundingBoxDataValue(specifier);
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
        for (TitledComponent panel : this.panels) {
            if (panel.getComponent() instanceof OutputComplexForm) {
                OutputComplexForm pan = (OutputComplexForm) panel.getComponent();
                if (pan.isSelected()) {
                    someThingSelected = true;
                }

            } else if (panel.getComponent() instanceof OutputLiteralForm) {
                OutputLiteralForm pan = (OutputLiteralForm) panel.getComponent();
                if (pan.isSelected()) {
                    someThingSelected = true;
                }
            } else if (panel.getComponent() instanceof OutputBBoxForm) {
                OutputBBoxForm pan = (OutputBBoxForm) panel.getComponent();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        selectedServer = new javax.swing.JLabel();
        selectedProcess = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        selectAllButton = new javax.swing.JButton();
        expandButton = new javax.swing.JButton();
        outputsPanelScrollPane = new javax.swing.JScrollPane();
        jSeparator1 = new javax.swing.JSeparator();

        setMinimumSize(new java.awt.Dimension(625, 667));
        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Please select required outputs.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedServer, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
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
        gridBagConstraints.gridwidth = 2;
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedProcessLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(85, 100));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        selectAllButton.setText("Select All");
        selectAllButton.setMaximumSize(new java.awt.Dimension(70, 32));
        selectAllButton.setMinimumSize(new java.awt.Dimension(70, 32));
        selectAllButton.setPreferredSize(new java.awt.Dimension(70, 32));
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
        jPanel2.add(selectAllButton, gridBagConstraints);

        expandButton.setText("Expand all");
        expandButton.setMaximumSize(new java.awt.Dimension(70, 32));
        expandButton.setMinimumSize(new java.awt.Dimension(70, 32));
        expandButton.setPreferredSize(new java.awt.Dimension(70, 32));
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
        jPanel2.add(expandButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        add(jPanel2, gridBagConstraints);

        outputsPanelScrollPane.setBorder(null);
        outputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputsPanelScrollPane.setViewportBorder(null);
        outputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 550));
        outputsPanelScrollPane.setPreferredSize(new java.awt.Dimension(610, 700));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(outputsPanelScrollPane, gridBagConstraints);
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

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (allSelected == false) {
            for (TitledComponent panel : this.panels) {
                if (panel.getComponent() instanceof OutputComplexForm) {
                    OutputComplexForm pan = (OutputComplexForm) panel.getComponent();
                    pan.setSelected();

                } else if (panel.getComponent() instanceof OutputLiteralForm) {
                    OutputLiteralForm pan = (OutputLiteralForm) panel.getComponent();
                    pan.setSelected();
                } else if (panel.getComponent() instanceof OutputBBoxForm) {
                    OutputBBoxForm pan = (OutputBBoxForm) panel.getComponent();
                    pan.setSelected();
                }
            }
            allSelected = true;
            this.selectAllButton.setText(AppConstants.DIALOG_BTN_DESELECT_ALL);
            return;
        }

        for (TitledComponent panel : this.panels) {
            if (panel.getComponent() instanceof OutputComplexForm) {
                OutputComplexForm pan = (OutputComplexForm) panel.getComponent();
                pan.setUnselected();

            } else if (panel.getComponent() instanceof OutputLiteralForm) {
                OutputLiteralForm pan = (OutputLiteralForm) panel.getComponent();
                pan.setUnselected();
            } else if (panel.getComponent() instanceof OutputBBoxForm) {
                OutputBBoxForm pan = (OutputBBoxForm) panel.getComponent();
                pan.setSelected();
                pan.setUnselected();
            }
        }
        allSelected = false;
        this.selectAllButton.setText(AppConstants.DIALOG_BTN_SELECT_ALL);
    }//GEN-LAST:event_selectAllButtonActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JScrollPane outputsPanelScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
