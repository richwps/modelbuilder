package de.hsos.richwps.mb.execView.dialog.components.renderer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.border.TitledBorder;

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
        this.setBorder(new TitledBorder(identifier));
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

        copyToClipBoard = new javax.swing.JButton();
        value = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(600, 400));
        setPreferredSize(new java.awt.Dimension(600, 400));
        setLayout(new java.awt.GridBagLayout());

        copyToClipBoard.setText("Copy To Clipboard");
        copyToClipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyToClipBoardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(copyToClipBoard, gridBagConstraints);

        value.setEditable(false);
        value.setColumns(20);
        value.setLineWrap(true);
        value.setRows(10);
        value.setMinimumSize(new java.awt.Dimension(450, 300));
        value.setPreferredSize(new java.awt.Dimension(450, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
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
    private javax.swing.JTextArea value;
    // End of variables declaration//GEN-END:variables
}
