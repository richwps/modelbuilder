package de.hsos.richwps.mb.server.boundary;

import de.hsos.richwps.mb.server.entity.DeployConfig;
import de.hsos.richwps.mb.server.view.SelectDeployConfigView;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import layout.TableLayout;

/**
 * Dialog for process deployment.
 *
 * @author dziegenh
 */
public class DeployView extends MbDialog {

    private SelectDeployConfigView selectView;

    public DeployView(Window parent, String title) {
        super(parent, title, (BTN_ID_OK | BTN_ID_CANCEL));

        this.parent = parent;
        Dimension size = new Dimension(480, 160);
        setSize(size);
        size.width = 100;
        setMinimumSize(size);
    }

    public void init(List<DeployConfig> configs) {
        Container content = getContentPane();
        double[][] layoutSize = new double[][]{
            {TableLayout.FILL},
            {TableLayout.PREFERRED, TableLayout.PREFERRED}
        };
        content.removeAll();
        content.setLayout(new TableLayout(layoutSize));

        selectView = new SelectDeployConfigView(this);
        selectView.init(configs);
        selectView.addSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtons();
            }
        });

        int y = 0;
        content.add(new JLabel("Select deployment configuration:"), "0 " + y);
        y++;
        content.add(selectView, "0 " + y);
    }

    protected void updateButtons() {
        boolean hasSelection = null != selectView.getSelectedItem();
        JButton okButton = getDialogButton(BTN_ID_OK);
        if (null != okButton) {
            okButton.setEnabled(hasSelection);
        }
    }

    public DeployConfig getConfig() {
        return selectView.getSelectedItem();
    }
}
