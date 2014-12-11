package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.ExceptionRenderer;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.LiteralResultRenderer;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.URIResultRenderer;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.impl.arguments.OutputLiteralDataArgument;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 *
 * @author dalcacer
 * @version 0.0.2
 */
public class ResultVisualisation extends ADialogPanel {
    
    private List<TitledComponent> renderers;
    private RichWPSProvider provider;
    private ExecuteRequest request;
    private boolean expand = false;

    /**
     *
     * @param provider
     * @param request
     */
    public ResultVisualisation(RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (ExecuteRequest) request;
        this.renderers = new ArrayList<>();
        initComponents();
        this.selectedProcess.setText(request.getIdentifier());
        this.selectedServer.setText(request.getEndpoint());
        ImageIcon ico = (ImageIcon) (UIManager.get(AppConstants.ICON_LOADING_STATUS_KEY));
        this.loadingLabel.setIcon(ico);
        this.loadingLabel.setText("Preparing statement.");
        this.resultPane.setVisible(false);
        this.expand = false;
    }

    /**
     */
    public void executeProcess() {
        this.resultPane.setVisible(false);
        this.loadingLabel.setVisible(true);
        
        ExecuteThread mt = new ExecuteThread(this, this.request, this.provider);
        mt.start();
        this.loadingLabel.setText("<html>Sending and processing statement.<br/>"
                + "This might take some time, depending on the remote process.</html>");
    }
    
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
        HashMap arguments = this.request.getOutputArguments();
        java.util.Set keys = results.keySet();
        
        JPanel outputsPanel = new JPanel();
        
        double size[][] = new double[2][1];
        size[0] = new double[]{TableLayout.FILL};
        
        double innersize[] = new double[results.size()];
        for (int i = 0; i < results.size(); i++) {
            innersize[i] = TableLayout.PREFERRED;
        }
        size[1] = innersize;
        
        TableLayout layout = new TableLayout(size);
        outputsPanel.setLayout(layout);
        
        int i = 0;
        for (Object key : keys) {
            String c = "0," + i;
            IOutputArgument argument = (IOutputArgument) arguments.get(key);
            if (argument instanceof OutputComplexDataArgument) {
                String uri = (String) results.get(key);
                OutputComplexDataArgument _argument = (OutputComplexDataArgument) argument;
                String identifier = (_argument.getSpecifier()).getIdentifier();
                URIResultRenderer pan = new URIResultRenderer(identifier, uri);
                TitledComponent tc = new TitledComponent(identifier, pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.setTitleBold();
                tc.fold();
                this.renderers.add(tc);
                outputsPanel.add(tc, c);
            } else if (argument instanceof OutputLiteralDataArgument) {
                String value = (String) results.get(key);
                OutputLiteralDataArgument _argument = (OutputLiteralDataArgument) argument;
                String identifier = (_argument.getSepcifier()).getIdentifier();
                LiteralResultRenderer pan = new LiteralResultRenderer(identifier, value);
                TitledComponent tc = new TitledComponent(identifier, pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.setTitleBold();
                tc.fold();
                this.renderers.add(tc);
                outputsPanel.add(tc, c);
            }
            i++;
        }
        String c = "0," + i + 1;
        outputsPanel.add(new JPanel(), c);
        
        if (this.renderers.size() <= 2) {
            this.expandButton.setText(AppConstants.DIALOG__BTN_COLLAPSE_ALL);
            this.expand = true;
            for (int u = 0; u < this.renderers.size(); u++) {
                this.renderers.get(u).setFolded(false);
            }
        }
        
        this.resultPane.setViewportView(outputsPanel);
        this.resultPane.setVisible(true);
        this.loadingLabel.setVisible(false);
        this.validate();
    }
    
    private void renderException(ExecuteRequest request) {
        this.request = request;
        this.loadingLabel.setVisible(false);
        ExceptionRenderer exception = new ExceptionRenderer(this.request.getException());
        TitledComponent tc = new TitledComponent("Exception", exception, TitledComponent.DEFAULT_TITLE_HEIGHT);
        
        this.resultPane.setViewportView(tc);
        this.remove(this.resultPane);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 2;
        g.fill = GridBagConstraints.BOTH;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.ipadx = 5;
        g.ipady = 5;
        g.insets = new Insets(5, 5, 5, 5);
        g.gridwidth = 3;
        
        this.add(tc, g);
        this.validate();
        this.expandButton.setEnabled(false);
    }

    /**
     *
     */
    @Override
    public void updateRequest() {
        //nop
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
        private ResultVisualisation parent;
        
        public ExecuteThread(ResultVisualisation parent, ExecuteRequest request, RichWPSProvider provider) {
            this.parent = parent;
            this.request = request;
            this.provider = provider;
        }
        
        @Override
        public void run() {
            this.provider.request(this.request);
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
        jPanel1 = new javax.swing.JPanel();
        selectedProcess = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        selectedServer = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        expandButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(600, 700));
        setPreferredSize(new java.awt.Dimension(600, 700));
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
        resultPane.setViewportBorder(null);
        resultPane.setMinimumSize(new java.awt.Dimension(600, 550));
        resultPane.setPreferredSize(new java.awt.Dimension(600, 550));
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
        gridBagConstraints.ipadx = 20;
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
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(selectedServerLabel, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        jLabel1.setText("Results for");
        jPanel1.add(jLabel1, new java.awt.GridBagConstraints());

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

        jPanel2.setMinimumSize(new java.awt.Dimension(85, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(85, 100));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        expandButton.setText("Expand all");
        expandButton.setMaximumSize(new java.awt.Dimension(70, 32));
        expandButton.setMinimumSize(new java.awt.Dimension(70, 32));
        expandButton.setPreferredSize(new java.awt.Dimension(70, 32));
        expandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(expandButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void expandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandButtonActionPerformed
        if (this.expand == true) {
            for (TitledComponent tc : this.renderers) {
                tc.fold();
            }
            this.expand = false;
            this.expandButton.setText(AppConstants.DIALOG_BTN_EXPAND_ALL);
            return;
        }
        
        for (TitledComponent tc : this.renderers) {
            tc.unfold();
            this.expand = true;
            this.expandButton.setText(AppConstants.DIALOG__BTN_COLLAPSE_ALL);
        }
    }//GEN-LAST:event_expandButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton expandButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JScrollPane resultPane;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
