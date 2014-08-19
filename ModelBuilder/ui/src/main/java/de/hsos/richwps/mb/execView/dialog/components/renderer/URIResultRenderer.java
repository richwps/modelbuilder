package de.hsos.richwps.mb.execView.dialog.components.renderer;

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
     */
    public URIResultRenderer(String identifier, String reference) {
        initComponents();
        this.identifier.setText(identifier);
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

        jLabel1 = new javax.swing.JLabel();
        uri = new javax.swing.JLabel();
        copyToClipBoard = new javax.swing.JButton();
        identifier = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(600, 200));
        setPreferredSize(new java.awt.Dimension(600, 200));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Output:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(copyToClipBoard, gridBagConstraints);

        identifier.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(identifier, gridBagConstraints);
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
    private javax.swing.JLabel identifier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel uri;
    // End of variables declaration//GEN-END:variables
}