package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.dialogs.EditQosTargetDialog;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.ui.ListWithButtons;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author dziegenh
 */
public class ManageTargetsPanel extends ListWithButtons<QoSTarget> {

    private final static String DEFAULT_TARGET_TITLE = AppConstants.DEFAULT_QOS_TARGET;

    public ManageTargetsPanel(final Window parent, List<QoSTarget> targets) {
        super(parent);

        init(targets);

        getAddItemButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // check if default target exists
                boolean defaultTargetExists = false;
                List<QoSTarget> allTargets = getAllItems();
                for (QoSTarget qoSTarget : allTargets) {
                    if (qoSTarget.getTargetTitle().equals(DEFAULT_TARGET_TITLE)) {
                        defaultTargetExists = true;
                    }
                }

                QoSTarget newTarget = new QoSTarget();
                if (!defaultTargetExists) {
                    newTarget.setTargetTitle(DEFAULT_TARGET_TITLE);
                }

                EditQosTargetDialog dialog = new EditQosTargetDialog(parent, newTarget);
                dialog.setVisible(true);
                dialog.dispose();
                QoSTarget target = dialog.getTarget();

                if (null != target) {
                    String title = target.getTargetTitle();
                    if (null != title && !title.isEmpty()) {
                        addItemToList(target);
                    }
                }
            }
        });

        getEditItemButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedIndex = getSelectedIndex();
                QoSTarget selectedItem = getSelectedItem();

                EditQosTargetDialog dialog = new EditQosTargetDialog(parent, selectedItem);
                dialog.setVisible(true);
                dialog.dispose();
                QoSTarget target = dialog.getTarget();

                String title = target.getTargetTitle();
                if (null != title && !title.isEmpty()) {
                    setItemAt(selectedIndex, target);
                }
            }
        });
    }

}
