package de.hsos.richwps.mb.app;

import java.util.prefs.Preferences;

/**
 * Provides saving/loading app configuration values.
 *
 * @author dziegenh
 */
public class AppConfig {

    /**
     * Config keys which should be used. Naming convention:
     * (OBJECTNAME)_(TYPE)_(KEYNAME) where TYPE can be: I (Integer) L (Long) F
     * (Float) D (Double) S (String) B (Boolean)
     */
    public static enum CONFIG_KEYS {

        FRAME_I_WIDTH,
        FRAME_I_HEIGHT,
        FRAME_I_POSITIONX,
        FRAME_I_POSITIONY,
        FRAME_B_MAXIMIZED,
        MODEL_S_LASTDIR,
        MODEL_S_LASTFILE,
        PREFERENCES_I_WIDTH,
        PREFERENCES_I_HEIGHT,
        PREFERENCES_I_POSITIONX,
        PREFERENCES_I_POSITIONY,
        SEMANTICPROXY_S_URL,
        SEMANTICPROXY_I_URL_COUNT,
        HTTPPROXY_S_HOST,
        HTTPPROXY_S_PORT,
        REMOTES_S_URL, 
        REMOTES_B_DISCOVER_ON_START, 
        MONITOR_S_URL
    }

    private static final String APP_CONFIG = "richwps/modelbuilder";
    private static Preferences app_config = Preferences.userRoot().node(APP_CONFIG);

    public static Preferences getConfig() {
        return app_config;
    }

}
