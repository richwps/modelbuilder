package de.hsos.richwps.mb.ui.dialogs.components.renderer;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dalcacer
 */
public class URIResultRenderer extends javax.swing.JPanel {

    private String httpuri;
    /**
     * Creates new form FeatureCollectionRenderer
     */
    public URIResultRenderer() {
        initComponents();
    }

    /**
     * Creates new form FeatureCollectionRenderer
     * @param identifier
     * @param reference
     */
    public URIResultRenderer(String identifier, String reference) {
        initComponents();
        this.httpuri=reference;
        String abbString = StringUtils.abbreviateMiddle(httpuri, "[...]" ,50);
        this.uri.setText(abbString);
        
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

        uri = new javax.swing.JLabel();
        copyToClipBoard = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(600, 100));
        setPreferredSize(new java.awt.Dimension(600, 100));
        setLayout(new java.awt.GridBagLayout());

        uri.setForeground(java.awt.Color.blue);
        uri.setText("value");
        uri.setMinimumSize(new java.awt.Dimension(450, 15));
        uri.setPreferredSize(new java.awt.Dimension(450, 15));
        uri.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                uriMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(uri, gridBagConstraints);

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(copyToClipBoard, gridBagConstraints);

        jLabel1.setMinimumSize(new java.awt.Dimension(450, 1));
        jLabel1.setPreferredSize(new java.awt.Dimension(450, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void copyToClipBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyToClipBoardActionPerformed
    StringSelection stringSelection = new StringSelection(httpuri);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }//GEN-LAST:event_copyToClipBoardActionPerformed

    private void uriMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uriMouseClicked
        JLabel self = (JLabel) evt.getSource();
        if (self.getText() == null) {
            return;
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(httpuri));
                    return;
                } catch (Exception exp) {
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Cannot launch browser...\n Please, visit\n" + self.getText(), "", JOptionPane.INFORMATION_MESSAGE);
        return;
    }//GEN-LAST:event_uriMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyToClipBoard;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel uri;
    // End of variables declaration//GEN-END:variables
}
