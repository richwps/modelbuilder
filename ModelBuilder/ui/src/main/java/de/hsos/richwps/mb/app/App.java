package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS;
import de.hsos.richwps.mb.app.view.AboutDialog;
import de.hsos.richwps.mb.app.view.AppFrame;
import de.hsos.richwps.mb.app.view.ManageRemotesDialog;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.execView.ExecuteDialog;
import de.hsos.richwps.mb.execView.ExecuteModelDialog;
import de.hsos.richwps.mb.graphView.GraphDropTargetAdapter;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.infoTabsView.InfoTabs;
import de.hsos.richwps.mb.processProvider.boundary.FormatProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.propertiesView.PropertiesView;
import de.hsos.richwps.mb.richWPS.boundary.RichWPSProvider;
import de.hsos.richwps.mb.richWPS.entity.impl.UndeployRequest;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.ColorBorder;
import de.hsos.richwps.mb.ui.JLabelWithBackground;
import de.hsos.richwps.mb.ui.TitledComponent;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
//    private AppDeployManager deployManager;
    private ExecuteDialog execAnyDialog;
    private ExecuteModelDialog execDialog;

    private boolean changesSaved = false;
    private CardLayout mainTreeViewCardLayout;
    private JPanel card1Panel;
    private FormatProvider formatProvider;

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

    /**
     * Checks if the current model file (still) exists.
     *
     * @return
     */
    boolean currentModelFileExists() {
        File file = null;
        try {
            file = new File(getCurrentModelFilename());
        } catch (Exception ex) {
            return false;
        }

        return null != file && file.exists();
    }

    public void setChangesSaved(boolean changesSaved) {
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
        getSubTreeView().initDnd();
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
        getActionProvider().getAction(APP_ACTIONS.DO_LAYOUT).setEnabled(!graphIsEmpty);

        getActionProvider().getAction(APP_ACTIONS.DEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.UNDEPLOY).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.EXECUTE).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.PROFILE).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.TEST).setEnabled(!graphIsEmpty);
        getActionProvider().getAction(APP_ACTIONS.PUBLISH).setEnabled(!graphIsEmpty);
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
        getPropertiesView().setObjectWithProperties(getGraphView().getGraph().getGraphModel());
    }

    /**
     * Deploys the opend model.
     */
    void deploy() {
        AppDeployManager manager = new AppDeployManager(this);
        manager.deploy();
        if (manager.isError()) {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.DEPLOY_ERROR_DIALOG_MSG,
                    AppConstants.DEPLOY_ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            //TODO get error?
        }
        new AppDeployManager(this).deploy();
    }

    /**
     * Undeploys the opend model.
     */
    void undeploy() {

        final GraphModel model = this.getGraphView().getGraph().getGraphModel();
        final String auri = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT);
        final String identifier = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER);
        if (RichWPSProvider.hasProcess(auri, identifier)) {
            String wpsendpoint = "";
            String wpstendpoint = "";
            if (RichWPSProvider.isWPSEndpoint(auri)) {
                wpsendpoint = auri;
                wpstendpoint = auri.replace(RichWPSProvider.DEFAULT_WPS_ENDPOINT, RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT);
            } else if (RichWPSProvider.isWPSTEndpoint(auri)) {
                wpstendpoint = auri;
                wpsendpoint = auri.replace(RichWPSProvider.DEFAULT_RICHWPS_ENDPOINT, RichWPSProvider.DEFAULT_WPS_ENDPOINT);
            }
            RichWPSProvider provider = new RichWPSProvider();
            try {
                provider.connect(wpsendpoint, wpstendpoint);

                UndeployRequest request = new UndeployRequest(wpsendpoint, wpstendpoint, identifier);
                provider.request(request);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        AppConstants.UNDEPLOY_ERROR_DIALOG_MSG,
                        AppConstants.UNDEPLOY_ERROR_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                Logger.log("Debug:\n" + ex);
                String msg = "An error occured while undeploying  " + identifier + " from"
                        + "on " + auri + ". " + ex.getLocalizedMessage();
                JOptionPane.showMessageDialog(this.frame, msg);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    AppConstants.PROCESSNOTFOUND_DIALOG_MSG,
                    AppConstants.PROCESSNOTFOUND_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            String msg = "The requested process " + identifier + " was not found"
                    + "on " + auri;
            JOptionPane.showMessageDialog(this.frame, msg);
            AppEventService appservice = AppEventService.getInstance();
            appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
        }

    }

    /**
     * Shotws stored WPS-uris, so one can be selected.
     *
     * @return selected WPS server.
     */
    String askRemote() {
        //TODO receive list from sp+cache.
        List<String> remotes = (List) processProvider.getAllServersFromSemanticProxy();
        Object[] remotes_arr = remotes.toArray();
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
    void manageRemotes() {
        final ManageRemotesDialog dialog = new ManageRemotesDialog(frame);
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                String[] remotes = dialog.getRemotes();
                if (null != remotes) {
                    getMainTreeView().setRemotes(remotes);
                    getGraphView().updateRemotes();
                }

                getFrame().setCursor(Cursor.getDefaultCursor());
            }

        });

        dialog.setVisible(true);
    }

    
    /**
     * Shows an dialog to execute a given process on any connected server.
     */
    void showExecute() {
        if (null == execAnyDialog) {
            //TODO change datasource.
            List<String> remotes = (List) processProvider.getAllServersFromSemanticProxy();
            execAnyDialog = new ExecuteDialog(getFrame(), false, remotes);
        }

        execAnyDialog.setVisible(true);
    }

    /**
     * Shows an dialog to execute the currently opened model.
     */
    void showExecuteModel() {
        if (null == execDialog) {
            final GraphModel model = this.getGraphView().getGraph().getGraphModel();
            final String auri = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_ENDPOINT);
            final String identifier = (String) model.getPropertyValue(AppConstants.PROPERTIES_KEY_MODELDATA_OWS_IDENTIFIER);
            if (RichWPSProvider.hasProcess(auri, identifier)) {
                execDialog = new ExecuteModelDialog(getFrame(), false, auri, identifier);
            } else {
                JOptionPane.showMessageDialog(frame,
                        AppConstants.PROCESSNOTFOUND_DIALOG_MSG,
                        AppConstants.PROCESSNOTFOUND_DIALOG_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                String msg = "The requested process " + identifier + " was not found"
                        + " on " + auri;
                JOptionPane.showMessageDialog(this.frame, msg);
                AppEventService appservice = AppEventService.getInstance();
                appservice.fireAppEvent(msg, AppConstants.INFOTAB_ID_SERVER);
            }
        }

        execDialog.setVisible(true);
    }

    void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(getFrame());
        aboutDialog.setVisible(true);
    }

    FormatProvider getFormatProvider() {
        if(null == formatProvider) {
            formatProvider = new FormatProvider(AppConstants.FORMATS_CSV_FILE);
        }

        return formatProvider;
    }

}
