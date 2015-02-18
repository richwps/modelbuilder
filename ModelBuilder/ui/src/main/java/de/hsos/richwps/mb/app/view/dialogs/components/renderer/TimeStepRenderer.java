package de.hsos.richwps.mb.app.view.dialogs.components.renderer;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class TimeStepRenderer extends javax.swing.JPanel {

    /**
     * Creates new form FeatureCollectionRenderer
     */
    public TimeStepRenderer(final String action, final String description,
            final String starttime, final String time) {

        initComponents();
        this.action.setText(action);
        this.description.setText(description);
        this.starttime.setText(starttime);
        this.time.setText(time);
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

        actionLabel = new javax.swing.JLabel();
        starttimeLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        action = new javax.swing.JLabel();
        description = new javax.swing.JLabel();
        starttime = new javax.swing.JLabel();
        time = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(400, 400));
        setPreferredSize(new java.awt.Dimension(400, 400));
        setLayout(new java.awt.GridBagLayout());

        actionLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        actionLabel.setText("Action:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(actionLabel, gridBagConstraints);

        starttimeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        starttimeLabel.setText("Startime:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(starttimeLabel, gridBagConstraints);

        timeLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        timeLabel.setText("Time:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(timeLabel, gridBagConstraints);

        descriptionLabel.setFont(new java.awt.Font("Droid Sans", 1, 12)); // NOI18N
        descriptionLabel.setText("Description:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(descriptionLabel, gridBagConstraints);

        action.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(action, gridBagConstraints);

        description.setText("jLabel3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(description, gridBagConstraints);

        starttime.setText("jLabel4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(starttime, gridBagConstraints);

        time.setText("jLabel5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(time, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel action;
    private javax.swing.JLabel actionLabel;
    private javax.swing.JLabel description;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel starttime;
    private javax.swing.JLabel starttimeLabel;
    private javax.swing.JLabel time;
    private javax.swing.JLabel timeLabel;
    // End of variables declaration//GEN-END:variables
}
