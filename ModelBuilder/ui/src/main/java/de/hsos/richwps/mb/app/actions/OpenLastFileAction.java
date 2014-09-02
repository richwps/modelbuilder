/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.app.actions;

/**
 *
 * @author dziegenh
 */
public class OpenLastFileAction extends AppAbstractAction {

    public OpenLastFileAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.OPEN_LAST_FILE, null);
    }

}
