package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.AppConstants;
import de.hsos.richwps.mb.app.view.toolbar.AppTreeToolbar;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

/**
 * Action for showing the preferences dialog.
 * A specific tab is selected depending on the event source's parent.
 *
 * @author dziegenh
 */
public class ShowPreferencesAction extends AppAbstractAction {

    private AppConstants.PREFERENCES_TAB tabToShow = null;

    public ShowPreferencesAction(IAppActionHandler actionHandler) {
        super(actionHandler, AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES, AppConstants.ICON_PREFERENCES_KEY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        // set specific preferences tab to be opened, depending on the event source's parent.
        if (null != source && source instanceof Component) {
            Container parent = ((Component) source).getParent();
            if (parent instanceof AppTreeToolbar) {
                e = new ActionEvent("", e.getID(), AppConstants.PREFERENCES_TAB.SEMANTICPROXY.name());
            }
        }

        super.actionPerformed(e);
    }

}
