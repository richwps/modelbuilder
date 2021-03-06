package de.hsos.richwps.mb.app.view.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.DescribeRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ProfileRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
import de.hsos.richwps.mb.app.view.dialogs.components.renderer.VariablesTable;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Dialog panel for variable selection.
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class VariablesPanel extends APanel {

    /**
     * Connection to RichWPS server.
     */
    private RichWPSProvider provider;
    /**
     * Request that should be executed.
     */
    private TestRequest request;

    private boolean allSelected = false;

    private Map<String, String> edges;

    /**
     * Creates new form ExecutePanel
     */
    public VariablesPanel() {
        initComponents();
    }

    /**
     * Creates new form InputPanel
     *
     * @param provider
     * @param request
     * @param edges
     */
    public VariablesPanel(final RichWPSProvider provider, IRequest request, Map<String, String> edges) {
        this.provider = provider;
        this.request = (TestRequest) request;
        this.edges = edges;
        initComponents();
        
        
        this.stepDescriptionLabel.setText("Please select relevant variables: "+request.getIdentifier());
        Border paddingBorder = BorderFactory.createEmptyBorder(0,0,25,0);
        this.stepDescriptionLabel.setBorder(paddingBorder);

        if (request instanceof TestRequest) {
            //noop
        } else if (request instanceof ProfileRequest) {
            //noop
        } else {
            //update the perform-object only if necessary 
            if (!request.isLoaded()) {
                this.provider.perform((DescribeRequest) this.request);
            }
        }
        this.prepare();
        this.visualize();
    }

    /**
     * Creates and adds inputpanels based on inputdescriptions.
     */
    @Override
    public void prepare() {
        final Collection<String> keys = this.edges.keySet();
        Object rowData[][] = new Object[this.edges.size()][3];
        int i = 0;
        for (String key : keys) {
            final String routes = (String) this.edges.get(key);
            rowData[i][0] = "var." + key;
            rowData[i][1] = routes;
            rowData[i][2] = new Boolean(false);
            i++;
        }

        this.variablesSelection = new VariablesTable(rowData);
    }

    /**
     * Creates and adds inputpanels based on inputdescriptions.
     */
    @Override
    public void visualize() {
        this.variablesSelection.setVisible(true);
        this.variablesPanelScrollPane.setViewportView(variablesSelection);
        this.variablesPanelScrollPane.setVisible(true);
    }

    /**
     * Transcodes the inputs panel-wise into an executerequest actualinputs.
     */
    @Override
    public void updateRequest() {
        int size = this.variablesSelection.getModel().getRowCount();
        for (int i = 0; i < size; i++) {
            Boolean selected = (Boolean) this.variablesSelection.getModel().getValueAt(i, 2);
            String id = (String) this.variablesSelection.getModel().getValueAt(i, 0);
            if (selected) {
                this.request.addVariable(id);
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public TestRequest getRequest() {
        return this.request;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isValidInput() {
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

        stepDescriptionLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        selectAllButton = new javax.swing.JButton();
        variablesPanelScrollPane = new javax.swing.JScrollPane();
        variablesSelection = new javax.swing.JTable();

        setMinimumSize(new java.awt.Dimension(625, 667));
        setPreferredSize(new java.awt.Dimension(620, 650));
        setLayout(new java.awt.BorderLayout());

        stepDescriptionLabel.setFont(new java.awt.Font("Droid Sans", 1, 14)); // NOI18N
        stepDescriptionLabel.setText("Please select required outputs.");
        add(stepDescriptionLabel, java.awt.BorderLayout.NORTH);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        selectAllButton.setText("Select All");
        selectAllButton.setMaximumSize(new java.awt.Dimension(80, 32));
        selectAllButton.setMinimumSize(new java.awt.Dimension(70, 32));
        selectAllButton.setPreferredSize(new java.awt.Dimension(85, 32));
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(selectAllButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        variablesPanelScrollPane.setBorder(null);
        variablesPanelScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        variablesPanelScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        variablesPanelScrollPane.setMinimumSize(new java.awt.Dimension(610, 550));

        variablesSelection.setModel(new javax.swing.table.DefaultTableModel(
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
        variablesSelection.setName(""); // NOI18N
        variablesPanelScrollPane.setViewportView(variablesSelection);

        add(variablesPanelScrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        if (allSelected == false) {

            int size = this.variablesSelection.getModel().getRowCount();
            for (int i = 0; i < size; i++) {
                this.variablesSelection.setValueAt(new Boolean(true), i, 2);
            }
            allSelected = true;
            this.selectAllButton.setText(AppConstants.DIALOG_BTN_DESELECT_ALL);
            this.selectAllButton.setPreferredSize(new Dimension(90,32));
            return;
        }

        int size = this.variablesSelection.getModel().getRowCount();
        for (int i = 0; i < size; i++) {
            this.variablesSelection.setValueAt(new Boolean(false), i, 2);
        }

        allSelected = false;
        this.selectAllButton.setText(AppConstants.DIALOG_BTN_SELECT_ALL);
        this.selectAllButton.setPreferredSize(new Dimension(85,32));
    }//GEN-LAST:event_selectAllButtonActionPerformed

    /**
     * Indicator if resizable.
     *
     * @return indicator
     */
    @Override
    public boolean isResizeable() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JLabel stepDescriptionLabel;
    private javax.swing.JScrollPane variablesPanelScrollPane;
    private javax.swing.JTable variablesSelection;
    // End of variables declaration//GEN-END:variables

}
