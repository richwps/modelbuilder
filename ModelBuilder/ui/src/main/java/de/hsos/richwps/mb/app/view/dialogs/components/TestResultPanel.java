package de.hsos.richwps.mb.app.view.dialogs.components;

import de.hsos.richwps.mb.ui.OpenInBrowserButton;
import de.hsos.richwps.mb.ui.CopyToClipboardButton;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.dialogs.components.renderer.ExceptionRenderer;
import de.hsos.richwps.mb.app.view.dialogs.components.renderer.ResultTable;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.GridBagConstraints;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.wps.x100.LiteralDataType;

/**
 * Dialog panel for testresult visualisation.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class TestResultPanel extends APanel {

    /**
     * List of displayable results.
     */
    private List<TitledComponent> panels;
    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;
    private TestRequest request;

    /**
     *
     * @param provider
     * @param request
     */
    public TestResultPanel(RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (TestRequest) request;
        this.panels = new ArrayList<>();
        initComponents();
        final String selectedprocess = this.request.getIdentifier();
        this.stepDescriptionLabel.setText("Results for: "+selectedprocess+".");
        Border paddingBorder = BorderFactory.createEmptyBorder(0,0,25,0);
        this.stepDescriptionLabel.setBorder(paddingBorder);
        
        ImageIcon ico = (ImageIcon) (UIManager.get(AppConstants.ICON_LOADING_STATUS_KEY));
        this.loadingLabel.setIcon(ico);
        this.loadingLabel.setText("Preparing statement.");
        this.resultPane.setVisible(false);
    }

    /**
     */
    public void testProcess() {
        this.resultPane.setVisible(false);
        this.loadingLabel.setVisible(true);

        TestThread mt = new TestThread(this, this.request, this.provider);
        mt.start();
        this.loadingLabel.setText(AppConstants.DIALOG_REQUEST_SENT);
    }

    private void update(TestRequest request) {
        this.loadingLabel.setText("Processing results.");
        this.request = (TestRequest) request;
        if (this.request.isException()) {
            this.renderException(request);
        } else {
            this.renderResults(request);
        }
    }

    private void renderResults(TestRequest request) {
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
            this.resultTable.setVisible(true);
            this.resultPane.setViewportView(resultTable);
            this.resultPane.setVisible(true);
            this.loadingLabel.setVisible(false);
        }
    }

    private void renderException(TestRequest request) {
        this.request = request;
        this.loadingLabel.setVisible(false);

        ExceptionRenderer exception = new ExceptionRenderer(request.getException());

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
    public IRequest getRequest() {
        return this.request;
    }

    private class TestThread extends Thread {

        private TestRequest request;
        private RichWPSProvider provider;
        private TestResultPanel parent;
        private boolean expand = false;

        public TestThread(TestResultPanel parent, TestRequest request, RichWPSProvider provider) {
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
     * Indicator if resizable.
     *
     * @return indicator
     */
    @Override
    public boolean isResizeable() {
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

        stepDescriptionLabel = new javax.swing.JLabel();
        loadingLabel = new javax.swing.JLabel();
        resultPane = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(620, 700));
        setPreferredSize(new java.awt.Dimension(620, 700));
        setLayout(new java.awt.BorderLayout());

        stepDescriptionLabel.setFont(new java.awt.Font("Droid Sans", 1, 14)); // NOI18N
        stepDescriptionLabel.setText("Results.");
        add(stepDescriptionLabel, java.awt.BorderLayout.NORTH);

        loadingLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        loadingLabel.setText("Loading");
        add(loadingLabel, java.awt.BorderLayout.SOUTH);

        resultPane.setBorder(null);
        resultPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        resultPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        resultPane.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
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

        add(resultPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JScrollPane resultPane;
    private javax.swing.JTable resultTable;
    private javax.swing.JLabel stepDescriptionLabel;
    // End of variables declaration//GEN-END:variables

}
