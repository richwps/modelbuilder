/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

import java.util.prefs.Preferences;

/**
 * Provides save/load of app configurations.
 * @author dziegenh
 */
public class AppConfig {

    /**
     * Config keys which should be used.
     * Naming convention: (OBJECTNAME)_(TYPE)_(KEYNAME) where TYPE can be:
     * I (Integer)
     * L (Long)
     * F (Float)
     * D (Double)
     * S (String)
     * B (Boolean)
     */
    public static enum CONFIG_KEYS {
        FRAME_I_WIDTH,
        FRAME_I_HEIGHT,
        FRAME_I_POSITIONX,
        FRAME_I_POSITIONY,
        FRAME_B_MAXIMIZED,

        SEMANTICPROXY_S_URL
    }

    private static final String APP_CONFIG = "richwps/modelbuilder";
    private static Preferences app_config = Preferences.userRoot().node(APP_CONFIG);

    public static Preferences getConfig() {
        return app_config;
    }

}
