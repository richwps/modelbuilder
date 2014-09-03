package de.hsos.richwps.mb.server.view;

import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.server.entity.DeployConfigField;
import de.hsos.richwps.mb.ui.TextField;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import layout.TableLayout;

/**
 * A Component for editing a deploy config.
 * @author dziegenh
 */
public class DeployConfigView extends JPanel {

    private DeployConfig config;

    public DeployConfigView(DeployConfig config) {
        if (null == config) {
            this.config = new DeployConfig();
        } else {
            this.config = config;
        }
    }

    public void init() {
        // 2 components for each field: label and textfield
        // + 1 empty row which fills up additional space
        int numComponents = 2 * DeployConfigField.values().length + 1;

        double[][] layoutSize = new double[2][];
        layoutSize[0] = new double[] {TableLayout.FILL};
        layoutSize[1] = new double[numComponents];
        Arrays.fill(layoutSize[1], TableLayout.PREFERRED);
        layoutSize[1][numComponents-1] = TableLayout.FILL; // fills up empty space
        setLayout(new TableLayout(layoutSize));

        ActionListener textFieldActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() instanceof TextField) {
                    TextField textField = (TextField) e.getSource();
                    setConfigValue(textField.getUserObject(), textField.getText());
                }
            }
        };

        int componentNum = 0;
        for(DeployConfigField field : DeployConfigField.values()) {
            TextField<DeployConfigField> textField = new TextField(config.getValue(field));
            textField.setUserObject(field);
            textField.addActionListener(textFieldActionListener);
            add(new JLabel(field.toString()+":"), "0 " + componentNum++);
            add(textField, "0 " + componentNum++);
        }
    }

    protected void setConfigValue(DeployConfigField field, String value) {
        config.setValue(field, value);
    }

    public DeployConfig getConfig() {
        return config;
    }

    public void updateAllFields() {
        for(Component c : getComponents()) {
            if(c instanceof TextField) {
                TextField<DeployConfigField> textField = (TextField) c;
                config.setValue(textField.getUserObject(), textField.getText().trim());
            }
        }
    }

}
