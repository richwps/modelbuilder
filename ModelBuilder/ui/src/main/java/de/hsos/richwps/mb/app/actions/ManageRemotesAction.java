package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for layouting the graph.
 *
 * @author dalcacer
 */
public class ManageRemotesAction extends AppAbstractAction {

    public ManageRemotesAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.MANAGE_REMOTES, AppConstants.ICON_ADD_REMOTE);
    }

}
