package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.richWPS.entity.impl.specifier.OutputComplexDataSpecifier;
import javax.swing.border.TitledBorder;
import net.opengis.wps.x100.ComplexDataDescriptionType;

/**
 *
 * @author dalcacer
 */
public class OutputComplexData extends javax.swing.JPanel {

    private OutputComplexDataSpecifier specifier;

    private String id;
    private ComplexDataDescriptionType[] subtypes_;

    /**
     * Creates new form OutputsParamPanel
     */
    public OutputComplexData(OutputComplexDataSpecifier specifier) {
        initComponents();
        this.specifier = specifier;

        //SupportedComplexDataType type = description.getComplexOutput();
        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();

        this.selectType.removeAllItems();

        for (java.util.List type : specifier.getTypes()) {
            String amimetype = (String) type.get(OutputComplexDataSpecifier.mimetype_IDX);
            String aschema = (String) type.get(OutputComplexDataSpecifier.schema_IDX);
            String aencoding = (String) type.get(OutputComplexDataSpecifier.encoding_IDX);
            String line = "";

            if (specifier.isDefaultType(type)){
                line = "<html><b>" + amimetype + "<br/>Schema: " + aschema + "<br/>Encoding: " + aencoding + "</b></html>";
                this.selectType.addItem(line);
                this.selectType.setSelectedItem(line);
            } else {
                line = "<html>" + amimetype + "<br/>Schema: " + aschema + "<br/>Encoding: " + aencoding + "</html>";
                this.selectType.addItem(line);
            }
        }

        //fixme
        this.id = theidentifier;
        //this.identifier.setText(theidentifier);

        this.setBorder(new TitledBorder(theidentifier));
        this.titleValue.setText(thetitel);
        this.abstractValue.setText(theabstract);
    }

    public OutputComplexDataSpecifier getSpecifier() {
        return this.specifier;
    }

    public boolean isSelected() {
        return this.selectOutput.isSelected();
    }

    public java.util.List getType() {
        int idx = this.selectType.getSelectedIndex();
        return specifier.getTypes().get(idx);
    }

    public boolean asReference() {
        return this.selectAsReference.isSelected();
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
        selectType = new javax.swing.JComboBox();
        selectAsReference = new javax.swing.JCheckBox();
        selectStore = new javax.swing.JCheckBox();
        titleLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        abstractLabel = new javax.swing.JLabel();
        abstractValue = new javax.swing.JTextArea();
        titleValue = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(550, 200));
        setPreferredSize(new java.awt.Dimension(600, 250));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        selectOutput.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectOutput, gridBagConstraints);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selectType.setPreferredSize(new java.awt.Dimension(450, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectType, gridBagConstraints);

        selectAsReference.setSelected(true);
        selectAsReference.setText("As Reference");
        selectAsReference.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectAsReference, gridBagConstraints);

        selectStore.setText("Store");
        selectStore.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectStore, gridBagConstraints);

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleLabel, gridBagConstraints);

        typeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        typeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(typeLabel, gridBagConstraints);

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setText("Abstract:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractLabel, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractValue, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleValue, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.JCheckBox selectAsReference;
    private javax.swing.JCheckBox selectOutput;
    private javax.swing.JCheckBox selectStore;
    private javax.swing.JComboBox selectType;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
