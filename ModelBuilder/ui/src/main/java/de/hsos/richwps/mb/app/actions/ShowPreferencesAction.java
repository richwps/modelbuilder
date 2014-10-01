/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

/**
 *
 * @author dziegenh
 */
public class ShowPreferencesAction extends AppAbstractAction {

    private AppConstants.PREFERENCES_TAB tabToShow = null;

    public ShowPreferencesAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES, AppConstants.ICON_PREFERENCES_KEY);
    }

    public AppConstants.PREFERENCES_TAB getTabToShow() {
        return tabToShow;
    }

    public void setTabToShow(AppConstants.PREFERENCES_TAB tabToShow) {
        this.tabToShow = tabToShow;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // set specific preferences tab to be opened, depending on the action's parent.
        if (null != source && source instanceof Component) {
            Container parent = ((Component) source).getParent();
            if (parent instanceof AppTreeToolbar) {
                e = new ActionEvent("", e.getID(), AppConstants.PREFERENCES_TAB.SEMANTICPROXY.name());
            }
        }

        super.actionPerformed(e);
    }

}
