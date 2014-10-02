package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for layouting the graph.
 *
 * @author dziegenh
 */
public class DoLayoutAction extends AppAbstractAction {

    public DoLayoutAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.DO_LAYOUT, AppConstants.ICON_LAYOUT_KEY);
    }

}
