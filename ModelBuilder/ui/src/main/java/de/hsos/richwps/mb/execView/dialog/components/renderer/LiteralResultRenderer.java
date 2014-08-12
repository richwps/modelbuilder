package de.hsos.richwps.mb.execView.dialog.components.renderer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author dalcacer
 */
public class LiteralResultRenderer extends javax.swing.JPanel {

    /**
     * Creates new form FeatureCollectionRenderer
     */
    public LiteralResultRenderer() {
        initComponents();
    }

    /**
     * Creates new form FeatureCollectionRenderer
     */
    public LiteralResultRenderer(String identifier, String value) {
        initComponents();
        this.identifier.setText(identifier);
        this.value.setText(value);
        
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

        jLabel1 = new javax.swing.JLabel();
        copyToClipBoard = new javax.swing.JButton();
        identifier = new javax.swing.JLabel();
        value = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(700, 300));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Output:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);

        copyToClipBoard.setText("Copy To Clipboard");
        copyToClipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyToClipBoardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(copyToClipBoard, gridBagConstraints);

        identifier.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(identifier, gridBagConstraints);

        value.setEditable(false);
        value.setColumns(20);
        value.setLineWrap(true);
        value.setRows(10);
        value.setMinimumSize(new java.awt.Dimension(300, 150));
        value.setPreferredSize(new java.awt.Dimension(300, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(value, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void copyToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyToClipBoardActionPerformed
    StringSelection stringSelection = new StringSelection(value.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }//GEN-LAST:event_copyToClipBoardActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyToClipBoard;
    private javax.swing.JLabel identifier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea value;
    // End of variables declaration//GEN-END:variables
}
