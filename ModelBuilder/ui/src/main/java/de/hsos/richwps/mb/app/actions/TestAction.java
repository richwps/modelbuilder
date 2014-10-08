package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;

/**
 * Action for opening the execute-dialog.
 *
 * @author dziegenh
 */
public class TestAction extends AppAbstractAction {

    public TestAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.TEST, AppConstants.ICON_TEST_KEY);
    }

}
