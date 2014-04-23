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
import de.hsos.richwps.mb.graphView.GraphView;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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
            default:
            // do nothing
        }
    }

    private void doNewModel() {
        int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_NEW_MODEL, AppConstants.CONFIRM_NEW_MODEL_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            getGraphView().newGraph();
        }
    }

    private void doSaveModelAs() {

        // TODO check for missing ProcessEntities !!!

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("XML-Files", "xml"));

        int state = fc.showSaveDialog(app.getFrame());
        if (state == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getPath();
            if (!filename.toLowerCase().endsWith(".xml")) {
                filename = filename.concat(".xml");
            }

            try {
                getGraphView().saveGraphToXml(filename);
                app.getFrame().setGraphViewTitle(getGraphView().getCurrentGraphName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app.getFrame(), AppConstants.SAVE_MODEL_FAILED);
            }
        }
    }

    private void doLoadModel() {
        int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_LOAD_MODEL, AppConstants.CONFIRM_LOAD_MODEL_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
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
                    app.getFrame().setGraphViewTitle(graphName);
                    app.getActionProvider().getAction(SAVE_MODEL).setEnabled(true);
                }

            } catch (Exception ex) {
                Logger.getLogger(AppActionHandler.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(app.getFrame(), AppConstants.LOAD_MODEL_FAILED);
                app.getActionProvider().getAction(SAVE_MODEL).setEnabled(false);
            }
        }
    }

    private void doPreferencesDialog() {
        // TODO just an dirty mocked dialog !!! create a real one :)
        final JDialog dialog = new JDialog(app.getFrame(), "Preferences");
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.setMinimumSize(new Dimension(200, 100));
        Point loc = dialog.getLocation();
        dialog.setLocation(loc.x + 100, loc.y + 100);
        double p = TableLayout.PREFERRED;
        dialog.setLayout(new TableLayout(new double[][]{{.5, .5}, {p, p, p}}));

        int i = 2;
        Border mockBorder = new EmptyBorder(i, i, i, i);

        JLabel urlLabel = new JLabel("Semantic Proxy URL:");
        urlLabel.setBorder(mockBorder);

        final JTextArea urlField = new JTextArea(AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.SEMANTICPROXY_S_URL.name(), "http://"));
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
//        app.getFrame().dispatchEvent(new WindowEvent(app.getFrame(), WindowEvent.WINDOW_CLOSING) {});
        int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_EXIT, AppConstants.CONFIRM_EXIT_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            app.getFrame().dispose();
            System.exit(0);
//            app.getFrame().dispatchEvent(new WindowEvent(app.getFrame(), WindowEvent.WINDOW_CLOSING) {
//            });
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
            Logger.getLogger(AppActionHandler.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(app.getFrame(), AppConstants.SAVE_MODEL_FAILED);
        }

    }
}
