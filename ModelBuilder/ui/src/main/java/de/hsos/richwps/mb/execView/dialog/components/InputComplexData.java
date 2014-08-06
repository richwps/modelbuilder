package de.hsos.richwps.mb.execView.dialog.components;

import de.hsos.richwps.mb.richWPS.entity.specifier.InputComplexDataSpecifier;
import javax.swing.border.TitledBorder;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import static net.opengis.wps.x100.DescriptionType.type;

/**
 *
 * @author dalcacer
 */
public class InputComplexData extends javax.swing.JPanel {

    private String id;
    private ComplexDataDescriptionType[] subtypes_;
    private InputComplexDataSpecifier specifier;

    /**
     * Creates new form ComplexInput
     */
    public InputComplexData(final InputComplexDataSpecifier specifier) {
        initComponents();
        this.specifier = specifier;
        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();
        this.selectType.removeAllItems();
        for (String mimetype : specifier.getMimetypes()) {
            this.selectType.addItem(mimetype);
        }

        //fixme
        this.id = theidentifier;
        //this.identifier.setText(theidentifier);

        this.setBorder(new TitledBorder(theidentifier));

        this.hint.setText("Hint: " + thetitel + ", " + theabstract);
    }

    public InputComplexDataSpecifier getSpecifier() {
        return this.specifier;
    }

    public boolean isReference() {
        return this.selectByReference.isSelected();
    }

    public boolean isValue() {
        return this.selectByValue.isSelected();
    }

    public String getMimeType() {
        return (String) this.selectType.getSelectedItem();
    }

    public String getValue() {
        return this.value.getText();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        hint = new javax.swing.JLabel();
        value = new javax.swing.JTextField();
        selectByValue = new javax.swing.JRadioButton();
        selectByReference = new javax.swing.JRadioButton();
        selectType = new javax.swing.JComboBox();

        setBorder(null);
        setLayout(new java.awt.GridBagLayout());

        hint.setText("Hint");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(hint, gridBagConstraints);

        value.setPreferredSize(new java.awt.Dimension(400, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(value, gridBagConstraints);

        buttonGroup1.add(selectByValue);
        selectByValue.setText("by value");
        selectByValue.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectByValue, gridBagConstraints);

        buttonGroup1.add(selectByReference);
        selectByReference.setSelected(true);
        selectByReference.setText("by reference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectByReference, gridBagConstraints);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectType, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel hint;
    private javax.swing.JRadioButton selectByReference;
    private javax.swing.JRadioButton selectByValue;
    private javax.swing.JComboBox selectType;
    private javax.swing.JTextField value;
    // End of variables declaration//GEN-END:variables
}
