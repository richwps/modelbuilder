package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for starting the deployment.
 *
 * @author dalcacer
 */
public class PreviewRolaAction extends AppAbstractAction {

    public PreviewRolaAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.PREVIEW_ROLA, AppConstants.ICON_PREVIEW_KEY);
//        setEnabled(false);
    }

}
