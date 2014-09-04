/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.ui.UiHelper;

/**
 *
 * @author dziegenh
 */
public class OpenRecentFileAction extends AppAbstractAction {

    public OpenRecentFileAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE, null);
    }

    @Override
    public void setName(String name) {
        name=UiHelper.limitString(name, AppConstants.RECENT_FILE_VISIBLE_WIDTH);
        super.setName(name);
    }



}
