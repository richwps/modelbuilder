package de.hsos.richwps.mb.app.actions;

import de.hsos.richwps.mb.Logger;
import de.hsos.richwps.mb.app.AppConstants;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * Creates and provides all app actions (e.g. for menu or toolbar items).
 *
 * @author dziegenh
 * @author dalcacer
 */
public class AppActionProvider {

    public static enum APP_ACTIONS {

        // FILE
        NEW_MODEL, LOAD_MODEL, SAVE_MODEL, SAVE_MODEL_AS, OPEN_RECENT_FILE, EXIT_APP,
        // EDIT
        UNDO, REDO, DO_LAYOUT, ADD_PORTS, SHOW_PREFERENCES,
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
        UNDEPLOY_ANY,
        //
        RELOAD_PROCESSES,
        MANAGE_REMOTES,
        PUBLISH,
        //others
        SHOW_ERROR_MSG
    }

    private final IAppActionHandler actionHandler;

    private HashMap<APP_ACTIONS, AppAction> actionInstances;

    private HashMap<APP_ACTIONS, AppActionConfig> actionConfigs;

    private final String THIS_PACKAGE;

    public AppActionProvider(IAppActionHandler actionHandler) throws Exception {
        this.actionHandler = actionHandler;
        this.actionInstances = new HashMap<>();
        this.actionConfigs = new HashMap<>();

        this.THIS_PACKAGE = this.getClass().getPackage().getName();

        for (String[] configValues : AppConstants.ACTIONS_CONFIG) {
            for (APP_ACTIONS anAction : APP_ACTIONS.values()) {
                if (anAction.name().equals(configValues[0])) {
                    String caption = "";
                    String iconKey = "";
                    if (configValues.length > 1) {
                        caption = configValues[1];
                    }
                    if (configValues.length > 2) {
                        iconKey = configValues[2];
                    }

                    if (actionConfigs.containsKey(anAction)) {
                        Logger.log(this.getClass().getSimpleName() + " WARNING overwritting action config values for '" + anAction.name() + "'");
                    }

                    actionConfigs.put(anAction, new AppActionConfig(caption, iconKey));
                }
            }
        }
    }

    /**
     * Returns the corresponding action instance for the given APP_ACTION.
     * Creates the instance using reflection if necessary.
     *
     * @param item
     * @return
     */
    public AppAction getAction(APP_ACTIONS item) {
        AppAction instance = actionInstances.get(item);

        if (null == instance) {

            try {
                Class actionClass = Class.forName(this.THIS_PACKAGE + "." + getActionClassName(item));
                for (Constructor ctor : actionClass.getDeclaredConstructors()) {
                    if (ctor.getGenericParameterTypes().length == 1) {
                        instance = (AppAction) ctor.newInstance(new Object[]{actionHandler});
                    }
                }

            } catch (Exception ex) {
                // instantiate appAbstractAction using appConstant values (=AppActionConfig) (if available)
                AppActionConfig config = actionConfigs.get(item);
                if (null != config) {
                    instance = new AppAction(actionHandler, item, config);
                }
            }

            // show error message action is just temporary and thus not saved
            if (null != instance && !item.equals(APP_ACTIONS.SHOW_ERROR_MSG)) {
                actionInstances.put(item, instance);
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
