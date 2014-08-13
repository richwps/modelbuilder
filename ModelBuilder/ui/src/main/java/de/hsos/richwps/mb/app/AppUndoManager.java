/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppAbstractAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.undoManager.MbUndoManager;

/**
 *
 * @author dziegenh
 */
public class AppUndoManager extends MbUndoManager {

    private final App app;

    public AppUndoManager(App app) {
        this.app = app;

        addChangeListener(new MbUndoManager.UndoManagerChangeListener() {
            @Override
            public void changed() {
                AppAbstractAction undoAction = getActionProvider().getAction(AppActionProvider.APP_ACTIONS.UNDO);
                undoAction.setName("Undo " + getPresentationName());
                undoAction.setEnabled(canUndo());

                AppAbstractAction redoAction = getActionProvider().getAction(AppActionProvider.APP_ACTIONS.REDO);
                redoAction.setName("Redo " + getPresentationName());
                redoAction.setEnabled(canRedo());
            }
        });
    }

    private AppActionProvider getActionProvider() {
        return app.getActionProvider();
    }

}
