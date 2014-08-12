package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import java.util.List;

/**
 *
 * @author dalcacer
 */
public class ProcessSelection extends ADialogPanel {

    private RichWPSProvider provider;
    private List<String> processes;
    private String wpsurl;
    private ExecuteRequestDTO dto;

    /**
     * Creates new form ProcessSelection
     */
    public ProcessSelection() {
        this.initComponents();
    }

    public ProcessSelection(RichWPSProvider provider, ExecuteRequestDTO dto) {
        this.dto = dto;
        this.wpsurl = dto.getEndpoint();
        this.provider = provider;
        this.processes = this.provider.getAvailableProcesses(wpsurl);
        this.initComponents();
        this.init();
    }

    private void init() {
        this.processesBox.removeAllItems();
        for (String process : this.processes) {
            this.processesBox.addItem(process);
        }
    }

    @Override
    public ExecuteRequestDTO getDTO() {
        return dto;
    }

    @Override
    public void updateDTO() {
        this.dto = new ExecuteRequestDTO();
        dto.setEndpoint(this.wpsurl);
        dto.setProcessid((String) this.processesBox.getSelectedItem());
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

        setLayout(new java.awt.GridBagLayout());

        processLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        processLabel.setLabelFor(processesBox);
        processLabel.setText("Process:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(processLabel, gridBagConstraints);

        processesBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        processesBox.setPreferredSize(new java.awt.Dimension(400, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(processesBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel processLabel;
    private javax.swing.JComboBox processesBox;
    // End of variables declaration//GEN-END:variables

}
