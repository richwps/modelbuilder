package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for showing the "save as" dialog.
 *
 * @author dziegenh
 */
public class SaveModelAsAction extends AppAbstractAction {

    public SaveModelAsAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS, AppConstants.ICON_SAVEAS_KEY);
    }

}
