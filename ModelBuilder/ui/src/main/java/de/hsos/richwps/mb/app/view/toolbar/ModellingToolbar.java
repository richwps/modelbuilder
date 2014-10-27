package de.hsos.richwps.mb.app.view.toolbar;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import javax.swing.JToolBar;

/**
 * The Model Editor toolbar.
 *
 * @author dziegenh
 */
public class ModellingToolbar extends JToolBar {

    public ModellingToolbar(AppActionProvider actionProvider) {
        setFloatable(false);

//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDO));
//        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.REDO));

        if (!AppConstants.GRAPH_AUTOLAYOUT) {
//            addSeparator();
            add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DO_LAYOUT));
        }

        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.DEPLOY));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.UNDEPLOY));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.EXECUTE));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.TEST));
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.PROFILE));
        addSeparator();
        add(actionProvider.getAction(AppActionProvider.APP_ACTIONS.PUBLISH));

    }

}
