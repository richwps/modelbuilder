/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 *
 * @author dziegenh
 */
public class ExecuteAction extends AppAbstractAction {

    public ExecuteAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.EXECUTE, AppConstants.ICON_EXECUTE_KEY);
    }

}
