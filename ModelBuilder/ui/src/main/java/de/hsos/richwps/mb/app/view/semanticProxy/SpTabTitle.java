package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.ComponentIconClickProxy;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.RIGHT;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * Tab Component for semantic proxy search result tabs.
 *
 * @author dziegenh
 */
class SpTabTitle extends JPanel {

    private Color bgColor = new Color(0f, 1f, 1f, 0f);

    SpTabTitle(final SemanticProxyTabs tabs, final String tabId, String title) {

        Icon closeIcon = UIManager.getIcon(AppConstants.ICON_CLOSE_KEY);

        JLabel titleLabel = new JLabel(title);

        // add click listener to close icon
        final ComponentIconClickProxy clickableIcon = new ComponentIconClickProxy(titleLabel, closeIcon);
        clickableIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabs.removeTab(tabId);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
//                clickableIcon.setB
            }
            
        });

        titleLabel.setIcon(clickableIcon);
        titleLabel.setHorizontalTextPosition(SwingConstants.LEFT);

        // add components
        setLayout(new TableLayout(new double[][]{{TableLayout.PREFERRED}, {TableLayout.PREFERRED}}));
        add(titleLabel, "0 0");

        setBackground(bgColor);
    }

}
