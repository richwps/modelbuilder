package de.hsos.richwps.mb.app;

import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import de.hsos.richwps.mb.app.actions.AppAction;
import de.hsos.richwps.mb.app.actions.AppActionProvider;
import de.hsos.richwps.mb.app.view.AppSplashScreen;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialog;
import de.hsos.richwps.mb.app.view.preferences.AppPreferencesDialogHandler;
import de.hsos.richwps.mb.app.view.properties.PropertyComponentQosTargets;
import de.hsos.richwps.mb.app.view.semanticProxy.SementicProxySearch;
import de.hsos.richwps.mb.app.view.treeView.MainTreeViewController;
import de.hsos.richwps.mb.app.view.treeView.MainTreeViewPanel;
import de.hsos.richwps.mb.app.view.treeView.SubTreeViewController;
import de.hsos.richwps.mb.appEvents.AppEvent;
import de.hsos.richwps.mb.appEvents.AppEventService;
import de.hsos.richwps.mb.appEvents.IAppEventObserver;
import de.hsos.richwps.mb.entity.OwsObjectWithProperties;
import de.hsos.richwps.mb.entity.ProcessEntity;
import de.hsos.richwps.mb.entity.ProcessPort;
import de.hsos.richwps.mb.entity.ports.BoundingBoxInput;
import de.hsos.richwps.mb.entity.ports.ComplexDataOutput;
import de.hsos.richwps.mb.entity.ports.LiteralInput;
import de.hsos.richwps.mb.entity.ports.ProcessInputPort;
import de.hsos.richwps.mb.graphView.GraphSetup;
import de.hsos.richwps.mb.graphView.GraphView;
import de.hsos.richwps.mb.graphView.ModelElementsChangedListener;
import de.hsos.richwps.mb.graphView.mxGraph.GraphModel;
import de.hsos.richwps.mb.graphView.mxGraph.codec.CellCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ComplexDataTypeFormatCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.GraphEdgeCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.GraphModelCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ObjectWithPropertiesCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ProcessEntityCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.ProcessPortCodec;
import de.hsos.richwps.mb.graphView.mxGraph.codec.PropertyGroupCodec;
import de.hsos.richwps.mb.monitor.boundary.ProcessMetricProvider;
import de.hsos.richwps.mb.processProvider.boundary.DatatypeProvider;
import de.hsos.richwps.mb.processProvider.boundary.ProcessProvider;
import de.hsos.richwps.mb.properties.Property;
import de.hsos.richwps.mb.properties.PropertyKeyTranslator;
import de.hsos.richwps.mb.treeView.TreenodeTransferHandler;
import de.hsos.richwps.mb.ui.UiHelper;
import de.hsos.richwps.mb.undoManager.MbUndoManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoableEdit;

/**
 * Creates the app components and connects them.
 *
 * @author dziegenh
 */
public class AppSetup {

    static void setup(final App app, boolean debugMode) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        AppSplashScreen splash = new AppSplashScreen();

        splash.showMessageAndProgress("Loading config", 0);
        {
            String hostKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_HOST.name();
            String portKey = AppConfig.CONFIG_KEYS.HTTPPROXY_S_PORT.name();

            String host = AppConfig.getConfig().get(hostKey, "");
            System.setProperty("http.proxyHost", host);

            String port = AppConfig.getConfig().get(portKey, "");
            System.setProperty("http.proxyPort", port);

            if (null != AppConstants.DEFAULT_LOCALE) {
                Locale.setDefault(AppConstants.DEFAULT_LOCALE);
            }
        }

        splash.showMessageAndProgress("Loading resources", 5);
        {
            setupUiManager();

            // Optional Debug Logging.
            if (debugMode) {
                AppConstants.DEBUG_MODE = true;
                AppEventService.getInstance().registerObserver(new IAppEventObserver() {
                    @Override
                    public void eventOccured(AppEvent e) {
                        de.hsos.richwps.mb.Logger.log(e);
                    }
                });
            }

            // Configure MB components before initialisation
            GraphSetup.localInputBgColor = AppConstants.INPUT_PORT_COLOR_STRING;
            GraphSetup.localOutputBgColor = AppConstants.OUTPUT_PORT_COLOR_STRING;
            addGraphCodecs();

        }

