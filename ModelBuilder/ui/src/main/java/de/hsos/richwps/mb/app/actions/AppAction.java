package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.app.App;
import de.hsos.richwps.mb.app.view.menu.AppMenuBar;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;

/**
 * Delegates ActionEvents to the actionHandler.
 * Provides the execution of action events.
 *
 * @author dziegenh
 */
public class AppAction extends AbstractAction {

    protected IAppActionHandler actionHandler;
    private Object actionSource;

    public AppAction(IAppActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public AppAction(IAppActionHandler actionHandler, Object actionSource) {
        this.actionHandler = actionHandler;
        this.actionSource = actionSource;
    }

    public AppAction(IAppActionHandler actionHandler, AppActionProvider.APP_ACTIONS item, String iconKey) {
        this(actionHandler, item);

        String name = App.getActionItemCaption(item);

        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
        
        if (null != iconKey && !iconKey.isEmpty()) {
            putValue(Action.SMALL_ICON, UIManager.getIcon(iconKey));
        }
    }

    public AppAction(IAppActionHandler actionHandler, AppActionProvider.APP_ACTIONS item, AppActionConfig config) {
        this(actionHandler, item);

        String name = config.getCaption();
        String iconKey = config.getIconKey();

        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
        
        if (null != iconKey) {
            putValue(Action.SMALL_ICON, UIManager.getIcon(iconKey));
        }
    }

    public void setActionSource(Object actionSource) {
        this.actionSource = actionSource;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (null != actionSource) {
            e.setSource(actionSource);
        }

        actionHandler.actionPerformed(e);
    }

    public void fireActionPerformed() {
        this.fireActionPerformed("");
    }

    public void fireActionPerformed(String command) {
        ActionEvent e = new ActionEvent(actionSource, (int) (Integer.MAX_VALUE * Math.random()), command);
        actionPerformed(e);
    }

    public void setName(String name) {
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
    }
    
}
