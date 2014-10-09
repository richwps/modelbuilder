package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for layouting the graph.
 *
 * @author dalcacer
 */
public class PublishAction extends AppAbstractAction {

    public PublishAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.PUBLISH, AppConstants.ICON_PUBLISH);
    }

}
