package de.hsos.richwps.mb.app.view.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.dialogs.components.renderer.ExceptionRenderer;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 * Dialog panel for profileresult visualisation.
 *
 * @author dalcacer
 * @version 0.0.3
 */
public class ProfileResultPanel extends APanel {

    /**
     * List of displayable results.
     */
    private List<TitledComponent> panels;
    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;
    private ProfileRequest request;
    private boolean expand = false;

    /**
     *
     * @param provider
     * @param request
     */
    public ProfileResultPanel(RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ProfileRequest) request;
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
        this.expand = false;
        this.timestepsTable.setVisible(false);
    }

    /**
     */
    public void profileProcess() {
        this.resultPane.setVisible(false);
        this.loadingLabel.setVisible(true);

        ProfileThread mt = new ProfileThread(this, this.request, this.provider);
        mt.start();
        this.loadingLabel.setText(AppConstants.DIALOG_REQUEST_SENT);
    }

    private void update(ProfileRequest request) {
        this.loadingLabel.setText("Processing results.");
        this.request = (ProfileRequest) request;
        if (this.request.isException()) {
            this.renderException(request);
        } else {
            this.renderResults(request);
        }
    }

    private void renderResults(ProfileRequest request) {
        this.request = (ProfileRequest) request;
        HashMap results = this.request.getResults();
        java.util.Set keys = results.keySet();

        String rowData[][] = new String[results.size()][4];

        int i = 0;
        for (Object key : keys) {
            TableColumn column = new TableColumn(4);
            List<String> aresult = (List) results.get(key);
            rowData[i][0] = aresult.get(0);
            rowData[i][1] = aresult.get(1);
            rowData[i][2] = aresult.get(2);
            rowData[i][3] = aresult.get(3);
            i++;
        }

        this.timestepsTable = new JTable(new TimeStepsTableModel(rowData));
        String currentfontname = this.timestepsTable.getTableHeader().getFont().getFontName();
        int currentfonsize = this.timestepsTable.getTableHeader().getFont().getSize();
        this.timestepsTable.getTableHeader().setFont(new Font(currentfontname, Font.BOLD, currentfonsize));
        this.timestepsTable.setAutoCreateRowSorter(true);
        this.timestepsTable.setVisible(true);
        this.resultPane.setViewportView(timestepsTable);
        this.resultPane.setVisible(true);
        this.loadingLabel.setVisible(false);
    }

    private void renderException(ProfileRequest request) {
        this.request = request;
        this.loadingLabel.setVisible(false);

        ExceptionRenderer exception = new ExceptionRenderer(request.getException());

        this.remove(this.resultPane);
        this.add(exception, BorderLayout.CENTER);
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

    private class ProfileThread extends Thread {

        private ProfileRequest request;
        private RichWPSProvider provider;
        private ProfileResultPanel parent;
        private boolean expand = false;

        public ProfileThread(ProfileResultPanel parent, ProfileRequest request, RichWPSProvider provider) {
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

        stepDescriptionLabel = new javax.swing.JLabel();
        loadingLabel = new javax.swing.JLabel();
        resultPane = new javax.swing.JScrollPane();
        timestepsTable = new javax.swing.JTable();

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

        timestepsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Action", "Description", "Starttime", "Time"
            }
        ));
        resultPane.setViewportView(timestepsTable);

        add(resultPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JScrollPane resultPane;
    private javax.swing.JLabel stepDescriptionLabel;
    private javax.swing.JTable timestepsTable;
    // End of variables declaration//GEN-END:variables

    private class TimeStepsTableModel extends AbstractTableModel {

        String colHeadings[] = new String[]{
            "Action", "Description", "Starttime", "Runtime"};

        String[][] data;

        public TimeStepsTableModel(String[][] rowdata) {
            this.data = rowdata;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return colHeadings.length;
        }

        @Override
        public String getColumnName(int col) {
            return colHeadings[col];
        }

        @Override
        public Object getValueAt(int i, int i1) {
            return data[i][i1];
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
}
