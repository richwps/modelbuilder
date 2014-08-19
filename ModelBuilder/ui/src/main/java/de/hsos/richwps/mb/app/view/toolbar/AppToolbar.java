/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.view.toolbar;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import javax.swing.JToolBar;

/**
 *
 * @author dziegenh
 */
public class AppToolbar extends JToolBar {

    public AppToolbar(AppActionProvider actionProvider) {
        setFloatable(false);

        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.NEW_MODEL));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.LOAD_MODEL));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS));

        addSeparator();

        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDO));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.REDO));

        if (!AppConstants.GRAPH_AUTOLAYOUT) {
            addSeparator();
            add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DO_LAYOUT));
        }

        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DEPLOY));
        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.EXECUTE));

    }

}
