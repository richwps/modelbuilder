package de.hsos.richwps.mb.app;

import de.hsos.richwps.mb.app.actions.AppActionProvider;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.util.Locale;

/**
 * Collection of app-wide constant values.
 *
 * @author dziegenh
 * @author dalcacer
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
     * Size in px of the graph grid.
     */
    public final static int GRAPH_GRID_SIZE = 10;

    /**
     * Ressources *
     */
    /**
     * File.seperator shortcut.
     */
    public final static String FSEP = File.separator;
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

    public final static String SEMANTICPROXY_DEFAULT_URL = "http://richwps.edvsz.hs-osnabrueck.de/semanticproxy";
    public final static String SEMANTICPROXY_NOT_REACHABLE = "SemanticProxy is not reachable. Please check the configuration.";
    public final static String SEMANTICPROXY_RECEIVE_ERROR = "An error occured while receiving from SemanticProxy.\n\tError type: '%s'\n\tError message: '%s'";
    public final static String SEMANTICPROXY_CANNOT_CREATE_CLIENT = "Error while creating SemanticProxy client! Please check the SemanticProxy preferences.";
    final static String SEMANTICPROXY_PUBLISH_SUCCESS = "The process has successfully been published at the SemanticProxy.";
    final static String SEMANTICPROXY_PUBLISH_ERROR = "An error occured while publishing the process at SemanticProxy.";
    final static String SEMANTICPROXY_PUBLISH_ERROR_DETAILLED = "An error occured while publishing the process at SemanticProxy.\n\tError type: '%s'\n\tError message: '%s'";

    // Dialoges
    public final static String DIALOG_BTN_CLOSE = "Close";
    public final static String DIALOG_BTN_CANCEL = "Cancel";
    public final static String DIALOG_BTN_START = "Start";
    public final static String DIALOG_BTN_BACK = "< Back";
    public final static String DIALOG_BTN_NEXT = "Next >";
    public final static String DIALOG_BTN_OK = "Ok";
    public final static String DIALOG_BTN_EXPAND_ALL = "Expand all";
    public final static String DIALOG__BTN_COLLAPSE_ALL = "Collapse all";
    public final static String DIALOG_BTN_SELECT_ALL = "Select all";
    public final static String DIALOG_BTN_DESELECT_ALL = "Deelect all";
    public final static String DIALOG_VALIDATION_MISSING_INPUT = "Please provide input for ";
    public final static Dimension DIALOG_BTN_SIZE = new Dimension(80, 20);
    public final static java.awt.Font DIALOG_TEXTPANE_FONT = new java.awt.Font("Arial", 0, 12);

    // Preferences Dialog
    public final static String PREFERENCES_DIALOG_TITLE = "Configuration";
    public final static Dimension PREFERENCES_DIALOG_SIZE = new Dimension(1024, 576);
    public final static Point PREFERENCES_DIALOG_LOCATION = new Point(200, 150);
    public final static String PREFERENCES_DIALOG_BTN_CANCEL = DIALOG_BTN_CANCEL;
    public final static String PREFERENCES_DIALOG_BTN_OK = DIALOG_BTN_OK;

    //Select Remote Dialog
    public final static String SELECTREMOTE_DIALOG_TITLE = "Sever selection";
    public final static String SELECTREMOTE_DIALOG_MSG = "Please select a RichWPS server to work with.";
    public final static String SELECTREMOTE_DIALOG_BTN_CANCEL = DIALOG_BTN_CANCEL;
    public final static String SELECTREMOTE_DIALOG_BTN_OK = DIALOG_BTN_OK;

    //Add Remote Dialog
    public final static String MANAGEREMOTES_DIALOG_TITLE = "Manage remote servers";
    public final static String MANAGEREMOTES_DIALOG_MSG = "Please enter a WPS server to work with.";

    //Deployerror Dialog
    public final static String DEPLOY_ERROR_DIALOG_TITLE = "Error while deployment";
    public final static String DEPLOY_ERROR_DIALOG_MSG = "An error occured while deployment. See logs for further information.";
    public final static String DEPLOY_ID_MISSING = "Insufficient model information. Identifier is missing.";
    public final static String DEPLOY_TITLE_MISSING = "Insufficient model information. Title is missing.";
    public final static String DEPLOY_VERSION_MISSING = "Insufficient model information. Version is missing.";
    public final static String DEPLOY_ROLA_FAILED = "Unable to create underlying workflow description (ROLA) based on model.";
    public final static String DEPLOY_DESC_FAILED = "Unable to create WPS:ProcessDescription based on model.";
    public final static String DEPLOY_CONNECT_FAILED = "Unable to connect to selected RichWPS server.";
    public final static String DEPLOY_SERVERSIDE_ERROR = "An error occured while deployment. A serverside error has been invoked.";
    public final static String DEPLOY_SUCCESS = "Deployment performed successfully.";
    public final static String DEPLOY_FAILURE = "Deployment failed.";

    //Execute Dialog
    public final static String EXECUTE_DIALOG_TITLE = "Execute a remote process";

    //Execute Dialog
    public final static String EXECUTE_THIS_DIALOG_TITLE = "Execute the opened model";

    //Undeploy Dialog
    public final static String DEPLOY_DIALOG_TITLE = "Undeploy a given process";
    public final static String UNDEPLOY_SUCCESS = "Undeployment performed successfully.";
    public final static String UNDEPLOY_FAILURE = "Undeployment failed.";
    //Undeployerror Dialog
    public final static String UNDEPLOY_ERROR_DIALOG_TITLE = "Error while undeployment";
    public final static String UNDEPLOY_ERROR_DIALOG_MSG = "An error occured while undeployment. See logs for further information.";

    //Test Dialog
    public final static String TEST_THIS_DIALOG_TITLE = "Test the opened model";

    //Profile Dialog
    public final static String PROFILE_THIS_DIALOG_TITLE = "Profile the opened model";

    //Execute/Undeployerror Dialog
    public final static String PROCESSNOTFOUND_DIALOG_TITLE = "Process not deployed";
    public final static String PROCESSNOTFOUND_DIALOG_MSG = "The process needs to be deployed before it can be undeployed or executed.";

    //Connectivity errors
    public final static String CONNECT_FAILED = "Unable to connect to selected server.";
    public final static String DIALOG_REQUEST_SENT = "<html>Sending and processing statement.<br/>"
            + "This might take some time, depending on the remote process <br/> and amount of data..</html>";

    // About Dialog
    /**
     * Title of the about dialog.
     */
    public final static String ABOUT_DIALOG_TITLE = "About";
    /**
     * Size of the about dialog.
     */
    public final static Dimension ABOUT_DIALOG_SIZE = new Dimension(400, 490);
    /**
     * Main about dialog image.
     */
    public final static String ABOUT_IMAGE = RES_IMAGES_DIR + FSEP + "about3.png"; // "about.png" // <-- old RichWPS turquoise
    /**
     * Content of the about dialog.
     */
    public final static String ABOUT_DIALOG_TEXT = "<html>"
            + "The <b>RichWPS ModelBuilder</b> is part of the RichWPS research project.<br />"
            + "<br />"
            + "RichWPS is a corporate project of<br />"
            + "<span style=\"color:#37ABC8;\">&#149;</span>&nbsp;<b>Disy Informationssysteme GmbH</b><br />"
            + "&nbsp;&nbsp;&nbsp;Ludwig-Erhard-Allee 6, 76131 Karlsruhe<br />"
            + "<span style=\"color:#37ABC8;\">&#149;</span>&nbsp;<b>Hochschule Osnabrück, Fakultät Ingenieurwissenschaften und Informatik</b><br />"
            + "&nbsp;&nbsp;&nbsp;Albrecht-str. 30, 49076 Osnabrück<br />"
            + "<span style=\"color:#37ABC8;\">&#149;</span>&nbsp;<b>Bundesanstalt für Wasserbau, Dienststelle Hamburg</b><br />"
            + "&nbsp;&nbsp;&nbsp;Wedeler Landstr. 157, 22559 Hamburg<br />"
            + "<span style=\"color:#37ABC8;\">&#149;</span>&nbsp;<b>Landesbetrieb Küstenschutz, Nationalpark und  Meeresschutz Schleswig-Holstein</b><br />"
            + "&nbsp;&nbsp;&nbsp;Schloßgarten 1, 25829 T&ouml;nning<br />"
            + "<br />"
            + "RichWPS is funded by Germany's <b>Federal Ministry of Education and Research</b>."
            + "</html>";
    /**
     * Text of the about dialog "close" button.
     */
    public final static String ABOUT_DIALOG_BTN_CLOSE = DIALOG_BTN_CLOSE;

    /**
     * The loading screen (dialog) title.
     */
    public final static String LOADING_SCREEN_TITLE = "Loading...";
    /**
     * Number of visible characters of the "recent file" path.
     */
    public static final int RECENT_FILE_VISIBLE_WIDTH = 70;

    public static int GRAPHVIEW_PROCESS_TITLE_MAX_VIEW_LENGTH = 21;
    public static int TREEVIEW_PROCESS_TITLE_MAX_VIEW_LENGTH = 50;

    final static String INPUT_PORT_COLOR_STRING = "bbe8c6";
    final static String OUTPUT_PORT_COLOR_STRING = "cfe1fc";
    final static Color INPUT_PORT_COLOR = new Color(0xbbe8c6);
    final static Color OUTPUT_PORT_COLOR = new Color(0xcfe1fc);

    static String FORMATS_CSV_FILE_LOAD_ERROR = "Error while loading complex data formats!";
    static String DIALOG_TITLE_ERROR = "An error occured";

    /**
     * Common CSS for input and output ports.
     */
    private final static String TOOLTIP_CSS_FOR_PORTS = "color:#000000;border:1px solid #cccccc;padding:2px;margin: 2px 0 1px 0;";

    /**
     * The CSS which is used for input port html containers in ToolTipTexts.
     */
    static String TOOLTIP_CSS_FOR_INPUTS = "background:#" + INPUT_PORT_COLOR_STRING + ";" + TOOLTIP_CSS_FOR_PORTS;

    /**
     * The CSS which is used for input port html containers in ToolTipTexts.
     */
    static String TOOLTIP_CSS_FOR_OUTPUTS = "background:#" + OUTPUT_PORT_COLOR_STRING + ";" + TOOLTIP_CSS_FOR_PORTS;

    final static String TOOLTIP_BG_COLOR_HEX_STRING = "FAFAFA";
    final static Color TOOLTIP_BG_COLOR = new Color(Integer.parseInt(TOOLTIP_BG_COLOR_HEX_STRING, 16));

    /**
     * The CSS which is used for the main html container in ToolTipTexts.
     */
    static String TOOLTIP_CSS_FOR_MAIN_CONTAINER = "color:#000000;background:#" + TOOLTIP_BG_COLOR_HEX_STRING + ";border:0;margin:0;";
    public static final String FORMATS_CSV_FILE = "formats.csv";

    static String REMOTES_NOT_AVAILABLE_ERROR_MSG = "No remote server available!"
            + System.lineSeparator()
            + "Please check the SemanticProxy preferences or add a remote server using the 'Manage Remotes' dialog.";

    public final static boolean PREFERENCES_DISCOVER_REMOTES_ON_STARTUP_DEFAULT = true;
    public static String PREFERENCES_TAB_SP_LOADONSTART_LABEL = "Discover processes of managed remote servers on application start";

    public static Insets DEFAULT_COMPONENT_INSETS = new Insets(0, 0, 0, 0); //new Insets(2, 2, 2, 2);

    /**
     * For action "open recently used file"
     */
    public static String CAPTION_RECENTLY_USED = "Recently used: ";

    static boolean ENABLE_SUB_TREE_VIEW = false;

    /**
     * BG color for targets group.
     */
    final static Color QOS_TARGETS_BG_COLOR = new Color(247, 231, 174);
    /**
     * BG color for a target sub group
     */
    final static Color QOS_TARGET_BG_COLOR = new Color(250, 240, 209);

    static String FORMATTED_HINT_PROCESS_ALREADY_DEPLOYED = "A process with identifier '%s' is already deployed at '%s'";

    static final String MONITOR_DEFAULT_URL = "http://localhost:1111";

    /**
     * ProcessProvider translations for views.
     */
    static String[][] PROCESS_PROVIDER_TRANSLATIONS = new String[][]{
        {"SECONDS", "sec"}, // SemanticProxy QoS EUOM
        {"UNDEFINED", ""}
    };

    /**
     * If not null, this locale will be set globally as default.
     */
    static Locale DEFAULT_LOCALE = null;

    /**
     * ToolTipText for selected supported formats.
     */
    public static String COMPLEX_FORMAT_TOOLTIP_SUPPORTEDFORMAT = "Supported format. Click to remove support for this format.";

    /**
     * ToolTipText for selected supported formats.
     */
    public static String COMPLEX_FORMAT_TOOLTIP_SUPPORTEDFORMAT_DESELECTED = "Click to add as supported format.";

    /**
     * ToolTipText for selected default format.
     */
    public static String COMPLEX_FORMAT_TOOLTIP_DEFAULTFORMAT = "Current default format.";

    /**
     * ToolTipText for deselected default format.
     */
    public static String COMPLEX_FORMAT_TOOLTIP_DEFAULTFORMAT_DESELECTED = "Click to set as the default supported format";

    public enum PREFERENCES_TAB {

        PROCESSSOURCES,
        HTTPPROXY
    }
    public static final String[][] PREFERENCES_TAB_TITLE = new String[][]{
        {PREFERENCES_TAB.PROCESSSOURCES.name(), "Process Sources"},
        {PREFERENCES_TAB.HTTPPROXY.name(), "HTTP Proxy"},};
    // SP preferences
    public final static String PREFERENCES_TAB_SP_URL_LABEL = "<html>SemanticProxy URL:<br />(e.g. http://localhost:4567/semanticproxy)</html>";
    // Http proxy preferences
    public final static String PREFERENCES_TAB_HTTP_HOST_LABEL = "Proxy host:";
    public final static String PREFERENCES_TAB_HTTP_PORT_LABEL = "Proxy port:";

    /**
     * Main MB Frame
     */
    public static final String FRAME_TITLE = "RichWPS ModelBuilder";
    public static final Dimension FRAME_DEFAULT_SIZE = new Dimension(1422, 800);
    public static final Point FRAME_DEFAULT_LOCATION = new Point(100, 100);
    public static final boolean FRAME_DEFAULT_MAXIMIZED = false;
    public static final Dimension LEFT_PANEL_MIN_SIZE = new Dimension(250, 200);
    public static final Dimension PROPERTIES_PANEL_PREFERRED_SIZE = new Dimension(250, 200);

    // InfoTabs View
    public static final Dimension INFOTABS_MIN_SIZE = new Dimension(100, 150);
    public static final Color INFOTABS_TEXTCOLOR = Color.GRAY;
    // Array of {tabId, tabTitle}-elements
    public final static String INFOTAB_ID_SERVER = "server";
    public final static String INFOTAB_ID_EDITOR = "editor";
    public final static String INFOTAB_ID_SEMANTICPROXY = "semanticproxy";
    public static final String[][] INFOTABS = new String[][]{
        {INFOTAB_ID_EDITOR, "Model"},
        {INFOTAB_ID_SEMANTICPROXY, "SemanticProxy"},
        {INFOTAB_ID_SERVER, "RichWPS-Server"},};

    /**
     * Sets a visible String caption and an icon for the app actions.
     */
    public static final String[][] ACTIONS_CONFIG = new String[][]{
        {AppActionProvider.APP_ACTIONS.NEW_MODEL.name(), "New", AppConstants.ICON_NEW_KEY}, // Action for creating a new model.
        {AppActionProvider.APP_ACTIONS.LOAD_MODEL.name(), "Open", AppConstants.ICON_OPEN_KEY}, // Action for showing the "open model file" dialog.
        {AppActionProvider.APP_ACTIONS.SAVE_MODEL.name(), "Save", AppConstants.ICON_SAVE_KEY}, // Action for saving the current model file.
        {AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS.name(), "Save as", AppConstants.ICON_SAVEAS_KEY}, //  Action for showing the "save as" dialog.
        {AppActionProvider.APP_ACTIONS.SHOW_PREFERENCES.name(), "Configuration"},
        {AppActionProvider.APP_ACTIONS.EXIT_APP.name(), "Exit", AppConstants.ICON_EXIT_KEY}, // Action for exiting the ModelBuilder.
        {AppActionProvider.APP_ACTIONS.UNDO.name(), "Undo", AppConstants.ICON_UNDO_KEY}, //  Undo action for the UndoManager.
        {AppActionProvider.APP_ACTIONS.REDO.name(), "Redo", AppConstants.ICON_REDO_KEY}, // Redo action for the UndoManager.
        {AppActionProvider.APP_ACTIONS.DO_LAYOUT.name(), "Layout", AppConstants.ICON_LAYOUT_KEY}, // Action for layouting the graph.
        {AppActionProvider.APP_ACTIONS.DEPLOY.name(), "Deploy", AppConstants.ICON_DEPLOY_KEY}, // Action for starting the deployment.
        {AppActionProvider.APP_ACTIONS.UNDEPLOY.name(), "Undeploy", AppConstants.ICON_UNDEPLOY_KEY}, //  Action for starting the undeployment.
        {AppActionProvider.APP_ACTIONS.UNDEPLOY_ANY.name(), "Undeploy a process", AppConstants.ICON_UNDEPLOY_ANY_KEY},
        {AppActionProvider.APP_ACTIONS.PREVIEW_ROLA.name(), "Preview ROLA", AppConstants.ICON_PREVIEW_KEY},
        {AppActionProvider.APP_ACTIONS.RELOAD_PROCESSES.name(), "Reload processes", AppConstants.ICON_RELOAD_KEY}, // Action for reloading processes from the SP (client).
        {AppActionProvider.APP_ACTIONS.MANAGE_REMOTES.name(), "Manage remotes", AppConstants.ICON_MANAGE_REMOTE},
        {AppActionProvider.APP_ACTIONS.PUBLISH.name(), "Publish model at SemanticProxy", AppConstants.ICON_PUBLISH},
        {AppActionProvider.APP_ACTIONS.EXECUTE.name(), "Execute this process", AppConstants.ICON_EXECUTE_KEY}, //  Action for opening the execute-current-model-dialog.
        {AppActionProvider.APP_ACTIONS.EXECUTE_ANY.name(), "Execute a process", AppConstants.ICON_EXECUTE_ANY_KEY}, //  Action for opening the execute-any-dialog.
        {AppActionProvider.APP_ACTIONS.PROFILE.name(), "Profile this process", AppConstants.ICON_PROFILE_KEY}, //  Action for opening the profile-current-model-dialog.
        {AppActionProvider.APP_ACTIONS.TEST.name(), "Test this process", AppConstants.ICON_TEST_KEY}, //  Action for opening the test-current-model-dialog.
        {AppActionProvider.APP_ACTIONS.ABOUT.name(), "About", AppConstants.ICON_ABOUT_KEY}}; // Action for showing the about dialog.

    /**
     * the keys refer to MenuBar enum.
     */
    public static final String[][] MENU_CAPTIONS = new String[][]{
        {"FILE", "File"},
        {"EDIT", "Edit"},
        {"TOOLS", "Tools"},
        {"MODEL", "Model"},
        {"HELP", "Help"},};

    // Tree View
    public static Object TREE_ROOT_NAME = "...";
    public static Object TREE_PROCESSES_NAME = "Processes";
    public static Object TREE_DOWNLOADSERVICES_NAME = "Download Services";
    public static Object TREE_INTERFACEOBJECTS_NAME = "In & Outputs";   // inputs, outputs etc.

    // Properties View
    public final static String PROPERTIES_MULTI_ELEMENTS_SELECTION = "(multiple elements selected)";
    public final static String PROPERTIES_PROCESS_TITLE = "Process";
    public final static String PROPERTIES_INPUTS_TITLE = "Inputs";
    public final static String PROPERTIES_OUTPUTS_TITLE = "Outputs";
    public final static String PROPERTIES_PANEL_TITLE = "Properties";
    public final static String PROPERTIES_PROCESS_IDENTIFIER_LABEL = "Identifier";
    public final static String PROPERTIES_PROCESS_ABSTRACT_LABEL = "Abstract";
    public final static String PROPERTIES_PROCESS_TITLE_LABEL = "Identifier";
    public final static String PROPERTIES_PORT_IDENTIFIER_LABEL = "Identifier";
    public final static String PROPERTIES_PORT_ABSTRACT_LABEL = "Identifier";
    public final static String PROPERTIES_PORT_TITLE_LABEL = "Title";
    public final static String PROPERTIES_PORT_DATATYPE_LABEL = "Datatype";
    public final static String PROPERTIES_BTN_EDIT_FORMAT_TTT = "Select format";
    public final static String PROPERTIES_PROPERTY_EDIT = "edit property '%s' of '%s'";
    public final static String PROPERTIES_MODELDATA_TITLE = "Model";
    public final static String PROPERTIES_MODELDATA_IDENTIFIER = "Name";
    public final static String PROPERTIES_MODELDATA = "OWS Data";
    public final static String PROPERTIES_GLOBALPORTDATA_TITLE = "Port";

    // Undo-/Redo-Manager
    public final static String UNDOMANAGER_CANT_UNDO = "Can't undo";
    public final static String UNDOMANAGER_CANT_REDO = "Can't redo";

    public final static String TREE_VIEW_TITLE = "Processes";//"Modelling Elements";
    public final static String EDITOR_DEFAULT_TITLE = "Model Editor"; //(untitled)";

    public final static Color SELECTION_BG_COLOR = new Color(0xb0c0e0);
    // new Color(0xcdff00); // <-- fr34ky ne0n (needs black FG)
    // new Color(0xa0ace5); // <-- original MB style
    // new Color(0xa8cde8); // <-- RichWPS logo blue
    // new Color(0x37ABC8); // <-- old RichWPS turquoise
    public final static Color SELECTION_FG_COLOR = Color.WHITE;

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
    public final static String TMP_FILE_FAILED = "An error occured while create temproary files.";
    public final static String SEE_LOGGING_TABS = "See logging tabs for details.";
    public final static String ERROR_MSG_IS_FORMAT = "Error message is '%s'.";

    /**
     * Error dialog message for mapping errors.
     */
    public final static String LOAD_MODEL_MAPPING_ERROR = "Errors occured while loading the file's processes.\nSee logging tabs for details.";

    /**
     * Error message for missing process ports.
     */
    public final static String LOAD_MODEL_MAPPING_ERROR_MISSINGPORT = "A port is missing in the file. It is recommended to replace this process with a valid version.";

    /**
     * Error message for unknown process ports.
     */
    public final static String LOAD_MODEL_MAPPING_ERROR_UNKNOWNPORT = "Unknown port found in model file:";

    /**
     * Formatable error message String for outputing a process port (Port-Id,
     * Process-ID, Server-URL).
     */
    public final static String LOAD_MODEL_MAPPING_ERROR_FORMAT = "\n\tPort\t'%s'\n\tProcess\t'%s'\n\tServer\t'%s'";

    /**
     * ToolTips
     */
    public final static int TOOLTIP_DISMISS_DELAY = Integer.MAX_VALUE;
    public final static int TOOLTIP_INITIAL_DELAY = 400;

    /**
     * UIManager keys.
     */
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
    public final static String ICON_INFO_KEY = "richwps_icon_info";
    public final static String ICON_RELOAD_KEY = "richwps_icon_reload";
    public final static String ICON_MANAGE_REMOTE = "richwps_icon_manageremote";
    public final static String ICON_PUBLISH = "richwps_icon_propagate";
    public final static String ICON_REFRESH_KEY = "richwps_icon_refresh";
    public final static String ICON_PROCESS_KEY = "richwps_icon_process";
    public final static String ICON_PREVIEW_KEY = "rwps_icon_preview";
    public final static String ICON_DEPLOY_KEY = "rwps_icon_deploy";
    public final static String ICON_UNDEPLOY_ANY_KEY = "rwps_icon_undeploy_any";
    public final static String ICON_UNDEPLOY_KEY = "rwps_icon_undeploy";
    public final static String ICON_EXECUTE_ANY_KEY = "richwps_icon_executeany";
    public final static String ICON_EXECUTE_KEY = "richwps_icon_execute";
    public final static String ICON_PROFILE_KEY = "richwps_icon_profile";
    public final static String ICON_TEST_KEY = "richwps_icon_debug";

    public final static String ICON_ABOUT_KEY = "richwps_icon_about";
    public final static String ICON_LOADING_STATUS_KEY = "richwps_icon_loading";
    public final static String ICON_LOADING_ANI_KEY = "richwps_icon_loading_ani";
    public final static String ICON_EDIT_KEY = "richwps_icon_edit";
    public final static String ICON_ADD_KEY = "richwps_icon_add";
    public final static String ICON_DELETE_KEY = "richwps_icon_delete";
    public final static String ICON_CLOSE_KEY = "richwps_icon_close";
    public final static String ICON_CLOSE_INACTIVE_KEY = "richwps_icon_close_inactive";
    public final static String ICON_SEARCH_KEY = "richwps_icon_search";
    public final static String ICON_CHECK_KEY = "richwps_icon_check";
    public final static String ICON_CHECK_DISABLED_KEY = "richwps_icon_disabled_check";
    public final static String ICON_CHECK_FAVOURITE_KEY = "richwps_icon_check_favourite";
    public final static String ICON_CHECK_FAVOURITE_DISABLED_KEY = "richwps_icon_check_favourite_disabled";
    public final static String ICON_FAVOURITE_DISABLED_KEY = "richwps_icon_favourite_disabled";
    public final static String ICON_FAVOURITE_KEY = "richwps_icon_favourite";
    public final static String ICON_EMPTY_KEY = "richwps_icon_empty";
    public final static String ICON_WARNING_KEY = "richwps_icon_warning";

    /**
     * large icons directory/path
     */
    public final static String LARGE_ICON_DIR = "64";

    /**
     * Prefix for icon keys to build up keys of large icons
     */
    public final static String LARGE_ICON_PREFIX = "large_";

    // Images
    public final static String ICON_ABOUTIMAGE_KEY = "richwps_image_about";
    public final static String ICON_MBLOGO_KEY = "rwps_image_mblogo";

    // Graph error messages
    public final static String GRAPH_ERROR_INPUT_OCCUPIED = "Port is already connected";
    public final static String GRAPH_ERROR_OUTPUT_OCCUPIED = "Port is already connected";
    public final static String GRAPH_ERROR_SINGLE_FEEDBACK = "Connecting an input to an output of the same process is not possible"; //"Feedback connection loops are not possible";
    public final static String GRAPH_ERROR_IN_TO_IN = "An input port must be connected to an output port";
    public final static String GRAPH_ERROR_OUT_TO_OUT = "An output port must be connected to an input port";
    public final static String GRAPH_ERROR_PORTTYPES_NOT_COMPATIBLE = "Port types are not compatible";
    public final static String GRAPH_EXCEPTION_PORT_NOT_GLOBAL = "Only global ports can be added to the graph";

    /**
     * Used for error messages, e.g. in ToolTips
     */
    public static Color ERROR_MESSAGE_COLOR = Color.RED;

    public final static String ERROR_MSG_IS = "Error message is: ";

    public final static String SUB_TREEVIEW_TITLE = "Used elements";

    public final static String DEPLOYMENT_FAILED = "Deployment failed!";

    public static String INCOMPATIBLE_DATATYPE_DESCRIPTION = "Datatype '%s' can not be described with '%s'";

    // Monitor key translations
    final static String MONITOR_DATA = "Monitor Data";
    final static String MONITOR_TRANSLATION_RESPONCE_METRIC = "Response Metric";
    static String[][] MONITOR_KEY_TRANSLATIONS = {
        {"response_metric", MONITOR_TRANSLATION_RESPONCE_METRIC}
    };

    final static Color MONITOR_DATA_BG_COLOR = new Color(0xfccfcf);

    public final static String EXCEPTION_ILLEGAL_DEFAULT_FORMAT = "The given default format is not a supported format.";

}
