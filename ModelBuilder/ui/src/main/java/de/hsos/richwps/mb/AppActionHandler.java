/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appActions.AppActionProvider;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.DO_LAYOUT;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.EXIT_APP;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.LOAD_MODEL;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.NEW_MODEL;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.SAVE_MODEL;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS;
import static de.hsos.richwps.mb.appActions.AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES;
import de.hsos.richwps.mb.appActions.IAppActionHandler;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.GraphView;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import layout.TableLayout;

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
                    // A new model has been loaded => add change listener for undo/redo
                    app.modelLoaded();
                    app.updateModelPropertiesView();
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
        // TODO just a dirty mocked dialog !!! create a real one :)
        final JDialog dialog = new JDialog(app.getFrame(), "Preferences");
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(true);
        dialog.setMinimumSize(new Dimension(200, 100));
        Point loc = dialog.getLocation();
        dialog.setLocation(loc.x + 100, loc.y + 100);
        double p = TableLayout.PREFERRED;
        dialog.setLayout(new TableLayout(new double[][]{{.5, .5}, {p, p, p}}));

        int i = 2;
        Border mockBorder = new EmptyBorder(i, i, i, i);

        JLabel urlLabel = new JLabel("Semantic Proxy URL:");
        urlLabel.setBorder(mockBorder);

        final JTextField urlField = new JTextField(AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), AppConstants.SEMANTICPROXY_DEFAULT_URL));
        urlField.setBorder(mockBorder);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppConfig.getConfig().put(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), urlField.getText());
                dialog.dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new TableLayout(new double[][]{{.5, .5}, {p}}));
        buttonPanel.setBorder(mockBorder);
        buttonPanel.add(okButton, "0 0");
        buttonPanel.add(cancelButton, "1 0");

        dialog.add(urlLabel, "0 0 1 0");
        dialog.add(urlField, "0 1 1 1");
        dialog.add(buttonPanel, "0 2 1 2");

        dialog.setVisible(true);
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

        // TODO check for missing ProcessEntities !!!
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "xml"));

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
    }

    private void doRedo() {
        app.getUndoManager().redo();
    }

    private void doReloadProcesses() {
        app.fillMainTree();
    }
}
