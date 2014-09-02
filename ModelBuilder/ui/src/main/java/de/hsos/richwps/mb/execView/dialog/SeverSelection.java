package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author dalcacer
 */
public class SeverSelection extends ADialogPanel {

    private List<String> remotes;
    private ExecuteRequest dto;

    /**
     * Creates new form SeverSelection
     */
    public SeverSelection() {
        initComponents();
        this.remotes = new ArrayList<String>();
        this.init();
    }

    /**
     *
     * @param wpsurls
     * @param dto
     */
    public SeverSelection(List<String> wpsurls, ExecuteRequest dto) {
        this.remotes = wpsurls;
        this.dto = dto;
        this.initComponents();
        this.init();
    }

    private void init() {
        this.serverSelectionBox.removeAllItems();
        for (String s : this.remotes) {
            this.serverSelectionBox.addItem(s);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isValidInput() {
        String server = (String) this.serverSelectionBox.getSelectedItem();
        // FIXME How else can we check the endpoints existence, and readiness?
        // HTTP::HEAD Operation 405 Method Not Allowed, instead of 404?
        // HTTP::GET Operation 405 Method Not Allowed, instead of 404?
        try {
            java.net.URL urlobj = new java.net.URL(server);
            URLConnection conn = urlobj.openConnection();
            conn.connect();
            HttpURLConnection httpConnection = (HttpURLConnection) conn;
            int resp = httpConnection.getResponseCode();
            if (resp != 200) {
                JOptionPane.showMessageDialog(this, "Unable to reach service.");
                return false;
            }
        } catch (Exception e) {
            de.hsos.richwps.mb.Logger.log("Unable to reach service: " + server);
            JOptionPane.showMessageDialog(this, "Unable to reach service.");
            return false;
        }
        return true;
    }

    /**
     *
     */
    @Override
    public void updateDTO() {
        this.dto = new ExecuteRequest();
        this.dto.setEndpoint((String) this.serverSelectionBox.getSelectedItem());
    }

    /**
     *
     * @return
     */
    @Override
    public ExecuteRequest getDTO() {
        return dto;
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

        serverSelectionLabel = new javax.swing.JLabel();
        serverSelectionBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        serverSelectionLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        serverSelectionLabel.setLabelFor(serverSelectionBox);
        serverSelectionLabel.setText("Server:");
        serverSelectionLabel.setName("serverSelectionLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(serverSelectionLabel, gridBagConstraints);

        serverSelectionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        serverSelectionBox.setPreferredSize(new java.awt.Dimension(400, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(serverSelectionBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox serverSelectionBox;
    private javax.swing.JLabel serverSelectionLabel;
    // End of variables declaration//GEN-END:variables

}
