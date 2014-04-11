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
public class NewAction extends AppAbstractAction {

    public NewAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppMenuBar.MENU_ITEMS.FILE_NEW, AppConstants.ICON_NEW_KEY);
    }

}
