package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for layouting the graph.
 *
 * @author dalcacer
 */
public class AddRemoteAction extends AppAbstractAction {

    public AddRemoteAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.ADD_REMOTE, AppConstants.ICON_ADD_REMOTE);
    }

}
