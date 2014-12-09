package de.hsos.richwps.mb.ui.dialogs.components;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.ExceptionRenderer;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.LiteralResultRenderer;
import de.hsos.richwps.mb.ui.dialogs.components.renderer.URIResultRenderer;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.IRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.ExecuteRequest;
import de.hsos.richwps.mb.richWPS.entity.impl.TestRequest;
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
 * @version 0.0.1
 */
public class TestResultVisualisation extends ADialogPanel {

    private List<TitledComponent> renderers;
    private RichWPSProvider provider;
    private TestRequest request;

    /**
     *
     * @param provider
     * @param request
     */
    public TestResultVisualisation(RichWPSProvider provider, IRequest request) {
        this.provider = provider;
        this.request = (TestRequest) request;
        this.renderers = new ArrayList<>();
        initComponents();
        this.selectedProcess.setText(request.getIdentifier());
        this.selectedServer.setText(request.getEndpoint());
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
        this.loadingLabel.setText("<html>Sending and processing statement.<br/>"
                + "This might take some time, depending on the remote process.</html>");
    }

    private void update(TestRequest request) {
        this.loadingLabel.setText("Processing results.");
        this.request = (TestRequest) request;
        if (this.request.isException()) {
            renderException(request);
        } else {
            this.renderResults(request);
        }
    }

    private void renderResults(TestRequest request) {
        this.request = (TestRequest) request;
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
            String value = (String) results.get(key);
            if (value.contains("http://")) {
                URIResultRenderer pan = new URIResultRenderer(key.toString(), value);
                TitledComponent tc = new TitledComponent(key.toString(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.setTitleBold();
                tc.fold();
                outputsPanel.add(tc, c);
            } else {
                LiteralResultRenderer pan = new LiteralResultRenderer(key.toString(), value);
                TitledComponent tc = new TitledComponent(key.toString(), pan, TitledComponent.DEFAULT_TITLE_HEIGHT, true);
                tc.setTitleBold();
                tc.fold();
                outputsPanel.add(tc, c);
            }
            i++;
        }

        String c = "0," + i + 1;
        outputsPanel.add(new JPanel(), c);

        this.resultPane.setViewportView(outputsPanel);
        this.resultPane.setVisible(true);
        this.loadingLabel.setVisible(false);
    }

    private void renderException(TestRequest request) {
        this.request = request;
        this.loadingLabel.setVisible(false);

        ExceptionRenderer r = new ExceptionRenderer(request.getException());
        TitledComponent tc = new TitledComponent("Exception", r, TitledComponent.DEFAULT_TITLE_HEIGHT);
        tc.unfold();
        tc.setTitleBold();

        this.remove(this.resultPane);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 3;
        g.fill = GridBagConstraints.BOTH;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.ipadx = 5;
        g.ipady = 5;
        g.insets = new Insets(5, 5, 5, 5);
        g.gridwidth = 2;

        this.add(tc, g);
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
    public IRequest getRequest() {
        return this.request;
    }

    private class TestThread extends Thread {

        private TestRequest request;
        private RichWPSProvider provider;
        private TestResultVisualisation parent;

        public TestThread(TestResultVisualisation parent, TestRequest request, RichWPSProvider provider) {
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
        selectedProcess = new javax.swing.JLabel();
        selectedProcessLabel = new javax.swing.JLabel();
        selectedServer = new javax.swing.JLabel();
        selectedServerLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(680, 700));
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
        resultPane.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        resultPane.setMinimumSize(new java.awt.Dimension(610, 600));
        resultPane.setPreferredSize(new java.awt.Dimension(610, 550));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(resultPane, gridBagConstraints);

        selectedProcess.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcess, gridBagConstraints);

        selectedProcessLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedProcessLabel.setText("Process:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedProcessLabel, gridBagConstraints);

        selectedServer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServer, gridBagConstraints);

        selectedServerLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        selectedServerLabel.setText("Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedServerLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel loadingLabel;
    private javax.swing.JScrollPane resultPane;
    private javax.swing.JLabel selectedProcess;
    private javax.swing.JLabel selectedProcessLabel;
    private javax.swing.JLabel selectedServer;
    private javax.swing.JLabel selectedServerLabel;
    // End of variables declaration//GEN-END:variables

}
