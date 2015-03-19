package de.hsos.richwps.mb.app.view.qos;

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

    public ManageTargetsPanel(final Window parent, List<QoSTarget> targets) {
        super(parent);

        init(targets);

        getAddItemButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditQosTargetDialog dialog = new EditQosTargetDialog(parent, null);
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
