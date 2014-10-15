package de.hsos.richwps.mb.server.view;

import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.server.entity.DeployConfigField;
import de.hsos.richwps.mb.ui.ListWithButtons;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import layout.TableLayout;

/**
 * GUI component for selecting a deployment config. Also handles creating,
 * editing and deleting configs.
 *
 * @author dziegenh
 */
public class SelectDeployConfigView extends ListWithButtons<DeployConfig> {

    public SelectDeployConfigView(Window parent) {
        super(parent);
    }

    @Override
    protected JButton createAddButton() {
        JButton button = super.createAddButton();
        button.setToolTipText("Create a new configuration");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeployConfig config = new DeployConfig();
                final EditDeployConfigDialog configDialog = new EditDeployConfigDialog(parent, "Deployment configuration", config);
                configDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        DeployConfig config = configDialog.getConfig();
                        if (null != config) {
                            listModel.addElement(config);
                            int idx = listModel.getSize() - 1;
                            viewList.getSelectionModel().setSelectionInterval(idx, idx);
                            viewList.invalidate();
                            viewList.updateUI();
                        }
                    }
                });

                configDialog.setVisible(true);
            }
        });

        return button;
    }

    @Override
    protected JButton createEditButton() {
        JButton button = super.createEditButton();
        button.setToolTipText("Edit selected configuration");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final EditDeployConfigDialog configDialog = new EditDeployConfigDialog(parent, "Deployment configuration", viewList.getSelectedValue());
                configDialog.setVisible(true);
            }
        });

        return button;
    }

    @Override
    protected JButton createDeleteButton() {
        JButton button = super.createDeleteButton();
        button.setToolTipText("Delete selected configuration");

        return button;
    }

    class EditDeployConfigDialog extends MbDialog {

        private DeployConfig config;
        private final DeployConfigView configView;

        private EditDeployConfigDialog(Window parent, String title, DeployConfig config) {
            super(parent, title, (BTN_ID_OK | BTN_ID_CANCEL));

            this.config = config;

            Dimension dialogSize = new Dimension(280, 280);
            setSize(dialogSize);
            dialogSize.width = 100;
            setMinimumSize(dialogSize);

            double[][] size = new double[][]{
                {TableLayout.FILL},
                {TableLayout.FILL}
            };
            Container content = getContentPane();
            content.setLayout(new TableLayout(size));

            configView = new DeployConfigView(config);
            configView.init();
            content.add(configView, "0 0");
        }

        @Override
        protected void handleDialogButton(int buttonId) {
            switch (buttonId) {
                case BTN_ID_OK:
                    if (!handleOk()) {
                        return;
                    }
                    break;

                case BTN_ID_CANCEL:
                    deleteConfig();
                    break;
            }

            super.handleDialogButton(buttonId);
        }

        protected boolean handleOk() {
            DeployConfig tmpConfig = configView.getConfig().clone();
            configView.saveTextFieldValuesToConfig();

            // assert endpoint value
            String endpoint = config.getValue(DeployConfigField.ENDPOINT);
            if (null == endpoint || endpoint.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "An endpoint must be specified.", "Error in configuration", JOptionPane.ERROR_MESSAGE);

                // restore config values
                config.setValue(DeployConfigField.ENDPOINT, tmpConfig.getValue(DeployConfigField.ENDPOINT));
                configView.update();
                return false;
            }

            return true;
        }

        public DeployConfig getConfig() {
            return config;
        }

        private void deleteConfig() {
            config = null;
        }
    }

}
