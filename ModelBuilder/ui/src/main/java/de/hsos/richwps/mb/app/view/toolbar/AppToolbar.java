package de.hsos.richwps.mb.app.view.toolbar;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import javax.swing.JToolBar;

/**
 * The ModelBuilder's main toolbar.
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
//
//        if (!AppConstants.GRAPH_AUTOLAYOUT) {
//            addSeparator();
//            add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DO_LAYOUT));
//        }

        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDEPLOY_ANY));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.EXECUTE_ANY));
//        addSeparator();
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DEPLOY));
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDEPLOY));
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.EXECUTE));
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.TEST));
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.PROFILE));
//        addSeparator();
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.PUBLISH));

    }

}
