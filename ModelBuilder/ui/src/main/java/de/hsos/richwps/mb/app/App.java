package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.view.treeView.MainTreeViewController;
import de.hsos.richwps.mb.app.view.treeView.SubTreeViewController;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.view.AboutDialog;
import de.hsos.richwps.mb.app.view.appFrame.AppFrame;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.semanticProxy.SementicProxySearch;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.app.view.dialogs.ProfileModelDialog;
import de.hsos.richwps.mb.app.view.dialogs.TestModelDialog;
import de.hsos.richwps.mb.app.view.dialogs.UndeployDialog;
import de.hsos.richwps.mb.app.view.dialogs.ExecuteDialog;
import de.hsos.richwps.mb.app.view.dialogs.ExecuteModelDialog;
import de.hsos.richwps.mb.app.view.treeView.MainTreeViewPanel;
import de.hsos.richwps.mb.processProvider.boundary.DatatypeProvider;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * ModelBuilder entry point. Creates and connects all components.
 *
 * @author dziegenh
 * @author dalcacer
 */
public class App {

    private AppActionProvider actionProvider;

    private boolean init = false;

    AppFrame frame;
    private ProcessProvider processProvider;

    private TreenodeTransferHandler processTransferHandler;

    private AppUndoManager undoManager;

    private String currentModelFilename = null;

    private MainTreeViewController mainTreeView;
    private AppGraphView graphView;
    private AppPropertiesView propertiesView;
    private JLabel graphDndProxy;

    GraphDropTargetAdapter dropTargetAdapter;
    private InfoTabs infoTabs;
    private MainTreeViewPanel mainTreeViewPanel;
    private JPanel subTreeViewPanel;
    private SubTreeViewController subTreeView;
    private AppPreferencesDialog preferencesDialog;
    private ExecuteDialog execAnyDialog;
    private ExecuteModelDialog execDialog;
    private UndeployDialog undeployAnyDialog;
    private TestModelDialog testDialog;
    private ProfileModelDialog profileDialog;

    boolean changesSaved = false;
    private DatatypeProvider datatypeProvider;
    private ProcessMetricProvider processMetricProvider;
    private SementicProxySearch semanticProxySearch;
    private AppActionHandler actionHandler;
    private AppProperties appProperties;

    /**
     * Creates and connects all ModelBuilder components.
     *
     * @param args
     */
    public void setup(String[] args) {
        boolean debugMode = Arrays.asList(args).contains("debug");

        this.frame = new AppFrame(AppConstants.FRAME_TITLE);

        AppSetup.setup(this, debugMode);
    }

    void init() {
        if (this.init) {
            return;
        }

        this.processMetricProvider = new ProcessMetricProvider();
        this.actionHandler = new AppActionHandler(this);
        this.undoManager = new AppUndoManager();
        this.processProvider = new ProcessProvider();
        this.processTransferHandler = new TreenodeTransferHandler();
        this.graphDndProxy = new JLabelWithBackground(new Color(0f, 1f, 0f, .1f));
        this.semanticProxySearch = new SementicProxySearch();
        this.mainTreeView = new MainTreeViewController();
        this.mainTreeViewPanel = new MainTreeViewPanel();
        this.preferencesDialog = new AppPreferencesDialog(this.frame);
        this.propertiesView = new AppPropertiesView(this);
        this.appProperties = new AppProperties();
        this.graphView = new AppGraphView();
        this.infoTabs = new AppInfoTabs();
        this.datatypeProvider = new DatatypeProvider();

        if (AppConstants.ENABLE_SUB_TREE_VIEW) {
            this.subTreeView = new SubTreeViewController();
        }

        try {
            this.actionProvider = new AppActionProvider();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    AppConstants.CREATE_APP_ACTION_FAIL,
                    AppConstants.CREATE_APP_ACTION_FAIL_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        this.init = true;
    }

    public AppProperties getAppProperties() {
        return appProperties;
    }

    public AppActionHandler getActionHandler() {
        return actionHandler;
    }

    /**
     * Returns true if the model doesn't have any unsaved changes.
     *
     * @return
     */
    public boolean areChangesSaved() {
        return changesSaved;
    }

    /**
     * Gets the process metric provider which uses the monitor client.
     *
     * @return
     */
    ProcessMetricProvider getProcessMetricProvider() {
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
        File file;
        try {
            file = new File(getCurrentModelFilename());
        } catch (Exception ex) {
            return false;
        }

        return file.exists();
    }

    /**
     * Set the changesSaved status of the model.
     *
     * @param changesSaved
     */
    void setChangesSaved(boolean changesSaved) {
        this.changesSaved = changesSaved;

        final AppAction saveAction = getActionProvider().getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL);

        if (currentModelFileExists()) {
            saveAction.setEnabled(!changesSaved);
        } else {
            saveAction.setEnabled(false);
        }
    }

