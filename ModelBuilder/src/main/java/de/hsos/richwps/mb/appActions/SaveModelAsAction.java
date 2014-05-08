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
public class SaveModelAsAction extends AppAbstractAction {

    public SaveModelAsAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS, AppConstants.ICON_SAVEAS_KEY);
    }

}
