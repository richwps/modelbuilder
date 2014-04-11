/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.hsos.richwps.mb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

/**
 *
 * @author dziegenh
 */
public class AppConstants {

    private final static String FSEP = File.separator;

    public final static String RESOURCES_DIR = "src" + FSEP + "main" + FSEP + "resources";

    /**
     * Full path to icons folder.
     */
    public final static String RES_ICONS_DIR = RESOURCES_DIR + FSEP + "icons";


    public final static String SEMANTICPROXY_DEFAULT_URL = "xyz://";

    public static final String FRAME_TITLE = "RichWPS ModelBuilder";
    public static final Dimension FRAME_DEFAULT_SIZE = new Dimension(1422, 800);
    public static final Point FRAME_DEFAULT_LOCATION = new Point(100, 100);
    public static final boolean FRAME_DEFAULT_MAXIMIZED = false;
    
    public static final Dimension LEFT_PANEL_MIN_SIZE = new Dimension(200, 200);
    
    public static final Dimension PROPERTIES_PANEL_MIN_SIZE = new Dimension(200, 200);
    
    public static final Dimension BOTTOM_TABS_MIN_SIZE = new Dimension(100, 150);


    // Array of {tabId, tabTitle}-elements
    public static final String[][] INFOTABS = new String[][] {
        {"server", "RichWPS-Server"},
        {"editor", "Problems"}
    };

    /**
     * the keys refer to MenuBar enum.
     */
    public static final String[][] MENU_ITEM_CAPTIONS = new String[][] {
        {"FILE_NEW", "New"},
        {"FILE_LOAD", "Open"},
        {"FILE_SAVE", "Save"},
        {"FILE_PREFERENCES", "Preferences"},
        {"FILE_EXIT", "Exit"},

        {"EDIT_UNDO", "Undo"},
        {"EDIT_REDO", "Redo"},
        {"EDIT_LAYOUT", "Layout"},
    };

    /**
     * the keys refer to MenuBar enum.
     */
    public static final String[][] MENU_CAPTIONS = new String[][] {
        {"FILE", "File"},
        {"EDIT", "Edit"},
    };

    public static Object TREE_ROOT_NAME = "...";
    public static Object TREE_PROCESSES_NAME = "Processes";
    public static Object TREE_DOWNLOADSERVICES_NAME = "Download Services";
    public static Object TREE_LOCALS_NAME = "Local";

    public final static String CARD_MULTI_PROCESS_SELECTION = "(multiple elements selected)";
    public final static String PROPERTIES_INPUTS_TITLE = "Inputs";
    public final static String PROPERTIES_OUTPUTS_TITLE = "Outputs";
    public final static String PROPERTIES_PANEL_TITLE = "Properties";

    public final static String TREE_VIEW_TITLE = null;
    public final static String OVERVIEW_TITLE = null;
    public final static String EDITOR_DEFAULT_TITLE = "(untitled)";

    // TODO get color from lookAndFeel
    public final static Color bgColor = new Color(222,227,250); // alt: (0xd0e0ff);

    // Messages
    public final static String CONFIRM_NEW_MODEL_TITLE = "Create new model?";
    public final static String CONFIRM_NEW_MODEL = "Create new model?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_LOAD_MODEL_TITLE = "Continue?";
    public final static String CONFIRM_LOAD_MODEL = "Continue loading?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_DELETE_CELLS_TITLE = "Delete selected elements?";
    public final static String CONFIRM_DELETE_CELLS = "Do you really want to delete the selected elements?";
    public final static String CONFIRM_EXIT = "Do you really want to close the application?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_EXIT_TITLE = "Exit";

    public final static String LOAD_MODEL_FAILED = "An error occured while loading the model.";
    public final static String SAVE_MODEL_FAILED = "An error occured while saving the model.";


    public final static int TOOLTIP_DISMISS_DELAY = Integer.MAX_VALUE;

    /**
     * UIManager keys.
     */
    public final static String ICON_NEW_KEY = "rwps_icon_new";
    public final static String ICON_OPEN_KEY = "rwps_icon_load";
    public final static String ICON_SAVE_KEY = "rwps_icon_save";
    public final static String ICON_EXIT_KEY = "rwps_icon_exit";
    public final static String ICON_PREFERENCES_KEY = "rwps_icon_prefs";
    public final static String ICON_UNDO_KEY = "rwps_icon_undo";
    public final static String ICON_REDO_KEY = "rwps_icon_redo";
    public final static String ICON_LAYOUT_KEY = "rwps_icon_layout";
}
