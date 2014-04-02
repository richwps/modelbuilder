/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author dziegenh
 */
public class AppConstants {

    public final static String SEMANTICPROXY_DEFAULT_URL = "xyz://";

    public static final String FRAME_TITLE = "RichWPS ModelBuilder";
    public static final Dimension FRAME_DEFAULT_SIZE = new Dimension(1422, 800);
    public static final Point FRAME_DEFAULT_LOCATION = new Point(100, 100);
    public static final boolean FRAME_DEFAULT_MAXIMIZED = false;
    
    public static final Dimension LEFT_PANEL_MIN_SIZE = new Dimension(200, 200);
    
    public static final Dimension PROPERTIES_PANEL_MIN_SIZE = new Dimension(200, 200);
    
    public static final Dimension BOTTOM_TABS_MIN_SIZE = new Dimension(100, 150);


    public static final String[] INFOTAB_TITLES = {"Server", "Editor"};

    public final static String MENU_FILE_PREFERENCES = "Preferences";
    public final static String MENU_FILE_EXIT = "Exit";
    public final static String MENU_FILE_SAVE = "Save";

    public static Object TREE_ROOT_NAME = "...";
    public static Object TREE_PROCESSES_NAME = "Processes";
    public static Object TREE_DOWNLOADSERVICES_NAME = "Download Services";
    public static Object TREE_LOCALS_NAME = "Local";

    public final static String CARD_MULTI_PROCESS_SELECTION = "(multiple elements selected)";
    public final static String PROPERTIES_INPUTS_TITLE = "Inputs";
    public final static String PROPERTIES_OUTPUTS_TITLE = "Outputs";
}
