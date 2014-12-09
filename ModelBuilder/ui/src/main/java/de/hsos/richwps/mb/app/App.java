package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.view.semanticProxy.MainTreeViewController;
import de.hsos.richwps.mb.app.view.semanticProxy.SubTreeViewController;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.view.AboutDialog;
import de.hsos.richwps.mb.app.view.appFrame.AppFrame;
import de.hsos.richwps.mb.app.view.ManageRemotesDialog;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.semanticProxy.SemanticProxyInteractionComponents;
import de.hsos.richwps.mb.app.view.semanticProxy.SementicProxySearch;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.ui.dialogs.ExecuteDialog;
import de.hsos.richwps.mb.ui.dialogs.ExecuteModelDialog;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.ui.dialogs.TestModelDialog;

import de.hsos.richwps.mb.ui.dialogs.UndeployDialog;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import layout.TableLayout;

/**
 * ModelBuilder entry point. Creates and connects all components.
 *
 * @author dziegenh
 * @author dalcacer
 */
public class App {

    private AppActionProvider actionProvider;

    AppFrame frame;
    private ProcessProvider processProvider;

    private TreenodeTransferHandler processTransferHandler;

    private MbUndoManager undoManager;

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
    private ExecuteDialog execAnyDialog;
    private ExecuteModelDialog execDialog;
    private UndeployDialog undeployAnyDialog;
    private TestModelDialog testDialog;

    boolean changesSaved = false;
    private FormatProvider formatProvider;
    private ProcessMetricProvider processMetricProvider;
    private SementicProxySearch semanticProxySearch;

    /**
     * ModelBuilder entry point. Creates and connects all components.
     *
     * @param args
     */
    public App(String[] args) {
        boolean debugMode = Arrays.asList(args).contains("debug");
        AppSetup.setup(this, debugMode);
    }

    public boolean areChangesSaved() {
        return changesSaved;
    }

