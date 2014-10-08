package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for starting the undeployment.
 *
 * @author dalcacer
 */
public class UndeployAction extends AppAbstractAction {

    public UndeployAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.UNDEPLOY, AppConstants.ICON_UNDEPLOY_KEY);
//        setEnabled(false);
    }

}
