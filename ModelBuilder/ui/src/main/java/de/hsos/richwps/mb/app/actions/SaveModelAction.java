package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for saving the current model file.
 *
 * @author dziegenh
 */
public class SaveModelAction extends AppAbstractAction {

    public SaveModelAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SAVE_MODEL, AppConstants.ICON_SAVE_KEY);
        setEnabled(false);
    }

}
