/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appActions;

import de.hsos.richwps.mb.AppConstants;

/**
 *
 * @author dziegenh
 */
public class ShowErrorMsgAction extends AppAbstractAction {

    public ShowErrorMsgAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SHOW_ERROR_MSG, AppConstants.ICON_MBLOGO_KEY);
    }

}
