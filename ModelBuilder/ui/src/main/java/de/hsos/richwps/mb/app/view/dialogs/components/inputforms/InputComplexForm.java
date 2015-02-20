package de.hsos.richwps.mb.app.view.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.values.InputComplexDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputComplexDataDescription;

/**
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class InputComplexForm extends javax.swing.JPanel {

    private String id;
    private InputComplexDataDescription description;
    private boolean isMandatory = false;

    /**
     * Creates new form ComplexInput
     *
     * @param description the description of the complexinputdata.
     */
    public InputComplexForm(final InputComplexDataDescription description) {
        initComponents();
        this.description = description;

        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();

        this.selectType.removeAllItems();

        for (java.util.List type : description.getTypes()) {

            String amimetype = (String) type.get(InputComplexDataDescription.mimetype_IDX);
            String aschema = (String) type.get(InputComplexDataDescription.schema_IDX);
            String aencoding = (String) type.get(InputComplexDataDescription.encoding_IDX);
            String line = "";

            if (description.isDefaultType(type)) {
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
        String occurstxt = "Min: " + this.description.getMinOccur()
                + " Max: " + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            this.isMandatory = false;
        } else {
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);
    }
    
    /**
     * Returns a title for this form.
     * @return title.
     */
    public String getTitle() {
        String title="";
        if (this.description.getMinOccur() == 0) {
            title = "ComplexData " + this.description.getIdentifier();
            this.isMandatory = false;
        } else {
            title = "ComplexData " + this.description.getIdentifier()+" (required)";;
        }
        return title;
    }
    
    
    /**
     * Creates new form ComplexInput
     *
     * @param description the description of the complexinputdata.
     */
    public InputComplexForm(final InputComplexDataDescription description, InputComplexDataValue value) {
        initComponents();
        this.description = description;

        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();

        this.selectType.removeAllItems();

        for (java.util.List type : description.getTypes()) {

            String amimetype = (String) type.get(InputComplexDataDescription.mimetype_IDX);
            String aschema = (String) type.get(InputComplexDataDescription.schema_IDX);
            String aencoding = (String) type.get(InputComplexDataDescription.encoding_IDX);
            String line = "";

            if (description.isDefaultType(type)) {
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
        String occurstxt = "Min: " + this.description.getMinOccur()
                + " Max: " + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            //this.setBorder(new TitledBorder("(OPTIONAL) " + theidentifier));
            this.isMandatory = false;
        } else {
            //this.setBorder(new TitledBorder("(MANDATORY) " + theidentifier));
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);
        this.value.setText(value.getURL());;
    }

    /**
     *
     * @return
     */
    public InputComplexDataDescription getDescription() {
        return this.description;
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
        return description.getTypes().get(idx);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        titleLabel = new javax.swing.JLabel();
        titleValue = new javax.swing.JTextArea();
        abstractLabel = new javax.swing.JLabel();
        abstractValue = new javax.swing.JTextArea();
        occursLabel = new javax.swing.JLabel();
        occurs = new javax.swing.JLabel();
        selectTypeLabel = new javax.swing.JLabel();
        selectType = new javax.swing.JComboBox();
        modeLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        selectByValue = new javax.swing.JRadioButton();
        selectByReference = new javax.swing.JRadioButton();
        valueLabel = new javax.swing.JLabel();
        value = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 300));
        setLayout(new java.awt.GridLayout(6, 2, -400, 0));

        titleLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        titleLabel.setText("Title:");
        add(titleLabel);

        titleValue.setEditable(false);
        titleValue.setBackground(java.awt.SystemColor.control);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(250, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        add(titleValue);

        abstractLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abstractLabel.setText("Abstract:");
        add(abstractLabel);

        abstractValue.setEditable(false);
        abstractValue.setBackground(java.awt.SystemColor.control);
        abstractValue.setColumns(20);
        abstractValue.setLineWrap(true);
        abstractValue.setRows(2);
        abstractValue.setMinimumSize(new java.awt.Dimension(250, 32));
        abstractValue.setPreferredSize(new java.awt.Dimension(300, 32));
        add(abstractValue);

        occursLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        occursLabel.setText("Occurs:");
        add(occursLabel);

        occurs.setText(".");
        add(occurs);

        selectTypeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectTypeLabel.setLabelFor(selectType);
        selectTypeLabel.setText("Type:");
        add(selectTypeLabel);

        selectType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selectType.setPreferredSize(new java.awt.Dimension(450, 50));
        add(selectType);

        modeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        modeLabel.setText("Provide by:");
        add(modeLabel);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        buttonGroup1.add(selectByValue);
        selectByValue.setText("value");
        selectByValue.setEnabled(false);
        jPanel1.add(selectByValue);

        buttonGroup1.add(selectByReference);
        selectByReference.setSelected(true);
        selectByReference.setText("reference (KVP)");
        jPanel1.add(selectByReference);

        add(jPanel1);

        valueLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel.setLabelFor(value);
        valueLabel.setText("Input (URL):");
        add(valueLabel);

        value.setMinimumSize(new java.awt.Dimension(450, 27));
        value.setPreferredSize(new java.awt.Dimension(450, 27));
        add(value);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
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
