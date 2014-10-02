package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Redo action for the UndoManager.
 *
 * @author dziegenh
 */
public class RedoAction extends AppAbstractAction {

    public RedoAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.REDO, AppConstants.ICON_REDO_KEY);
        setEnabled(false);
    }

}
