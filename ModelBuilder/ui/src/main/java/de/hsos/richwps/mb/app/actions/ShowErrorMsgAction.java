package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 * Action for showing a message box.
 *
 * @author dziegenh
 */
public class ShowErrorMsgAction extends AppAction {

    public ShowErrorMsgAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SHOW_ERROR_MSG, AppConstants.ICON_MBLOGO_KEY);
    }

}
