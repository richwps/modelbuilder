package de.hsos.richwps.mb.ui.testView;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.AppRichWPSManager;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.ui.testView.dialog.*;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.ui.MbDialog;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * A dialog that displays three consecutive panels for inputparameterisation,
 * outputparameterisation and resultvisualisation.
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class TestModelDialog extends MbDialog {

    private ADialogPanel currentPanel;
    private InputParameterization inputspanel;
    private OutputSelection outputsspanel;
    private ResultVisualisation resultpanel;
    private List<String> remotes;
    private RichWPSProvider provider;
    private TestRequest request;
    private List<String> transitions;

    /**
     * Creates new form ExecuteModelDialog, starting with the
     * inputparamerization.
     *
     * @param parent
     * @param modal
     *
     * @param serverid
     * @param processid
     * @param manager manager to access graph and exporter.
     *
     */
    public TestModelDialog(java.awt.Frame parent, boolean modal, final String serverid,
            final String processid, AppRichWPSManager manager) {
        super(parent, "Test model");
        this.currentPanel = null;
        this.request = new TestRequest();
        this.request.setEndpoint(serverid);
        this.request.setIdentifier(processid);
        this.provider = new RichWPSProvider();
        this.request.setDeploymentprofile(RichWPSProvider.deploymentProfile);
        this.request.setExecutionUnit(manager.getROLA());
        this.transitions=manager.getTransitions();
        for (String var : manager.getVariables()) {
            this.request.addVariable("var."+var);
        }

        try {
            this.provider.disconnect();
            this.provider.connect(request.getEndpoint());
        } catch (Exception ex) {
            String msg = "Unable to connect to selected WebProcessingService.";
            JOptionPane.showMessageDialog(this, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            Logger.log(this.getClass(), "TestModelDialog()", ex);
            return;
        }

        this.remotes = new ArrayList();
        this.remotes.add(serverid);
        this.initComponents();
        this.showParameterizeInputsPanel(false);
    }

    /**
     * Switches from processselectionpanel to parameterizeinputspanel.
     */
    private void showParameterizeInputsPanel(boolean isBackAction) {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);

        this.inputspanel = new InputParameterization(this.provider, this.request);
        if (this.currentPanel != null) {
            this.remove(this.currentPanel);
            this.currentPanel.setVisible(false);
        }

        this.add(this.inputspanel);
        this.pack();
        this.currentPanel = inputspanel;
    }

    /**
     * Switches from parameterizeinputspanel to parameterizeoutputsspanel.
     */
    private void showOutputSelectionPanel(boolean isBackAction) {
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

        this.outputsspanel = new OutputSelection(this.provider, this.request, this.transitions);
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

        this.resultpanel.testProcess();
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
        if (this.currentPanel == this.inputspanel) {
            this.showOutputSelectionPanel(false);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showResultPanel(false);
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed

        //make sure the client cache is emptied.
        if (provider != null) {
            try {
                provider.disconnect();
                this.request = new TestRequest();
            } catch (Exception ex) {
                Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
            }
        }

        //this.showParameterizeInputsPanel(false);     //Reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.currentPanel == this.outputsspanel) {
            this.showParameterizeInputsPanel(true);
        } else if (this.currentPanel == this.resultpanel) {
            this.showOutputSelectionPanel(true);
        }
    }//GEN-LAST:event_backButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables
}
