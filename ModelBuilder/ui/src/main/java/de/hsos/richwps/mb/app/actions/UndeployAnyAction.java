package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for opening the execute-dialog.
 *
 * @author dalcacer
 */
public class UndeployAnyAction extends AppAbstractAction {

    public UndeployAnyAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.UNDEPLOY_ANY, AppConstants.ICON_UNDEPLOY_ANY_KEY);
    }

}
