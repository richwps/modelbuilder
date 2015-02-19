package de.hsos.richwps.mb.app.view.dialogs.components;

import de.hsos.richwps.mb.ui.OpenInBrowserButton;
import de.hsos.richwps.mb.ui.CopyToClipboardButton;
import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.dialogs.components.listener.ResultTableMouseListener;
import de.hsos.richwps.mb.app.view.dialogs.components.renderer.*;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.wps.x100.LiteralDataType;

/**
 * Dialog panel for result visualisation.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class ResultPanel extends APanel {

    /**
     * List of displayable results.
     */
    private List<TitledComponent> panels;
    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;

    private ExecuteRequest request;

    /**
     *
     * @param provider
     * @param request
     */
    public ResultPanel(RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        this.panels = new ArrayList<>();
        initComponents();
        this.selectedProcess.setText(request.getIdentifier());
        this.selectedServer.setText(request.getEndpoint());
        ImageIcon ico = (ImageIcon) (UIManager.get(AppConstants.ICON_LOADING_STATUS_KEY));
        this.loadingLabel.setIcon(ico);
        this.loadingLabel.setText("Preparing statement.");
        this.resultPane.setVisible(false);
        this.resultTable.setVisible(false);
    }

    /**
     */
    public void executeProcess() {
        this.resultPane.setVisible(false);
        this.loadingLabel.setVisible(true);

        ExecuteThread mt = new ExecuteThread(this, this.request, this.provider);
        mt.start();
        this.loadingLabel.setText(AppConstants.DIALOG_REQUEST_SENT);
    }

    /**
     * Updates this panel, onces the thread is run.
     *
     * @param request.
     * @see ExecuteThread.
     */
    private void update(ExecuteRequest request) {
        this.loadingLabel.setText("Processing results.");
        this.request = request;
        if (this.request.isException()) {
            renderException(request);
        } else {
            this.renderResults(request);
        }
    }

    private void renderResults(ExecuteRequest request) {
        this.request = request;
        HashMap results = this.request.getResults();
        java.util.Set keys = results.keySet();

     
        Object rowData[][] = new Object[results.size()][4];
        int i = 0;
        for (Object key : keys) {

            Object value = results.get(key);
            if (value instanceof URL) {
                URL val = (URL) value;
                rowData[i][0] = "C";
                rowData[i][1] = key;
                rowData[i][2] = value;
                rowData[i][3] = new OpenInBrowserButton(val);
                i++;
            } else if (value instanceof LiteralDataType) {
                String val = ((LiteralDataType) value).getStringValue();
                rowData[i][0] = "L";
                rowData[i][1] = key;
                rowData[i][2] = value;
                rowData[i][3] = new CopyToClipboardButton(val);
                i++;
            } else if (value instanceof BoundingBoxType) {
                String val = ((BoundingBoxType) value).toString();
                rowData[i][0] = "B";
                rowData[i][1] = key;
                rowData[i][2] = value;
                rowData[i][3] = new CopyToClipboardButton(val);
                i++;
            }

            this.resultTable = new ResultTable(rowData);
            this.resultTable.setVisible(true);
            this.resultPane.setViewportView(resultTable);
            this.resultPane.setVisible(true);
            this.loadingLabel.setVisible(false);
        }
    }

    private void renderException(ExecuteRequest request) {
        this.request = request;
        this.loadingLabel.setVisible(false);
        ExceptionRenderer exception = new ExceptionRenderer(this.request.getException());
        /*TitledComponent tc = new TitledComponent("Exception", exception, TitledComponent.DEFAULT_TITLE_HEIGHT);
         this.resultPane.setViewportView(tc);*/

        this.remove(this.resultPane);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

        this.add(exception, gridBagConstraints);
        this.validate();
    }

    /**
     *
     */
    @Override
    public void updateRequest() {
        //noop
    }

    /**
     *
     * @return
     */
    @Override
    public ExecuteRequest getRequest() {
        return this.request;
    }

    private class ExecuteThread extends Thread {

        private ExecuteRequest request;
        private RichWPSProvider provider;
        private ResultPanel parent;

        public ExecuteThread(ResultPanel parent, ExecuteRequest request, RichWPSProvider provider) {
            this.parent = parent;
            this.request = request;
            this.provider = provider;
        }

        @Override
        public void run() {
            this.provider.perform(this.request);
            this.parent.update(this.request);
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

        loadingLabel = new javax.swing.JLabel();
        resultPane = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        selectedProcess = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        selectedServer = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setMinimumSize(new java.awt.Dimension(620, 700));
        setPreferredSize(new java.awt.Dimension(620, 700));
        setLayout(new java.awt.GridBagLayout());

        loadingLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        loadingLabel.setText("Loading");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(loadingLabel, gridBagConstraints);

        resultPane.setBorder(null);
        resultPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        resultPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        resultPane.setMinimumSize(new java.awt.Dimension(600, 600));
        resultPane.setPreferredSize(new java.awt.Dimension(600, 600));

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        resultPane.setViewportView(resultTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(resultPane, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedProcess, gridBagConstraints);

        selectedProcessLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedProcessLabel.setText("Process:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedProcessLabel, gridBagConstraints);

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedServer, gridBagConstraints);

        selectedServerLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedServerLabel.setText("Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedServerLabel, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Results for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jSeparator1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JScrollPane resultPane;
    private javax.swing.JTable resultTable;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
