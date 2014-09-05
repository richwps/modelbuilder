/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.server;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.server.view.SelectDeployConfigView;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class DeployView extends MbDialog {

    private final JButton okButton;
    private SelectDeployConfigView selectView;

    public DeployView(Window parent, String title) {
        super(parent, title, MB_DIALOG_BUTTONS.CANCEL_OK);

        this.parent = parent;
        Dimension size = new Dimension(480, 160);
        setSize(size);
        size.width = 100;
        setMinimumSize(size);

        okButton = new JButton(AppConstants.DIALOG_BTN_OK);
    }

    public void init(List<DeployConfig> configs) {
        Container content = getContentPane();
        double[][] layoutSize = new double[][]{
            {TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED},
            {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED}
        };
        content.removeAll();
        content.setLayout(new TableLayout(layoutSize));

        JButton cancelButton = new JButton(AppConstants.DIALOG_BTN_CANCEL);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        okButton.setFont(okButton.getFont().deriveFont(Font.BOLD));
        okButton.setEnabled(null != configs && configs.size() > 0);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO let caller get selected config
                dispose();
            }
        });
        selectView = new SelectDeployConfigView(this);
        selectView.init(configs);
        selectView.addSelectConfigListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtons();
            }
        });

        int y = 0;
        content.add(new JLabel("Select deployment configuration:"), "0 "+y+" 2 "+y);
        y++;
        content.add(selectView, "0 "+y+" 2 "+y);
        y+=2;
        content.add(cancelButton, "1 "+y);
        content.add(okButton, "2 "+y);
    }

    protected void updateButtons() {
        boolean hasSelection = null != selectView.getSelectedConfig();
        okButton.setEnabled(hasSelection);
    }

    public DeployConfig getConfig() {
        return selectView.getSelectedConfig();
    }
}
