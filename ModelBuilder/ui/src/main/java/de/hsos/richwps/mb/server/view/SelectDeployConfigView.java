/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.server.view;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class SelectDeployConfigView extends JPanel {

    private Window parent;
    private final JButton addConfigButton;
    private final JButton editConfigButton;
    private final JButton deleteConfigButton;
    private final JList<DeployConfig> viewList;
    private DefaultListModel<DeployConfig> listModel;

    public SelectDeployConfigView(Window parent) {
        super();
        this.parent = parent;

        addConfigButton = createAddButton();
        editConfigButton = createEditButton();
        deleteConfigButton = createDeleteButton();

        listModel = new DefaultListModel<>();
        viewList = createList();
    }

    public void init(List<DeployConfig> configs) {
        double[][] layoutSize = new double[][]{
            {TableLayout.FILL, TableLayout.PREFERRED},
            {TableLayout.PREFERRED, TableLayout.FILL}
        };
        setLayout(new TableLayout(layoutSize));

        // Setup and add Config List
        listModel.removeAllElements();
        for (DeployConfig aConfig : configs) {
            listModel.addElement(aConfig);
        }
        add(viewList, "0 0");

        // Add Buttons
        add(createButtonsPanel(), "1 0");

        if (configs.isEmpty()) {
            editConfigButton.setEnabled(false);
            deleteConfigButton.setEnabled(false);
        } else {
            viewList.setSelectedIndex(0);
        }
    }

    private JList<DeployConfig> createList() {
        final JList<DeployConfig> viewList = new JList<>(listModel);
        viewList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        viewList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DeployConfig selectedValue = viewList.getSelectedValue();
                boolean hasSelection = null != selectedValue;
                editConfigButton.setEnabled(hasSelection);
                deleteConfigButton.setEnabled(hasSelection);
            }
        });
        viewList.setBorder(new LineBorder(Color.DARK_GRAY, 1));

        return viewList;
    }

    private Container createButtonsPanel() {
        JToolBar pseudoBar = new JToolBar(JToolBar.VERTICAL);
        pseudoBar.setFloatable(false);
        pseudoBar.add(addConfigButton);
        pseudoBar.add(editConfigButton);
        pseudoBar.add(deleteConfigButton);
        return pseudoBar;
    }

    private JButton createAddButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_ADD_KEY));
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

    private JButton createEditButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_EDIT_KEY));
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

    private JButton createDeleteButton() {
        JButton button = new JButton(UIManager.getIcon(AppConstants.ICON_DELETE_KEY));
        button.setToolTipText("Delete selected configuration");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showOptionDialog(parent, "Delete the selected deployment configuration?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (choice == JOptionPane.YES_OPTION) {
                    listModel.removeElementAt(viewList.getSelectionModel().getMinSelectionIndex());
                }
            }
        });

        return button;
    }

    public void addSelectConfigListener(ListSelectionListener listener) {
        viewList.getSelectionModel().addListSelectionListener(listener);
    }

    public DeployConfig getSelectedConfig() {
        return viewList.getSelectedValue();
    }

    class EditDeployConfigDialog extends MbDialog {

        private DeployConfig config;
        private final DeployConfigView configView;

        private EditDeployConfigDialog(Window parent, String title, DeployConfig config) {
            super(parent, title, MB_DIALOG_BUTTONS.CANCEL_OK);

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
        protected void handleCancel() {
            deleteConfig();
            super.handleCancel();
        }

        @Override
        protected void handleOk() {
            configView.saveTextFieldValuesToConfig();
            super.handleOk();
        }

        public DeployConfig getConfig() {
            return config;
        }

        private void deleteConfig() {
            config = null;
        }
    }

}
