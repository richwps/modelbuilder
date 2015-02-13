package de.hsos.richwps.mb.app.view.dialogs;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.richWPS.boundary.IRichWPSProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import de.hsos.richwps.mb.ui.UiHelper;
import de.hsos.richwps.mb.app.view.dialogs.components.ProcessPanel;
import de.hsos.richwps.mb.app.view.dialogs.components.ServerPanel;
import de.hsos.richwps.mb.richWPS.boundary.RequestFactory;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

/**
 * A dialog that displays two consecutive panels for serverselection and
 * processselection in order to undeploy a process.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class UndeployDialog extends ADialog {

    private ServerPanel serverselectionpanel;
    private ProcessPanel processesselectionpanel;

    /**
     * Describe-Request for discovery.
     */
    private DescribeRequest desc_request;
    /**
     * Undeploy-Request for performing the undeploy-operation.
     */
    private UndeployRequest undeploy_request;

    /**
     * Creates new form UndeployDialog, starting with the first dialog
     * (serverselection).
     *
     * @param parent
     * @param modal
     * @param severids list of viable serverids.
     */
    public UndeployDialog(java.awt.Frame parent, boolean modal, List<String> severids) {
        super(parent, AppConstants.DEPLOY_DIALOG_TITLE);

        this.provider = new RichWPSProvider();
        this.desc_request = new DescribeRequest();
        this.serverids = severids;
        this.initComponents();
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        this.backButton.setText(AppConstants.DIALOG_BTN_BACK);
        this.abortButton.setText(AppConstants.DIALOG_BTN_CANCEL);
        this.backButton.setVisible(false);
        this.showServersPanel();
    }

    /**
     * Shows serverselection panel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    @Override
    public void showServersPanel() {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);
        this.previewButton.setVisible(false);
        this.serverselectionpanel = new ServerPanel(this.serverids, this.desc_request);

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
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    @Override
    public void showProcessesPanel(final boolean isBackAction) {//noop
        //in case of a next-action block, if the provided input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }

        this.backButton.setVisible(true);
        this.previewButton.setVisible(true);
        this.nextButton.setVisible(true);

        //refresh the perform
        this.currentPanel.updateRequest();
        this.desc_request = (DescribeRequest) this.currentPanel.getRequest();

        this.processesselectionpanel = new ProcessPanel(this.provider, this.desc_request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.processesselectionpanel);
        this.pack();
        this.currentPanel = processesselectionpanel;
    }

    /**
     * Performs the undeployment by means of this_desc_request.
     *
     */
    private void undeploy() {
        //in case of a next-action block, if the provided input is not valid.
        if (!this.currentPanel.isValidInput()) {
            return;
        }
        //refresh the perform
        this.currentPanel.updateRequest();
        this.desc_request = (DescribeRequest) this.currentPanel.getRequest();

        //prepare the perform
        String auri = desc_request.getServerId();
        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];
        this.undeploy_request
                = (de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest) RequestFactory.createUndeployRequest(wpsendpoint, richwpsendpoint, this.desc_request.getIdentifier());

        //perform the perform
        try {
            this.provider.perform(undeploy_request);
        } catch (Exception ex) {
            Logger.log(this.getClass(), "performUndeploy()", ex);
            String msg = AppConstants.UNDEPLOY_FAILURE
                    + "An error occured while undeploying  " + this.undeploy_request.getIdentifier() + " from"
                    + " " + wpsendpoint + ". " + undeploy_request.getException();

            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            JOptionPane.showMessageDialog(null, msg,
                    AppConstants.UNDEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }

        //Analyyse the response
        if (undeploy_request.isException()) {
            String msg = AppConstants.UNDEPLOY_FAILURE
                    + "An error occured while undeploying  " + this.undeploy_request.getIdentifier() + " from"
                    + " " + wpsendpoint + ". " + undeploy_request.getException();

            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            JOptionPane.showMessageDialog(null, msg,
                    AppConstants.UNDEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } else {
            AppEventService.getInstance().fireAppEvent(AppConstants.UNDEPLOY_SUCCESS,
                    AppConstants.INFOTAB_ID_SERVER);
            JOptionPane.showMessageDialog(null,
                    AppConstants.UNDEPLOY_SUCCESS,
                    AppConstants.UNDEPLOY_SUCCESS,
                    JOptionPane.INFORMATION_MESSAGE);
            //make sure the client cache is emptied.
            if (provider != null) {
                try {
                    this.desc_request = new DescribeRequest();
                } catch (Exception ex) {
                    Logger.log(this.getClass(), "performUndeploy()", ex);
                }
            }
            this.showServersPanel(); //reset
            this.setVisible(false);
            this.dispose();
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
        previewButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        abortButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Undeploy a process");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        previewButton.setIcon(UIManager.getIcon(AppConstants.ICON_PREVIEW_KEY));
        previewButton.setMnemonic('P');
        previewButton.setText("Preview");
        previewButton.setMinimumSize(new java.awt.Dimension(70, 32));
        previewButton.setPreferredSize(new java.awt.Dimension(70, 32));
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        navpanel.add(previewButton);

        backButton.setMnemonic('B');
        backButton.setText("Back");
        backButton.setMinimumSize(new java.awt.Dimension(70, 32));
        backButton.setPreferredSize(new java.awt.Dimension(70, 32));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        navpanel.add(backButton);

        nextButton.setMnemonic('N');
        nextButton.setText("Next");
        nextButton.setMinimumSize(new java.awt.Dimension(70, 32));
        nextButton.setPreferredSize(new java.awt.Dimension(70, 32));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        navpanel.add(nextButton);

        abortButton.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        abortButton.setMnemonic('A');
        abortButton.setText("Abort");
        abortButton.setMinimumSize(new java.awt.Dimension(70, 32));
        abortButton.setPreferredSize(new java.awt.Dimension(70, 32));
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
        final boolean isBackAction = false;
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        if (this.currentPanel == this.serverselectionpanel) {
            this.showProcessesPanel(isBackAction);
            this.nextButton.setText(AppConstants.DIALOG_BTN_START);
        } else if (this.currentPanel == this.processesselectionpanel) {
            this.undeploy();
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed

        //make sure the client cache is emptied.
        if (provider != null) {
            try {
                this.desc_request = new DescribeRequest();
            } catch (Exception ex) {
                Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
            }
        }
        this.showServersPanel(); //reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        if (this.currentPanel == this.processesselectionpanel) {
            this.showServersPanel();
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_backButtonActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        this.currentPanel.updateRequest();
        this.desc_request = (DescribeRequest) this.currentPanel.getRequest();
        String auri = desc_request.getServerId();
        String[] endpoints = RichWPSProvider.deliverEndpoints(auri);
        final String wpsendpoint = endpoints[0];
        final String richwpsendpoint = endpoints[1];
        this.undeploy_request
                = (de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest) RequestFactory.createUndeployRequest(wpsendpoint, richwpsendpoint, this.desc_request.getIdentifier());
        String requeststr = this.provider.richwpsPreviewUndeployProcess(this.undeploy_request);
        final JTextPane textpane = new javax.swing.JTextPane();
        textpane.setContentType("text");
        textpane.setFont(AppConstants.DIALOG_TEXTPANE_FONT);
        textpane.setText(requeststr);
        textpane.setEditable(false);
        final javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textpane);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 500));
        JOptionPane.showMessageDialog(this, scrollPane);
    }//GEN-LAST:event_previewButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previewButton;
    // End of variables declaration//GEN-END:variables
}
