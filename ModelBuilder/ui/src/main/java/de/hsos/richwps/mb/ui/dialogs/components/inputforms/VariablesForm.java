package de.hsos.richwps.mb.ui.dialogs.components.inputforms;

/**
 *
 * @author dalcacer
 * @version 0.0.1
 */
public class VariablesForm extends javax.swing.JPanel {

    private String variablenames;
    /**
     * Creates new form OutputsParamPanel
     *
     */
    public VariablesForm(final String variablenames, final String route) {
        initComponents();
        this.variablenames=variablenames;
        this.variableName.setText(variablenames);
        String[] routes = route.split(",");
        String text = "";
        for (String r : routes) {
            text += r + "\n";
        }
        this.routeDescription.setText(text);
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return this.variableName.isSelected();
    }

    public void setSelected() {
        this.variableName.setSelected(true);
    }

    public void setUnselected() {
        this.variableName.setSelected(false);
    }
    
    public String getIdentifier(){
        return "var."+this.variablenames;
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

        variableName = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        routeDescription = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        setMinimumSize(new java.awt.Dimension(550, 200));
        setPreferredSize(new java.awt.Dimension(500, 250));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        variableName.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(variableName, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(250, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 100));

        routeDescription.setEditable(false);
        routeDescription.setColumns(20);
        routeDescription.setRows(5);
        routeDescription.setMinimumSize(new java.awt.Dimension(250, 100));
        routeDescription.setPreferredSize(new java.awt.Dimension(250, 100));
        jScrollPane1.setViewportView(routeDescription);

        add(jScrollPane1, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea routeDescription;
    private javax.swing.JCheckBox variableName;
    // End of variables declaration//GEN-END:variables
}
