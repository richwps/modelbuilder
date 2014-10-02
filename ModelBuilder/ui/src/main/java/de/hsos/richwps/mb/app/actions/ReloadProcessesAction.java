package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for reloading processes from the SP (client).
 *
 * @author dziegenh
 */
public class ReloadProcessesAction extends AppAbstractAction {

    public ReloadProcessesAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.RELOAD_PROCESSES, AppConstants.ICON_RELOAD_KEY);
    }

}