        splash.showMessageAndProgress("Creating components", 10);
        {
            app.init();
            setupProcessMetricProvider(app);
            setupActionProvider(app);
            setupUndoManager(app);
            setupProcessProvider(app);
            setupTreenodeTransferHandler(app);
            setupGraphDndProxy(app);
            setupSubTreeView(app);
            setupSemanticProxySearch(app);
            setupMainTreeView(app);
            setupMainTreeViewPanel(app);
            setupPreferences(app);
            setupProperties(app);
            setupPropertiesView(app);
            setupGraphView(app);
            setupDatatypeProvider(app);
        }

        splash.showMessageAndProgress("Creating actions", 15);
        {

            // Load last used filename
            String lastFilename = AppConfig.getConfig().get(AppConfig.CONFIG_KEYS.MODEL_S_LASTFILE.name(), "");
            File lastFile = new File(lastFilename);
            AppAction recentFileAction = app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.OPEN_RECENT_FILE);
            recentFileAction.setEnabled(lastFile.exists());
            if (lastFile.exists()) {
                recentFileAction.setName(lastFilename);
            }
        }

        splash.showMessageAndProgress("Creating window", 20);
        {
            // Create frame.
            app.getFrame().init(app);
            app.getFrame().setModellingEnabled(false);
            app.getFrame().setIconImage(((ImageIcon) UIManager.getIcon(AppConstants.ICON_MBLOGO_KEY)).getImage());

            // Delegate frame closing action
            app.getFrame().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    app.getActionProvider().fire(AppActionProvider.APP_ACTIONS.EXIT_APP);
                }
            });
        }

        splash.showMessageAndProgress("Initialising UI texts", 30);
        {
            // Setup ToolTip.
            ToolTipManager.sharedInstance().setInitialDelay(AppConstants.TOOLTIP_INITIAL_DELAY);
            ToolTipManager.sharedInstance().setDismissDelay(AppConstants.TOOLTIP_DISMISS_DELAY);
            UIManager.put("ToolTip.background", AppConstants.TOOLTIP_BG_COLOR);

            ProcessPort.TOOLTIP_STYLE_INPUT = AppConstants.TOOLTIP_CSS_FOR_INPUTS;
            ProcessPort.TOOLTIP_STYLE_OUTPUT = AppConstants.TOOLTIP_CSS_FOR_OUTPUTS;

            ProcessEntity.toolTipCssForMainContainer = AppConstants.TOOLTIP_CSS_FOR_MAIN_CONTAINER;

            // Setup property key translations
            final PropertyKeyTranslator propertyKeyTranslator = app.getPropertiesView().getPropertyKeyTranslator();
            setupPropertyTranslator(propertyKeyTranslator);
        }

        splash.showMessageAndProgress("Initialising user interactions", 45);
        {
            app.initDragAndDrop();
            app.getPreferencesDialog().init();
            app.getGraphView().init();
        }

        splash.showMessageAndProgress("Requesting processes", 60);
        {
            // connect to SP and fill tree with services etc. received from SP and managed remotes
            String loadRemotesKey = AppConfig.CONFIG_KEYS.REMOTES_B_DISCOVER_ON_START.name();
            boolean loadRemotesDefault = AppConstants.PREFERENCES_DISCOVER_REMOTES_ON_STARTUP_DEFAULT;
            boolean loadRemotes = AppConfig.getConfig().getBoolean(loadRemotesKey, loadRemotesDefault);
            app.getProcessProvider().setManagedRemotesEnabled(loadRemotes);

            app.getMainTreeView().fillTree();

            // after startup, remotes are always loaded
            app.getProcessProvider().setManagedRemotesEnabled(true);

            AppEventService.getInstance().addSourceCommand(AppConstants.INFOTAB_ID_SERVER, AppConstants.INFOTAB_ID_SERVER);
        }

        splash.showMessageAndProgress("ModelBuilder is ready!", 100);
        splash.setVisible(false);

        app.getFrame().setVisible(true);

        // Validate frame location and reset it if necessary.
        Dimension screenSize = UiHelper.getMultiMonitorScreenSize();
        if (app.getFrame().getX() > screenSize.width || app.getFrame().getY() > screenSize.height) {
            app.getFrame().setLocation(AppConstants.FRAME_DEFAULT_LOCATION);
        }

        // set existing pseudo-filename to enable setting the "changes saved" status.
        app.setCurrentModelFilename(null);
        app.setChangesSaved(true);

        // update actions as no model is loaded
        app.getGraphView().setEnabled(false);
        app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.SAVE_MODEL_AS).setEnabled(false);
        app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.UNDO).setEnabled(false);
        app.getActionProvider().getAction(AppActionProvider.APP_ACTIONS.REDO).setEnabled(false);

        if (app.hasSubTreeView()) {
            app.getSubTreeView().fillTree();
        }
    }

    /**
     * Loads icons into UIManager.
     */
    private static void loadIcons() {
        String iconDir = AppConstants.RES_ICONS_DIR + File.separator;
        String largeIconDir = iconDir + AppConstants.LARGE_ICON_DIR + File.separator;

        // App Icon
        UIManager.put(AppConstants.ICON_MBLOGO_KEY, new ImageIcon(iconDir + "mb_logo.png", "mb logo image"));

        // File Menu icons
        UIManager.put(AppConstants.ICON_NEW_KEY, new ImageIcon(iconDir + "document-new-6.png", "new icon"));
        UIManager.put(AppConstants.ICON_OPEN_KEY, new ImageIcon(iconDir + "document-open-2.png", "open icon"));
        UIManager.put(AppConstants.ICON_SAVE_KEY, new ImageIcon(iconDir + "document-save-5.png", "save icon"));
        UIManager.put(AppConstants.ICON_SAVEAS_KEY, new ImageIcon(iconDir + "document-save-as-4.png", "save as icon"));
        UIManager.put(AppConstants.ICON_PREFERENCES_KEY, new ImageIcon(iconDir + "system-settings.png", "prefs icon"));
        UIManager.put(AppConstants.ICON_EXIT_KEY, new ImageIcon(iconDir + "application-exit-2.png", "exit icon"));

        // Edit Menu Icons
        UIManager.put(AppConstants.ICON_UNDO_KEY, new ImageIcon(iconDir + "arrow-undo.png", "undo icon"));
        UIManager.put(AppConstants.ICON_REDO_KEY, new ImageIcon(iconDir + "arrow-redo.png", "redo icon"));
        // Modelling
        UIManager.put(AppConstants.ICON_LAYOUT_KEY, new ImageIcon(iconDir + "zoom-fit-best-4.png", "layout icon"));
        UIManager.put(AppConstants.ICON_ADDPORTS_KEY, new ImageIcon(iconDir + "zoom-fit-height.png", "addports icon"));
        UIManager.put(AppConstants.ICON_PROCESS_REPLACE_KEY, new ImageIcon(iconDir + "process_replace.png", "process replace icon"));
        UIManager.put(AppConstants.ICON_REARRANGE_KEY, new ImageIcon(iconDir + "distribute-horizontal-center.png", "rearrange icon"));

        // Tools Menu Icons
        // Help Menu Icons
        UIManager.put(AppConstants.ICON_ABOUT_KEY, new ImageIcon(iconDir + "help-about-3.png", "about icon"));

        // (unsorted Icons)
        UIManager.put(AppConstants.ICON_PREVIEW_KEY, new ImageIcon(iconDir + "layer-novisible.png", "preview icon"));
        UIManager.put(AppConstants.ICON_DEPLOY_KEY, new ImageIcon(iconDir + "server-add.png", "deploy icon"));
        UIManager.put(AppConstants.ICON_UNDEPLOY_ANY_KEY, new ImageIcon(iconDir + "archive-remove.png", "undeploy any icon"));
        UIManager.put(AppConstants.ICON_UNDEPLOY_KEY, new ImageIcon(iconDir + "server-delete.png", "undeploy icon"));
        UIManager.put(AppConstants.ICON_EXECUTE_ANY_KEY, new ImageIcon(iconDir + "arrow-right-3.png", "execute icon"));
        UIManager.put(AppConstants.ICON_EXECUTE_KEY, new ImageIcon(iconDir + "server-go.png", "execute icon"));
        UIManager.put(AppConstants.ICON_PROFILE_KEY, new ImageIcon(iconDir + "server-chart.png", "profile icon"));
        UIManager.put(AppConstants.ICON_TEST_KEY, new ImageIcon(iconDir + "report-magnify.png", "debug icon"));

        UIManager.put(AppConstants.ICON_INFO_KEY, new ImageIcon(iconDir + "dialog-information-4.png", "info icon"));

        UIManager.put(AppConstants.ICON_RELOAD_KEY, new ImageIcon(iconDir + "database-refresh.png", "reload icon"));
        UIManager.put(AppConstants.ICON_REFRESH_KEY, new ImageIcon(iconDir + "view-refresh-4.png", "refresh icon"));
        UIManager.put(AppConstants.ICON_MANAGE_REMOTE, new ImageIcon(iconDir + "database-gear.png", "manage remotes icon"));
        UIManager.put(AppConstants.ICON_PUBLISH, new ImageIcon(iconDir + "database-go.png", "publish icon"));

        // TreeNode Icons
        UIManager.put(AppConstants.ICON_PROCESS_KEY, new ImageIcon(iconDir + "process.png", "process icon"));
        UIManager.put(AppConstants.ICON_PROCESSES_KEY, new ImageIcon(iconDir + "tree_processes.png", "processes icon"));
        UIManager.put(AppConstants.ICON_PORTS_KEY, new ImageIcon(iconDir + "tree_ports.png", "ports icon"));
        UIManager.put(AppConstants.ICON_PORT_OUT_L_KEY, new ImageIcon(iconDir + "port_out_l.png", "port out L icon"));
        UIManager.put(AppConstants.ICON_PORT_OUT_C_KEY, new ImageIcon(iconDir + "port_out_c.png", "port out B icon"));
        UIManager.put(AppConstants.ICON_PORT_OUT_B_KEY, new ImageIcon(iconDir + "port_out_b.png", "port out C icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_L_KEY, new ImageIcon(iconDir + "port_in_l.png", "port in L icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_C_KEY, new ImageIcon(iconDir + "port_in_c.png", "port in B icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_B_KEY, new ImageIcon(iconDir + "port_in_b.png", "port in C icon"));
        UIManager.put(AppConstants.ICON_EDIT_KEY, new ImageIcon(iconDir + "edit-3.png", "edit icon"));
        UIManager.put(AppConstants.ICON_ADD_KEY, new ImageIcon(iconDir + "list-add-6.png", "add icon"));
        UIManager.put(AppConstants.ICON_DELETE_KEY, new ImageIcon(iconDir + "edit-delete-7.png", "delete icon"));
        UIManager.put(AppConstants.ICON_CLOSE_KEY, new ImageIcon(iconDir + "window-close-3.png", "close icon"));
        UIManager.put(AppConstants.ICON_CLOSE_INACTIVE_KEY, new ImageIcon(iconDir + "window-close-3_inactive.png", "close inactive icon"));
        UIManager.put(AppConstants.ICON_SEARCH_KEY, new ImageIcon(iconDir + "system-search-6.png", "search icon"));
        UIManager.put(AppConstants.ICON_CHECK_KEY, new ImageIcon(iconDir + "dialog-clean.png", "check icon"));
        UIManager.put(AppConstants.ICON_CHECK_DISABLED_KEY, new ImageIcon(iconDir + "dialog-clean_disabled.png", "disabled check icon"));
        UIManager.put(AppConstants.ICON_CHECK_FAVOURITE_KEY, new ImageIcon(iconDir + "dialog-clean-star.png", "check fav icon"));
        UIManager.put(AppConstants.ICON_CHECK_FAVOURITE_DISABLED_KEY, new ImageIcon(iconDir + "dialog-clean-star_disabled.png", "disabled check fav icon"));
        UIManager.put(AppConstants.ICON_FAVOURITE_DISABLED_KEY, new ImageIcon(iconDir + "bookmark-4_disabled.png", "disabled fav icon"));
        UIManager.put(AppConstants.ICON_FAVOURITE_KEY, new ImageIcon(iconDir + "bookmark-4.png", "fav icon"));
        UIManager.put(AppConstants.ICON_WARNING_KEY, new ImageIcon(iconDir + "dialog-warning-4.png", "warning icon"));
        UIManager.put(AppConstants.ICON_EMPTY_KEY, new ImageIcon(iconDir + "empty.png", "empty icon"));

        // bigger port icons
        UIManager.put(AppConstants.ICON_PORT_OUT_L_BIG_KEY, new ImageIcon(iconDir + "port_out_l_big.png", "port out L big icon"));
        UIManager.put(AppConstants.ICON_PORT_OUT_C_BIG_KEY, new ImageIcon(iconDir + "port_out_c_big.png", "port out B big icon"));
        UIManager.put(AppConstants.ICON_PORT_OUT_B_BIG_KEY, new ImageIcon(iconDir + "port_out_b_big.png", "port out C big icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_L_BIG_KEY, new ImageIcon(iconDir + "port_in_l_big.png", "port in L big icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_C_BIG_KEY, new ImageIcon(iconDir + "port_in_c_big.png", "port in B big icon"));
        UIManager.put(AppConstants.ICON_PORT_IN_B_BIG_KEY, new ImageIcon(iconDir + "port_in_b_big.png", "port in C big icon"));

        UIManager.put(AppConstants.ICON_LOADING_STATUS_KEY, new ImageIcon(iconDir + "ajax-loader.gif", "loading"));
        UIManager.put(AppConstants.ICON_LOADING_ANI_KEY, new ImageIcon(iconDir + "720.gif", "loading ani"));

        UIManager.put(AppConstants.ICON_EDIT_COPY_KEY, new ImageIcon(iconDir + "edit-copy-3.png", "edit copy icon"));
        UIManager.put(AppConstants.ICON_OPEN_IN_BROWSER, new ImageIcon(iconDir + "applications-internet.png", "open in browser"));

        // large icons
        UIManager.put(getLargeIconKey(AppConstants.ICON_NEW_KEY), new ImageIcon(largeIconDir + "document-new-6.png", "largeicon new"));
        UIManager.put(getLargeIconKey(AppConstants.ICON_OPEN_KEY), new ImageIcon(largeIconDir + "document-open-4.png", "largeicon open"));
    }

    private static String getLargeIconKey(String iconKey) {
        return AppConstants.LARGE_ICON_PREFIX + iconKey;
    }

    /**
     * Register custom mxGraph codecs
     */
    public static void addGraphCodecs() {
        mxCodecRegistry.register(new CellCodec());

        mxCodecRegistry.addPackage("de.hsos.richwps.mb.entity");
        mxCodecRegistry.register(new ProcessEntityCodec(new de.hsos.richwps.mb.entity.ProcessEntity()));
        mxCodecRegistry.addPackage("de.hsos.richwps.mb.entity.datatypes");
        mxCodecRegistry.register(new ComplexDataTypeFormatCodec(new de.hsos.richwps.mb.entity.datatypes.ComplexDataTypeFormat()));
        mxCodecRegistry.register(new mxObjectCodec(new de.hsos.richwps.mb.entity.datatypes.DataTypeDescriptionComplex()));
        mxCodecRegistry.addPackage("de.hsos.richwps.mb.entity.ports");
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.BoundingBoxInput()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.BoundingBoxOutput()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.ComplexDataInput()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.ComplexDataOutput()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.LiteralInput()));
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.ports.LiteralOutput()));

        mxCodecRegistry.addPackage("de.hsos.richwps.mb.graphView.mxGraph");
        mxCodecRegistry.register(new GraphEdgeCodec(new de.hsos.richwps.mb.graphView.mxGraph.GraphEdge()));
        mxCodecRegistry.register(new GraphModelCodec(new de.hsos.richwps.mb.graphView.mxGraph.GraphModel()));

        mxCodecRegistry.addPackage("de.hsos.richwps.mb.graphView.mxGraph.codec.objects");
        mxCodecRegistry.register(new ObjectWithPropertiesCodec(new de.hsos.richwps.mb.graphView.mxGraph.codec.objects.tmpPropertyGroup()));

        mxCodecRegistry.addPackage("de.hsos.richwps.mb.properties");
        mxCodecRegistry.register(new ObjectWithPropertiesCodec(new Property()));
        mxCodecRegistry.register(new PropertyGroupCodec(new de.hsos.richwps.mb.properties.PropertyGroup<>()));

        // COMPABILITY SUPPORT FOR OLDER MODEL VERSIONS
        mxCodecRegistry.addPackage("de.hsos.richwps.mb.entity.oldVersions");
        mxCodecRegistry.register(new ProcessPortCodec(new de.hsos.richwps.mb.entity.oldVersions.ProcessPort()));

    }

    private static void setupUiManager() {
        // Load colors
        String[] bgColorKeys = new String[]{
            "CheckBoxMenuItem.selectionBackground",
            "ComboBox.selectionBackground",
            "EditorPane.selectionBackground",
            "List.selectionBackground",
            "Menu.selectionBackground",
            "MenuItem.selectionBackground",
            "RadioButtonMenuItem.selectionBackground",
            "PasswordField.selectionBackground",
            "ProgressBar.foreground",
            "ProgressBar.selectionBackground",
            "Table.selectionBackground",
            "TextArea.selectionBackground",
            "TextField.selectionBackground",
            "TextPane.selectionBackground",
            "Tree.selectionBackground"
        };
        for (String key : bgColorKeys) {
            UIManager.put(key, AppConstants.SELECTION_BG_COLOR);
        }
        String[] fgColorKeys = new String[]{
            "CheckBoxMenuItem.selectionForeground",
            "ComboBox.selectionForeground",
            "EditorPane.selectionForeground",
            "List.selectionForeground",
            "Menu.selectionForeground",
            "MenuItem.selectionForeground",
            "RadioButtonMenuItem.selectionForeground",
            "PasswordField.selectionForeground",
            "ProgressBar.background",
            "ProgressBar.selectionForeground",
            "Table.selectionForeground",
            "TextArea.selectionForeground",
            "TextField.selectionForeground",
            "TextPane.selectionForeground",
            "Tree.selectionForeground"
        };
        for (String key : fgColorKeys) {
            UIManager.put(key, AppConstants.SELECTION_FG_COLOR);
        }

        loadIcons();
    }

    private static void setupPropertyTranslator(PropertyKeyTranslator translator) {

        // Processes and Ports common properties
        translator.addTranslation(OwsObjectWithProperties.PROPERTIES_KEY_IDENTIFIER, "Identifier");
        translator.addTranslation(OwsObjectWithProperties.PROPERTIES_KEY_TITLE, "Title");
        translator.addTranslation(OwsObjectWithProperties.PROPERTIES_KEY_ABSTRACT, "Abstract");
        translator.addTranslation(OwsObjectWithProperties.PROPERTY_KEY_OWS_GROUP, "OWS Data");

        // process properties
        translator.addTranslation(ProcessEntity.PROPERTIES_KEY_SERVER, "Server");
        translator.addTranslation(ProcessEntity.PROPERTIES_KEY_INPUT_PORTS, "Inputs");
        translator.addTranslation(ProcessEntity.PROPERTIES_KEY_OUTPUT_PORTS, "Outputs");
        translator.addTranslation(ProcessEntity.PROPERTIES_KEY_VERSION, "Version");

        // port properties
        translator.addTranslation(ProcessInputPort.PROPERTY_KEY_MINOCCURS, "Min occurs");
        translator.addTranslation(ProcessInputPort.PROPERTY_KEY_MAXOCCURS, "Max occurs");
        translator.addTranslation(LiteralInput.PROPERTY_KEY_DEFAULTVALUE, "Default literal value");
        translator.addTranslation(LiteralInput.PROPERTY_KEY_LITERALDATATYPE, "Literal Datatype");
        translator.addTranslation(ComplexDataOutput.PROPERTY_KEY_MAXMB, "Max MB");
        translator.addTranslation(ComplexDataOutput.PROPERTY_KEY_DATATYPEDESCRIPTION, "Complex datatype");
        translator.addTranslation(BoundingBoxInput.PROPERTY_KEY_DESCRIPTION, "Datatype description bbox");

        // model properties
        translator.addTranslation(GraphModel.PROPERTIES_KEY_OWS_IDENTIFIER, "Identifier");
        translator.addTranslation(GraphModel.PROPERTIES_KEY_OWS_ABSTRACT, "Abstract");
        translator.addTranslation(GraphModel.PROPERTIES_KEY_OWS_ENDPOINT, "Endpoint");
        translator.addTranslation(GraphModel.PROPERTIES_KEY_OWS_VERSION, "Version");
        translator.addTranslation(GraphModel.PROPERTIES_KEY_OWS_TITLE, "Title");

        // monitor 
        translator.addTranslation(ProcessMetricProvider.PROPERTY_KEY_MONITOR_DATA, "Monitor Data");
        translator.addTranslation(ProcessMetricProvider.PROPERTY_KEY_RESPONCE_METRIC, "Response Metric");

        // QoS overview
        translator.addTranslation(PropertyComponentQosTargets.PROPERTY_NAME, "Manage Targets");

    }

    private static void setupUndoManager(final App app) {
        AppUndoManager undoManager = app.getUndoManager();

        undoManager.setGraphView(app.getGraphView());
        undoManager.setSubTreeView(app.getSubTreeView());
        undoManager.setPropertiesView(app.getPropertiesView());
        undoManager.setActionProvider(app.getActionProvider());

        undoManager.init();
        undoManager.addChangeListener(new MbUndoManager.UndoManagerChangeListener() {
            @Override
            public void changed(MbUndoManager.UNDO_MANAGER_CHANGE change, UndoableEdit edit) {
                app.setChangesSaved(false);
            }
        });

    }

    private static void setupActionProvider(App app) {
        AppActionHandler actionHandler = app.getActionHandler();
        app.getActionProvider().setActionHandler(actionHandler);
    }

    private static void setupProcessMetricProvider(App app) {
        ProcessMetricProvider processMetricProvider = app.getProcessMetricProvider();

        // get URL from config
        String key = AppConfig.CONFIG_KEYS.MONITOR_S_URL.name();
        String defUrl = AppConstants.MONITOR_DEFAULT_URL;
        String confUrl = AppConfig.getConfig().get(key, defUrl);

        processMetricProvider.setMonitorUrl(confUrl);
    }

    private static void setupTreenodeTransferHandler(App app) {
        TreenodeTransferHandler treenodeTransferHandler = app.getProcessTransferHandler();
        treenodeTransferHandler.setProcessProvider(app.getProcessProvider());
    }

    private static void setupProcessProvider(App app) {
        ProcessProvider processProvider = app.getProcessProvider();
        ProcessMetricProvider processMetricProvider = app.getProcessMetricProvider();
        processProvider.setProcessMetricProvider(processMetricProvider);

        for (String[] keyAndValue : AppConstants.PROCESS_PROVIDER_TRANSLATIONS) {
            processProvider.getTranslator().addTranslation(keyAndValue[0], keyAndValue[1]);
        }

        AppEventService.getInstance().addSourceCommand(processProvider, AppConstants.INFOTAB_ID_SEMANTICPROXY);
    }

    private static void setupGraphDndProxy(App app) {
        Component graphDndProxy = app.getGraphDndProxy();
        graphDndProxy.setVisible(false);
    }

    private static void setupSubTreeView(App app) {
        SubTreeViewController subTreeView = app.getSubTreeView();
        if (AppConstants.ENABLE_SUB_TREE_VIEW) {

            subTreeView.setGraphDndProxy(app.getGraphDndProxy());
            subTreeView.setProcessProvider(app.getProcessProvider());
            subTreeView.setGraphView(app.getGraphView());
            subTreeView.setParent(app.getFrame());
            subTreeView.setProcessTransferHandler(app.getProcessTransferHandler());
        }
    }

    private static void setupSemanticProxySearch(App app) {
        SementicProxySearch semanticProxySearch = app.getSemanticProxySearch();
        semanticProxySearch.setGraphDndProxy(app.getGraphDndProxy());
        semanticProxySearch.setProcessProvider(app.getProcessProvider());
        semanticProxySearch.setGraphView(app.getGraphView());
        semanticProxySearch.setParent(app.getFrame());
        semanticProxySearch.setProcessTransferHandler(app.getProcessTransferHandler());
    }

    private static void setupMainTreeView(App app) {
        MainTreeViewController mainTreeView = app.getMainTreeView();

        mainTreeView.setGraphDndProxy(app.getGraphDndProxy());
        mainTreeView.setProcessProvider(app.getProcessProvider());
        mainTreeView.setGraphView(app.getGraphView());
        mainTreeView.setParent(app.getFrame());
        mainTreeView.setProcessTransferHandler(app.getProcessTransferHandler());

        // handle changes of the SP url config
        AppPreferencesDialog preferencesDialog = app.getPreferencesDialog();
        preferencesDialog.addWindowListener(mainTreeView.getPreferencesListener());
    }

    private static void setupMainTreeViewPanel(App app) {
        JTree tree = app.getMainTreeView().getTreeView().getGui();
        AppActionProvider actionProvider = app.getActionProvider();

        MainTreeViewPanel mainTreeViewPanel = app.getMainTreeViewPanel();
        mainTreeViewPanel.init(tree, actionProvider);
    }

    private static void setupPreferences(App app) {
        AppPreferencesDialog preferencesDialog = app.getPreferencesDialog();
        final AppPreferencesDialogHandler handler = new AppPreferencesDialogHandler();
        handler.setProcessMetricProvider(app.getProcessMetricProvider());
        handler.setProcessProvider(app.getProcessProvider());
        handler.setMainTreeView(app.getMainTreeView());
        preferencesDialog.addWindowListener(handler);
    }

    private static void setupProperties(App app) {
        AppProperties appProperties = app.getAppProperties();
        appProperties.setGraphView(app.getGraphView());
        appProperties.setPropertiesView(app.getPropertiesView());
        appProperties.setDatatypeProvider(app.getDatatypeProvider());
        appProperties.setParentFrame(app.getFrame());
        appProperties.setActionProvider(app.getActionProvider());
    }

    private static void setupPropertiesView(App app) {
        AppPropertiesView propertiesView = app.getPropertiesView();

        propertiesView.setAppProperties(app.getAppProperties());

        propertiesView.setParentFrame(app.getFrame());
        propertiesView.setUndoManager(app.getUndoManager());
        propertiesView.setActionProvider(app.getActionProvider());
        propertiesView.setDatatypeProvider(app.getDatatypeProvider());
        propertiesView.setGraphView(app.getGraphView());

        // update properties view depending on the graph selection
        ListSelectionListener selectionListener = propertiesView.getListSelectionListener();
        app.getGraphView().addSelectionListener(selectionListener);
    }

    private static void setupGraphView(final App app) {
        AppGraphView graphView = app.getGraphView();
        graphView.setParentFrame(app.getFrame());
        graphView.setUndoManager(app.getUndoManager());
        graphView.setProcessProvider(app.getProcessProvider());
        graphView.setActionProvider(app.getActionProvider());
        graphView.setAppProperties(app.getAppProperties());
        graphView.setSubTreeView(app.getSubTreeView());

        graphView.addModelElementsChangedListener(new ModelElementsChangedListener() {
            @Override
            public void modelElementsChanged(Object element, GraphView.ELEMENT_TYPE type, GraphView.ELEMENT_CHANGE_TYPE changeType) {
                app.updateGraphDependentActions();
            }
        });
    }

    private static void setupDatatypeProvider(App app) {
        String complexCsv = AppConstants.COMPLEX_FORMATS_CSV_FILE;
        String literalCsv = AppConstants.LITERAL_DATATYPES_CSV_FILE;

        DatatypeProvider datatypeProvider = app.getDatatypeProvider();
        datatypeProvider.setComplexCsvFile(complexCsv);
        datatypeProvider.setLiteralCsvFile(literalCsv);
    }

}
