package de.hsos.richwps.mb.ui.dialogs.components.inputforms;

import de.hsos.richwps.mb.richWPS.entity.impl.values.InputLiteralDataValue;
import de.hsos.richwps.mb.richWPS.entity.impl.descriptions.InputLiteralDataDescription;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class InputLiteralForm extends javax.swing.JPanel {

    private String id;
    private InputLiteralDataDescription description;
    private boolean isMandatory = false;

    /**
     *
     * @param description
     */
    public InputLiteralForm(InputLiteralDataDescription description) {
        initComponents();
        this.description = description;
        final String theidentifier = description.getIdentifier();
        final String theabstract = description.getAbstract();
        final String thetitel = description.getTitle();
        final String datatype = this.description.getType();
        final String defaultvalue = this.description.getDefaultvalue();
        //FIXME
        this.id = theidentifier;
        //this.identifier.setText(theidentifier+ "("+datatype+"):");

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        final String occurstxt = "Min: " + this.description.getMinOccur() + " Max: " + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            this.isMandatory = false;
        } else {
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

        this.type.setText(datatype);
        this.value.setText(defaultvalue);
    }

    /**
     * Returns a title for this form.
     *
     * @return title.
     */
    public String getTitle() {
        String title = "";
        if (this.description.getMinOccur() == 0) {
            title = "LiteralData " + this.description.getIdentifier();
            this.isMandatory = false;
        } else {
            title = "LiteralData " + this.description.getIdentifier() + " (required)";
        }
        return title;
    }

    /**
     *
     * @param description
     * @param value
     */
    public InputLiteralForm(InputLiteralDataDescription description, InputLiteralDataValue value) {
        initComponents();
        this.description = description;
        String theidentifier = description.getIdentifier();
        String theabstract = description.getAbstract();
        String thetitel = description.getTitle();
        String datatype = this.description.getType();
        String defaultvalue = this.description.getDefaultvalue();
        //FIXME
        this.id = theidentifier;
        //this.identifier.setText(theidentifier+ "("+datatype+"):");

        this.abstractValue.setText(theabstract);
        this.titleValue.setText(thetitel);

        String occurstxt = "Min: " + this.description.getMinOccur() + " Max: " + this.description.getMaxOccur();
        if (this.description.getMinOccur() == 0) {
            //this.setBorder(new TitledBorder("(OPTIONAL) " + theidentifier));
            this.isMandatory = false;
        } else {
            //this.setBorder(new TitledBorder("(MANDATORY) " + theidentifier));
            this.isMandatory = true;
        }
        this.occurs.setText(occurstxt);

        this.type.setText(datatype);
        this.value.setText(value.getValue());
    }

    public void setText(String content) {
        this.value.setText(content);
    }

    /**
     *
     * @return
     */
    public InputLiteralDataDescription getDescription() {
        return this.description;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return this.value.getText();
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

        value = new javax.swing.JTextField();
        abstractLabel = new javax.swing.JLabel();
        titleLabel = new javax.swing.JLabel();
        valueLabel = new javax.swing.JLabel();
        titleValue = new javax.swing.JTextArea();
        abstractValue = new javax.swing.JTextArea();
        occursLabel = new javax.swing.JLabel();
        occurs = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        type = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 200));
        setLayout(new java.awt.GridBagLayout());

        value.setMinimumSize(new java.awt.Dimension(450, 27));
        value.setPreferredSize(new java.awt.Dimension(450, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(value, gridBagConstraints);

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

        valueLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        valueLabel.setLabelFor(value);
        valueLabel.setText("Value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(valueLabel, gridBagConstraints);

        titleValue.setEditable(false);
        titleValue.setBackground(java.awt.Color.white);
        titleValue.setColumns(20);
        titleValue.setLineWrap(true);
        titleValue.setRows(2);
        titleValue.setMinimumSize(new java.awt.Dimension(250, 32));
        titleValue.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(titleValue, gridBagConstraints);

        abstractValue.setEditable(false);
        abstractValue.setBackground(java.awt.Color.white);
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

        occursLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        occursLabel.setText("Occurs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
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

        typeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        typeLabel.setLabelFor(titleValue);
        typeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(typeLabel, gridBagConstraints);

        type.setEditable(false);
        type.setBackground(java.awt.Color.white);
        type.setColumns(20);
        type.setLineWrap(true);
        type.setRows(2);
        type.setMinimumSize(new java.awt.Dimension(250, 32));
        type.setPreferredSize(new java.awt.Dimension(300, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(type, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abstractLabel;
    private javax.swing.JTextArea abstractValue;
    private javax.swing.JLabel occurs;
    private javax.swing.JLabel occursLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextArea titleValue;
    private javax.swing.JTextArea type;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JTextField value;
    private javax.swing.JLabel valueLabel;
    // End of variables declaration//GEN-END:variables
}
