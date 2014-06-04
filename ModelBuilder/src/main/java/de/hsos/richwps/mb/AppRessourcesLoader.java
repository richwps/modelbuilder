/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.richwps.mb;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Provides methods for loading ressources into the UIManager.
 * @author dziegenh
 */
public class AppRessourcesLoader {

    /**
     * Calls all loading methods.
     */
    public static void loadAll() {
        loadIcons();
    }

    /**
     * Loads icons into UIManager.
     */
    public static void loadIcons() {
        String iconDir = AppConstants.RES_ICONS_DIR + File.separator;

        // Logo
        UIManager.put(AppConstants.ICON_MBLOGO_KEY, new ImageIcon(iconDir + "mb_logo.png", "mb logo icon"));

        // File icons
        UIManager.put(AppConstants.ICON_NEW_KEY, new ImageIcon(iconDir + "document-new-6.png", "new icon"));
        UIManager.put(AppConstants.ICON_OPEN_KEY, new ImageIcon(iconDir + "document-open-2.png", "open icon"));
        UIManager.put(AppConstants.ICON_SAVE_KEY, new ImageIcon(iconDir + "document-save-5.png", "save icon"));
        UIManager.put(AppConstants.ICON_SAVEAS_KEY, new ImageIcon(iconDir + "document-save-as-4.png", "save as icon"));
        UIManager.put(AppConstants.ICON_PREFERENCES_KEY, new ImageIcon(iconDir + "system-settings.png", "prefs icon"));
        UIManager.put(AppConstants.ICON_EXIT_KEY, new ImageIcon(iconDir + "dialog-close-2.png", "exit icon"));
        
        // Edit Icons
        UIManager.put(AppConstants.ICON_UNDO_KEY, new ImageIcon(iconDir + "arrow-undo.png", "undo icon"));
        UIManager.put(AppConstants.ICON_REDO_KEY, new ImageIcon(iconDir + "arrow-redo.png", "redo icon"));
        UIManager.put(AppConstants.ICON_LAYOUT_KEY, new ImageIcon(iconDir + "zoom-fit-best-4.png", "layout icon"));

        // to be sorted...
        UIManager.put(AppConstants.ICON_DEPLOY_KEY, new ImageIcon(iconDir + "server-go.png", "deploy icon"));
        UIManager.put(AppConstants.ICON_INFO_KEY, new ImageIcon(iconDir + "dialog-information-4.png", "info icon"));
        UIManager.put(AppConstants.ICON_RELOAD_KEY, new ImageIcon(iconDir + "database-refresh.png", "reload icon"));
        UIManager.put(AppConstants.ICON_REFRESH_KEY, new ImageIcon(iconDir + "view-refresh-4.png", "refresh icon"));
    }

}
