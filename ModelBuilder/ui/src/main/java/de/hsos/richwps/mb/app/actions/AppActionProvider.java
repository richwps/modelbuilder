package de.hsos.richwps.mb.app.actions;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates and provides all app actions (e.g. for menu or toolbar items).
 *
 * @author dziegenh
 * @author dalcacer
 */
public class AppActionProvider {

    public static enum APP_ACTIONS {

        // FILE
// FILE
        NEW_MODEL, LOAD_MODEL, SAVE_MODEL, SAVE_MODEL_AS, OPEN_RECENT_FILE, EXIT_APP,
        // EDIT
        UNDO, REDO, DO_LAYOUT, SHOW_PREFERENCES,
        // TOOLS
        EXECUTE,
        EXECUTE_ANY,
        PROFILE,
        TEST,
        // HELP
        ABOUT,
        // (others)
        PREVIEW_ROLA,
        DEPLOY,
        UNDEPLOY,
        //
        RELOAD_PROCESSES,
        MANAGE_REMOTES,
        PUBLISH,
        //others
        SHOW_ERROR_MSG
    }

    private IAppActionHandler actionHandler;

    private HashMap<APP_ACTIONS, AppAbstractAction> actionInstances;

    public AppActionProvider(IAppActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.actionInstances = new HashMap<>();
    }

    /**
     * Returns the corresponding action instance for the given APP_ACTION.
     * Creates the instance using reflection if necessary.
     *
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

                        // show error message action is just temporary and thus not saved
                        if (!item.equals(APP_ACTIONS.SHOW_ERROR_MSG)) {
                            actionInstances.put(item, instance);
                        }
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
