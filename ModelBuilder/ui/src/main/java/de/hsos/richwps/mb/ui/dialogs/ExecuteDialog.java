package de.hsos.richwps.mb.ui.dialogs;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.ui.dialogs.components.InputPanel;
import de.hsos.richwps.mb.ui.dialogs.components.OutputPanel;
import de.hsos.richwps.mb.ui.dialogs.components.ProcessPanel;
import de.hsos.richwps.mb.ui.dialogs.components.ResultPanel;
import de.hsos.richwps.mb.ui.dialogs.components.ServerPanel;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.ui.MbDialog;
import de.hsos.richwps.mb.ui.UiHelper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A dialog that displays five consecutive panels for serverselection,
 * processselection, inputparameterisation, outputparameterisation and
 * resultvisualisation.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class ExecuteDialog extends MbDialog {

    /**
     * The currently shown panel.
     */
    private APanel currentPanel;
    /**
     * A panel used for serverselection.
     */
    private ServerPanel serverselectionpanel;
    /**
     * A panel used for processselection.
     */
    private ProcessPanel processesselectionpanel;
    /**
     * A panel used to parameterize given ogc inputs.
     */
    private InputPanel inputspanel;
    /**
     * A panel used to parameterize given ogc outputs.
     */
    private OutputPanel outputsspanel;
    /**
     * A panel used to visualized ogc outputs.
     */
    private ResultPanel resultpanel;
    /**
     * List of viable endpoints.
     */
    private List<String> serverids;
    /**
     * Interface to WPS/RichWPS-server.
     */
    private RichWPSProvider provider;
    /**
     * The final request that is parameterized and executed.
     */
    private ExecuteRequest request;

    /**
     * Creates new form ExecuteDialog, starting with the serverselection- panel.
     *
     * @param parent
     * @param modal
     * @param severids list of viable serverids.
     */
    public ExecuteDialog(java.awt.Frame parent, boolean modal, List<String> severids) {
        super(parent, AppConstants.EXECUTE_DIALOG_TITLE, MbDialog.BTN_ID_NONE);
        this.initComponents();

        this.serverids = severids;
        this.provider = new RichWPSProvider();
        this.request = new ExecuteRequest();
        this.backButton.setVisible(false);
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        this.backButton.setText(AppConstants.DIALOG_BTN_BACK);
        this.abortButton.setText(AppConstants.DIALOG_BTN_CANCEL);
        this.showServerSelection();
    }

    /**
     * Shows serverselection panel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showServerSelection() {
        this.backButton.setVisible(false);
        this.nextButton.setVisible(true);
        this.loadButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.previewButton.setVisible(false);
        this.serverselectionpanel = new ServerPanel(this.serverids, this.request);

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
    private void showProcessSelection(final boolean isBackAction, final boolean update) {

        //in case of a next-action block, if the provided input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }

        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.loadButton.setVisible(false);
        this.previewButton.setVisible(false);

        //refresh the request
        if (update) {
            this.currentPanel.updateRequest();
            DescribeRequest req = (DescribeRequest) this.currentPanel.getRequest();
            this.request = new ExecuteRequest(req);
        }

        try {
            this.provider.connect(this.request.getEndpoint());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, AppConstants.CONNECT_FAILED);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(AppConstants.CONNECT_FAILED, AppConstants.INFOTAB_ID_SERVER);
            Logger.log(this.getClass(), "showProcessSelection()", ex);
            return;
        }
        this.processesselectionpanel = new ProcessPanel(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.processesselectionpanel);
        this.pack();
        this.currentPanel = processesselectionpanel;
    }

    /**
     * Switches from processselectionpanel to parameterizeinputspanel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showParameterizeInputsPanel(final boolean isBackAction, final boolean update) {

        //in case of a next-action block, if the provided input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.loadButton.setVisible(false);
        this.previewButton.setVisible(false);

        if (update) {//refresh the request
            this.currentPanel.updateRequest();
            DescribeRequest req = (DescribeRequest) this.currentPanel.getRequest();
            this.request = new ExecuteRequest(req);
        }

        this.inputspanel = new InputPanel(this.provider, this.request);
        this.remove(this.currentPanel);
        this.currentPanel.setVisible(false);
        this.add(this.inputspanel);
        this.pack();
        this.currentPanel = inputspanel;
    }

    /**
     * Switches from parameterizeinputspanel to parameterizeoutputsspanel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showParameterizeOutputsPanel(final boolean isBackAction, final boolean update) {

        //in case of a next-action block, if the provided input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(true);
        this.loadButton.setVisible(false);
        this.previewButton.setVisible(true);

        //refresh the request
        if (update) {
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
     * Switches from parametizeoutputspabnel to showresultspanel.
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showResultPanel() {

        this.backButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.nextButton.setVisible(false);
        this.loadButton.setVisible(false);
        this.previewButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();

        //in case the request was already used.
        if (request.isLoaded()) {
            this.request.flushException();
            this.request.flushResults();
        }

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
        loadButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        previewButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        abortButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Execute a process");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        loadButton.setIcon(UIManager.getIcon(AppConstants.ICON_OPEN_KEY));
        loadButton.setMnemonic('L');
        loadButton.setText("Load");
        loadButton.setToolTipText("Load request from template. (EXPERIMENTAL)");
        loadButton.setMinimumSize(new java.awt.Dimension(70, 32));
        loadButton.setPreferredSize(new java.awt.Dimension(70, 32));
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        navpanel.add(loadButton);

        saveButton.setIcon(UIManager.getIcon(AppConstants.ICON_SAVE_KEY));
        saveButton.setMnemonic('S');
        saveButton.setText("Save");
        saveButton.setToolTipText("Save request as template. (EXPERIMENTAL)");
        saveButton.setMinimumSize(new java.awt.Dimension(70, 32));
        saveButton.setPreferredSize(new java.awt.Dimension(70, 32));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        navpanel.add(saveButton);

        previewButton.setIcon(UIManager.getIcon(AppConstants.ICON_PREVIEW_KEY));
        previewButton.setMnemonic('P');
        previewButton.setText("Preview");
        previewButton.setToolTipText("Preview request");
        previewButton.setMinimumSize(new java.awt.Dimension(70, 32));
        previewButton.setPreferredSize(new java.awt.Dimension(70, 32));
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        navpanel.add(previewButton);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        navpanel.add(jSeparator1);

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
        if (this.currentPanel == this.serverselectionpanel) {
            this.showProcessSelection(isBackAction, true);
        } else if (this.currentPanel == this.processesselectionpanel) {
            this.showParameterizeInputsPanel(isBackAction, true);
        } else if (this.currentPanel == this.inputspanel) {
            this.nextButton.setText(AppConstants.DIALOG_BTN_START);
            this.showParameterizeOutputsPanel(isBackAction, true);
        } else if (this.currentPanel == this.outputsspanel) {
            this.abortButton.setText(AppConstants.DIALOG_BTN_CLOSE);
            this.showResultPanel();
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void abortButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortButtonActionPerformed

        //make sure the client cache is emptied.
        if (provider != null) {
            try {
                provider.disconnect();
                this.request = new ExecuteRequest();
            } catch (Exception ex) {
                Logger.log(this.getClass(), "abortButtonActionPerformed()", ex);
            }
        }
        this.showServerSelection(); //reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        final boolean isBackAction = true;
        this.nextButton.setText(AppConstants.DIALOG_BTN_NEXT);
        if (this.currentPanel == this.processesselectionpanel) {
            this.showServerSelection();
        } else if (this.currentPanel == this.inputspanel) {
            this.showProcessSelection(isBackAction, false);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showParameterizeInputsPanel(isBackAction, false);
        } else if (this.currentPanel == this.resultpanel) {
            this.nextButton.setText(AppConstants.DIALOG_BTN_START);
            this.showParameterizeOutputsPanel(isBackAction, false);
        }
        UiHelper.centerToWindow(this, parent);
    }//GEN-LAST:event_backButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        javax.swing.filechooser.FileFilter filter = new FileNameExtensionFilter("Execute Requests (*.req)", "req");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File f = chooser.getSelectedFile();

            try {
                this.currentPanel.updateRequest();
                this.request = (ExecuteRequest) this.currentPanel.getRequest();
                //in case the request was already used.
                if (request.isLoaded()) {
                    this.request.flushException();
                    this.request.flushResults();
                }

                FileOutputStream fout = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(this.request);
                oos.close();;
                fout.close();

            } catch (Exception ex) {
                String msg = "Unable to persist Execute request.";
                JOptionPane.showMessageDialog(this, msg);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
                Logger.log(this.getClass(), "saveButtonActionPerformed()", ex);
            }
        }

    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        javax.swing.filechooser.FileFilter filter = new FileNameExtensionFilter("Execute Requests (*.req)", "req");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File f = chooser.getSelectedFile();

            try {
                FileInputStream streamIn = new FileInputStream(f);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                this.request = (ExecuteRequest) objectinputstream.readObject();
                objectinputstream.close();
                streamIn.close();
                this.showParameterizeInputsPanel(false, false);

            } catch (IOException | ClassNotFoundException ex) {
                String msg = "Unable to open execute request.";
                JOptionPane.showMessageDialog(this, msg);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
                Logger.log(this.getClass(), "loadButtonActionPerformed()", ex);
            }
        }
    }//GEN-LAST:event_loadButtonActionPerformed

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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton loadButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
