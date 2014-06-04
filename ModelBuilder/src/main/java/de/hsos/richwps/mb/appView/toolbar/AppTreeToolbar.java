/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.appView.toolbar;

import de.hsos.richwps.mb.appActions.AppActionProvider;
import javax.swing.JToolBar;

/**
 *
 * @author dziegenh
 */
public class AppTreeToolbar extends JToolBar {

    public AppTreeToolbar(AppActionProvider actionProvider) {
        setFloatable(false);

        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES));
        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.RELOAD_PROCESSES));

    }

}