    /**
     * Deletes the current UndoManager and adds a new instance.
     */
    void reloadUndoManager() {
        undoManager = null;
        getUndoManager();
    }

    AppUndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * Returns the file name of the current model.
     *
     * @return
     */
    public String getCurrentModelFilename() {
        return currentModelFilename;
    }

    /**
     * Sets the file name of the current model.
     *
     * @param name
     */
    public void setCurrentModelFilename(String name) {
        this.currentModelFilename = name;
    }

    /**
     * Returns the process provider which manages the process instances and
     * data.
     *
     * @return
     */
    ProcessProvider getProcessProvider() {
        return processProvider;
    }

    /**
     * Gets the drag and drop transfer handler for process entities.
     *
     * @return
     */
    TreenodeTransferHandler getProcessTransferHandler() {
        return processTransferHandler;
    }

    /**
     * Sets up the drag and drop components.
     */
    void initDragAndDrop() {
        getMainTreeView().initDnd();

        if (hasSubTreeView()) {
            getSubTreeView().initDnd();
        }
    }

    /**
     * Returns true if the app has a sub tree view for used modelling elements.
     *
     * @return
     */
    boolean hasSubTreeView() {
        return AppConstants.ENABLE_SUB_TREE_VIEW;
    }

    /**
     * Returns an overlay panel for the graph.
     *
     * @return
     */
    public Component getGraphDndProxy() {
        return graphDndProxy;
    }

