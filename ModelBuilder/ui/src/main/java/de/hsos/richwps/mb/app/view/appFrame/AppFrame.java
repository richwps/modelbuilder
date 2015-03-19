package de.hsos.richwps.mb.app.view.appFrame;

import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.AppConfig;
import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.menu.AppMenuBar;
import de.hsos.richwps.mb.app.view.toolbar.AppToolbar;
import de.hsos.richwps.mb.ui.JSplitPaneForTitledComponents;
import de.hsos.richwps.mb.ui.TitledComponent;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import layout.TableLayout;

/**
 * The ModelBuilder's main frame.
 *
 * <pre>
 *  +-----------------------------------------[AppFrame]-----------------------------------------------+
 *  |                                                                                                  |
 *  | [---------------------------------------[AppMenuBar]-------------------------------------------] |
 *  |                                                                                                  |
 *  | [---------------------------------------[AppToolbar]-------------------------------------------] |
 *  |                                                                                                  |
 *  | +---------------------------------------#MainPanel#--------------------------------------------+ |
 *  | |                                  |                                                           | |
 *  | |  +-------#LeftPanel#----------+  |  +------#CenterPanel#---------------------------------+   | |
 *  | |  |                            |  |  |                                                    |   | |
 *  | |  | +------[TreeView]--------+ |  |  |  +--------[AppGraphView]-----+-[PropertiesView]-+  |   | |
 *  | |  | |                        | |  |  |  |                           |                  |  |   | |
 *  | |  | |                        | |  |  |  |                           |                  |  |   | |
 *  | |  | |                        | |  |  |  |                           |                  |  |   | |
 *  | |  | |                        | |  |  |  |                           |                  |  |   | |
 *  | |  | +------------------------+ |  |  |  |                           |                  |  |   | |
 *  | |  |                            |  |  |  |                           |                  |  |   | |
 *  | |  |----------------------------|  |  |  |                           |                  |  |   | |
 *  | |  |                            |  |  |  |                           |                  |  |   | |
 *  | |  | +------[SummaryView]-----+ |  |  |  +---------------------------+------------------+  |   | |
 *  | |  | |                        | |  |  |  +-----------------[InfoTabs]-------------------+  |   | |
 *  | |  | |                        | |  |  |  |                                              |  |   | |
 *  | |  | +------------------------+ |  |  |  +----------------------------------------------+  |   | |
 *  | |  |                            |  |  |                                                    |   | |
 *  | |  +----------------------------+  |  +----------------------------------------------------+   | |
 *  | |                                  |                                                           | |
 *  | +----------------------------------------------------------------------------------------------+ |
 *  |                                                                                                  |
 *  +--------------------------------------------------------------------------------------------------+</pre>
 *
 * @author dziegenh
 * @see AppFrame#getMenuBar()
 * @see AppFrame#getToolbar()
 * @see AppFrame#getMainPanel()
 */
public class AppFrame extends JFrame {

    // Comnponent container
    private JSplitPane leftPanel;
    private JSplitPane mainPanel;
    private JSplitPane centerPanel;
    private App app;
    private Component infoTabs;
    private AppMenuBar appMenuBar;
    private TitledComponent treeViewGui;

    private AppActionProvider actionProvider;
    private AppToolbar toolbar;
    private TitledComponent graphViewGui;
    private JPanel serviceSummaryView;
    private String frameTitle;
    private JPanel mainModellingPanel;
    private CardLayout modellingLayout;
    private ModellingPanel modellingPanel;

    private boolean init = false;

    /**
     * Frame setup.
     */
    public AppFrame(String title) {
        super(title);
        this.frameTitle = title;
    }

    public void init(App app) {
        if (this.init) {
            return;
        }

        this.app = app;

        this.actionProvider = app.getActionProvider();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocation(getStartLocation());
        setSize(getStartSize());
        if (isStartMaximized()) {
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        }

        // init gui
        setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
        addComponents();
        modellingPanel.init(app);

        // call after component is visible and has a size
        getLeftPanel().setDividerLocation(.5);

        // TODO restore divider locations from config
        this.init = true;
    }

    @Override
    public void dispose() {
        saveConfig();
        super.dispose();
    }

