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
public class SaveModelAction extends AppAbstractAction {

    public SaveModelAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SAVE_MODEL, AppConstants.ICON_SAVE_KEY);
        setEnabled(false);
    }

}
