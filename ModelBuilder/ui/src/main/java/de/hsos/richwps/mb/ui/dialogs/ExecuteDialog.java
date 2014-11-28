package de.hsos.richwps.mb.ui.dialogs;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.ui.dialogs.components.ADialogPanel;
import de.hsos.richwps.mb.ui.dialogs.components.InputParameterization;
import de.hsos.richwps.mb.ui.dialogs.components.OutputParameterization;
import de.hsos.richwps.mb.ui.dialogs.components.ProcessSelection;
import de.hsos.richwps.mb.ui.dialogs.components.ResultVisualisation;
import de.hsos.richwps.mb.ui.dialogs.components.SeverSelection;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.ui.MbDialog;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
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
public class ExecuteDialog extends MbDialog {

    private ADialogPanel currentPanel;
    private SeverSelection serverselectionpanel;
    private ProcessSelection processesselectionpanel;
    private InputParameterization inputspanel;
    private OutputParameterization outputsspanel;
    private ResultVisualisation resultpanel;
    private List<String> serverids;
    private RichWPSProvider provider;
    private ExecuteRequest request;

    /**
     * Creates new form execViewDialog, starting with the first dialog
     * (serverselection).
     *
     * @param parent
     * @param modal
     * @param severids list of viable serverids.
     */
    public ExecuteDialog(java.awt.Frame parent, boolean modal, List<String> severids) {
        super(parent, "Execute process");

        this.provider = new RichWPSProvider();
        this.request = new ExecuteRequest();
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
        this.loadButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.serverselectionpanel = new SeverSelection(this.serverids, this.request);

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
    private void showProcessSelection(boolean isBackAction) {
        //blockage if input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }

        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.loadButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();

        try {
            this.provider.connect(this.request.getEndpoint());
        } catch (Exception ex) {
            String msg = "Unable to connect to selected WebProcessingService.";
            JOptionPane.showMessageDialog(this, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            Logger.log(this.getClass(), "showProcessSelection()", ex);
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
     *
     * @param isBackAction indicator if a backaction is performed.
     */
    private void showParameterizeInputsPanel(boolean isBackAction, boolean updateneeded) {
        //blockage if input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.loadButton.setVisible(false);

        if (updateneeded) {//refresh the request
            this.currentPanel.updateRequest();
            this.request = (ExecuteRequest) this.currentPanel.getRequest();
        }

        this.inputspanel = new InputParameterization(this.provider, this.request);
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
    private void showParameterizeOutputsPanel(boolean isBackAction) {
        //blockage if input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.nextButton.setVisible(true);
        this.saveButton.setVisible(true);
        this.loadButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();

        this.outputsspanel = new OutputParameterization(this.provider, this.request);
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
    private void showResultPanel(boolean isBackAction) {
        //blockage if input is not valid.
        if (!isBackAction) {
            if (!this.currentPanel.isValidInput()) {
                return;
            }
        }
        this.backButton.setVisible(true);
        this.saveButton.setVisible(false);
        this.nextButton.setVisible(false);
        this.loadButton.setVisible(false);

        //refresh the request
        this.currentPanel.updateRequest();
        this.request = (ExecuteRequest) this.currentPanel.getRequest();

        //in case the request was already used.
        if (request.isLoaded()) {
            this.request.flushException();
            this.request.flushResults();
        }

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
        loadButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        abortButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Execute");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        loadButton.setText("Load request");
        loadButton.setToolTipText("Load request from template. (EXPERIMENTAL)");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        navpanel.add(loadButton);

        saveButton.setText("Save request");
        saveButton.setToolTipText("Save request as template. (EXPERIMENTAL)");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        navpanel.add(saveButton);

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
            this.showParameterizeInputsPanel(false, true);
        } else if (this.currentPanel == this.inputspanel) {
            this.showParameterizeOutputsPanel(false);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showResultPanel(false);
        }
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
        this.showServerSelection(false); //reset
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_abortButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.currentPanel == this.processesselectionpanel) {
            this.showServerSelection(true);
        } else if (this.currentPanel == this.inputspanel) {
            this.showProcessSelection(true);
        } else if (this.currentPanel == this.outputsspanel) {
            this.showParameterizeInputsPanel(true, true);
        } else if (this.currentPanel == this.resultpanel) {
            this.showParameterizeOutputsPanel(true);
        }
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

            } catch (Exception ex) {
                String msg = "Unable to open execute request.";
                JOptionPane.showMessageDialog(this, msg);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
                Logger.log(this.getClass(), "loadButtonActionPerformed()", ex);
            }
        }
    }//GEN-LAST:event_loadButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortButton;
    private javax.swing.JButton backButton;
    private javax.swing.JButton loadButton;
    private javax.swing.JPanel navpanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
