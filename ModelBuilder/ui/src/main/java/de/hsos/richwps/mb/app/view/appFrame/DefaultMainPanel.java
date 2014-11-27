package de.hsos.richwps.mb.app.view.appFrame;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import layout.TableLayout;

/**
 * The default panel for the frame's main content area. Provides common actions
 * like new/open model etc.
 *
 * @author dziegenh
 */
public class DefaultMainPanel extends JPanel {

    private AppActionProvider actionProvider;
    private static final double DIVIDER_SIZE = 1d;
    private static final int TOOLBAR_SPACER_SIZE = 10;

    public DefaultMainPanel(AppActionProvider actionProvider) {
        this.actionProvider = actionProvider;

        double[] layoutX = {
            TableLayout.FILL,
            TableLayout.PREFERRED,
            TableLayout.FILL
        };

        double[] layoutY = {
            TableLayout.FILL,
            TableLayout.PREFERRED, // toolbar with large icons
            DIVIDER_SIZE,
            TOOLBAR_SPACER_SIZE,
            TableLayout.PREFERRED, // open recent caption
            TableLayout.PREFERRED, // open recent button
            TableLayout.FILL
        };

        setLayout(new TableLayout(new double[][]{layoutX, layoutY}));

        addComponents();
    }

    private void addComponents() {

        // TB with large icons
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.setFloatable(false);
        toolbar.add(
                createFromAction(actionProvider.getAction(AppActionProvider.APP_ACTIONS.NEW_MODEL), AppConstants.ICON_NEW_KEY)
        );
        toolbar.add(createToolbarSpacer());
        toolbar.add(
                createFromAction(actionProvider.getAction(AppActionProvider.APP_ACTIONS.LOAD_MODEL), AppConstants.ICON_OPEN_KEY)
        );
        JPanel toolbarCentering = new JPanel(new TableLayout(new double[][]{{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL}, {TableLayout.PREFERRED}}));
        toolbarCentering.add(toolbar, "1 0");
        add(toolbarCentering, "1 1");

        // divider
        add(new JLabelWithBackground(AppConstants.SELECTION_BG_COLOR), "1 2");

        // Recently used
        AppAction recentAction = actionProvider.getAction(AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE);

        if (recentAction.isEnabled()) {
            // caption
            JLabel recentlyCaption = new JLabel(AppConstants.CAPTION_RECENTLY_USED);
            recentlyCaption.setFont(recentlyCaption.getFont().deriveFont(Font.ITALIC));
            add(recentlyCaption, "1 4");

            // button
            JButton recentlyButton = new JButton(recentAction);
            recentlyButton.setForeground(Color.BLUE);

            // button wrapper (for using toolbar button style)
            JToolBar recentWrapper = new JToolBar();
            recentWrapper.setFloatable(false);
            recentWrapper.add(recentlyButton);
            add(recentWrapper, "1 5");
        }
    }

    private JButton createFromAction(AppAction action, String smallIconKey) {
        JButton button = new JButton(action);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setIcon(UIManager.getIcon(AppConstants.LARGE_ICON_PREFIX + smallIconKey));
        return button;
    }

    private Component createToolbarSpacer() {
        JLabel spacer = new JLabel();
        spacer.setBorder(new EmptyBorder(0, TOOLBAR_SPACER_SIZE, 0, 0));
        return spacer;
    }

}
