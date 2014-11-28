package de.hsos.richwps.mb.ui.dialogs.components.renderer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.border.TitledBorder;

/**
 *
 * @author dalcacer
 */
public class ExceptionRenderer extends javax.swing.JPanel {

    /**
     * Creates new form FeatureCollectionRenderer
     */
    public ExceptionRenderer() {
        initComponents();
    }

    /**
     * Creates new form FeatureCollectionRenderer
     * @param identifier
     * @param value
     */
    public ExceptionRenderer(String identifier, String value) {
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
        jScrollPane1 = new javax.swing.JScrollPane();
        value = new javax.swing.JTextArea();

        setMinimumSize(new java.awt.Dimension(600, 600));
        setPreferredSize(new java.awt.Dimension(600, 600));
        setLayout(new java.awt.GridBagLayout());

        copyToClipBoard.setText("Copy To Clipboard");
        copyToClipBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyToClipBoardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(copyToClipBoard, gridBagConstraints);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Exception:"));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(500, 500));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 500));

        value.setEditable(false);
        value.setColumns(20);
        value.setLineWrap(true);
        value.setRows(10);
        value.setMinimumSize(new java.awt.Dimension(600, 500));
        value.setPreferredSize(new java.awt.Dimension(700, 500));
        jScrollPane1.setViewportView(value);

        add(jScrollPane1, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void copyToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyToClipBoardActionPerformed
    StringSelection stringSelection = new StringSelection(value.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }//GEN-LAST:event_copyToClipBoardActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyToClipBoard;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea value;
    // End of variables declaration//GEN-END:variables
}