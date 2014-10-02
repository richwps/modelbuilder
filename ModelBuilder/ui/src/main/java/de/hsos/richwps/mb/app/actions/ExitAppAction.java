package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for exiting the ModelBuilder.
 *
 * @author dziegenh
 */
public class ExitAppAction extends AppAbstractAction {

    public ExitAppAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.EXIT_APP, AppConstants.ICON_EXIT_KEY);
    }

}
