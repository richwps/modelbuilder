package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for showing the about dialog.
 *
 * @author dziegenh
 */
public class AboutAction extends AppAbstractAction {

    public AboutAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.ABOUT, AppConstants.ICON_ABOUT_KEY);
    }

}
