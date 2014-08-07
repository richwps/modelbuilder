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

    static boolean DEBUG_MODE = false;

    /**
     * GRAPH handling
     */
    // true if the graph should auto-layout after user edits
    public static boolean GRAPH_AUTOLAYOUT = false;
    // true if multiple outputs can be connected to the same input
    public static boolean GRAPH_ALLOW_OUTS_TO_IN = false;
    // true if an output can be connected to multiple inputs
    public static boolean GRAPH_ALLOW_OUT_TO_INS = true;
    // true if the graph may contain loops
    public static boolean GRAPH_ALLOW_FEEDBACK_LOOPS = false;
    // true if a process output can be connected to an input of the same process
    public static boolean GRAPH_ALLOW_SINGLE_FEEDBACK_LOOPS = false;

    /**
     * Ressources
     */
    private final static String FSEP = File.separator;
    /**
     *
     */
    public final static String RESOURCES_DIR = "src" + FSEP + "main" + FSEP + "resources";
    /**
     * Full path to icons folder.
     */
    public final static String RES_ICONS_DIR = RESOURCES_DIR + FSEP + "icons";
    /**
     * Full path to images folder.
     */
    public final static String RES_IMAGES_DIR = RESOURCES_DIR + FSEP + "images";

public static String SPLASH_BG = RES_IMAGES_DIR + FSEP + "splash_bg.png";

    public final static String SEMANTICPROXY_DEFAULT_URL = "http://localhost:4567/semanticproxy/resources";
    public final static String SEMANTICPROXY_NOT_REACHABLE = "SemanticProxy is not reachable. Please check the configuration.";
    public final static String SEMANTICPROXY_RECEIVE_ERROR = "An error occured while receiving from SemanticProxy.\n\tError type: '%s'\n\tError message: '%s'";
    public final static String SEMANTICPROXY_CANNOT_CREATE_CLIENT = "Error while creating SemanticProxy client! Please check the SemanticProxy preferences.";

    /**
     * Frame
     */
    public static final String FRAME_TITLE = "RichWPS ModelBuilder";
    public static final Dimension FRAME_DEFAULT_SIZE = new Dimension(1422, 800);
    public static final Point FRAME_DEFAULT_LOCATION = new Point(100, 100);
    public static final boolean FRAME_DEFAULT_MAXIMIZED = false;
    public static final Dimension LEFT_PANEL_MIN_SIZE = new Dimension(200, 200);
    public static final Dimension PROPERTIES_PANEL_MIN_SIZE = new Dimension(200, 200);
    public static final Dimension BOTTOM_TABS_MIN_SIZE = new Dimension(100, 150);

    public static final Color INFOTABS_TEXTCOLOR = Color.GRAY;

    // Array of {tabId, tabTitle}-elements
    public final static String INFOTAB_ID_SERVER = "server";
    public final static String INFOTAB_ID_EDITOR = "editor";
    public final static String INFOTAB_ID_SEMANTICPROXY = "semanticproxy";
    public static final String[][] INFOTABS = new String[][]{
        {INFOTAB_ID_EDITOR, "Model"},
        {INFOTAB_ID_SEMANTICPROXY, "SemanticProxy"},
        {INFOTAB_ID_SERVER, "RichWPS-Server"},
    };

    /**
     * the keys refer to MenuBar enum.
     */
    public static final String[][] MENU_ITEM_CAPTIONS = new String[][]{
        {"NEW_MODEL", "New"},
        {"LOAD_MODEL", "Open"},
        {"SAVE_MODEL", "Save"},
        {"SAVE_MODEL_AS", "Save as"},
        {"SHOW_PREFERENCES", "Configuration"},
        {"EXIT_APP", "Exit"},
        {"UNDO", "Undo"},
        {"REDO", "Redo"},
        {"DO_LAYOUT", "Layout"},
        {"DEPLOY", "Deploy"},
        {"RELOAD_PROCESSES", "Reload processes"},};

    /**
     * the keys refer to MenuBar enum.
     */
    public static final String[][] MENU_CAPTIONS = new String[][]{
        {"FILE", "File"},
        {"EDIT", "Edit"},};

    public static Object TREE_ROOT_NAME = "...";
    public static Object TREE_PROCESSES_NAME = "Processes";
    public static Object TREE_DOWNLOADSERVICES_NAME = "Download Services";
    public static Object TREE_LOCALS_NAME = "Local";

    // Properties View
    public final static String CARD_MULTI_PROCESS_SELECTION = "(multiple elements selected)";
    public final static String PROPERTIES_PROCESS_TITLE = "Process";
    public final static String PROPERTIES_INPUTS_TITLE = "Inputs";
    public final static String PROPERTIES_OUTPUTS_TITLE = "Outputs";
    public final static String PROPERTIES_PANEL_TITLE = "Properties";
    public final static String PROCESS_IDENTIFIER_LABEL = "Identifier";
    public final static String PROCESS_ABSTRACT_LABEL = "Identifier";
    public final static String PROCESS_TITLE_LABEL = "Identifier";
    public final static String PORT_IDENTIFIER_LABEL = "Identifier";
    public final static String PORT_ABSTRACT_LABEL = "Identifier";
    public final static String PORT_TITLE_LABEL = "Identifier";
    public final static String PORT_DATATYPE_LABEL = "Datatype";

    public final static String TREE_VIEW_TITLE = null;//"Modelling Elements";
    public final static String OVERVIEW_TITLE = null;
    public final static String EDITOR_DEFAULT_TITLE = "(untitled)";

    // TODO move & get colors from lookAndFeel / UIManager
    @Deprecated
    public final static Color bgColor = new Color(222, 227, 250); // alt: (0xd0e0ff);
    @Deprecated
    public final static Color SELECTION_BG_COLOR = new Color(0xa0ace5); // 0xabb6f1);

    /**
     * Messages
     */
    public final static String CONFIRM_NEW_MODEL_TITLE = "Create new model?";
    public final static String CONFIRM_NEW_MODEL = "Create new model?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_LOAD_MODEL_TITLE = "Continue?";
    public final static String CONFIRM_LOAD_MODEL = "Continue loading?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_DELETE_CELLS_TITLE = "Delete selected elements?";
    public final static String CONFIRM_DELETE_CELLS = "Do you really want to delete the selected elements?";
    public final static String CONFIRM_EXIT = "Do you really want to close the application?\nAny changes of the current model will be lost.";
    public final static String CONFIRM_EXIT_TITLE = "Exit";
    public final static String CONFIRM_OVERWRITE_FILE_TITLE = "File already exists";
    public final static String CONFIRM_OVERWRITE_FILE = "Overwrite the selected file?";

    public final static String LOAD_MODEL_FAILED = "An error occured while loading the model.";
    public final static String SAVE_MODEL_FAILED = "An error occured while saving the model.";
    public final static String SEE_LOGGING_TABS = "See logging tabs for details.";
    public final static String ERROR_MSG_IS_FORMAT = "Error message is '%s'.";

    /**
     * ToolTips
     */
    public final static int TOOLTIP_DISMISS_DELAY = Integer.MAX_VALUE;
    public final static int TOOLTIP_INITIAL_DELAY = 400;

    /**
     * UIManager keys.
     */
    public final static String ICON_MBLOGO_KEY = "rwps_icon_mblogo";
    // Icons
    public final static String ICON_NEW_KEY = "rwps_icon_new";
    public final static String ICON_OPEN_KEY = "rwps_icon_load";
    public final static String ICON_SAVE_KEY = "rwps_icon_save";
    public final static String ICON_SAVEAS_KEY = "rwps_icon_saveas";
    public final static String ICON_EXIT_KEY = "rwps_icon_exit";
    public final static String ICON_PREFERENCES_KEY = "rwps_icon_prefs";
    public final static String ICON_UNDO_KEY = "rwps_icon_undo";
    public final static String ICON_REDO_KEY = "rwps_icon_redo";
    public final static String ICON_LAYOUT_KEY = "rwps_icon_layout";
    public final static String ICON_DEPLOY_KEY = "rwps_icon_deploy";
    public final static String ICON_INFO_KEY = "richwps_icon_info";
    public final static String ICON_RELOAD_KEY = "richwps_icon_reload";
    public final static String ICON_REFRESH_KEY = "richwps_icon_refresh";
    public final static String ICON_PROCESS_KEY = "richwps_icon_process";

    // Graph error messages
    public final static String GRAPH_ERROR_INPUT_OCCUPIED = "Port is already connected";
    public final static String GRAPH_ERROR_OUTPUT_OCCUPIED = "Port is already connected";
    public final static String GRAPH_ERROR_SINGLE_FEEDBACK = "Connecting an input to an output of the same process is not possible"; //"Feedback connection loops are not allowed";
    public final static String GRAPH_ERROR_IN_TO_IN = "An input port must be connected to an output port";
    public final static String GRAPH_ERROR_OUT_TO_OUT = "An output port must be connected to an input port";
    public final static String GRAPH_ERROR_PORTTYPES_NOT_COMPATIBLE = "Port types are not compatible";
    public final static String GRAPH_EXCEPTION_PORT_NOT_GLOBAL = "Only global ports can be added to the graph";

    /**
     * Used for error messages, e.g. in ToolTips
     */
    public static Color ERROR_MESSAGE_COLOR = Color.RED;

    public final static String ERROR_MSG_IS = "Error message is: ";

    public final static String PROPERTIES_MODELDATA_TITLE = "Model";
    public final static String PROPERTIES_MODELDATA_NAME = "Name";
    public final static String PROPERTIES_GLOBALPORTDATA_TITLE = "Port";

    public final static String SUB_TREEVIEW_TITLE = "Used elements";

    final static String DEPLOYMENT_FAILED = "Deployment failed!";

}
