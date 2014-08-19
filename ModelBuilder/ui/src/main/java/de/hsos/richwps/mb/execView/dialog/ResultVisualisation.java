package de.hsos.richwps.mb.execView.dialog;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.execView.dialog.components.renderer.LiteralResultRenderer;
import de.hsos.richwps.mb.execView.dialog.components.renderer.URIResultRenderer;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.IOutputArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.ExecuteRequestDTO;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputComplexDataArgument;
import de.hsos.richwps.mb.richWPS.entity.execute.OutputLiteralDataArgument;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 *
 * @author dalcacer
 */
public class ResultVisualisation extends ADialogPanel {

    private List<JPanel> renderers;
    private RichWPSProvider provider;
    private ExecuteRequestDTO dto;

    public ResultVisualisation(RichWPSProvider provider, ExecuteRequestDTO dto) {
        this.provider = provider;
        this.dto = dto;
        this.renderers = new ArrayList<>();
        initComponents();
        
        ImageIcon ico = (ImageIcon) (UIManager.get(AppConstants.ICON_LOADING_STATUS_KEY));
        this.loadingLabel.setIcon(ico);
    }

    /**
     *
     */
    public void executeProcess() {
        this.jScrollPane1.setVisible(false);
        this.loadingLabel.setVisible(true);

        ExecuteThread mt = new ExecuteThread(this, this.dto, this.provider);
        mt.start();
    }

    private void update(ExecuteRequestDTO dto) {
        this.dto = dto;
        HashMap results = this.dto.getResults();
        HashMap arguments = this.dto.getOutputArguments();
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
                outputsPanel.add(pan, c);
            } else if (argument instanceof OutputLiteralDataArgument) {
                String value = (String) results.get(key);
                OutputLiteralDataArgument _argument = (OutputLiteralDataArgument) argument;
                String identifier = (_argument.getSepcifier()).getIdentifier();
                LiteralResultRenderer pan = new LiteralResultRenderer(identifier, value);
                outputsPanel.add(pan, c);
            }
            i++;
        }

        this.jScrollPane1.setViewportView(outputsPanel);
        this.jScrollPane1.setVisible(true);
        this.loadingLabel.setVisible(false);
    }

    @Override
    public void updateDTO() {
        //nop
    }

    @Override
    public ExecuteRequestDTO getDTO() {
        //nop
        return this.dto;
    }

    private class ExecuteThread extends Thread {

        private ExecuteRequestDTO dto;
        private RichWPSProvider provider;
        private ResultVisualisation parent;

        public ExecuteThread(ResultVisualisation parent, ExecuteRequestDTO dto, RichWPSProvider provider) {
            this.parent = parent;
            this.dto = dto;
            this.provider = provider;
        }

        public void run() {
            this.dto = this.provider.executeProcess(this.dto);
            this.parent.update(this.dto);
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
        jScrollPane1 = new javax.swing.JScrollPane();

        setPreferredSize(new java.awt.Dimension(600, 600));
        setLayout(new java.awt.GridBagLayout());

        loadingLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        loadingLabel.setText("Loading");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(loadingLabel, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(610, 600));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(610, 600));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel loadingLabel;
    // End of variables declaration//GEN-END:variables

}
