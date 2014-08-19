package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.richWPS.entity.specifier.OutputLiteralDataSpecifier;
import javax.swing.border.TitledBorder;

/**
 *
 * @author dalcacer
 */
public class OutputLiteralData extends javax.swing.JPanel {
    
    private OutputLiteralDataSpecifier specifier;
    
    /**
     * Creates new form OutputLiteralData
     */
    public OutputLiteralData(final OutputLiteralDataSpecifier specifier) {
        this.specifier = specifier;
        initComponents();

        //SupportedComplexDataType type = description.getComplexOutput();
        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();
        
        this.titleValue.setText(thetitel);
        
        this.abstractValue.setText(theabstract);

        this.setBorder(new TitledBorder(theidentifier));
        
    }

    
    public OutputLiteralDataSpecifier getSpecifier(){
        return this.specifier;
    }
    
    public boolean isSelected(){
        return this.selectOutput.isSelected();
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

        selectOutput = new javax.swing.JCheckBox();
        titleLabel = new javax.swing.JLabel();
        abstractLabel = new javax.swing.JLabel();
        titleValue = new javax.swing.JTextArea();
        abstractValue = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(550, 113));
        setPreferredSize(new java.awt.Dimension(600, 200));
        setLayout(new java.awt.GridBagLayout());

        selectOutput.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectOutput, gridBagConstraints);

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setLabelFor(titleValue);
        titleLabel.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleLabel, gridBagConstraints);

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setLabelFor(abstractValue);
        abstractLabel.setText("Abstract:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractLabel, gridBagConstraints);

        titleValue.setEditable(false);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(250, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleValue, gridBagConstraints);

        abstractValue.setEditable(false);
        abstractValue.setColumns(20);
        abstractValue.setLineWrap(true);
        abstractValue.setRows(2);
        abstractValue.setMinimumSize(new java.awt.Dimension(250, 32));
        abstractValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractValue, gridBagConstraints);

        jLabel1.setMinimumSize(new java.awt.Dimension(450, 1));
        jLabel1.setPreferredSize(new java.awt.Dimension(450, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox selectOutput;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    // End of variables declaration//GEN-END:variables
}
