/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appActions;

import de.hsos.richwps.mb.App;
import de.hsos.richwps.mb.AppConstants;

/**
 *
 * @author dziegenh
 */
public class RedoAction extends AppAbstractAction {

    public RedoAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.REDO, AppConstants.ICON_REDO_KEY);
        setEnabled(false);
    }

}
