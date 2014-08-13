/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.graphView.GraphView;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author dziegenh
 */
public class AppGraphView extends GraphView {

    private final App app;

    public AppGraphView(App app) {
        super(app.getProcessProvider());
        this.app = app;

        getGui().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // Delete
                    case 127:
                        Object[] selection = getSelection();
//                            if (null != graphView.getSelection() && selection.length > 0) {
                        if (hasSelection()) {
                            int choice = JOptionPane.showConfirmDialog(getApp().getFrame(), AppConstants.CONFIRM_DELETE_CELLS, AppConstants.CONFIRM_DELETE_CELLS_TITLE, JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                deleteSelectedCells();
                            }
                        }
                        break;

                    // Select All
                    case 65:
                        if (0 < (e.getModifiers() & KeyEvent.CTRL_MASK)) {
                            selectAll();
                        }
                        break;

                    default:
                        break;
                }
            }
        });

        // register graph components for the event service.
        AppEventService.getInstance().addSourceCommand(this, AppConstants.INFOTAB_ID_EDITOR);
        AppEventService.getInstance().addSourceCommand(this.getGraph(), AppConstants.INFOTAB_ID_EDITOR);

        // setup model listeners etc.
        app.modelLoaded();
    }

    private App getApp() {
        return this.app;
    }
}
