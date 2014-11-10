package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for starting the deployment.
 *
 * @author dalcacer
 */
public class PreviewAction extends AppAbstractAction {

    public PreviewAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.PREVIEW, AppConstants.ICON_PREVIEW_KEY);
//        setEnabled(false);
    }

}
