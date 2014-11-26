package de.hsos.richwps.mb.ui.undeplView;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.ui.undeplView.dialog.ADialogPanel;
import de.hsos.richwps.mb.ui.undeplView.dialog.ProcessSelection;
import de.hsos.richwps.mb.ui.undeplView.dialog.SeverSelection;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import de.hsos.richwps.mb.ui.MbDialog;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A dialog that displays five consecutive panels for serverselection,
 * processselection, inputparameterisation, outputparameterisation and
 * resultvisualisation.
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class UndeployDialog extends MbDialog {

    private ADialogPanel currentPanel;
    private SeverSelection serverselectionpanel;
    private ProcessSelection processesselectionpanel;
    private List<String> serverids;
    private RichWPSProvider provider;
    /**
     * Describe-Request for discovery.
     */
    private DescribeRequest desc_request;
    /**
     * Undeploy-Request for performing undeploy-operation.
     */
    private UndeployRequest undeploy_request;

    /**
     * Creates new form execViewDialog, starting with the first dialog
     * (serverselection).
     *
     * @param parent
     * @param modal
     * @param severids list of viable serverids.
     */
    public UndeployDialog(java.awt.Frame parent, boolean modal, List<String> severids) {
        super(parent, "Undeploy process");

        this.provider = new RichWPSProvider();
        this.desc_request = new DescribeRequest();
        //this.request = new UndeployRequest();
        this.serverids = severids;
        this.initComponents();
        this.backButton.setVisible(false);
        this.showServerSelection(false);
    }

    /**
     * Shows serverselection panel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showServerSelection(boolean isBackAction) {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);
        this.serverselectionpanel = new SeverSelection(this.serverids, this.desc_request);

        if (this.currentPanel != null) {
            this.remove(this.currentPanel);
            this.currentPanel.setVisible(false);
        }
        this.nextButton.setText("Next");
        this.add(this.serverselectionpanel);
        this.pack();
        this.currentPanel = serverselectionpanel;
    }

    /**
     * Switches from serverselectionpanel to processselectionpanel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showProcessSelection(boolean isBackAction) {
        //blockage if input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }

        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);

        //refresh the request
        this.currentPanel.updateRequest();
        this.desc_request = this.currentPanel.getRequest();

        try {
            this.provider.connect(this.desc_request.getEndpoint());
        } catch (Exception ex) {
            String msg = "Unable to connect to selected WebProcessingService.";
            JOptionPane.showMessageDialog(this, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            Logger.log(this.getClass(), "showProcessSelection()", ex);
            return;
        }
        this.processesselectionpanel = new ProcessSelection(this.provider, this.desc_request);
        this.nextButton.setText("Undeploy");
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.processesselectionpanel);
        this.pack();
        this.currentPanel = processesselectionpanel;
    }

    /**
     * Switches from serverselectionpanel to processselectionpanel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void performUndeploy() {
        try {
            //refresh the request
            this.currentPanel.updateRequest();
            this.desc_request = this.currentPanel.getRequest();
            String endp = desc_request.getServerId();
            endp = endp.replace(IRichWPSProvider.DEFAULT_WPS_ENDPOINT, IRichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
            Logger.log(endp);
            Logger.log(desc_request.getServerId());
            Logger.log(desc_request.getIdentifier());
            this.undeploy_request = new UndeployRequest(this.desc_request.getServerId(), endp, this.desc_request.getIdentifier());
            this.provider.connect(this.desc_request.getServerId(), endp);
            this.provider.richwpsUndeployProcess(undeploy_request);
            if (undeploy_request.isException()) {
                AppEventService.getInstance().fireAppEvent("Undeployment failed",
                        AppConstants.INFOTAB_ID_SERVER);
            } else {
                AppEventService.getInstance().fireAppEvent("Undeployment succeeded",
                        AppConstants.INFOTAB_ID_SERVER);
                //make sure the client cache is emptied.
                if (provider != null) {
                    try {
                        provider.disconnect();
                        this.desc_request = new DescribeRequest();
                    } catch (Exception ex) {
                        Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
                    }
                }
                this.showServerSelection(false); //reset
                this.setVisible(false);
                this.dispose();
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UndeployDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        navpanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        abortButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Execute");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        navpanel.add(backButton);

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        navpanel.add(nextButton);

        abortButton.setText("Abort");
        abortButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortButtonActionPerformed(evt);
            }
        });
        navpanel.add(abortButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        getContentPane().add(navpanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (this.currentPanel == this.serverselectionpanel) {
            this.showProcessSelection(false);
        } else if (this.currentPanel == this.processesselectionpanel) {
            this.performUndeploy();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed

        //make sure the client cache is emptied.
        if (provider != null) {
            try {
                provider.disconnect();
                this.desc_request = new DescribeRequest();
            } catch (Exception ex) {
                Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
            }
        }
        this.showServerSelection(false); //reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.currentPanel == this.processesselectionpanel) {
            this.showServerSelection(true);
        }
    }//GEN-LAST:event_backButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables
}
