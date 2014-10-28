package de.hsos.richwps.mb.app.actions;

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
public abstract class AppAbstractAction extends AbstractAction {

    protected IAppActionHandler actionHandler;
    private Object actionSource;

    public AppAbstractAction(IAppActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    public AppAbstractAction(IAppActionHandler actionHandler, Object actionSource) {
        this.actionHandler = actionHandler;
        this.actionSource = actionSource;
    }

    public AppAbstractAction(IAppActionHandler actionHandler, AppActionProvider.APP_ACTIONS item, String iconKey) {
        this(actionHandler, item);

        String name = AppMenuBar.getMenuItemCaption(item);

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
