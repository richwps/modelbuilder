package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for showing the "open model file" dialog.
 *
 * @author dziegenh
 */
public class LoadModelAction extends AppAbstractAction {

    public LoadModelAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.LOAD_MODEL, AppConstants.ICON_OPEN_KEY);
    }

}
