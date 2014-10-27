package de.hsos.richwps.mb.app.view.appFrame;

import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.menu.AppMenuBar;
import de.hsos.richwps.mb.app.view.toolbar.ModellingToolbar;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import layout.TableLayout;

/**
 * The ModelBuilder's main frame.
 *
 * @author dziegenh
 */
public class ModellingPanel extends JPanel {

    // Comnponent container
    private JSplitPane mainPanel;
    
    private App app;
    private Component propertiesView;
    private TitledComponent graphViewGui;
    private AppActionProvider actionProvider;
    private ModellingToolbar toolbar;

    /**
     * Empty.
     */
    public ModellingPanel() {
    }

    public void init(App app) {
        this.app = app;
        this.actionProvider = app.getActionProvider();

        // init gui
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        addComponents();
    }

    /**
     * Creates and adds all frame components.
     */
    private void addComponents() {
        add(getToolbar(), "0 0");
        add(getMainPanel(), "0 1");
    }

    /**
     * The properties view (east side of the frame).
     *
     * @return
     */
    private Component getPropertiesView() {
        if (null == propertiesView) {
            propertiesView = app.getPropertiesViewGui();
            propertiesView.setPreferredSize(AppConstants.PROPERTIES_PANEL_PREFERRED_SIZE);
        }

        return propertiesView;
    }

    /**
     * The main toolbar.
     *
     * @return
     */
    private ModellingToolbar getToolbar() {
        if (null == toolbar) {
            toolbar = new ModellingToolbar(actionProvider);
        }

        return toolbar;
    }


    /**
     * A horizontal splitpane containing the center panel (left) and the
     * properties view (right).
     *
     * @return
     */
    private Component getMainPanel() {

        if (null == mainPanel) {
            mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainPanel.add(getGraphViewGui(), JSplitPane.LEFT);
            mainPanel.add(getPropertiesView(), JSplitPane.RIGHT);
            // only expand the center panel on resize
            mainPanel.setResizeWeight(1);
            // TODO Resizing the panel is disabled because of buggy behaviours in SingleProcessCard
        }

        return mainPanel;
    }

    private TitledComponent getGraphViewGui() {
        if (null == graphViewGui) {
            graphViewGui = new TitledComponent(AppConstants.EDITOR_DEFAULT_TITLE, app.getGraphViewGui());

            // Add proxy layer with minimum z index (=on top).
            graphViewGui.add(app.getGraphDndProxy(), "0 1");
            app.getGraphDndProxy().setLocation(0, 0);
            graphViewGui.setComponentZOrder(app.getGraphDndProxy(), 0);
        }

        return graphViewGui;
    }

}
