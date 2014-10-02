package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for opening the execute-dialog.
 *
 * @author dziegenh
 */
public class ExecuteAction extends AppAbstractAction {

    public ExecuteAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.EXECUTE, AppConstants.ICON_EXECUTE_KEY);
    }

}
