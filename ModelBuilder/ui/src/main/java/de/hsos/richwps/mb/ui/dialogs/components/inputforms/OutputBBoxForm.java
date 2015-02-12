package de.hsos.richwps.mb.ui.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.OutputBoundingBoxDataDescription;
import java.util.List;
import javax.swing.border.TitledBorder;

/**
 * Shows title & abstract of BBOutput and allows selection of type.
 * @author caduevel
 */
public class OutputBBoxForm extends javax.swing.JPanel {

    private final OutputBoundingBoxDataDescription description;

    private final String id;

    /**
     * Creates new form OutputsParamPanel
     * @param description
     */
    public OutputBBoxForm(
            final OutputBoundingBoxDataDescription description ){
        initComponents();
        this.description = description;

        //SupportedComplexDataType type = description.getComplexOutput();
        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();

        this.selectType.removeAllItems();

        List<String> suppCRS = description.getSupportedCRS();
        String defaultCRS = description.getDefaultCRS();


        //Fill combobox, select defaultCRS
        boolean defaultInSupported = false;
        for (String type : suppCRS) {
            if (type != null && !type.equals("")) {
                this.selectType.addItem(type);
                if (type.equals(defaultCRS)) {
                    defaultInSupported = true;
                    this.selectType.setSelectedItem(type);
                }
            }
        }
        if (!defaultInSupported) {
            this.selectType.addItem(defaultCRS);
            this.selectType.setSelectedItem(defaultCRS);
        }
        
        //FIXME
        this.id = theidentifier;
        //this.identifier.setText(theidentifier);

        //this.setBorder(new TitledBorder(theidentifier));
        this.titleValue.setText(thetitel);
        this.abstractValue.setText(theabstract);
    }

    /**
     *
     * @return
     */
    public OutputBoundingBoxDataDescription getDescription() {
        return this.description;
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return this.selectOutput.isSelected();
    }

    public void setSelected(){
        this.selectOutput.setSelected(true);
    }
    
     public void setUnselected(){
        this.selectOutput.setSelected(false);
    }
    /**
     *
     * @return
     */
    public String getType() {
        return this.selectType.getSelectedItem().toString();
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

        selectType = new javax.swing.JComboBox();
        titleLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        abstractLabel = new javax.swing.JLabel();
        abstractValue = new javax.swing.JTextArea();
        titleValue = new javax.swing.JTextArea();
        selectOutput = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(550, 200));
        setPreferredSize(new java.awt.Dimension(500, 250));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "$supported-crs-types" }));
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
        typeLabel.setText("CRS-Type:");
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

        selectOutput.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectOutput, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.JCheckBox selectOutput;
    private javax.swing.JComboBox selectType;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
}
