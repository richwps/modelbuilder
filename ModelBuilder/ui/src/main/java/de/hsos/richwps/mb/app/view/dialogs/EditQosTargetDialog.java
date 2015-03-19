package de.hsos.richwps.mb.app.view.dialogs;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.qos.EditTargetPanel;
import de.hsos.richwps.mb.app.view.qos.InvalidValueException;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.ui.MbDialog;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class EditQosTargetDialog extends MbDialog {

    private final EditTargetPanel editTargetPanel;

    private QoSTarget exportTarget;

    public EditQosTargetDialog(Window window, QoSTarget target) {
        super(window, AppConstants.QOS_EDIT_TARGET_DIALOG_TITLE, MbDialog.BTN_ID_CANCEL | MbDialog.BTN_ID_OK);

//        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        setLayout(new TableLayout(new double[][]{{f}, {f}}));

        editTargetPanel = new EditTargetPanel(target);
        add(editTargetPanel, "0 0");

        this.exportTarget = target;

        setSize(300, 200);
    }

    @Override
    protected void handleDialogButton(int buttonId) {
        if (isTheDialogButton(buttonId, MbDialog.BTN_ID_OK)) {
            try {
                this.exportTarget = editTargetPanel.getQosTarget();
            } catch (InvalidValueException ex) {

                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                // cancel window closing
                return;
            }
        }

        super.handleDialogButton(buttonId);
    }

    public QoSTarget getTarget() {
        return this.exportTarget;
    }

}
