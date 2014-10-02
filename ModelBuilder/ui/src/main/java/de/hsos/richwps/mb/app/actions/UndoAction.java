package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Undo action for the UndoManager.
 *
 * @author dziegenh
 */
public class UndoAction extends AppAbstractAction {

    public UndoAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.UNDO, AppConstants.ICON_UNDO_KEY);
        setEnabled(false);
    }

}
