package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for creating a new model.
 *
 * @author dziegenh
 */
public class NewModelAction extends AppAbstractAction {

    public NewModelAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.NEW_MODEL, AppConstants.ICON_NEW_KEY);
    }

}
