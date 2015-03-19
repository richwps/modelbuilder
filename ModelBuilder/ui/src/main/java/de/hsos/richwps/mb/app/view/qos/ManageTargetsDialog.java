package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Container;
import java.awt.Window;
import java.util.List;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class ManageTargetsDialog extends MbDialog {

    private final ManageTargetsPanel manageTargetsPanel;

    private List<QoSTarget> exportTargets;

    public ManageTargetsDialog(Window parent, List<QoSTarget> targets) {
        super(parent, AppConstants.QOS_MANAGE_TARGETS_DIALOG_TITLE, MbDialog.BTN_ID_CANCEL | MbDialog.BTN_ID_OK);

        this.exportTargets = targets;

        manageTargetsPanel = new ManageTargetsPanel(parent, targets);

        double f = TableLayout.FILL;
        Container contentPane = getContentPane();
        contentPane.setLayout(new TableLayout(new double[][]{{f}, {f}}));
        contentPane.add(manageTargetsPanel, "0 0");

        setSize(420, 180);
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
            this.exportTargets = manageTargetsPanel.getAllItems();
        }

        super.handleDialogButton(buttonId);
    }

    List<QoSTarget> getTargets() {
        return exportTargets;
    }

}
