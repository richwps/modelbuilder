package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.AppInfoTabs;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.view.AppFrame;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.semanticProxy.boundary.ProcessProvider;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.DndProxyLabel;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * ModelBuilder entry point. Creates and connects all components.
 *
 * @author dziegenh
 */
public class App {

    private AppActionProvider actionProvider;

    AppFrame frame;
    private ProcessProvider processProvider;

    private TreenodeTransferHandler processTransferHandler;

    private MbUndoManager undoManager;

    // TODO move to model (which has to be created...)
    private String currentModelFilename = null;

    private MainTreeViewController mainTreeView;
    private AppGraphView graphView;
    private PropertiesView propertiesView;
    private JLabel graphDndProxy;

    GraphDropTargetAdapter dropTargetAdapter;
    private InfoTabs infoTabs;
    private AppTreeToolbar treeViewToolbar;
    private JPanel mainTreeViewPanel;
    private JPanel subTreeViewPanel;
    private SubTreeViewController subTreeView;
    private AppPreferencesDialog preferencesDialog;
    private AppDeployManager deployManager;

    /**
     * ModelBuilder entry point. Creates and connects all components.
     *
     * @param args
     */
    public App(String[] args) {
        boolean debugMode = Arrays.asList(args).contains("debug");
        AppSetup.setup(this, debugMode);
    }

    /**
     * Deletes the current UndoManager and adds a new instance.
     */
    void reloadUndoManager() {
        undoManager = null;
        getUndoManager();
    }

    MbUndoManager getUndoManager() {
        if (null == undoManager) {
            undoManager = new AppUndoManager(this);
        }

        return undoManager;
    }

    public String getCurrentModelFilename() {
        return currentModelFilename;
    }

    public void setCurrentModelFilename(String name) {
        this.currentModelFilename = name;
    }

    ProcessProvider getProcessProvider() {
        if (null == processProvider) {
            processProvider = new ProcessProvider();
            AppEventService.getInstance().addSourceCommand(processProvider, AppConstants.INFOTAB_ID_SEMANTICPROXY);
        }
        return processProvider;
    }

    protected TreenodeTransferHandler getProcessTransferHandler() {
        if (null == processTransferHandler) {
            processTransferHandler = new TreenodeTransferHandler();
        }

        return processTransferHandler;
    }

    void initDragAndDrop() {
        getMainTreeView().initDnd();
        getSubTreeView().initDnd();
    }

    /**
     * Returns an overlay panel for the graph.
     *
     * @return
     */
    public Component getGraphDndProxy() {
        if (null == graphDndProxy) {
            graphDndProxy = new DndProxyLabel();
            graphDndProxy.setVisible(false);
        }
        return graphDndProxy;
    }

    /**
     *
     * @return
     */
    SubTreeViewController getSubTreeView() {
        if (null == subTreeView) {
            subTreeView = new SubTreeViewController(this);
        }

        return subTreeView;
    }

    /**
     * The SubTree panel containg the SubTree swing component.
     *
     * @return
     */
    public JPanel getSubTreeViewGui() {
        if (null == subTreeViewPanel) {
            JTree tree = getSubTreeView().getTreeView().getGui();
            subTreeViewPanel = new TitledComponent(AppConstants.SUB_TREEVIEW_TITLE, tree);
        }
        return subTreeViewPanel;
    }

    public void fillSubTree() {
        getSubTreeView().fillTree();
    }

    private MainTreeViewController getMainTreeView() {
        if (null == mainTreeView) {
            mainTreeView = new MainTreeViewController(this);
        }

        return mainTreeView;
    }

    /**
     * The MainTree panel containg the MainTree swing component.
     *
     * @return
     */
    public JPanel getMainTreeViewGui() {
        if (null == mainTreeViewPanel) {
            JTree tree = getMainTreeView().getTreeView().getGui();

            mainTreeViewPanel = new JPanel();
            mainTreeViewPanel.setLayout(new TableLayout(new double[][]{{TableLayout.FILL}, {TableLayout.PREFERRED, TableLayout.FILL}}));
            treeViewToolbar = new AppTreeToolbar(getActionProvider());
            treeViewToolbar.setBorder(new ColorBorder(UIManager.getColor("activeCaptionBorder"), 0, 0, 1, 0));
            mainTreeViewPanel.add(treeViewToolbar, "0 0");
            mainTreeViewPanel.add(tree, "0 1");

        }
        return mainTreeViewPanel;
    }

    void fillMainTree() {
        getMainTreeView().fillTree();
    }

    AppPreferencesDialog getPreferencesDialog() {
        if (null == preferencesDialog) {
            preferencesDialog = new AppPreferencesDialog(frame);
        }

        return preferencesDialog;
    }

    /**
     * The graph GUI component.
     *
     * @return
     */
    public Component getGraphViewGui() {
        return getGraphView().getGui();
    }

    /**
     * The graph. Lazy init, binds listeners to it on creation.
     *
     * @return
     */
    protected AppGraphView getGraphView() {
        if (null == graphView) {
            graphView = new AppGraphView(this);
        }
        return graphView;
    }

    /**
     * Add the model's undoable graph edits to the UndoManager. Needs to be
     * called after a new model has been created or loaded.
     */
    void modelLoaded() {
        getGraphView().modelLoaded();
        getSubTreeView().fillTree();
        updateGraphDependentActions();
        updateModelPropertiesView();
    }

    /**
     * Sets specific actions enabled depending on the graph content. Should be
     * called when graph model changes (eg by user edits or new/load actions).
     */
    void updateGraphDependentActions() {
        boolean graphIsEmpty = getGraphView().isEmpty();
        getActionProvider().getAction(APP_ACTIONS.DEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.DO_LAYOUT).setEnabled(!graphIsEmpty);
    }

    protected PropertiesView getPropertiesView() {
        if (null == propertiesView) {
            propertiesView = new AppPropertiesView(this);
        }

        return propertiesView;
    }

    public Component getPropertiesViewGui() {
        return getPropertiesView();
    }

    public AppFrame getFrame() {
        return frame;
    }

    public AppActionProvider getActionProvider() {
        if (null == actionProvider) {
            AppActionHandler actionHandler = new AppActionHandler(this);
            actionProvider = new AppActionProvider(actionHandler);
        }

        return actionProvider;
    }

    /**
     * Return the visible caption String for the given item.
     *
     * @param item
     * @return
     */
    public static String getMenuItemCaption(APP_ACTIONS item) {
        String itemString = item.name();
        for (String[] itemCaption : AppConstants.MENU_ITEM_CAPTIONS) {
            if (itemCaption[0].equals(itemString)) {
                return itemCaption[1];
            }
        }
        return "";
    }

    boolean isAppAction(Object source) {
        return getActionProvider().isAppAction(source);
    }

    private InfoTabs getInfoTabs() {
        if (null == infoTabs) {
            infoTabs = new AppInfoTabs();
        }
        return infoTabs;
    }

    /**
     * Blackboxed component getter for InfoTabs.
     *
     * @return
     */
    public Component getInfoTabGui() {
        return getInfoTabs();
    }

    public void updateModelPropertiesView() {
        getPropertiesView().setModel(getGraphView().getGraph().getGraphModel());
    }

    void deploy() {
        if(null == deployManager) {
            deployManager = new AppDeployManager(this);
        }

        deployManager.deploy();
    }

}
