package de.hsos.richwps.mb.app.view.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.values.InputBoundingBoxDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputBoundingBoxDataDescription;
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
     * Description For usage in InputParameterization.
     */
    private InputBoundingBoxDataDescription description;

    private boolean isMandatory = false;

    /**
     *
     * @param description
     */
    public InputBBoxForm(final InputBoundingBoxDataDescription description) {
        initComponents();

        InputVerifier verifier = new CoordinateVerifier();
        this.lowerCornerXCoords.setInputVerifier(verifier);
        this.lowerCornerYCoords.setInputVerifier(verifier);
        this.upperCornerXCoords.setInputVerifier(verifier);
        this.upperCornerYCoords.setInputVerifier(verifier);

        this.description = description;
        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();
        String defaultvalue = this.description.getDefaultCRS();

        this.id = theidentifier;

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        String occurstxt = "Min: " + this.description.getMinOccur() + " Max: "
                + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            this.isMandatory = false;
        } else {
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

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
    }

      /**
     * Returns a title for this form.
     * @return title.
     */
     public String getTitle() {
        String title="";
        if (this.description.getMinOccur() == 0) {
            title = "BBox " + this.description.getIdentifier();
            this.isMandatory = false;
        } else {
            title = "BBox " + this.description.getIdentifier()+" (required)";
        }
        return title;
    }
     
    /**
     *
     * @param description
     */
    public InputBBoxForm(final InputBoundingBoxDataDescription description, InputBoundingBoxDataValue value) {
        initComponents();

        InputVerifier verifier = new CoordinateVerifier();
        this.lowerCornerXCoords.setInputVerifier(verifier);
        this.lowerCornerYCoords.setInputVerifier(verifier);
        this.upperCornerXCoords.setInputVerifier(verifier);
        this.upperCornerYCoords.setInputVerifier(verifier);

        this.description = description;
        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();
        String defaultvalue = this.description.getDefaultCRS();

        this.id = theidentifier;

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        String occurstxt = "Min: " + this.description.getMinOccur() + " Max: "
                + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            //this.setBorder(new TitledBorder("(OPTIONAL) " + theidentifier));
            this.isMandatory = false;
        } else {
            //this.setBorder(new TitledBorder("(MANDATORY) " + theidentifier));
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

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
        String coords = value.getValue();
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
    public InputBoundingBoxDataDescription getDescription() {
        return this.description;
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

        titleLabel = new javax.swing.JLabel();
        titleValue = new javax.swing.JTextArea();
        abstractLabel = new javax.swing.JLabel();
        abstractValue = new javax.swing.JTextArea();
        occursLabel = new javax.swing.JLabel();
        occurs = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        selectType = new javax.swing.JComboBox();
        valueLabel1 = new javax.swing.JLabel();
        lowerCornerXCoords = new javax.swing.JTextField();
        valueLabel2 = new javax.swing.JLabel();
        lowerCornerYCoords = new javax.swing.JTextField();
        valueLabel3 = new javax.swing.JLabel();
        upperCornerXCoords = new javax.swing.JTextField();
        valueLabel4 = new javax.swing.JLabel();
        upperCornerYCoords = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 250));
        setLayout(new java.awt.GridLayout(8, 2, -200, 0));

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setLabelFor(titleValue);
        titleLabel.setText("Title:");
        add(titleLabel);

        titleValue.setEditable(false);
        titleValue.setBackground(java.awt.SystemColor.control);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(16, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        add(titleValue);

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setLabelFor(abstractValue);
        abstractLabel.setText("Abstract:");
        add(abstractLabel);

        abstractValue.setEditable(false);
        abstractValue.setBackground(java.awt.SystemColor.control);
        abstractValue.setColumns(20);
        abstractValue.setLineWrap(true);
        abstractValue.setRows(2);
        abstractValue.setMinimumSize(new java.awt.Dimension(16, 32));
        abstractValue.setPreferredSize(new java.awt.Dimension(300, 32));
        add(abstractValue);

        occursLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        occursLabel.setText("Occurs:");
        add(occursLabel);

        occurs.setText(".");
        add(occurs);

        typeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        typeLabel.setLabelFor(selectType);
        typeLabel.setText("CRS-Type:");
        add(typeLabel);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "$Supported-CRS-Types" }));
        selectType.setMinimumSize(new java.awt.Dimension(16, 27));
        selectType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTypeActionPerformed(evt);
            }
        });
        add(selectType);

        valueLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel1.setLabelFor(lowerCornerXCoords);
        valueLabel1.setText("LowerCorner Coordinates X:");
        valueLabel1.setToolTipText("LowerCorner Coordinates X");
        add(valueLabel1);
        valueLabel1.getAccessibleContext().setAccessibleDescription("");

        lowerCornerXCoords.setText("-2.0");
        lowerCornerXCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        lowerCornerXCoords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowerCornerXCoordsActionPerformed(evt);
            }
        });
        add(lowerCornerXCoords);
        lowerCornerXCoords.getAccessibleContext().setAccessibleName("lowerCornerXCoords");

        valueLabel2.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel2.setLabelFor(lowerCornerYCoords);
        valueLabel2.setText("Y:");
        add(valueLabel2);

        lowerCornerYCoords.setText("-1.0");
        lowerCornerYCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        add(lowerCornerYCoords);
        lowerCornerYCoords.getAccessibleContext().setAccessibleName("lowerCornerYCoords");

        valueLabel3.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel3.setLabelFor(upperCornerXCoords);
        valueLabel3.setText("UpperCorner Coordinates X:");
        valueLabel3.setToolTipText("UpperCorner Coordinates X");
        add(valueLabel3);

        upperCornerXCoords.setText("3.0");
        upperCornerXCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        add(upperCornerXCoords);
        upperCornerXCoords.getAccessibleContext().setAccessibleName("upperCornerXCoords");

        valueLabel4.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel4.setLabelFor(upperCornerYCoords);
        valueLabel4.setText("Y:");
        add(valueLabel4);

        upperCornerYCoords.setText("4.0");
        upperCornerYCoords.setMinimumSize(new java.awt.Dimension(84, 28));
        add(upperCornerYCoords);
        upperCornerYCoords.getAccessibleContext().setAccessibleName("upperCornerYCoords");
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
