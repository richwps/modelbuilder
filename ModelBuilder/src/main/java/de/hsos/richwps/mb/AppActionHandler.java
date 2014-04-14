/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import de.hsos.richwps.mb.appView.IAppActionHandler;
import de.hsos.richwps.mb.appView.menu.AppMenuBar;
import de.hsos.richwps.mb.graphView.GraphView;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
//    private AppFrame frame;

    public AppActionHandler(App app) {
        this.app = app;
//        this.frame = app.getFrame();
    }

    public void actionPerformed(ActionEvent e) {

        if (!app.getFrame().getAppMenuBar().isMenuItem(e.getSource())) {
            return;
        }

        AppMenuBar.MENU_ITEMS item = (AppMenuBar.MENU_ITEMS) e.getSource();
        switch (item) {
            case FILE_NEW:
                doNewModel();
                break;
            case FILE_LOAD:
                doLoadModel();
                break;
            case FILE_SAVE:
                doSaveModel();
                break;
            case FILE_SAVEAS:
                doSaveModelAs();
                break;
            case FILE_PREFERENCES:
                doPreferencesDialog();
                break;
            case FILE_EXIT:
                doExit();
                break;

            case EDIT_LAYOUT:
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
                    if(null == graphName) {
                        graphName = "";
                    }
                    app.getFrame().setGraphViewTitle(graphName);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(app.getFrame(), AppConstants.LOAD_MODEL_FAILED);
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
        int choice = JOptionPane.showConfirmDialog(app.getFrame(), AppConstants.CONFIRM_EXIT, AppConstants.CONFIRM_EXIT_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            app.getFrame().dispatchEvent(new WindowEvent(app.getFrame(), WindowEvent.WINDOW_CLOSING) {
            });
        }
    }

    private GraphView getGraphView() {
        return app.getGraphView();
    }

    private void doLayout() {
        getGraphView().layoutGraph();
    }

    private void doSaveModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
