package de.hsos.richwps.mb.ui.dialogs;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.ui.dialogs.components.InputPanel;
import de.hsos.richwps.mb.ui.dialogs.components.OutputPanel;
import de.hsos.richwps.mb.ui.dialogs.components.ResultPanel;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.ui.UiHelper;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

/**
 * A dialog that displays three consecutive panels for inputparameterisation,
 * outputparameterisation and resultvisualisation.
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class ExecuteModelDialog extends ADialog {

    private InputPanel inputspanel;
    private OutputPanel outputsspanel;
    private ResultPanel resultpanel;
    private ExecuteRequest request;

    /**
     * Creates new form ExecuteModelDialog, starting with the
     * inputparamerization.
     *
     * @param parent
     * @param modal
     *
     * @param serverid
     * @param processid
     *
     */
    public ExecuteModelDialog(java.awt.Frame parent, boolean modal, final String serverid,
            final String processid) {
        super(parent, AppConstants.EXECUTE_THIS_DIALOG_TITLE);
        this.currentPanel = null;
        this.request = new ExecuteRequest();
        this.request.setEndpoint(serverid);
        this.request.setIdentifier(processid);
        this.provider = new RichWPSProvider();
        try {
            if (!RichWPSProvider.isWPSEndpoint(serverid)) {
                JOptionPane.showMessageDialog(this, AppConstants.CONNECT_FAILED);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(AppConstants.CONNECT_FAILED, AppConstants.INFOTAB_ID_SERVER);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, AppConstants.CONNECT_FAILED);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(AppConstants.CONNECT_FAILED, AppConstants.INFOTAB_ID_SERVER);
            Logger.log(this.getClass(), "ExecuteModelDialog()", ex);
            return;
        }
        this.serverids = new ArrayList();
        this.serverids.add(serverid);
        this.initComponents();
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        this.backButton.setText(AppConstants.DIALOG_BTN_BACK);
        this.abortButton.setText(AppConstants.DIALOG_BTN_CANCEL);
        this.showInputsPanel(false);
    }

    /**
     * Switches from processselectionpanel to parameterizeinputspanel.
     */
    @Override
    public void showInputsPanel(boolean isBackAction) {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);
        this.previewButton.setVisible(false);

        this.inputspanel = new InputPanel(this.provider, this.request);
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
    @Override
    public void showOutputsPanel(final boolean isBackAction) {
        //in case of a next-action block, if the provided input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.previewButton.setVisible(true);

        //refresh the request
        if (!isBackAction) {
            this.currentPanel.updateRequest();
            this.request = (ExecuteRequest) this.currentPanel.getRequest();
        }

        this.outputsspanel = new OutputPanel(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.outputsspanel);
        this.pack();
        this.currentPanel = outputsspanel;

    }

    /**
     *
     * @param isBackAction
     * @param update
     */
    @Override
    public void showResultsPanel() {
        //in case of a next-action block, if the provided input is not valid.
        if (!this.currentPanel.isValidInput()) {
            return;
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(false);
        this.previewButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();

        //in case the request was allready used.
        this.request.flushException();
        this.request.flushResults();

        this.resultpanel = new ResultPanel(this.provider, this.request);
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
        previewButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        abortButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Execute opened model");
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
        this.abortButton.setText(AppConstants.DIALOG_BTN_CANCEL);
        if (this.currentPanel == this.inputspanel) {
            this.nextButton.setText(AppConstants.DIALOG_BTN_START);
            this.showOutputsPanel(isBackAction);
        } else if (this.currentPanel == this.outputsspanel) {
            this.abortButton.setText(AppConstants.DIALOG_BTN_CLOSE);
            this.showResultsPanel();
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed

        //make sure the client cache is emptied.
        if (provider != null) {
            try {
                this.request = new ExecuteRequest();
            } catch (Exception ex) {
                Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
            }
        }

        //this.showParameterizeInputsPanel(false);     //Reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        final boolean isBackAction = true;
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        if (this.currentPanel == this.outputsspanel) {
            this.showInputsPanel(isBackAction);
        } else if (this.currentPanel == this.resultpanel) {
            this.nextButton.setText(AppConstants.DIALOG_BTN_START);
            this.showOutputsPanel(isBackAction);
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_backButtonActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();
        String requeststr = this.provider.wpsPreviewExecuteProcess(this.request);
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