    /**
     * Gets the SubTreeView's controller for currently used modelling elements.
     * (currently not used).
     *
     * @return
     */
    SubTreeViewController getSubTreeView() {
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

    /**
     * Returns the component for searching the semantic proxy.
     */
    SementicProxySearch getSemanticProxySearch() {
        return semanticProxySearch;
    }

    /**
     * Creates tree nodes for used processes (if available).
     */
    public void fillSubTree() {
        if (hasSubTreeView()) {
            getSubTreeView().fillTree();
        }
    }

    /**
     * Returns the main tree view containing all available processes and
     * modelling elements.
     *
     * @return
     */
    MainTreeViewController getMainTreeView() {
        return mainTreeView;
    }

    /**
     * The MainTree panel containg the MainTree + toolbar swing components.
     *
     * @return
     */
    public JPanel getMainTreeViewGui() {
        return mainTreeViewPanel;
    }

    MainTreeViewPanel getMainTreeViewPanel() {
        return mainTreeViewPanel;
    }

    /**
     * Gets the dialog for configuring the modelbuilder components.
     *
     * @return
     */
    AppPreferencesDialog getPreferencesDialog() {
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
    AppGraphView getGraphView() {
        return graphView;
    }

    /**
     * Add the model's undoable graph edits to the UndoManager. Needs to be
     * called after a new model has been created or loaded.
     */
    void modelLoaded() {

        // clear tab text and remove notification icons
        getInfoTabs().clearTabContents(AppConstants.INFOTAB_ID_EDITOR);
        getInfoTabs().clearTabContents(AppConstants.INFOTAB_ID_SERVER);

        getGraphView().setEnabled(true);
        getGraphView().modelLoaded();

        getPropertiesView().clearPropertyCache();

        if (hasSubTreeView()) {
            getSubTreeView().fillTree();
        }

        updateGraphDependentActions();
        updateDeploymentDependentActions();
        updateModelPropertiesView();

        setChangesSaved(true);

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
        boolean hasSingleSelection = false;

        Object[] selection = getGraphView().getSelection();
        hasSingleSelection = (null != selection) && (1 == selection.length);

        // MB Actions
        getActionProvider().getAction(APP_ACTIONS.SAVE_MODEL_AS).setEnabled(getGraphView().isEnabled());

        // modelling actions
        getActionProvider().getAction(APP_ACTIONS.DO_LAYOUT).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.ADD_PORTS).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.PREVIEW_ROLA).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.DEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.TEST).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.PROFILE).setEnabled(!graphIsEmpty);

        getActionProvider().getAction(APP_ACTIONS.REARRANGE_PORTS).setEnabled(hasSingleSelection);
        getActionProvider().getAction(APP_ACTIONS.REPLACE_PROCESS).setEnabled(hasSingleSelection);
    }

    /**
     * Updates the status of actions which depent on the model's deployment
     * status.
     */
    void updateDeploymentDependentActions() {
        boolean modelDeployed = currentModelIsDeployed();
        getActionProvider().getAction(APP_ACTIONS.UNDEPLOY).setEnabled(modelDeployed);
        getActionProvider().getAction(APP_ACTIONS.EXECUTE).setEnabled(modelDeployed);
        getActionProvider().getAction(APP_ACTIONS.PUBLISH).setEnabled(modelDeployed);
    }

    /**
     * Checks if the current model is deployed at its endpoint.
     *
     * @return
     */
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

    /**
     * Returns the properties view controller component.
     *
     * @return
     */
    protected AppPropertiesView getPropertiesView() {
        return propertiesView;
    }

    /**
     * Returns the properties view Swing component.
     *
     * @return
     */
    public Component getPropertiesViewGui() {
        return getPropertiesView();
    }

    /**
     * Returns the main modelbuilder frame.
     *
     * @return
     */
    public AppFrame getFrame() {
        return frame;
    }

    /**
     * Returns the main component for managing application actions.
     *
     * @return
     */
    public AppActionProvider getActionProvider() {
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

    /**
     * Returns true if the given object is an app action.
     *
     * @param source
     * @return
     */
    boolean isAppAction(Object source) {
        return getActionProvider().isAppAction(source);
    }

    /**
     * Returns the component containing tabs for outputting data.
     *
     * @return
     */
    private InfoTabs getInfoTabs() {
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

    /**
     * Sets the current model into the properties view.
     */
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
        updateDeploymentDependentActions();
    }

    /**
     * Previews the opend model.
     */
    void preview() {
        AppRichWPSManager manager = new AppRichWPSManager(this);
        String rola = "<html font=\"Arial\">" + manager.getROLA() + "</html>";
        String[] bold = {"bind process", "execute", "store", " to ", " with ", " as "};
        for (String s : bold) {
            rola = rola.replace(s, "<b>" + s + "</b>");
        }
        rola = rola.replace("\n", "<br/>");
        if (manager.isError()) {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.DEPLOY_ERROR_DIALOG_MSG,
                    AppConstants.DEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } else {
            final JTextPane textpane = new javax.swing.JTextPane();
            textpane.setContentType("text/html");
            textpane.setFont(AppConstants.DIALOG_TEXTPANE_FONT);
            textpane.setText(rola);
            textpane.setEditable(false);
            final javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textpane);
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
    }

    /**
     * Shows an dialog to test the currently opened model.
     */
    void showTestModel() {
        final GraphModel model = this.getGraphView().getGraph().getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        if (!RichWPSProvider.hasProcess(auri, identifier)) {
            AppRichWPSManager manager = new AppRichWPSManager(this);
            testDialog = new TestModelDialog(getFrame(), false, manager);
            testDialog.setVisible(true);
        } else {
            String msg = "The requested process " + identifier + " is already present"
                    + " on " + auri;
            JOptionPane.showMessageDialog(this.frame, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            return;
        }
    }

    /**
     * Shows an dialog to profile the currently opened model.
     */
    void showProfileModel() {
        final GraphModel model = this.getGraphView().getGraph().getGraphModel();
        final String auri = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER);
        if (!RichWPSProvider.hasProcess(auri, identifier)) {
            AppRichWPSManager manager = new AppRichWPSManager(this);
            profileDialog = new ProfileModelDialog(getFrame(), false, manager);
            profileDialog.setVisible(true);
        } else {
            String msg = "The requested process " + identifier + " is already present"
                    + " on " + auri;
            JOptionPane.showMessageDialog(this.frame, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            return;
        }
    }

    /**
     * Opens the about dialog.
     */
    void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(getFrame());
        aboutDialog.setVisible(true);
    }

    /**
     * Returns the (complex data) format provider.
     *
     * @return
     */
    DatatypeProvider getDatatypeProvider() {
        return datatypeProvider;
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

    public DefaultMutableTreeNode getMainTreeProcessesNode() {
        return getMainTreeView().getProcessesNode();
    }

}