    ProcessMetricProvider getProcessMetricProvider() {
        if (null == processMetricProvider) {
            try {
                // TODO create settings
                processMetricProvider = new ProcessMetricProvider(AppConstants.MONITOR_DEFAULT_URL);
            } catch (MalformedURLException ex) {
                showErrorMessage(ex.getMessage());
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        }

        return processMetricProvider;
    }

    /**
     * Checks if the current model file (still) exists.
     *
     * @return
     */
    boolean currentModelFileExists() {
        // no current file => return false
        if (null == getCurrentModelFilename() || getCurrentModelFilename().isEmpty()) {
            return false;
        }

        // check file existence
        File file = null;
        try {
            file = new File(getCurrentModelFilename());
        } catch (Exception ex) {
            return false;
        }

        return null != file && file.exists();
    }

    
    void setChangesSaved(boolean changesSaved) {
        if (currentModelFileExists()) {
            this.changesSaved = changesSaved;
            getActionProvider().getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL).setEnabled(!changesSaved);
        }
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
            processProvider.setProcessMetricProvider(getProcessMetricProvider());
            AppEventService.getInstance().addSourceCommand(processProvider, AppConstants.INFOTAB_ID_SEMANTICPROXY);
        }
        return processProvider;
    }

    protected TreenodeTransferHandler getProcessTransferHandler() {
        if (null == processTransferHandler) {
            processTransferHandler = new TreenodeTransferHandler(getProcessProvider());
        }

        return processTransferHandler;
    }

    void initDragAndDrop() {
        getMainTreeView().initDnd();

        if (hasSubTreeView()) {
            getSubTreeView().initDnd();
        }
    }

    boolean hasSubTreeView() {
        return AppConstants.ENABLE_SUB_TREE_VIEW;
    }

    /**
     * Returns an overlay panel for the graph.
     *
     * @return
     */
    public Component getGraphDndProxy() {
        if (null == graphDndProxy) {
            graphDndProxy = new JLabelWithBackground(new Color(0f, 1f, 0f, .1f));
            graphDndProxy.setVisible(false);
        }
        return graphDndProxy;
    }

    /**
     * Gets the SubTreeView's controller for currently used modelling elements.
     * (currently not used).
     *
     * @return
     */
    SubTreeViewController getSubTreeView() {
        if (null == subTreeView) {
            subTreeView = new SubTreeViewController(createSemanticProxyInteractionComponents());
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

    /**
     * The SubTree panel containg the SubTree swing component.
     *
     * @return
     */
    public JPanel getSemanticProxySearchGui() {
        return getSemanticProxySearch();
    }

    SementicProxySearch getSemanticProxySearch() {
        if (null == semanticProxySearch) {
            semanticProxySearch = new SementicProxySearch(createSemanticProxyInteractionComponents());

        }

        return semanticProxySearch;
    }

    public void fillSubTree() {
        if (hasSubTreeView()) {
            getSubTreeView().fillTree();
        }
    }

    private MainTreeViewController getMainTreeView() {
        if (null == mainTreeView) {
            mainTreeView = new MainTreeViewController(getPreferencesDialog(), createSemanticProxyInteractionComponents());
        }

        return mainTreeView;
    }

    private SemanticProxyInteractionComponents createSemanticProxyInteractionComponents() {
        return new SemanticProxyInteractionComponents(
                getFrame(),
                getGraphView(),
                getProcessProvider(),
                getGraphDndProxy(),
                getProcessTransferHandler()
        );
    }

    /**
     * The MainTree panel containg the MainTree + toolbar swing components.
     *
     * @return
     */
    public JPanel getMainTreeViewGui() {
        if (null == mainTreeViewPanel) {

            double f = TableLayout.FILL;
            double p = TableLayout.PREFERRED;
            JTree tree = getMainTreeView().getTreeView().getGui();

            mainTreeViewPanel = new JPanel();
            mainTreeViewPanel.setLayout(new TableLayout(new double[][]{{f}, {p, f}}));

            // add tree toolbar
            treeViewToolbar = new AppTreeToolbar(getActionProvider());
            treeViewToolbar.setBorder(new ColorBorder(UIManager.getColor("activeCaptionBorder"), 0, 0, 1, 0));
            mainTreeViewPanel.add(treeViewToolbar, "0 0");

            // add tree
            JScrollPane treeScrollPane = new JScrollPane(tree);
            treeScrollPane.setBorder(null);
            mainTreeViewPanel.add(treeScrollPane, "0 1");
        }

        return mainTreeViewPanel;
    }

    void fillMainTree(boolean loadRemotes) {
        getMainTreeView().fillTree(loadRemotes);
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
        getGraphView().setEnabled(true);
        getGraphView().modelLoaded();
        
        getPropertiesView().clearPropertyCache();

        if (hasSubTreeView()) {
            getSubTreeView().fillTree();
        }

        updateGraphDependentActions();
        updateDeploymentDependentActions();
        updateModelPropertiesView();

        getSemanticProxySearch().setAppHasModel(true);

        getFrame().init(this);
        getFrame().setModellingEnabled(true);
    }

    /**
     * Sets specific actions enabled depending on the graph content. Should be
     * called when graph model changes (eg by user edits or new/load actions).
     */
    void updateGraphDependentActions() {
        boolean graphIsEmpty = getGraphView().isEmpty();

        // MB Actions
        getActionProvider().getAction(APP_ACTIONS.SAVE_MODEL_AS).setEnabled(getGraphView().isEnabled());

        // modelling actions
        getActionProvider().getAction(APP_ACTIONS.DO_LAYOUT).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.PREVIEW_ROLA).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.DEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.TEST).setEnabled(!graphIsEmpty);
        // TODO set status when profiling is implemented
        getActionProvider().getAction(APP_ACTIONS.PROFILE).setEnabled(false);
    }

    void updateDeploymentDependentActions() {
        boolean modelDeployed = currentModelIsDeployed();
        getActionProvider().getAction(APP_ACTIONS.UNDEPLOY).setEnabled(modelDeployed);
        getActionProvider().getAction(APP_ACTIONS.EXECUTE).setEnabled(modelDeployed);
        getActionProvider().getAction(APP_ACTIONS.PUBLISH).setEnabled(modelDeployed);
    }

    boolean currentModelIsDeployed() {
        GraphModel model = getGraphView().getGraph().getGraphModel();
        String serverKey = GraphModel.PROPERTIES_KEY_OWS_ENDPOINT;
        String identifierKey = GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER;
        String server = (String) model.getPropertyValue(serverKey);
        String identifier = (String) model.getPropertyValue(identifierKey);

        if (null != server && !server.isEmpty() && null != identifier && !identifier.isEmpty()) {
            return RichWPSProvider.hasProcess(server, identifier);
        }

        return false;
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

            try {
                actionProvider = new AppActionProvider(actionHandler);
            } catch (Exception ex) {

                // TODO move strings to app constants
                JOptionPane.showMessageDialog(
                        null,
                        "Can't create app actions. ModelBuilder will now exit.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        return actionProvider;
    }

    /**
     * Return the visible caption String for the given item.
     *
     * @param item
     * @return
     */
    public static String getActionItemCaption(APP_ACTIONS item) {
        String itemString = item.name();
        for (String[] itemCaption : AppConstants.ACTIONS_CONFIG) {
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
        getPropertiesView().setObjectWithProperties(getGraphView().getGraph().getGraphModel());
    }

    /**
     * Shows stored WPS-uris, so one can be selected.
     *
     * @return selected WPS server.
     */
    String askRemote() {

        String[] remotes_arr = processProvider.getAllServers();

        if (null == remotes_arr || remotes_arr.length < 1) {
            showErrorMessage(AppConstants.REMOTES_NOT_AVAILABLE_ERROR_MSG);
            return null;
        }

        String selectedRemote = (String) JOptionPane.showInputDialog(getFrame(),
                AppConstants.SELECTREMOTE_DIALOG_MSG,
                AppConstants.SELECTREMOTE_DIALOG_TITLE,
                JOptionPane.QUESTION_MESSAGE,
                null,
                remotes_arr,
                remotes_arr[0]);
        return selectedRemote;
    }

    /**
     * Shows a dialog to enter a new uri.
     *
     * @return entered wps-server.
     */
    void showManageRemotes() {
        final ManageRemotesDialog dialog = new ManageRemotesDialog(frame);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                String[] remotes = dialog.getRemotes();
                if (null != remotes) {
                    getMainTreeView().setRemotes(remotes);
                    getGraphView().updateRemotes();
                }

//                getFrame().setCursor(Cursor.getDefaultCursor());
            }

        });

        dialog.setVisible(true);
    }

    /**
     * Shows an dialog to execute a given process on any connected server.
     */
    void showExecute() {
        if (null == execAnyDialog) {
            String[] remotes_arr = processProvider.getAllServers();
            List aslist = new ArrayList<>(Arrays.asList(remotes_arr));
            execAnyDialog = new ExecuteDialog(getFrame(), false, aslist);
        }

        execAnyDialog.setVisible(true);
    }

    /**
     * Shows an dialog to execute a given process on any connected server.
     */
    void showUndeploy() {
        if (null == undeployAnyDialog) {
            String[] remotes_arr = processProvider.getAllServers();
            List aslist = new ArrayList<>(Arrays.asList(remotes_arr));
            undeployAnyDialog = new UndeployDialog(getFrame(), false, aslist);
        }

        undeployAnyDialog.setVisible(true);
    }

    /**
     * Previews the opend model.
     */
    void preview() {
        AppRichWPSManager manager = new AppRichWPSManager(this);
        final String rola = manager.getROLA();

        if (manager.isError()) {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.DEPLOY_ERROR_DIALOG_MSG,
                    AppConstants.DEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } else {
            final javax.swing.JTextArea textArea = new javax.swing.JTextArea(rola);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            final javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(500, 500));
            JOptionPane.showMessageDialog(frame, scrollPane);
        }
    }

    /**
     * Deploys the opend model.
     */
    void deploy() {
        AppRichWPSManager manager = new AppRichWPSManager(this);
        manager.deploy();
        if (manager.isError()) {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.DEPLOY_ERROR_DIALOG_MSG,
                    AppConstants.DEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);

        }

        updateDeploymentDependentActions();
    }

    /**
     * Undeploys the opend model.
     */
    void undeploy() {
        AppRichWPSManager manager = new AppRichWPSManager(this);
        manager.undeploy();
        if (manager.isError()) {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.UNDEPLOY_ERROR_DIALOG_MSG,
                    AppConstants.UNDEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }

        updateDeploymentDependentActions();
    }

    /**
     * Shows an dialog to execute the currently opened model.
     */
    void showExecuteModel() {
        //if (null == execDialog) {
        final GraphModel model = this.getGraphView().getGraph().getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        if (RichWPSProvider.hasProcess(auri, identifier)) {
            execDialog = new ExecuteModelDialog(getFrame(), false, auri, identifier);
            execDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.PROCESSNOTFOUND_DIALOG_MSG,
                    AppConstants.PROCESSNOTFOUND_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            String msg = "The requested process " + identifier + " was not found"
                    + " on " + auri;

            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            return;
        }
        //}

    }

    /**
     * Shows an dialog to execute the currently opened model.
     */
    void showTestModel() {
        //if (null == execDialog) {
        final GraphModel model = this.getGraphView().getGraph().getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        if (!RichWPSProvider.hasProcess(auri, identifier)) {
            AppRichWPSManager manager = new AppRichWPSManager(this);
            testDialog = new TestModelDialog(getFrame(), false, manager);
            testDialog.setVisible(true);
        } else {
            /*JOptionPane.showMessageDialog(frame,
             AppConstants.PROCESSNOTFOUND_DIALOG_MSG,
             AppConstants.PROCESSNOTFOUND_DIALOG_TITLE,
             JOptionPane.ERROR_MESSAGE);*/
            String msg = "The requested process " + identifier + " is already present"
                    + " on " + auri;
            JOptionPane.showMessageDialog(this.frame, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            return;
        }
        //}
    }

    void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(getFrame());
        aboutDialog.setVisible(true);
    }

    FormatProvider getFormatProvider() {
        if (null == formatProvider) {
            formatProvider = new FormatProvider(AppConstants.FORMATS_CSV_FILE);
        }

        return formatProvider;
    }

    /**
     * Fires the "show error message" action with the given message.
     *
     * @param message
     */
    void showErrorMessage(String message) {
        AppAction action = getActionProvider().getAction(APP_ACTIONS.SHOW_ERROR_MSG);
        action.fireActionPerformed(message);
    }

}
