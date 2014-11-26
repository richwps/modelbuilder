package de.hsos.richwps.mb.app.view.semanticProxy;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import layout.TableLayout;

/**
 * Tab Component for semantic proxy search result tabs.
 *
 * @author dziegenh
 */
class SpTabTitle extends JPanel {

    private Color bgColor = new Color(1f, 1f, 1f, 0f);

    private static Insets borderInsets = new Insets(1, 1, 1, 1);

    private Border highlightBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
    private Border defaultBorder = new EmptyBorder(borderInsets);
    private final JLabel titleLabel;

    SpTabTitle(final SemanticProxyTabs tabs, final String tabId, String title) {

        // title label
        // closable via middle mouse button (eg using mousewheel as button)
        titleLabel = new JLabel(title);
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    tabs.removeTab(tabId);

                } else {
                    // delegate click
                    fireClickEvent(e);
                }
            }
        });

        // close button
        Icon closeIcon = UIManager.getIcon(AppConstants.ICON_CLOSE_KEY);
        final JLabel buttonWrapper = new JLabel(closeIcon);
        buttonWrapper.setBorder(new EmptyBorder(borderInsets));
        buttonWrapper.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                tabs.removeTab(tabId);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // highlight icon
                buttonWrapper.setBorder(highlightBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // un-highlight icon
                buttonWrapper.setBorder(defaultBorder);
            }
        });

        // add components
        setLayout(new TableLayout(new double[][]{{TableLayout.PREFERRED, TableLayout.PREFERRED}, {TableLayout.PREFERRED}}));
        add(titleLabel, "0 0");
        add(buttonWrapper, "1 0");

        // make it transparent
        setBackground(bgColor);
        titleLabel.setOpaque(false);
        buttonWrapper.setOpaque(false);
        setOpaque(false);
    }

    void setTitle(String title) {
        this.titleLabel.setText(title);
    }
    
    private List<MouseListener> clickListener = new LinkedList<>();

    void addClickListener(MouseListener listener) {
        clickListener.add(listener);
    }

    void removeClickListener(MouseListener listener) {
        clickListener.remove(listener);
    }

    private void fireClickEvent(MouseEvent e) {
        for (MouseListener listener : clickListener) {
            listener.mouseClicked(e);
        }

    }
    
}
