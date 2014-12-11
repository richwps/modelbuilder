package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author dalcacer
 */
public class ProcessSelection extends ADialogPanel {

    private RichWPSProvider provider;
    private List<String> processes;
    private String wpsurl;
    private IRequest request;

    /**
     * Creates new form ProcessSelection
     */
    public ProcessSelection() {
        this.initComponents();
    }

    /**
     *
     * @param provider
     * @param dto
     */
    public ProcessSelection(RichWPSProvider provider, IRequest request) {
        this.request = (DescribeRequest) request;
        this.wpsurl = this.request.getEndpoint();
        this.provider = provider;
        try {
            this.processes = this.provider.wpsGetAvailableProcesses(wpsurl);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load processes.");
            return;
        }
        this.initComponents();
        this.init();
    }

    private void init() {
        this.processesBox.removeAllItems();
        
        if (this.processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unable to load processes.");
            return;
        }
        
        for (String process : this.processes) {
            this.processesBox.addItem(process);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public IRequest getRequest() {
        return request;
    }

    /**
     *
     */
    @Override
    public void updateRequest() {
        this.request = new DescribeRequest();
        request.setEndpoint(this.wpsurl);
        request.setIdentifier((String) this.processesBox.getSelectedItem());
    }

    /**
     *
     * @return
     */
    public boolean isValidInput() {
        return true;
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

        processLabel = new javax.swing.JLabel();
        processesBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        processLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        processLabel.setLabelFor(processesBox);
        processLabel.setText("Process:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(processLabel, gridBagConstraints);

        processesBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        processesBox.setPreferredSize(new java.awt.Dimension(400, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(processesBox, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Please select a process");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel processLabel;
    private javax.swing.JComboBox processesBox;
    // End of variables declaration//GEN-END:variables

}
