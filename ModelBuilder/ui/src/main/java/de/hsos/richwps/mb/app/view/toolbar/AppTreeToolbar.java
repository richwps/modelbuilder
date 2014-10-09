package de.hsos.richwps.mb.app.view.toolbar;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import javax.swing.JToolBar;

/**
 * Toolbar for the main tree view, providing interactions with the
 * SemanticProxy.
 *
 * @author dziegenh
 */
public class AppTreeToolbar extends JToolBar {

    public AppTreeToolbar(AppActionProvider actionProvider) {
        setFloatable(false);

        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES));
        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.RELOAD_PROCESSES));
        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.ADD_REMOTE));

    }

}
