package de.hsos.richwps.mb.ui.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputBoundingBoxDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputBoundingBoxDataSpecifier;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Shows title and abstract of BBInput and allows selection of type and value.
 *
 * @author caduevel
 */
public class InputBBoxForm extends javax.swing.JPanel {

    public class CoordinateVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {

            String text = null;

            if (input instanceof JTextField) {
                text = ((JTextField) input).getText();
            }

            try {
                Double.parseDouble(text);
            } catch (NumberFormatException e) {
                return false;
            }

            return true;
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            boolean valid = verify(input);
            if (!valid) {
                JOptionPane.showMessageDialog(null, "Invalid value.");
            }

            return valid;
        }
    }

    
    /**
     * id For input-identification in InputParameterization.
     */
    private String id;

    /**
     * specifier For usage in InputParameterization.
     */
    private InputBoundingBoxDataSpecifier specifier;

    private boolean isMandatory = false;

    /**
     *
     * @param specifier
     */
    public InputBBoxForm(final InputBoundingBoxDataSpecifier specifier) {
        initComponents();

        InputVerifier verifier = new CoordinateVerifier();
        this.lowerCornerXCoords.setInputVerifier(verifier);
        this.lowerCornerYCoords.setInputVerifier(verifier);
        this.upperCornerXCoords.setInputVerifier(verifier);
        this.upperCornerYCoords.setInputVerifier(verifier);

        this.specifier = specifier;
        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();
        String defaultvalue = this.specifier.getDefaultCRS();

        this.id = theidentifier;

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        String occurstxt = "Min: " + this.specifier.getMinOccur() + " Max: "
                + this.specifier.getMaxOccur();
        if (this.specifier.getMinOccur() == 0) {
            this.isMandatory = false;
        } else {
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

        this.selectType.removeAllItems();

        List<String> suppCRS = specifier.getSupportedCRS();
        String defaultCRS = specifier.getDefaultCRS();

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
    }

      /**
     * Returns a title for this form.
     * @return title.
     */
     public String getTitle() {
        String title="";
        if (this.specifier.getMinOccur() == 0) {
            title = "BBox " + this.specifier.getIdentifier();
            this.isMandatory = false;
        } else {
            title = "BBox " + this.specifier.getIdentifier()+" (required)";
        }
        return title;
    }
     
    /**
     *
     * @param specifier
     */
    public InputBBoxForm(final InputBoundingBoxDataSpecifier specifier, InputBoundingBoxDataArgument argument) {
        initComponents();

        InputVerifier verifier = new CoordinateVerifier();
        this.lowerCornerXCoords.setInputVerifier(verifier);
        this.lowerCornerYCoords.setInputVerifier(verifier);
        this.upperCornerXCoords.setInputVerifier(verifier);
        this.upperCornerYCoords.setInputVerifier(verifier);

        this.specifier = specifier;
        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();
        String defaultvalue = this.specifier.getDefaultCRS();

        this.id = theidentifier;

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        String occurstxt = "Min: " + this.specifier.getMinOccur() + " Max: "
                + this.specifier.getMaxOccur();
        if (this.specifier.getMinOccur() == 0) {
            //this.setBorder(new TitledBorder("(OPTIONAL) " + theidentifier));
            this.isMandatory = false;
        } else {
            //this.setBorder(new TitledBorder("(MANDATORY) " + theidentifier));
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

        this.selectType.removeAllItems();

        List<String> suppCRS = specifier.getSupportedCRS();
        String defaultCRS = specifier.getDefaultCRS();

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
        String coords = argument.getValue();
        String[] split = coords.split(",");
        String lower[] = split[0].split(" ");
        String upper[] = split[1].split(" ");
        this.lowerCornerXCoords.setText(lower[0]);
        this.lowerCornerYCoords.setText(lower[1]);
        this.upperCornerXCoords.setText(upper[0]);
        this.upperCornerYCoords.setText(upper[1]);
    }

    /**
     *
     * @return
     */
    public InputBoundingBoxDataSpecifier getSpecifier() {
        return this.specifier;
    }

    /**
     *
     * @return
     */
    public String getText() {
        String text;
        text = this.lowerCornerXCoords.getText() + " ";
        text += this.lowerCornerYCoords.getText() + ",";
        text += this.upperCornerXCoords.getText() + " ";
        text += this.upperCornerYCoords.getText();
        return text;
    }

    public String getCRS() {
        return (String) this.selectType.getSelectedItem();
    }

    public boolean isMandatory() {
        return this.isMandatory;
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

        abstractLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        titleValue = new javax.swing.JTextArea();
        abstractValue = new javax.swing.JTextArea();
        occursLabel = new javax.swing.JLabel();
        occurs = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        selectType = new javax.swing.JComboBox();
        lowerCornerXCoords = new javax.swing.JTextField();
        lowerCornerYCoords = new javax.swing.JTextField();
        upperCornerXCoords = new javax.swing.JTextField();
        upperCornerYCoords = new javax.swing.JTextField();
        valueLabel1 = new javax.swing.JLabel();
        valueLabel2 = new javax.swing.JLabel();
        valueLabel3 = new javax.swing.JLabel();
        valueLabel4 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 250));
        setLayout(new java.awt.GridBagLayout());

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setLabelFor(abstractValue);
        abstractLabel.setText("Abstract:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractLabel, gridBagConstraints);

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setLabelFor(titleValue);
        titleLabel.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleLabel, gridBagConstraints);

        titleValue.setEditable(false);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(16, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleValue, gridBagConstraints);

        abstractValue.setEditable(false);
        abstractValue.setColumns(20);
        abstractValue.setLineWrap(true);
        abstractValue.setRows(2);
        abstractValue.setMinimumSize(new java.awt.Dimension(16, 32));
        abstractValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractValue, gridBagConstraints);

        occursLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        occursLabel.setText("Occurs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(occursLabel, gridBagConstraints);

        occurs.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(occurs, gridBagConstraints);

        typeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        typeLabel.setLabelFor(selectType);
        typeLabel.setText("CRS-Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(typeLabel, gridBagConstraints);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "$Supported-CRS-Types" }));
        selectType.setMinimumSize(new java.awt.Dimension(16, 27));
        selectType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectType, gridBagConstraints);

        lowerCornerXCoords.setText("-2.0");
        lowerCornerXCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        lowerCornerXCoords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowerCornerXCoordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lowerCornerXCoords, gridBagConstraints);
        lowerCornerXCoords.getAccessibleContext().setAccessibleName("lowerCornerXCoords");

        lowerCornerYCoords.setText("-1.0");
        lowerCornerYCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lowerCornerYCoords, gridBagConstraints);
        lowerCornerYCoords.getAccessibleContext().setAccessibleName("lowerCornerYCoords");

        upperCornerXCoords.setText("3.0");
        upperCornerXCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(upperCornerXCoords, gridBagConstraints);
        upperCornerXCoords.getAccessibleContext().setAccessibleName("upperCornerXCoords");

        upperCornerYCoords.setText("4.0");
        upperCornerYCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(upperCornerYCoords, gridBagConstraints);
        upperCornerYCoords.getAccessibleContext().setAccessibleName("upperCornerYCoords");

        valueLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel1.setLabelFor(lowerCornerXCoords);
        valueLabel1.setText("LowerCorner Coordinates X:");
        valueLabel1.setToolTipText("LowerCorner Coordinates X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel1, gridBagConstraints);
        valueLabel1.getAccessibleContext().setAccessibleDescription("");

        valueLabel2.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel2.setLabelFor(lowerCornerYCoords);
        valueLabel2.setText("Y:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel2, gridBagConstraints);

        valueLabel3.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel3.setLabelFor(upperCornerXCoords);
        valueLabel3.setText("UpperCorner Coordinates X:");
        valueLabel3.setToolTipText("UpperCorner Coordinates X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel3, gridBagConstraints);

        valueLabel4.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel4.setLabelFor(upperCornerYCoords);
        valueLabel4.setText("Y:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel4, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void selectTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectTypeActionPerformed

    private void lowerCornerXCoordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowerCornerXCoordsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lowerCornerXCoordsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.JTextField lowerCornerXCoords;
    private javax.swing.JTextField lowerCornerYCoords;
    private javax.swing.JLabel occurs;
    private javax.swing.JLabel occursLabel;
    private javax.swing.JComboBox selectType;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JTextField upperCornerXCoords;
    private javax.swing.JTextField upperCornerYCoords;
    private javax.swing.JLabel valueLabel1;
    private javax.swing.JLabel valueLabel2;
    private javax.swing.JLabel valueLabel3;
    private javax.swing.JLabel valueLabel4;
    // End of variables declaration//GEN-END:variables
}
