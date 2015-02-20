package de.hsos.richwps.mb.app.view.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.dialogs.components.inputforms.OutputBBoxForm;
import de.hsos.richwps.mb.app.view.dialogs.components.inputforms.OutputComplexForm;
import de.hsos.richwps.mb.app.view.dialogs.components.inputforms.OutputLiteralForm;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputComplexDataDescription;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IOutputDescription;
import de.hsos.richwps.mb.richWPS.entity.IOutputValue;
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
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
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

        final String selectedprocess = this.request.getIdentifier();
        this.stepDescriptionLabel.setText("Please select required outputs: "+selectedprocess+".");
        
        Border paddingBorder = BorderFactory.createEmptyBorder(0,0,25,0);
        this.stepDescriptionLabel.setBorder(paddingBorder);

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

        for (IOutputValue description : this.request.getOutputs()) {
            if (description instanceof OutputLiteralDataDescription) {
                OutputLiteralForm pan = new OutputLiteralForm((OutputLiteralDataDescription) description);
                TitledComponent tc = new TitledComponent(description.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            } else if (description instanceof OutputComplexDataDescription) {
                OutputComplexForm pan = new OutputComplexForm((OutputComplexDataDescription) description);
                TitledComponent tc = new TitledComponent(description.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.fold();
                tc.setTitleBold();
                this.panels.add(tc);
            } else if (description instanceof OutputBoundingBoxDataDescription) {
                OutputBBoxForm pan = new OutputBBoxForm((OutputBoundingBoxDataDescription) description);
                TitledComponent tc = new TitledComponent(description.getIdentifier(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
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
        HashMap<String, IOutputDescription> theoutputs = new HashMap<>();

        for (TitledComponent panel : this.panels) {

            if (panel.getComponent() instanceof OutputComplexForm) {

                OutputComplexForm pan = (OutputComplexForm) panel.getComponent();
                if (pan.isSelected()) {
                    OutputComplexDataDescription description = pan.getDescription();
                    OutputComplexDataValue value = new OutputComplexDataValue(description);

                    Boolean asRef = pan.asReference();
                    value.setAsReference(asRef);
                    List type = pan.getType();
                    String amimetype = (String) type.get(OutputComplexDataDescription.mimetype_IDX);
                    String aschema = (String) type.get(OutputComplexDataDescription.schema_IDX);
                    String aencoding = (String) type.get(OutputComplexDataDescription.encoding_IDX);

                    value.setMimetype(amimetype);
                    value.setSchema(aschema);
                    value.setEncoding(aencoding);

                    theoutputs.put(value.getIdentifier(), value);
                }
            } else if (panel.getComponent() instanceof OutputLiteralForm) {
                OutputLiteralForm pan = (OutputLiteralForm) panel.getComponent();

                if (pan.isSelected()) {
                    OutputLiteralDataDescription description = pan.getDescription();
                    OutputLiteralDataValue value = new OutputLiteralDataValue(description);
                    theoutputs.put(value.getIdentifier(), value);
                }

            } else if (panel.getComponent() instanceof OutputBBoxForm) {
                OutputBBoxForm pan = (OutputBBoxForm) panel.getComponent();

                if (pan.isSelected()) {
                    OutputBoundingBoxDataDescription description = pan.getDescription();
                    OutputBoundingBoxDataValue value = new OutputBoundingBoxDataValue(description);
                    theoutputs.put(value.getIdentifier(), value);
                }
            }

        }
        this.request.setOutputValues(theoutputs);
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

        stepDescriptionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        selectAllButton = new javax.swing.JButton();
        expandButton = new javax.swing.JButton();
        outputsPanelScrollPane = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(625, 667));
        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.BorderLayout());

        stepDescriptionLabel.setFont(new java.awt.Font("Droid Sans", 1, 14)); // NOI18N
        stepDescriptionLabel.setText("Please select required outputs.");
        add(stepDescriptionLabel, java.awt.BorderLayout.NORTH);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        selectAllButton.setText("Select All");
        selectAllButton.setMaximumSize(new java.awt.Dimension(80, 32));
        selectAllButton.setMinimumSize(new java.awt.Dimension(70, 32));
        selectAllButton.setPreferredSize(new java.awt.Dimension(70, 32));
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(selectAllButton);

        expandButton.setText("Expand all");
        expandButton.setMaximumSize(new java.awt.Dimension(80, 32));
        expandButton.setMinimumSize(new java.awt.Dimension(70, 32));
        expandButton.setPreferredSize(new java.awt.Dimension(70, 32));
        expandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(expandButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        outputsPanelScrollPane.setBorder(null);
        outputsPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputsPanelScrollPane.setViewportBorder(null);
        outputsPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 550));
        outputsPanelScrollPane.setName(""); // NOI18N
        outputsPanelScrollPane.setPreferredSize(null);
        add(outputsPanelScrollPane, java.awt.BorderLayout.CENTER);
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

    /**
     * Indicator if resizable.
     *
     * @return indicator
     */
    @Override
    public boolean isResizeable() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton expandButton;
    private javax.swing.JScrollPane outputsPanelScrollPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JLabel stepDescriptionLabel;
    // End of variables declaration//GEN-END:variables

}
