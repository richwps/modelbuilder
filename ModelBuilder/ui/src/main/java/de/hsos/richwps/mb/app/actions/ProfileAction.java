package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for opening the execute-dialog.
 *
 * @author dalcacer
 */
public class ProfileAction extends AppAbstractAction {

    public ProfileAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.PROFILE, AppConstants.ICON_PROFILE_KEY);
    }

}
