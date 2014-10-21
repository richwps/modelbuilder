package de.hsos.richwps.mb.execView;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.execView.dialog.ADialogPanel;
import de.hsos.richwps.mb.execView.dialog.InputParameterization;
import de.hsos.richwps.mb.execView.dialog.OutputParameterization;
import de.hsos.richwps.mb.execView.dialog.ProcessSelection;
import de.hsos.richwps.mb.execView.dialog.ResultVisualisation;
import de.hsos.richwps.mb.execView.dialog.SeverSelection;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.ui.MbDialog;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * A dialog that displays five consecutive panels for serverselection,
 * processselection, inputparameterisation, outputparameterisation and
 * resultvisualisation.
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class ExecuteDialog extends MbDialog {

    private ADialogPanel currentPanel;
    private SeverSelection serverselectionpanel;
    private ProcessSelection processesselectionpanel;
    private InputParameterization inputspanel;
    private OutputParameterization outputsspanel;
    private ResultVisualisation resultpanel;
    private List<String> remotes;
    private RichWPSProvider provider;
    private ExecuteRequest request;

    /**
     * Creates new form execViewDialog, starting with the first dialog
     * (serverselection).
     */
    public ExecuteDialog(java.awt.Frame parent, boolean modal, List<String> wpsurls) {
        super(parent, "Execute");

        this.provider = new RichWPSProvider();
        this.request = new ExecuteRequest();
        this.remotes = wpsurls;
        this.initComponents();
        this.backButton.setVisible(false);
        this.showServerSelection(false);
    }

   
    private void showServerSelection(boolean isBackAction) {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);
        this.serverselectionpanel = new SeverSelection(this.remotes, this.request);

        if (this.currentPanel != null) {
            this.remove(this.currentPanel);
            this.currentPanel.setVisible(false);
        }

        this.add(this.serverselectionpanel);
        this.pack();
        this.currentPanel = serverselectionpanel;
    }

    /**
     * Switches from serverselectionpanel to processselectionpanel.
     */
    private void showProcessSelection(boolean isBackAction) {
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }

        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = this.currentPanel.getRequest();

        try {
            this.provider.connect(this.request.getEndpoint());
        } catch (Exception ex) {
            String msg = "Unable to connect to selected WebProcessingService.";
            JOptionPane.showMessageDialog(this, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            Logger.log("Debug:\n " + ex);
            return;
        }
        this.processesselectionpanel = new ProcessSelection(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.processesselectionpanel);
        this.pack();
        this.currentPanel = processesselectionpanel;

    }

    /**
     * Switches from processselectionpanel to parameterizeinputspanel.
     */
    private void showParameterizeInputsPanel(boolean isBackAction) {
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = this.currentPanel.getRequest();

        this.inputspanel = new InputParameterization(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.inputspanel);
        this.pack();
        this.currentPanel = inputspanel;
    }

    /**
     * Switches from parameterizeinputspanel to parameterizeoutputsspanel.
     */
    private void showParameterizeOutputsPanel(boolean isBackAction) {
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = this.currentPanel.getRequest();

        this.outputsspanel = new OutputParameterization(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.outputsspanel);
        this.pack();
        this.currentPanel = outputsspanel;

    }

    private void showResultPanel(boolean isBackAction) {
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = this.currentPanel.getRequest();
        //in case the request was allready used.
        this.request.flushException();
        this.request.flushResults();

        this.resultpanel = new ResultVisualisation(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.resultpanel);
        this.pack();
        this.currentPanel = resultpanel;
        this.currentPanel.setVisible(true);

        this.resultpanel.executeProcess();
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
            this.showParameterizeInputsPanel(false);
        } else if (this.currentPanel == this.inputspanel) {
            this.showParameterizeOutputsPanel(false);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showResultPanel(false);
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed
        this.showServerSelection(false);     //Reset

        //Make sure the client cache is emptied.
        if (provider != null) {
            try {
                provider.disconnect();
            } catch (Exception ex) {
                Logger.log("Debug:\n " + ex);
            }
        }

        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.currentPanel == this.processesselectionpanel) {
            this.showServerSelection(true);
        } else if (this.currentPanel == this.inputspanel) {
            this.showProcessSelection(true);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showParameterizeInputsPanel(true);
        } else if (this.currentPanel == this.resultpanel) {
            this.showParameterizeOutputsPanel(true);
        }
    }//GEN-LAST:event_backButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables
}