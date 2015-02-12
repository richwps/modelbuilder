package de.hsos.richwps.mb.app.view.dialogs.processReplacer;

import de.hsos.richwps.mb.entity.ProcessPort;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author dziegenh
 */
public class ProcessPortListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (null == value) {
            ((JLabel) component).setText("(skip mapping)");
            
        } else {
            ((JLabel) component).setText(((ProcessPort) value).getOwsIdentifier());
        }

        return component;
    }

}
