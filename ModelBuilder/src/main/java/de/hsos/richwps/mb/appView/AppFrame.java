/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView;

import de.hsos.richwps.mb.App;
import de.hsos.richwps.mb.AppConfig;
import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appView.menu.AppMenuBar;
import de.hsos.richwps.mb.appView.toolbar.AppToolbar;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import layout.TableLayout;

/**
 *
 * @author dziegenh
 */
public class AppFrame extends JFrame {

    // Comnponent container
    private JSplitPane leftPanel;
    private JSplitPane mainPanel;
    private JSplitPane centerAndRightPanel;
    private JSplitPane centerPanel;
    private App app;
    private InfoTabs infoTabs;
    private Component propertiesView;
    private AppMenuBar appMenuBar;
    private TitledComponent treeViewGui;

    private IAppActionHandler actionHandler;
    private AppToolbar toolbar;
    private TitledComponent graphViewGui;
    private JPanel serviceSummaryView;

    /**
     * Frame setup.
     */
    public AppFrame(App app) {
        super();

        // TODO Inversion of Control: AppFrame should not know class App!
        this.app = app;

        this.actionHandler = app.getActionHandler();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(AppConstants.FRAME_TITLE);
        setLocation(getStartLocation());
        setSize(getStartSize());
        if (isStartMaximized()) {
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        }

        // init gui
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        addComponents();

        // call after component is visible and has a size
        getLeftPanel().setDividerLocation(.5);
        // TODO restore divider locations from config

        // save frame location, size etc. when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Boolean maximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
                AppConfig.getConfig().putBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), maximized);

                if (maximized) {
                    setExtendedState(getExtendedState() & ~Frame.MAXIMIZED_BOTH);
                }

                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), getSize().width);
                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), getSize().height);

                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), getLocation().x);
                AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), getLocation().y);
            }
        });
    }

    /**
     * Creates and adds all frame components.
     */
    private void addComponents() {
        setJMenuBar(getAppMenuBar());
        add(getToolbar(), "0 0");
        add(getMainPanel(), "0 1");
    }

    /**
     * The summary component (south-west of the frame).
     *
     * @return
     */
    private Component getServiceSummaryView() {
        // TODO mock
        if (null == serviceSummaryView) {
            serviceSummaryView = new JPanel();
//            mxGraph graph = new mxGraph();
//            Object parent = graph.getDefaultParent();
//
//            graph.getModel().beginUpdate();
//            try {
//                Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
//                        30);
//                Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
//                        80, 30);
//                graph.insertEdge(parent, null, "Edge", v1, v2);
//            } finally {
//                graph.getModel().endUpdate();
//            }
//
//            mxGraphComponent graphComponent = new mxGraphComponent(graph);
//            serviceSummaryView.add(graphComponent);
        }

        return serviceSummaryView;
//        return new TitledComponent(AppConstants.OVERVIEW_TITLE, new JLabel(""));
    }

    /**
     * The InfoTabs component.
     *
     * @return
     */
    public InfoTabs getInfoTabsView() {
        if (null == infoTabs) {
            infoTabs = new InfoTabs();
            infoTabs.setMinimumSize(AppConstants.BOTTOM_TABS_MIN_SIZE);
            for (String[] tabData : AppConstants.INFOTABS) {
                infoTabs.addTab(tabData[0], tabData[1]);
            }

            infoTabs.output("server", "** connecting RichWPS-server...");
            infoTabs.output("server", "** server connection established.");
            infoTabs.output("server", "** requesting processes...");
            infoTabs.output("server", "** processes received.");

        }
        return infoTabs;
    }

    /**
     * The properties view (east side of the frame).
     *
     * @return
     */
    private Component getPropertiesView() {
        if (null == propertiesView) {
            propertiesView = app.getPropertiesViewGui();
            propertiesView.setMinimumSize(AppConstants.PROPERTIES_PANEL_MIN_SIZE);
        }

        return propertiesView;
    }

    /**
     * The main toolbar.
     *
     * @return
     */
    private AppToolbar getToolbar() {
        if (null == toolbar) {
            toolbar = new AppToolbar(actionHandler);
        }

        return toolbar;
        // TODO mock
//        return new JLabel("Toolbar?");
    }

    /**
     * The main panel contains all content panels and components below the
     * toolbar.
     *
     * @return
     */
    private Component getMainPanel() {
        if (null == mainPanel) {
            mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainPanel.add(getLeftPanel(), JSplitPane.LEFT);
            mainPanel.add(getCenterAndRightPanel(), JSplitPane.RIGHT);
        }

        return mainPanel;
    }

    /**
     * Vertical splitbar containing the tree (top component) and the summary
     * panel (bottom component).
     *
     * @return
     */
    private JSplitPane getLeftPanel() {
        if (null == leftPanel) {
            leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            leftPanel.add(getTreeViewGui(), JSplitPane.TOP);
            leftPanel.add(getServiceSummaryView(), JSplitPane.BOTTOM);
            // expand both components on resize
            leftPanel.setResizeWeight(.5);
            leftPanel.setMinimumSize(AppConstants.LEFT_PANEL_MIN_SIZE);
        }

        return leftPanel;
    }

    /**
     * A horizontal splitpane containing the center panel (left) and the
     * properties view (right).
     *
     * @return
     */
    private Component getCenterAndRightPanel() {
        if (null == centerAndRightPanel) {
            centerAndRightPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerAndRightPanel.add(getCenterPanel(), JSplitPane.LEFT);
            centerAndRightPanel.add(getPropertiesView(), JSplitPane.RIGHT);
            // only expand the center panel on resize
            centerAndRightPanel.setResizeWeight(1);
        }

        return centerAndRightPanel;
    }

    /**
     * The center splitpane contains the graph (top component) and tabs (bottom
     * component).
     *
     * @return
     */
    private Component getCenterPanel() {
        if (null == centerPanel) {
            centerPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            centerPanel.add(getGraphViewGui(), JSplitPane.TOP);
            centerPanel.add(getBottomView(), JSplitPane.BOTTOM);
            // only expand the graph panel on resize
            centerPanel.setResizeWeight(1);
        }

        return centerPanel;
    }

    private Component getBottomView() {
        return getInfoTabsView();
    }

    // TODO move to App
    private Point getStartLocation() {
        int x = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), AppConstants.FRAME_DEFAULT_LOCATION.x);
        int y = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), AppConstants.FRAME_DEFAULT_LOCATION.y);
        return new Point(x, y);
    }

    // TODO move to App
    private boolean isStartMaximized() {
        return AppConfig.getConfig().getBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), AppConstants.FRAME_DEFAULT_MAXIMIZED);
    }

    // TODO move to App
    private Dimension getStartSize() {
        int w = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), AppConstants.FRAME_DEFAULT_SIZE.width);
        int h = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), AppConstants.FRAME_DEFAULT_SIZE.height);
        return new Dimension(w, h);
    }

    private Component getTreeViewGui() {
        if (null == treeViewGui) {
            treeViewGui = new TitledComponent(AppConstants.TREE_VIEW_TITLE, app.getTreeViewGui());
        }

        return treeViewGui;
    }

    public void setGraphViewTitle(String title) {
        getGraphViewGui().setTitle(title);
        getGraphViewGui().setTitleBold();
    }

    private TitledComponent getGraphViewGui() {
        if (null == graphViewGui) {
            graphViewGui = new TitledComponent(AppConstants.EDITOR_DEFAULT_TITLE, app.getGraphViewGui());
            graphViewGui.setTitleItalic();
        }

        return graphViewGui;
    }

    public AppMenuBar getAppMenuBar() {
        if (null == appMenuBar) {
            appMenuBar = new AppMenuBar(actionHandler);
        }
        return appMenuBar;
    }
}
