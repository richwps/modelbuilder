package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.UiHelper;

/**
 * Action for loading the most recently used model file.
 *
 * @author dziegenh
 */
public class OpenRecentFileAction extends AppAction {

    public OpenRecentFileAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE, "");
    }

    @Override
    public void setName(String name) {
        name = UiHelper.limitString(name, AppConstants.RECENT_FILE_VISIBLE_WIDTH);
        super.setName(name);
    }

}
