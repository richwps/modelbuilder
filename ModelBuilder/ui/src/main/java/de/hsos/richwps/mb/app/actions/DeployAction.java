package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for starting the deployment.
 *
 * @author dziegenh
 */
public class DeployAction extends AppAbstractAction {

    public DeployAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.DEPLOY, AppConstants.ICON_DEPLOY_KEY);
//        setEnabled(false);
    }

}
