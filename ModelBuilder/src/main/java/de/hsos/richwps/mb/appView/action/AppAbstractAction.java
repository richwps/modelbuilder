/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView.action;

import de.hsos.richwps.mb.appView.IAppActionHandler;
import de.hsos.richwps.mb.appView.menu.AppMenuBar;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;

/**
 * Delegates ActionEvents to the actionHandler.
 *
 * @author dziegenh
 */
public abstract class AppAbstractAction extends AbstractAction {

    protected IAppActionHandler actionHandler;
    private Object actionSource;

    public AppAbstractAction(IAppActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public AppAbstractAction(IAppActionHandler actionHandler, Object actionSource) {
        this.actionHandler = actionHandler;
        this.actionSource = actionSource;
    }

    public AppAbstractAction(IAppActionHandler actionHandler, AppMenuBar.MENU_ITEMS item, String iconKey) {
        this(actionHandler, item);

        String name = AppMenuBar.getMenuItemCaption(item);

        putValue(Action.NAME, name);
        putValue(Action.SMALL_ICON, UIManager.getIcon(iconKey));
        putValue(Action.SHORT_DESCRIPTION, name);
    }

    public void setActionSource(Object actionSource) {
        this.actionSource = actionSource;
    }

    public void actionPerformed(ActionEvent e) {
        if (null != actionSource) {
            e.setSource(actionSource);
        }

        actionHandler.actionPerformed(e);
    }

}
