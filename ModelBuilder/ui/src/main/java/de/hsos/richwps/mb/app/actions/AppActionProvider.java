/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb.app.actions;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dziegenh
 */
public class AppActionProvider {

    public static enum APP_ACTIONS {
        NEW_MODEL, LOAD_MODEL, SAVE_MODEL, SAVE_MODEL_AS, SHOW_PREFERENCES, EXIT_APP,
        UNDO, REDO, DO_LAYOUT,
        DEPLOY,
        RELOAD_PROCESSES,
        SHOW_ERROR_MSG,
        EXECUTE
    }

    private IAppActionHandler actionHandler;

    private HashMap<APP_ACTIONS, AppAbstractAction> actionInstances;

    public AppActionProvider(IAppActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.actionInstances = new HashMap<APP_ACTIONS, AppAbstractAction>();
    }

    /**
     * Returns the corresponding action instance for the given APP_ACTION.
     * Creates the instance using reflection if necessary.
     * @param item
     * @return
     */
    public AppAbstractAction getAction(APP_ACTIONS item) {
        AppAbstractAction instance = actionInstances.get(item);

        if (null == instance) {

            try {
                Class actionClass = Class.forName("de.hsos.richwps.mb.app.actions." + getActionClassName(item));
                for (Constructor ctor : actionClass.getDeclaredConstructors()) {
                    if (ctor.getGenericParameterTypes().length == 1) {
                        instance = (AppAbstractAction) ctor.newInstance(new Object[]{actionHandler});
                        actionInstances.put(item, instance);
                    }
                }

            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
                // ignore; returning null indicates any exception
            }
        }

        return instance;
    }

    protected String getActionClassName(APP_ACTIONS item) {
        StringBuilder sb = new StringBuilder(item.toString().length() + 6);
        String[] split = item.toString().split("_");
        for (String part : split) {
            if (part.length() > 1) {
                sb.append(part.substring(0, 1)).append(part.substring(1).toLowerCase());
            } else {
                sb.append(part);
            }
        }

        sb.append("Action");

        return sb.toString();
    }

    public boolean isAppAction(Object o) {
        if (null == o) {
            return false;
        }
        for (APP_ACTIONS item : APP_ACTIONS.values()) {
            if (o.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public void fire(APP_ACTIONS action) {
        getAction(action).fireActionPerformed();
    }

    public void fire(APP_ACTIONS action, String command) {
        getAction(action).fireActionPerformed(command);
    }

}
