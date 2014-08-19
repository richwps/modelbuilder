/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.GraphView;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author dziegenh
 */
public class AppActionHandler implements IAppActionHandler {

    private App app;

    public AppActionHandler(App app) {
        this.app = app;
    }

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
            case SHOW_PREFERENCES:
                doPreferencesDialog();
                break;
            case EXIT_APP:
                doExit();
                break;
            case DO_LAYOUT:
                doLayout();
                break;
            case DEPLOY:
                doDeploy();
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
            case SHOW_ERROR_MSG:
                JOptionPane.showMessageDialog(app.getFrame(), e.getActionCommand());
                break;

            default:
            // do nothing
        }
    }

    private void doNewModel() {
        boolean doNew = true;
        if (app.getUndoManager().canUndoOrRedo()) {
            int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_NEW_MODEL, AppConstants.CONFIRM_NEW_MODEL_TITLE, JOptionPane.YES_NO_OPTION);
            doNew = choice == JOptionPane.YES_OPTION;
        }

        if (doNew) {
            getGraphView().newGraph();
            app.getFrame().resetGraphViewTitle();
            app.getUndoManager().discardAllEdits();
            app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL).setEnabled(false);
            app.updateModelPropertiesView();
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
        boolean doLoad = true;
        if (app.getUndoManager().canUndoOrRedo()) {
            int choice = JOptionPane.showConfirmDialog(
                    app.getFrame(),
                    AppConstants.CONFIRM_LOAD_MODEL,
                    AppConstants.CONFIRM_LOAD_MODEL_TITLE,
                    JOptionPane.YES_NO_OPTION);
            doLoad = choice == JOptionPane.YES_OPTION;
        }

        if (doLoad) {
            try {

                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "xml"));

                setupFileChooser(fc);

                int state = fc.showOpenDialog(app.getFrame());
                if (state == JFileChooser.APPROVE_OPTION) {
                    String filename = fc.getSelectedFile().getPath();
                    getGraphView().loadGraphFromXml(filename);
                    String graphName = getGraphView().getCurrentGraphName();
                    if (null == graphName) {
                        graphName = "";
                    }
                    app.setCurrentModelFilename(filename);
//                    app.getFrame().setGraphViewTitle(graphName);
                    app.getFrame().setGraphViewTitle(filename);
                    app.getActionProvider().getAction(SAVE_MODEL).setEnabled(true);
                    app.getUndoManager().discardAllEdits();
                    // A new model has been loaded => add change listener e
                    app.modelLoaded();

                    rememberLastDir(fc.getSelectedFile());
                }

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
            }
        }
    }

    private void doPreferencesDialog() {
        app.getPreferencesDialog().setVisible(true);
    }

    private void doExit() {
        boolean doExit = true;
        if (app.getUndoManager().canUndoOrRedo()) {
            int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_EXIT, AppConstants.CONFIRM_EXIT_TITLE, JOptionPane.YES_NO_OPTION);
            doExit = choice == JOptionPane.YES_OPTION;
        }

        if (doExit) {
            app.getFrame().dispose();
            System.exit(0);
        }
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

    private void doLayout() {
        getGraphView().layoutGraph();
    }

    private void doSaveModel() {

        // TODO check for missing ProcessEntities !!!
        String filename = app.getCurrentModelFilename();
        if (null == filename || filename.isEmpty()) {
            JOptionPane.showMessageDialog(app.getFrame(), AppConstants.SAVE_MODEL_FAILED);
            return;
        }

        try {
            getGraphView().saveGraphToXml(filename);

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
//                app.getFrame().setGraphViewTitle(getGraphView().getCurrentGraphName());

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

    private void doDeploy() {
        app.deploy();
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
        app.fillMainTree();
    }
}
