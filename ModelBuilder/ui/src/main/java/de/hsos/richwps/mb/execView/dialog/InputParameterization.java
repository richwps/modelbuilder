package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.execView.dialog.components.InputLiteralData;
import de.hsos.richwps.mb.execView.dialog.components.InputComplexData;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IInputArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.IInputSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputLiteralDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputLiteralDataSpecifier;
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
public class InputParameterization extends ADialogPanel {

    private List<JPanel> inputs;
    private RichWPSProvider provider;
    private ExecuteRequest dto;

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
     * @param dto
     */
    public InputParameterization(final RichWPSProvider provider, ExecuteRequest dto) {
        this.provider = provider;
        this.dto = dto;
        initComponents();
        this.inputs = new ArrayList<>();
        String selectedserver = this.dto.getEndpoint();
        String selectedprocess = this.dto.getIdentifier();
        this.selectedServer.setText(selectedserver);
        this.selectedProcess.setText(selectedprocess);
        this.provider.describeProcess(this.dto);
        this.showInputs();
    }

    private void showInputs() {
        if (this.dto.getInputs().isEmpty()) {
            JOptionPane optionPane = new JOptionPane("Unable to load inputs.", JOptionPane.WARNING_MESSAGE);
            optionPane.setVisible(true);
            return;
        }
        for (IInputSpecifier specifier : this.dto.getInputs()) {
            if (specifier instanceof InputLiteralDataSpecifier) {
                this.inputs.add(new InputLiteralData((InputLiteralDataSpecifier) specifier));
            } else if (specifier instanceof InputComplexDataSpecifier) {
                this.inputs.add(new InputComplexData((InputComplexDataSpecifier) specifier));
            }
            //FIXME BoundingBox
        }

        this.jScrollPane1.setAlignmentX(javax.swing.JScrollPane.LEFT_ALIGNMENT);
        this.jScrollPane1.setAlignmentY(javax.swing.JScrollPane.TOP_ALIGNMENT);

        JPanel inputsPanel = new JPanel();

        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL};

        double innersize[] = new double[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;

        TableLayout layout = new TableLayout(size);
        inputsPanel.setLayout(layout);

        int i = 0;
        for (JPanel panel : this.inputs) {
            String c = "0," + i;
            inputsPanel.add(panel, c);
            i++;
        }
        this.jScrollPane1.setViewportView(inputsPanel);
    }

    /**
     *
     */
    @Override
    public void updateDTO() {
        HashMap<String, IInputArgument> theinputs = new HashMap<>();

        for (JPanel panel : this.inputs) {

            if (panel instanceof InputComplexData) {
                InputComplexData pan = (InputComplexData) panel;
                InputComplexDataSpecifier specifier = pan.getSpecifier();

                List type = pan.getMimeType();

                String amimetype = (String) type.get(InputComplexDataSpecifier.mimetype_IDX);
                String aschema = (String) type.get(InputComplexDataSpecifier.schema_IDX);
                String aencoding = (String) type.get(InputComplexDataSpecifier.encoding_IDX);

                InputComplexDataArgument param = null;
                if (pan.isReference()) {
                    param = new InputComplexDataArgument(specifier);
                    String url = pan.getValue();
                    param.setURL(url);
                } else {
                    throw new UnsupportedOperationException("Not implemented, yet.");
                }

                param.setMimeType(amimetype);
                if (aschema != null) {
                    System.out.println(aschema);
                    param.setSchema(aschema);
                }
                if (aencoding != null) {
                    System.out.println(aencoding);
                    param.setEncoding(aencoding);
                }

                theinputs.put(param.getIdentifier(), param);
            } else if (panel instanceof InputLiteralData) {
                InputLiteralData pan = (InputLiteralData) panel;
                InputLiteralDataSpecifier specifier = pan.getSpecifier();
                InputLiteralDataArgument param = new InputLiteralDataArgument(specifier, pan.getText());
                theinputs.put(param.getIdentifier(), param);
            }
            //FIXME BoundingBox
        }
        this.dto.setInputArguments(theinputs);
    }

    /**
     *
     * @return
     */
    public ExecuteRequest getDTO() {
        return this.dto;
    }

    /**
     *
     * @return
     */
    public boolean isValidInput() {
        for (JPanel panel : this.inputs) {
            if (panel instanceof InputComplexData) {
                InputComplexData pan = (InputComplexData) panel;
                //this parameter is optional
                if(pan.getSpecifier().getMinOccur()==0){
                    return true;
                }
                if (pan.getValue().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please provide input for " + pan.getSpecifier().getIdentifier());
                    return false;
                }
            } else if (panel instanceof InputLiteralData) {
                InputLiteralData pan = (InputLiteralData) panel;
                //this parameter is optional
                if(pan.getSpecifier().getMinOccur()==0){
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
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedServerLabel = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();

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

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(610, 600));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(610, 600));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

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
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
