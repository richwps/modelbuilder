/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppAbstractAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author dziegenh
 */
public class AppUndoManager extends MbUndoManager {

    private final App app;

    public AppUndoManager(App theApp) {
        this.app = theApp;

        addChangeListener(new MbUndoManager.UndoManagerChangeListener() {
            @Override
            public void changed() {

                String undoName = AppConstants.UNDOMANAGER_CANT_UNDO;
                if (canUndo()) {
                    undoName = "Undo " + getUndoPresentationName();
                }
                AppAbstractAction undoAction = getActionProvider().getAction(AppActionProvider.APP_ACTIONS.UNDO);
                undoAction.setName(undoName);
                undoAction.setEnabled(canUndo());

                String redoName = "Can't redo";
                if (canRedo()) {
                    redoName = "Redo " + getRedoPresentationName();
                }
                AppAbstractAction redoAction = getActionProvider().getAction(AppActionProvider.APP_ACTIONS.REDO);
                redoAction.setName(redoName);
                redoAction.setEnabled(canRedo());

                app.setChangesSaved(false);
            }
        });
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit edit) {
        return super.addEdit((AppUndoableEdit) edit);
    }

    private AppActionProvider getActionProvider() {
        return app.getActionProvider();
    }

    @Override
    public String getUndoPresentationName() {
        return ((AppUndoableEdit) editToBeUndone()).getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return ((AppUndoableEdit) editToBeRedone()).getPresentationName();
    }

}
