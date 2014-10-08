package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for opening the execute-dialog.
 *
 * @author dziegenh
 */
public class ExecuteAnyAction extends AppAbstractAction {

    public ExecuteAnyAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.EXECUTE_ANY, AppConstants.ICON_EXECUTE_ANY_KEY);
    }

}