    /**
     * Save frame location, size etc. when closing.
     */
    void saveConfig() {
        Boolean maximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        AppConfig.getConfig().putBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), maximized);

        if (maximized) {
            setExtendedState(getExtendedState() & ~Frame.MAXIMIZED_BOTH);
        }

        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), getSize().width);
        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), getSize().height);

        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), getLocation().x);
        AppConfig.getConfig().putInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), getLocation().y);

        modellingPanel.saveConfig();
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
     * {@link de.hsos.richwps.mb.app.view.semanticProxy.SementicProxySearch}
     *
     * @see de.hsos.richwps.mb.app.view.semanticProxy.SementicProxySearch
     * @see App#getSemanticProxySearchGui()
     */
    private Component getModellingSummaryView() {
        // TODO mock
        if (null == serviceSummaryView) {
            serviceSummaryView = app.getSemanticProxySearchGui();
        }

        return serviceSummaryView;
    }

    /**
     * The InfoTabs component.
     *
     * @return
     */
    public Component getInfoTabsView() {
        // TODO move init to App !!
        if (null == infoTabs) {
            infoTabs = app.getInfoTabGui();

        }
        return infoTabs;
    }

    /**
     * The main toolbar.
     *
     * @return
     */
    private AppToolbar getToolbar() {
        if (null == toolbar) {
            toolbar = new AppToolbar(actionProvider);
        }

        return toolbar;
    }

    /**
     * The main panel contains all content panels and components below the
     * toolbar. (Horizontal splitbar)
     *
     * @return
     *
     * <pre>
     *  +-----------------------+
     *  |leftPanel | centerPanel|
     *  +-----------------------+
     * </pre>
     *
     * @see getLeftPanel()
     * @see getCenterPanel()
     *
     */
    private Component getMainPanel() {
        if (null == mainPanel) {
            mainPanel = new JSplitPaneForTitledComponents(JSplitPane.HORIZONTAL_SPLIT);
            mainPanel.add(getLeftPanel(), JSplitPane.LEFT);
            mainPanel.add(getCenterPanel(), JSplitPane.RIGHT);
        }

        return mainPanel;
    }

    /**
     * Vertical splitbar containing the tree (top component) and the summary
     * panel (bottom component).
     *
     * @return      <pre>
     * +-----------+
     * |TreeView   |
     * |-----------|
     * |SummaryView|
     * +-----------+</pre>
     *
     * @see getMainTreeViewGui()
     * @see getModellingSummaryView()
     */
    private JSplitPane getLeftPanel() {
        if (null == leftPanel) {
            leftPanel = new JSplitPaneForTitledComponents(JSplitPane.VERTICAL_SPLIT);
            leftPanel.add(getMainTreeViewGui(), JSplitPane.TOP);
            leftPanel.add(getModellingSummaryView(), JSplitPane.BOTTOM);
            // expand both components on resize
            leftPanel.setResizeWeight(.5);
            leftPanel.setMinimumSize(AppConstants.LEFT_PANEL_MIN_SIZE);
        }

        return leftPanel;
    }

    /**
     * The center splitpane contains the graph (top component) and tabs (bottom
     * component).
     *
     * @return
     *
     * <pre>
     *  +------------------+
     *  |mainModellingPanel|
     *  |------------------|
     *  |InfoTabsView      |
     *  +------------------+</pre> "mainModellingPanel" contains DefaultMainPanel
     * and ModellingPanel.
     *
     * @see DefaultMainPanel
     * @see ModellingPanel
     * @see de.hsos.richwps.mb.infoTabsView.InfoTabs
     *
     */
    private Component getCenterPanel() {
        if (null == centerPanel) {
            centerPanel = new JSplitPaneForTitledComponents(JSplitPane.VERTICAL_SPLIT);
            centerPanel.add(getMainModellingPanel(), JSplitPane.TOP);
            centerPanel.add(getBottomView(), JSplitPane.BOTTOM);

            // remove border => there is already another surrounding splitpane/border
            centerPanel.setBorder(null);

            // only expand the graph panel on resize
            centerPanel.setResizeWeight(1);
        }

        return centerPanel;
    }

    private static enum MODELLING_CARDS {

        DISABLED,
        ENABLED
    }

    private JPanel getMainModellingPanel() {
        if (null == mainModellingPanel) {

            modellingLayout = new CardLayout();
            modellingPanel = new ModellingPanel();

            this.mainModellingPanel = new JPanel(modellingLayout);
            this.mainModellingPanel.add(new DefaultMainPanel(actionProvider), MODELLING_CARDS.DISABLED.name());
            this.mainModellingPanel.add(new TitledComponent("Modelling", modellingPanel), MODELLING_CARDS.ENABLED.name());
        }

        return mainModellingPanel;
    }

    public void setModellingEnabled(boolean enabled) {

        MODELLING_CARDS card = MODELLING_CARDS.DISABLED;
        if (enabled) {
            card = MODELLING_CARDS.ENABLED;
        }

        modellingLayout.show(mainModellingPanel, card.name());

        getAppMenuBar().setModelMenuEnabled(enabled);

        if (enabled) {
            modellingPanel.restoreDividerLocation();
        }
    }

    private Component getBottomView() {
        return getInfoTabsView();
    }

    private Point getStartLocation() {
        int x = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONX.name(), AppConstants.FRAME_DEFAULT_LOCATION.x);
        int y = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_POSITIONY.name(), AppConstants.FRAME_DEFAULT_LOCATION.y);
        return new Point(x, y);
    }

    private boolean isStartMaximized() {
        return AppConfig.getConfig().getBoolean(AppConfig.CONFIG_KEYS.FRAME_B_MAXIMIZED.name(), AppConstants.FRAME_DEFAULT_MAXIMIZED);
    }

    private Dimension getStartSize() {
        int w = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_WIDTH.name(), AppConstants.FRAME_DEFAULT_SIZE.width);
        int h = AppConfig.getConfig().getInt(AppConfig.CONFIG_KEYS.FRAME_I_HEIGHT.name(), AppConstants.FRAME_DEFAULT_SIZE.height);
        return new Dimension(w, h);
    }

    /**
     * The tree component.
     *
     * @return      <pre>
     * +----[TiledComponent]---+
     * |+-[mainTreeViewPanel]-+|
     * || AppTreeToolbar      ||
     * ||---------------------||
     * || JTree               ||
     * |+---------------------+|
     * +-----------------------+</pre>
     *
     * @see de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar
     * @see javax.swing.JTree
     */
    private Component getMainTreeViewGui() {
        if (null == treeViewGui) {
            treeViewGui = new TitledComponent(AppConstants.TREE_VIEW_TITLE, app.getMainTreeViewGui());
        }

        return treeViewGui;
    }

    /**
     * Sets GraphView title and style to show the current model's name.
     *
     * @param title
     */
    public void setGraphViewTitle(String title) {
        setTitle(frameTitle + " - " + title);
    }

    /**
     * Resets style and title of the GraphView to indicate an unnamed model.
     */
    public void resetGraphViewTitle() {
        setTitle(frameTitle);
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

    protected AppMenuBar getAppMenuBar() {
        if (null == appMenuBar) {
            appMenuBar = new AppMenuBar(actionProvider);
        }
        return appMenuBar;
    }
}
