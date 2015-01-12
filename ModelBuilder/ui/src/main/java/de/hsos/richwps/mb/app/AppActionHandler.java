package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.DO_LAYOUT;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.EXIT_APP;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.LOAD_MODEL;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.NEW_MODEL;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.SAVE_MODEL;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS;
import static de.hsos.richwps.mb.app.actions.AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES;
import de.hsos.richwps.mb.app.actions.IAppActionHandler;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.graphView.mxGraph.Graph;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Handles all app actions (e.g. user interactions with the GUI).
 *
 * @author dziegenh
 * @author dalcacer
 *
 */
public class AppActionHandler implements IAppActionHandler {

    private App app;

    public AppActionHandler(App app) {
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!app.isAppAction(e.getSource())) {
            return;
        }

        AppActionProvider.APP_ACTIONS item = (AppActionProvider.APP_ACTIONS) e.getSource();
        switch (item) {
            case NEW_MODEL:
                doNewModel();
                break;
            case LOAD_MODEL:
                doLoadModel();
                break;
            case SAVE_MODEL:
                doSaveModel();
                break;
            case SAVE_MODEL_AS:
                doSaveModelAs();
                break;
            case OPEN_RECENT_FILE:
                doOpenLastFile();
                break;
            case SHOW_PREFERENCES:
                doPreferencesDialog(e.getActionCommand());
                break;
            case EXIT_APP:
                doExit();
                break;
            case DO_LAYOUT:
                doLayout();
                break;
            case PREVIEW_ROLA:
                doPreview();
                break;
            case DEPLOY:
                doDeploy();
                break;
            case UNDEPLOY:
                doUnDeploy();
                break;
            case UNDEPLOY_ANY:
                doUnDeployAny();
                break;
            case UNDO:
                doUndo();
                break;
            case REDO:
                doRedo();
                break;
            case RELOAD_PROCESSES:
                doReloadProcesses();
                break;
            case MANAGE_REMOTES:
                doShowManageRemotes();
                break;
            case PUBLISH:
                doPublish();
                break;
            case SHOW_ERROR_MSG:
                JOptionPane.showMessageDialog(
                        app.getFrame(),
                        e.getActionCommand(),
                        AppConstants.DIALOG_TITLE_ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
                break;
            case EXECUTE:
                doExecuteModel();
                break;
            case EXECUTE_ANY:
                doExecute();
                break;
            case PROFILE:
                doProfileModel();
                break;
            case TEST:
                doTestModel();
                break;
            case ABOUT:
                doAbout();
                break;
            default:
            // do nothing
        }
    }

    private void doNewModel() {
        if (confirmDiscardChanges(AppConstants.CONFIRM_NEW_MODEL, AppConstants.CONFIRM_NEW_MODEL_TITLE)) {

            String remote = app.askRemote();

            // cancel if no remote server is set
            if (null == remote || remote.isEmpty()) {
                return;
            }

            getGraphView().newGraph(remote);
            app.getFrame().resetGraphViewTitle();

            // override "changes saved" status
            app.setCurrentModelFilename(".");
            app.setChangesSaved(true);
            app.setCurrentModelFilename(null);

            app.modelLoaded();
        }
    }

    private void setupFileChooser(JFileChooser fc) {
        String lastDirPath = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.MODEL_S_LASTDIR.name(), "");
        File lastDir = new File(lastDirPath);
        if (!lastDirPath.isEmpty() && lastDir.exists()) {
            fc.setCurrentDirectory(lastDir);
        }

        fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "xml"));
    }

    private void rememberLastDir(File file) {
        AppConfig.getConfig().put(AppConfig.CONFIG_KEYS.MODEL_S_LASTDIR.name(), file.getAbsolutePath());
    }

    private void doLoadModel() {
        if (confirmDiscardChanges(AppConstants.CONFIRM_LOAD_MODEL, AppConstants.CONFIRM_LOAD_MODEL_TITLE)) {

            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "xml"));

            setupFileChooser(fc);

            int state = fc.showOpenDialog(app.getFrame());
            if (state == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getPath();
                loadModelFromFile(filename);
            }
        }
    }

    private void loadModelFromFile(final String filename) {

        // show loading cursor
        app.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    getGraphView().loadGraphFromXml(filename);
                    String previousModelFilename = app.getCurrentModelFilename();
                    app.setCurrentModelFilename(filename);
                    app.getFrame().setGraphViewTitle(filename);
                    app.getActionProvider().getAction(SAVE_MODEL).setEnabled(true);
                    app.getUndoManager().discardAllEdits();
                    // A new model has been loaded => add change listener
                    app.modelLoaded();

                    rememberLastDir(new File(filename));
                    rememberLastModelFile(previousModelFilename);

                } catch (Exception ex) {
                    StringBuilder sb = new StringBuilder(100);
                    sb.append(AppConstants.LOAD_MODEL_FAILED);
                    sb.append("\n");
                    sb.append(AppConstants.SEE_LOGGING_TABS);
                    JOptionPane.showMessageDialog(app.getFrame(), sb.toString());

                    sb = new StringBuilder(100);
                    sb.append(AppConstants.LOAD_MODEL_FAILED);
                    sb.append("\n");
                    sb.append(String.format(AppConstants.ERROR_MSG_IS_FORMAT, ex.getMessage()));
                    AppEventService.getInstance().fireAppEvent(sb.toString(), getGraphView());

                    app.getActionProvider().getAction(SAVE_MODEL).setEnabled(false);

                } finally {
                    app.getFrame().setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private void doPreferencesDialog(String tabName) {
        AppPreferencesDialog dialog = app.getPreferencesDialog();
        AppConstants.PREFERENCES_TAB tabToShow = null;

        if (null != tabName) {
            for (AppConstants.PREFERENCES_TAB tab : AppConstants.PREFERENCES_TAB.values()) {
                if (tab.name().equals(tabName)) {
                    tabToShow = tab;
                }
            }
        }

        dialog.showTab(tabToShow);
    }

    private void doExit() {
        boolean doExit = true;
        if (!app.areChangesSaved()) {
            int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_EXIT, AppConstants.CONFIRM_EXIT_TITLE, JOptionPane.YES_NO_OPTION);
            doExit = choice == JOptionPane.YES_OPTION;
        }

        if (doExit) {
            app.getFrame().dispose();

            // remember last opened or saved file
            rememberLastModelFile(app.getCurrentModelFilename());

            System.exit(0);
        }
    }

    private AppGraphView getGraphView() {
        return app.getGraphView();
    }

    private void doLayout() {
        getGraphView().layoutGraph();
    }

    private void doSaveModel() {

        String filename = app.getCurrentModelFilename();
        if (null == filename || filename.isEmpty()) {
            JOptionPane.showMessageDialog(app.getFrame(), AppConstants.SAVE_MODEL_FAILED);
            return;
        }

        try {
            getGraphView().saveGraphToXml(filename);
            app.setChangesSaved(true);

        } catch (Exception ex) {
            handleSaveModelFailedException(ex);
        }

    }

    private void doSaveModelAs() {

        JFileChooser fc = new JFileChooser();
        setupFileChooser(fc);

        int state = fc.showSaveDialog(app.getFrame());
        if (state == JFileChooser.APPROVE_OPTION) {

            if (fc.getSelectedFile().exists()) {
                int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_OVERWRITE_FILE, AppConstants.CONFIRM_OVERWRITE_FILE_TITLE, JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            String filename = fc.getSelectedFile().getPath();
            if (!filename.toLowerCase().endsWith(".xml")) {
                filename = filename.concat(".xml");
            }

            rememberLastDir(fc.getSelectedFile());

            try {
                getGraphView().saveGraphToXml(filename);
                app.getActionProvider().getAction(SAVE_MODEL).setEnabled(true);
                app.setCurrentModelFilename(filename);
                app.getFrame().setGraphViewTitle(filename);
                app.setChangesSaved(true);

            } catch (Exception ex) {
                handleSaveModelFailedException(ex);
                app.getActionProvider().getAction(SAVE_MODEL).setEnabled(false);
            }
        }
    }

    private void handleSaveModelFailedException(Exception ex) {
        StringBuilder sb = new StringBuilder(100);
        sb.append(AppConstants.SAVE_MODEL_FAILED);
        sb.append("\n");
        sb.append(AppConstants.SEE_LOGGING_TABS);
        JOptionPane.showMessageDialog(app.getFrame(), sb.toString());

        sb = new StringBuilder(100);
        sb.append(AppConstants.SAVE_MODEL_FAILED);
        sb.append("\n");
        sb.append(String.format(AppConstants.ERROR_MSG_IS_FORMAT, ex.getMessage()));
        AppEventService.getInstance().fireAppEvent(sb.toString(), getGraphView());
    }

    private void doPreview() {
        app.preview();
    }

    private void doDeploy() {
        app.deploy();
    }

    private void doUnDeploy() {
        app.undeploy();
    }

    private void doUnDeployAny() {
        app.showUndeploy();
    }

    private void doShowManageRemotes() {
        app.showManageRemotes();
    }

    private void doUndo() {
        app.getUndoManager().undo();
        app.updateGraphDependentActions();
    }

    private void doRedo() {
        app.getUndoManager().redo();
        app.updateGraphDependentActions();
    }

    private void doReloadProcesses() {
        app.getPropertiesView().clearPropertyCache();
        app.getProcessProvider().clear();
        app.fillMainTree(true);
    }

    private void doExecute() {
        app.showExecute();
    }

    private void doExecuteModel() {
        app.showExecuteModel();
    }

    private void doTestModel() {
        app.showTestModel();
    }

    private void doProfileModel() {
        app.showProfileModel();
    }

    private void doAbout() {
        app.showAbout();
    }

    private void doOpenLastFile() {
        if (confirmDiscardChanges(AppConstants.CONFIRM_LOAD_MODEL, AppConstants.CONFIRM_LOAD_MODEL_TITLE)) {
            String filename = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.MODEL_S_LASTFILE.name(), "");
            loadModelFromFile(filename);
        }
    }

    private void rememberLastModelFile(String filename) {
        if (null != filename && new File(filename).exists()) {
            AppConfig.getConfig().put(AppConfig.CONFIG_KEYS.MODEL_S_LASTFILE.name(), filename);
            app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE).setName(filename);
        }
    }

    private boolean confirmDiscardChanges(String text, String title) {
        if (!app.areChangesSaved()) {
            int choice = JOptionPane.showConfirmDialog(
                    app.getFrame(),
                    text, title,
                    JOptionPane.YES_NO_OPTION);
            return (choice == JOptionPane.YES_OPTION);
        }

        return true;
    }

    private void doPublish() {
        Graph graph = app.getGraphView().getGraph();
        ProcessEntity process = graph.getRepresentingProcess();

        String tabMsg;
        AppEvent.PRIORITY eventPrio = AppEvent.PRIORITY.DEFAULT;

        try {
            app.getProcessProvider().publishProcess(process);
            doReloadProcesses();

            tabMsg = AppConstants.SEMANTICPROXY_PUBLISH_SUCCESS;

        } catch (Exception ex) {

            // Format error message
            tabMsg = String.format(
                    AppConstants.SEMANTICPROXY_PUBLISH_ERROR_DETAILLED,
                    ex.getClass().getSimpleName(),
                    ex.getMessage());

            eventPrio = AppEvent.PRIORITY.URGENT;

            String msg
                    = AppConstants.SEMANTICPROXY_PUBLISH_ERROR
                    + "\n\t"
                    + AppConstants.SEE_LOGGING_TABS;
            app.showErrorMessage(msg);
        }

        AppEventService.getInstance().fireAppEvent(tabMsg, app.getProcessProvider(), eventPrio);
    }
}
