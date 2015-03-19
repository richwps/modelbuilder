package de.hsos.richwps.mb.app.view.qos;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.entity.QoSTarget;
import de.hsos.richwps.mb.properties.Property;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import layout.TableLayout;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author dziegenh
 */
public class QosTargetsPanel extends JPanel {

    private final Window window;
    private final JButton editButton;
    private final Property<List<QoSTarget>> targetsProperty;

    public QosTargetsPanel(Window parent, final Property<List<QoSTarget>> targetsProperty) {
        super();
        
        this.window = parent;
        this.targetsProperty = targetsProperty;

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        setLayout(new TableLayout(new double[][]{{p, f}, {p}}));

        final String iconKey = AppConstants.ICON_EDIT_KEY;
        editButton = new JButton(UIManager.getIcon(iconKey));
        editButton.setToolTipText(AppConstants.QOS_MANAGE_TARGETS_BUTTON_TTT);

        // Use toolbar for button style
        JToolBar jToolBar = new JToolBar();
        jToolBar.add(editButton);
        jToolBar.setFloatable(false);
        add(jToolBar, "0 0");

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ManageTargetsDialog dialog = new ManageTargetsDialog(window, targetsProperty.getValue());
                dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(WindowEvent e) {
                        List<QoSTarget> targets = dialog.getTargets();
                        List<QoSTarget> oldValue = targetsProperty.getValue();
                        if (!CollectionUtils.isEqualCollection(targets, oldValue)) {
                            targetsProperty.setValue(targets);
                            
                        }
                    }

                });
                dialog.setVisible(true);
            }
        });

    }

    public void setEditable(boolean editable) {
        editButton.setEnabled(editable);
    }

    public void setQosPropertyGroup(Property<List<QoSTarget>> targetsProperty) {
        // TODO (necessary?!?)
        Logger.log("QOS TARGETS PROPERTY CHANGED!! NEED TO REACT?");
    }

}
