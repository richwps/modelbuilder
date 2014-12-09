package de.hsos.richwps.mb.ui.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.arguments.InputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.specifier.InputComplexDataSpecifier;
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
    private boolean isMandatory = false;

    /**
     * Creates new form ComplexInput
     *
     * @param specifier the specifier of the complexinputdata.
     */
    public InputComplexData(final InputComplexDataSpecifier specifier) {
        initComponents();
        this.specifier = specifier;

        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();

        this.selectType.removeAllItems();

        for (java.util.List type : specifier.getTypes()) {

            String amimetype = (String) type.get(InputComplexDataSpecifier.mimetype_IDX);
            String aschema = (String) type.get(InputComplexDataSpecifier.schema_IDX);
            String aencoding = (String) type.get(InputComplexDataSpecifier.encoding_IDX);
            String line = "";

            if (specifier.isDefaultType(type)) {
                line = "<html><b>" + amimetype + "<br/>Schema: " + aschema
                        + "<br/>Encoding: " + aencoding + "</b></html>";
                this.selectType.addItem(line);
                this.selectType.setSelectedItem(line);
            } else {
                line = "<html>" + amimetype + "<br/>Schema: " + aschema
                        + "<br/>Encoding: " + aencoding + "</html>";
                this.selectType.addItem(line);
            }

        }

        //FIXME
        this.id = theidentifier;
        //this.identifier.setText(theidentifier);

        this.titleValue.setText(thetitel);
        this.abstractValue.setText(theabstract);
        String occurstxt = "Min: " + this.specifier.getMinOccur()
                + " Max: " + this.specifier.getMaxOccur();
        if (this.specifier.getMinOccur() == 0) {
            this.isMandatory = false;
        } else {
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);
    }
    
     public String getTitle() {
        String title="";
        if (this.specifier.getMinOccur() == 0) {
            title = "(OPTIONAL) " + this.specifier.getIdentifier();
            this.isMandatory = false;
        } else {
            title = "(MANDATORY) " + this.specifier.getIdentifier();
        }
        return title;
    }
    
    
    /**
     * Creates new form ComplexInput
     *
     * @param specifier the specifier of the complexinputdata.
     */
    public InputComplexData(final InputComplexDataSpecifier specifier, InputComplexDataArgument argument) {
        initComponents();
        this.specifier = specifier;

        String theidentifier = specifier.getIdentifier();
        String theabstract = specifier.getAbstract();
        String thetitel = specifier.getTitle();

        this.selectType.removeAllItems();

        for (java.util.List type : specifier.getTypes()) {

            String amimetype = (String) type.get(InputComplexDataSpecifier.mimetype_IDX);
            String aschema = (String) type.get(InputComplexDataSpecifier.schema_IDX);
            String aencoding = (String) type.get(InputComplexDataSpecifier.encoding_IDX);
            String line = "";

            if (specifier.isDefaultType(type)) {
                line = "<html><b>" + amimetype + "<br/>Schema: " + aschema
                        + "<br/>Encoding: " + aencoding + "</b></html>";
                this.selectType.addItem(line);
                this.selectType.setSelectedItem(line);
            } else {
                line = "<html>" + amimetype + "<br/>Schema: " + aschema
                        + "<br/>Encoding: " + aencoding + "</html>";
                this.selectType.addItem(line);
            }

        }

        //FIXME
        this.id = theidentifier;
        //this.identifier.setText(theidentifier);

        this.titleValue.setText(thetitel);
        this.abstractValue.setText(theabstract);
        String occurstxt = "Min: " + this.specifier.getMinOccur()
                + " Max: " + this.specifier.getMaxOccur();
        if (this.specifier.getMinOccur() == 0) {
            //this.setBorder(new TitledBorder("(OPTIONAL) " + theidentifier));
            this.isMandatory = false;
        } else {
            //this.setBorder(new TitledBorder("(MANDATORY) " + theidentifier));
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);
        this.value.setText(argument.getURL());;
    }

    /**
     *
     * @return
     */
    public InputComplexDataSpecifier getSpecifier() {
        return this.specifier;
    }

    /**
     *
     * @return
     */
    public boolean isReference() {
        return this.selectByReference.isSelected();
    }

    /**
     *
     * @return
     */
    public boolean isValue() {
        return this.selectByValue.isSelected();
    }

    /**
     *
     * @return
     */
    public java.util.List getMimeType() {
        int idx = this.selectType.getSelectedIndex();
        return specifier.getTypes().get(idx);
    }

    /**
     *
     * @return
     */
    public String getText() {
        return this.value.getText();
    }

    public boolean isMandatory() {
        return isMandatory;
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
        titleLabel = new javax.swing.JLabel();
        value = new javax.swing.JTextField();
        selectByValue = new javax.swing.JRadioButton();
        selectByReference = new javax.swing.JRadioButton();
        selectType = new javax.swing.JComboBox();
        selectTypeLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        abstractLabel = new javax.swing.JLabel();
        modeLabel = new javax.swing.JLabel();
        abstractValue = new javax.swing.JTextArea();
        titleValue = new javax.swing.JTextArea();
        occursLabel = new javax.swing.JLabel();
        occurs = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(500, 250));
        setPreferredSize(new java.awt.Dimension(500, 300));
        setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleLabel, gridBagConstraints);

        value.setMinimumSize(new java.awt.Dimension(450, 27));
        value.setPreferredSize(new java.awt.Dimension(450, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(value, gridBagConstraints);

        buttonGroup1.add(selectByValue);
        selectByValue.setText("value");
        selectByValue.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectByValue, gridBagConstraints);

        buttonGroup1.add(selectByReference);
        selectByReference.setSelected(true);
        selectByReference.setText("reference (KVP)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectByReference, gridBagConstraints);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selectType.setPreferredSize(new java.awt.Dimension(450, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectType, gridBagConstraints);

        selectTypeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectTypeLabel.setLabelFor(selectType);
        selectTypeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectTypeLabel, gridBagConstraints);

        valueLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel.setLabelFor(value);
        valueLabel.setText("Input (URL):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel, gridBagConstraints);

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setText("Abstract:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractLabel, gridBagConstraints);

        modeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        modeLabel.setText("Provide by:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(modeLabel, gridBagConstraints);

        abstractValue.setEditable(false);
        abstractValue.setColumns(20);
        abstractValue.setLineWrap(true);
        abstractValue.setRows(2);
        abstractValue.setMinimumSize(new java.awt.Dimension(250, 32));
        abstractValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(abstractValue, gridBagConstraints);

        titleValue.setEditable(false);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(250, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleValue, gridBagConstraints);

        occursLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        occursLabel.setText("Occurs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(occursLabel, gridBagConstraints);

        occurs.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(occurs, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JLabel occurs;
    private javax.swing.JLabel occursLabel;
    private javax.swing.JRadioButton selectByReference;
    private javax.swing.JRadioButton selectByValue;
    private javax.swing.JComboBox selectType;
    private javax.swing.JLabel selectTypeLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    private javax.swing.JTextField value;
    private javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables
}
