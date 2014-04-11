/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb.appView.action;

import de.hsos.richwps.mb.AppConstants;
import de.hsos.richwps.mb.appView.IAppActionHandler;
import de.hsos.richwps.mb.appView.menu.AppMenuBar;

/**
 *
 * @author dziegenh
 */
public class LoadAction extends AppAbstractAction {

    public LoadAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppMenuBar.MENU_ITEMS.FILE_LOAD, AppConstants.ICON_OPEN_KEY);
    }

}
